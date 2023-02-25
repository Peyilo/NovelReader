package org.anvei.novelreader.widget.readview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import org.anvei.novelreader.R;
import org.anvei.novelreader.bean.Book;
import org.anvei.novelreader.bean.Chapter;
import org.anvei.novelreader.widget.readview.flip.FlipLayout;
import org.anvei.novelreader.widget.readview.flip.PageDirection;
import org.anvei.novelreader.widget.readview.interfaces.TaskListener;
import org.anvei.novelreader.widget.readview.page.Page;
import org.anvei.novelreader.widget.readview.page.PageConfig;
import org.anvei.novelreader.widget.readview.page.PageFactory;
import org.anvei.novelreader.widget.readview.utils.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.security.auth.DestroyFailedException;

public class ReadView extends FlipLayout {

    private static final String TAG = "ReadView";
    private PageConfig pageConfig;
    private PageFactory pageFactory;
    private Book book;
    private BookLoader bookLoader;
    private final List<Task> taskList = new ArrayList<>();
    // 待刷新的视图
    private final View[] needRefreshedPage = new View[3];
    public static final int PRE_PAGE = 0x00;
    public static final int CUR_PAGE = 0x01;
    public static final int NEXT_PAGE = 0x02;

    private int chapterIndex = 1;
    private int pageIndex = 1;
    public static final int THE_LAST = -1;

    private int preLoadBefore = 2;          // 预加载当前章节之前的两章节
    private int preLoadBehind = 2;          // 预加载当前章节之后的一章节

    public ReadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addOnFlipOverListener(new OnFlipListener() {
            @Override
            public void onNext() {
                if (pageIndex == getPageCount()) {
                    chapterIndex++;
                    pageIndex = 1;
                } else {
                    pageIndex++;
                }
                Log.d(TAG, "onNext: chapter" + chapterIndex + ", page" + pageIndex);
            }
            @Override
            public void onPre() {
                if (pageIndex == 1) {
                    chapterIndex--;
                    pageIndex = getPageCount();
                } else {
                    pageIndex--;
                }
                Log.d(TAG, "onPre: chapter" + chapterIndex + ", page" + pageIndex);
            }
        });
        for (int i = 0; i < 3; i++) {
            addView(createChildView());
        }
        setCurPagePointer(1);
    }

    private ReadPage createChildView() {
        if (pageConfig == null) {
            pageConfig = new PageConfig();
            pageFactory = new PageFactory(pageConfig);
            pageConfig.pageFactory = pageFactory;
        }
        ReadPage page = new ReadPage(getContext());
        page.setView(R.layout.view_page_item);
        page.setTitleView(page.getView().findViewById(R.id.page_title));
        page.setContentView(page.getView().findViewById(R.id.page_content));
        page.getContentView().setPageConfig(pageConfig);
        return page;
    }

    /**
     * 判断是否有下一页，只有以下两种情况会返回false：<br/>
     * <li>当前章节未完成初始化 </li>
     * <li>当前页面为最后一章的最后一页</li>
     */
    @Override
    protected boolean hasNextPage() {
        if (book == null) {
            return false;
        }
        Chapter chapter = book.getChapter(chapterIndex);
        if (chapter.getStatus() != Chapter.Status.INITIALIZED) {
            requestLoadChapter(chapterIndex);
            return false;
        }
        if (isLastChapter()) {
            if (chapter.getPages().size() == pageIndex) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否有上一页
     */
    @Override
    protected boolean hasPrePage() {
        if (book == null) {
            return false;
        }
        Chapter chapter = book.getChapter(chapterIndex);
        if (chapter.getStatus() != Chapter.Status.INITIALIZED) {
            requestLoadChapter(chapterIndex);
            return false;
        }
        if (isFirstChapter() && pageIndex == 1) {
            return false;
        }
        return true;
    }

    // 当前章节是否是最后一章节
    public boolean isLastChapter() {
        return chapterIndex == getChapterCount();
    }

    // 当前章节是否是第一章
    public boolean isFirstChapter() {
        return chapterIndex == 1;
    }

    @Override
    protected View getView(View convertView, PageDirection direction) {
        switch (direction) {
            case TO_NEXT:
                if (hasNextPage()) {
                    // 这里返回的nextPage可能为null，是因为这里已经在加载下一章节，但是加载需要时间
                    Page nextPage = getNextPage();
                    if (nextPage == null) {
                        refresh(NEXT_PAGE);
                    }
                    ((ReadPage) convertView).getContentView().setPage(nextPage, null);
                }
                break;
            case TO_PREV:
                if (hasPrePage()) {
                    Page page = getPrePage();
                    if (page == null) {
                        refresh(PRE_PAGE);
                    }
                    ((ReadPage) convertView).getContentView().setPage(page, null);
                }
                break;
        }
        return convertView;
    }

    /**
     * 打开一本小说，注意：该方法只允许调用一次! <br/>
     * openBook()方法内部会直接new一个Task对象，在子线程中初始化Book对象，并且完成当前章节的分页 <br/>
     * @param bookLoader 小说加载器，可以通过该接口来自定义获取小说的方式
     */
    public void openBook(BookLoader bookLoader) {
        this.openBook(bookLoader, 1, 1);
    }

    /**
     * 如果chapterIndex为-1，则加载最后一章，如果pageIndex为负数则加载当前章节的最后一页
     * @param chapterIndex 章节序号
     * @param pageIndex 当前页面再本章节中的序号
     */
    public void openBook(BookLoader bookLoader, @IntRange(from = 1) int chapterIndex,
                         @IntRange(from = 1) int pageIndex) {
        this.bookLoader = bookLoader;
        this.chapterIndex = chapterIndex;
        this.pageIndex = pageIndex;
        startTask(() -> {
            book = bookLoader.getBook();    // 先加载完目录信息
            preLoad(chapterIndex);
            int count = book.getChapterCount();
            if (chapterIndex == -1) {
                this.chapterIndex = count;
            }
            // 加载当前章节
            Chapter curChapter = book.getChapter(this.chapterIndex);
            bookLoader.loadChapter(curChapter);
            post(() -> {
                pageConfig.width = ((ReadPage) getPageView(0)).contentView.getWidth();
                pageConfig.height = ((ReadPage) getPageView(0)).contentView.getHeight();
                curChapter.setPages(pageFactory.splitPage(curChapter.getContent()));
                ((ReadPage) getPageView(0)).getContentView().setPage(getCurPage(), null);
                if (hasNextPage()) {
                    Log.d(TAG, "openBook: init hasNextPage = " + true);
                    Page nextPage = getNextPage();
                    if (nextPage == null) {
                        Log.d(TAG, "openBook: init need refresh NEXT_PAGE");
                        refresh(NEXT_PAGE);
                    }
                    ((ReadPage) getPageView(1)).getContentView().setPage(nextPage, null);
                }
                if (hasPrePage()) {
                    Log.d(TAG, "openBook: init hasPrePage = " + true);
                    Page prePage = getPrePage();
                    if (prePage == null) {
                        Log.d(TAG, "openBook: init need refresh PRE_PAGE");
                        refresh(PRE_PAGE);
                    }
                    ((ReadPage) getPageView(-1)).getContentView().setPage(prePage, null);
                }
            });
        });
    }

    protected Page getCurPage() {
        return getPage(chapterIndex, pageIndex);
    }

    protected Page getPrePage() {
        if (pageIndex == 1) {
            if (isFirstChapter()) {
                throw new IllegalStateException("当前没有上一页！");
            } else {
                // 返回上一章的最后一页
                return getPage(chapterIndex - 1, -1);
            }
        }
        return getPage(chapterIndex, pageIndex - 1);
    }

    protected Page getNextPage() {
        List<Page> pages = book.getChapter(chapterIndex).getPages();
        if (pages == null) {
            throw new IllegalStateException("当前章节还未完成分页！");
        }
        if (pageIndex < Objects.requireNonNull(pages).size()) {
            return getPage(chapterIndex, pageIndex + 1);
        } else {
            // 当前分页为本章节的最后一页
            if (isLastChapter()) {
                throw new IllegalStateException("当前没有下一页！");
            } else {
                return getPage(chapterIndex + 1, 1);
            }
        }
    }

    // 获取指定页面的数据
    protected Page getPage(int chapterIndex, int pageIndex) {
        Log.d(TAG, "getPage: chapterIndex = " + chapterIndex +", pageIndex = " + pageIndex);
        Chapter chapter = book.getChapter(chapterIndex);
        preLoad(this.chapterIndex);
        if (chapter.getStatus() == Chapter.Status.INITIALIZED) {
            if (pageIndex == -1) {
                pageIndex = chapter.getPages().size();
            }
            if (pageIndex > 0) {
                return chapter.getPages().get(pageIndex - 1);
            }
        }
        requestLoadChapter(chapterIndex);
        return null;
    }

    /**
     * 请求加载指定章节，并完成章节分页
     */
    protected synchronized void requestLoadChapter(int chapterIndex) {
        Chapter chapter = book.getChapter(chapterIndex);
        Chapter.Status status = chapter.getStatus();
        if (status != Chapter.Status.IS_LOADING && status != Chapter.Status.INITIALIZED) {
            chapter.setStatus(Chapter.Status.IS_LOADING);
            startTask(() -> {
                bookLoader.loadChapter(chapter);
                pageConfig.width = getWidth();
                pageConfig.height = getHeight();
                chapter.setPages(pageFactory.splitPage(chapter.getContent()));
                Log.d(TAG, "requestLoadChapter: load chapter " + chapterIndex);
                requestRefreshPage();
            });
        }
        preLoad(chapterIndex);
    }

    /**
     * 请求对指定章节进行重新分页（数据层重新分页）
     * 数据层完成分页以后还会调用requestRefreshPage()方法，刷新视图层（该方法没有开启子线程）
     */
    protected synchronized void requestSplitChapter(int chapterIndex) {
        Chapter chapter = book.getChapter(chapterIndex);
        if (chapter.getStatus() != Chapter.Status.IS_LOADING) {
            chapter.setStatus(Chapter.Status.IS_LOADING);
            pageConfig.width = getWidth();
            pageConfig.height = getHeight();
            chapter.setPages(pageFactory.splitPage(chapter.getContent()));
            Log.d(TAG, "requestSplitChapter: split chapter" + chapterIndex);
            requestRefreshPage();
        }
    }

    // 请求刷新页面
    public void requestRefreshPage() {
        post(() -> {
            for (int i = 0; i < needRefreshedPage.length; i++) {
                ReadPage readPage = (ReadPage) needRefreshedPage[i];
                if (readPage != null) {
                    Page page;
                    String log;
                    if (i == PRE_PAGE) {
                        log = "PRE_PAGE";
                        page = getPrePage();
                    } else if (i == CUR_PAGE) {
                        log = "CUR_PAGE";
                        page = getCurPage();
                    } else {
                        log = "NEXT_PAGE";
                        page = getNextPage();
                    }
                    Log.d(TAG, "requestRefreshPage: refresh " + log);
                    readPage.getContentView().setPage(page, null);
                    if (page != null) {
                        needRefreshedPage[i] = null;
                    }
                }
            }
        });
    }

    /**
     * 该方法不会直接刷新页面，只是将指定页面标记为待刷新，需要调用requestRefreshPage()方法触发实际刷新
     * @param page 取值为CUR_PAGE、PRE_PAGE、NEXT_PAGE，分别对应当前页面、上一页面、下一页面
     */
    public void refresh(int page) {
        needRefreshedPage[PRE_PAGE] = getPageView(page - 1);
    }

    protected void preLoad(int chapterIndex) {
        preLoad(chapterIndex, null);
    }
    /**
     * 完成以指定章节周围章节的预加载（不回加载指定的章节），
     * 如果想同时加载指定章节及其对应的预加载章节，请调用requestLoadChapter()
     */
    protected void preLoad(int chapterIndex, @Nullable TaskListener listener) {
        Log.d(TAG, "preLoad: called");
        startTask(() -> {
            preLoadWithNoSubThread(chapterIndex, true);
            requestRefreshPage();
        }, listener);
    }

    protected void preLoadWithNoSubThread(int chapterIndex, boolean needSplit) {
        // 生成待加载章节列表
        ArrayList<Integer> indexList = new ArrayList<>();
        for (int i = chapterIndex - 1; i > 0
                && i >= chapterIndex - preLoadBefore; i--) {
            indexList.add(i);
        }
        for (int i = chapterIndex + 1; i <= getChapterCount()
                && i <= chapterIndex + preLoadBehind; i++) {
            indexList.add(i);
        }
        for (int i : indexList) {
            Chapter chapter = book.getChapter(i);
            switch (chapter.getStatus()) {
                case IS_LOADING:
                case INITIALIZED:
                    break;
                case NO_CONTENT:
                    chapter.setStatus(Chapter.Status.IS_LOADING);
                    bookLoader.loadChapter(chapter);
                    if (!needSplit) {
                        break;
                    }
                case NO_SPLIT:
                    if (chapter.getStatus() == Chapter.Status.NO_CONTENT) {
                        chapter.setStatus(Chapter.Status.IS_LOADING);
                    }
                    Log.d(TAG, "preLoad: load chapter " + i);
                    List<Page> pages = pageConfig.pageFactory.splitPage(chapter.getContent());
                    chapter.setPages(pages);
                    break;
            }
        }
    }

    public interface BookLoader {
        // 该方法需要完成小说目录的加载
        Book getBook();
        // 加载指定章节
        void loadChapter(Chapter chapter);

    }

    public void destroy() {
        for (Task task : taskList) {
            if (task != null) {
                if (task.isLoading()) {
                    task.stop();
                }
                try {
                    task.destroy();
                } catch (DestroyFailedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Book getBook() {
        return book;
    }

    public int getChapterIndex() {
        return chapterIndex;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getChapterCount() {
        return book.getChapterCount();
    }

    /**
     * 对外提供的API接口函数，跳转到指定章节的首页
     * @param chapterIndex 将要跳转的章节序号
     */
    public void jumpToChapter(@IntRange(from = 1) int chapterIndex) {
        this.chapterIndex = chapterIndex;
        this.pageIndex = 1;
        refresh(CUR_PAGE);
        refresh(PRE_PAGE);
        refresh(NEXT_PAGE);
        requestLoadChapter(chapterIndex);
    }

    // 跳转到下一章节
    public void nextChapter() {
        jumpToChapter(chapterIndex + 1);
    }

    public void preChapter() {
        jumpToChapter(chapterIndex - 1);
    }

    // 获取当前章节的分页数量
    public int getPageCount() {
        List<Page> pages = book.getChapter(chapterIndex).getPages();
        if (pages == null) {
            return 1;
        }
        return pages.size();
    }

    public int getPreLoadBefore() {
        return preLoadBefore;
    }

    // 需要在调用openBook()之前设置预加载参数，否则可能会无效
    public void setPreLoadBefore(int preLoadBefore) {
        this.preLoadBefore = preLoadBefore;
    }

    public int getPreLoadBehind() {
        return preLoadBehind;
    }

    public void setPreLoadBehind(int preLoadBehind) {
        this.preLoadBehind = preLoadBehind;
    }

    /**
     * 开启一个子线程任务
     */
    protected void startTask(Runnable task, @Nullable TaskListener listener) {
        Task t = new Task(task);
        taskList.add(t);
        t.start(new TaskListener() {
            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onSuccess();
                }
            }
            @Override
            public void onFailed() {
                if (listener != null) {
                    listener.onFailed();
                }
            }
            @Override
            public void onFinished() {
                if (listener != null) {
                    listener.onFinished();
                }
                taskList.remove(t);
            }
        });
    }

    protected void startTask(Runnable task) {
        startTask(task, null);
    }

}

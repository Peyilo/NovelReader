package org.anvei.novelreader.widget.readview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.IntRange;

import org.anvei.novelreader.widget.readview.bean.Book;
import org.anvei.novelreader.widget.readview.bean.Chapter;
import org.anvei.novelreader.widget.readview.flip.BaseReadView;
import org.anvei.novelreader.widget.readview.flip.PageDirection;
import org.anvei.novelreader.widget.readview.page.IPageFactory;
import org.anvei.novelreader.widget.readview.page.PageData;
import org.anvei.novelreader.widget.readview.page.PageConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReadView extends BaseReadView<ReadPage> {

    private static final String TAG = "ReadView";

    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private final PageConfig pageConfig;
    private Book book;
    private BookLoader bookLoader;

    private int chapterIndex = 1;
    private int pageIndex = 1;
    public static final int THE_LAST = -1;

    private int preLoadBefore = 2;          // 预加载当前章节之前的两章节
    private int preLoadBehind = 2;          // 预加载当前章节之后的一章节

    private OnLoadListener onLoadListener;

    public ReadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        pageConfig = new PageConfig();
        addOnFlipOverListener(new OnFlipListener() {
            @Override
            public void onNext() {
                if (pageIndex == getPageCount()) {
                    onChapterChange(chapterIndex, chapterIndex + 1);
                    chapterIndex++;
                    pageIndex = 1;
                } else {
                    pageIndex++;
                }
                Log.d(TAG, "onNext: curChapter = " + chapterIndex  + ", curPage = " + pageIndex);
            }
            @Override
            public void onPre() {
                if (pageIndex == 1) {
                    onChapterChange(chapterIndex, chapterIndex - 1);
                    chapterIndex--;
                    pageIndex = getPageCount();
                } else {
                    pageIndex--;
                }
                Log.d(TAG, "onNext: curChapter = " + chapterIndex  + ", curPage = " + pageIndex);
            }
        });
    }

    /**
     * ReadPage的初始化器
     */
    public interface PageInitializer {
        // 在这里完成ReadPage的初始化
        void initPage(ReadPage page);

    }

    /**
     * 根据pageInitializer创建一个ReadPage，并完成ReadPage的初始化
     */
    private ReadPage createView(PageInitializer pageInitializer) {
        ReadPage page = null;
        if (pageInitializer != null) {
            page = new ReadPage(getContext());
            pageInitializer.initPage(page);
            page.setPageConfig(pageConfig);
        }
        return page;
    }

    /**
     * 初始化页面，该方法必须被调用，用于初始化页面的contentView、headerView、footerView <br/>
     * 其中， contentView是必须初始化的
     */
    public void setPageInitializer(PageInitializer pageInitializer) {
        for (int i = 0; i < 3; i++) {
            ReadPage childView = createView(pageInitializer);
            addView(childView);
        }
        setCurPagePointer(1);
    }

    /**
     * 判断是否有下一页，只有当前为最后一章节且为最后一页时才会返回false
     */
    @Override
    protected boolean hasNextPage() {
        if (book == null) {             // Book对象为null，即目录未完成加载
            return false;
        }
        Chapter chapter = book.getChapter(chapterIndex);
        if (isLastChapter()) {
            int size = chapter.getStatus() != Chapter.Status.INITIALIZED
                    ? 1 : chapter.getPages().size();
            if (pageIndex == size) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否有上一页，只有当前章节为第一章且第一页时才会返回false
     */
    @Override
    protected boolean hasPrePage() {
        if (book == null) {
            return false;
        }
        if (isFirstChapter() && pageIndex == 1) {
            return false;
        }
        return true;
    }

    @Override
    protected ReadPage getView(ReadPage convertView, PageDirection direction) {
        switch (direction) {
            case TO_NEXT:
                if (hasNextPage()) {        // 页码已经更新了一次，这次是对翻页结束以后是否有下一页的检测
                    PageData nextPageData = getNextPageData();
                    convertView.setPage(nextPageData);
                }
                break;
            case TO_PREV:
                if (hasPrePage()) {
                    PageData pageData = getPrePageData();
                    convertView.setPage(pageData);
                }
                break;
        }
        return convertView;
    }

    /**
     * 切换章节时的回调函数
     * @param oldChapterIndex 旧章节的序号
     * @param newChapterIndex 新章节的序号
     */
    protected void onChapterChange(int oldChapterIndex, int newChapterIndex) {
        Log.d(TAG, "onChapterChange: oldChapterIndex = " + oldChapterIndex +
                ", newChapterIndex = " + newChapterIndex);
        preLoadAndSplitOnSubThread(newChapterIndex);
    }

    /**
     * 当前章节是否是最后一章节
     */
    public boolean isLastChapter() {
        return chapterIndex == getChapterCount();
    }

    /**
     * 当前章节是否是第一章
     */
    public boolean isFirstChapter() {
        return chapterIndex == 1;
    }

    public interface BookLoader {
        Book loadBook();
        void loadChapter(Chapter chapter);
    }

    /**
     * 打开一本小说，注意：该方法只允许调用一次! <br/>
     * openBook()方法内部会在子线程中完成网络请求或者文件读取，初始化Book对象（书籍目录信息） <br/>
     * 并且完成当前章节的分页 <br/>
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
            book = bookLoader.loadBook();           // 先加载完目录信息
            int count = book.getChapterCount();
            if (chapterIndex == THE_LAST) {         // 处理-1，将其设置为最后一章
                this.chapterIndex = count;
            }
            requestPreLoad(this.chapterIndex);  // 加载当前章节的加载以及周围章节的预加载
            post(() -> {
                // 切割章节
                requestPreSplit(this.chapterIndex);
                refreshPages();
                if (onLoadListener != null) {
                    onLoadListener.onLoadFinished(book);
                }
            });
        });
    }

    protected PageData getCurPage() {
        return getPageData(chapterIndex, pageIndex);
    }

    protected PageData getPrePageData() {
        if (pageIndex == 1) {
            if (isFirstChapter()) {
                throw new IllegalStateException("当前没有上一页！");
            } else {
                // 返回上一章的最后一页
                return getPageData(chapterIndex - 1, -1);
            }
        }
        return getPageData(chapterIndex, pageIndex - 1);
    }

    protected PageData getNextPageData() {
        Chapter chapter = book.getChapter(chapterIndex);
        List<PageData> pageData = chapter.getPages();
        assert pageData != null;
        if (pageIndex < pageData.size()) {          // 当前章节内有下一页
            return getPageData(chapterIndex, pageIndex + 1);
        } else {                                    // 当前分页为本章节的最后一页
            if (isLastChapter()) {
                throw new IllegalStateException("当前没有下一页！");
            } else {
                return getPageData(chapterIndex + 1, 1);
            }
        }
    }

    /**
     * 获取指定页面的数据
     * @param chapterIndex 章节序号
     * @param pageIndex 页面序号
     * @return 返回指定页面的PageData对象，如果指定页面还未完成加载，就返回一个正在加载中的页面
     */
    protected PageData getPageData(int chapterIndex, int pageIndex) {
        Chapter chapter = book.getChapter(chapterIndex);
        switch (chapter.getStatus()) {
            case NO_SPLIT:
                requestSplitChapter(chapterIndex);
            case INITIALIZED:
                if (pageIndex == -1) {
                    pageIndex = chapter.getPages().size();
                }
                if (pageIndex > 0) {
                    return chapter.getPages().get(pageIndex - 1);
                }
                break;
        }
        throw new IllegalStateException("未知状态！");
    }

    /**
     * 生成待加载章节的序号列表
     */
    private List<Integer> getPreChapterList(int chapterIndex) {
        ArrayList<Integer> indexList = new ArrayList<>();
        indexList.add(chapterIndex);
        for (int i = chapterIndex - 1; i > 0
                && i >= chapterIndex - preLoadBefore; i--) {
            indexList.add(i);
        }
        for (int i = chapterIndex + 1; i <= getChapterCount()
                && i <= chapterIndex + preLoadBehind; i++) {
            indexList.add(i);
        }
        return indexList;
    }

    protected void preLoadAndSplitOnSubThread(int chapterIndex) {
        Log.d(TAG, "preLoadAndSplitOnSubThread: chapterIndex " + chapterIndex);
        startTask(() -> {
            requestPreLoad(chapterIndex);
            requestPreSplit(chapterIndex);
        });
    }

    /**
     * 请求加载指定章节，并完成章节分页
     */
    protected void requestPreLoad(int chapterIndex) {
        List<Integer> indexList = getPreChapterList(chapterIndex);
        // 遍历待加载章节列表，完成网络加载
        for (int i : indexList) {
            Chapter chapter = book.getChapter(i);
            // 只有当章节未加载的情况下，才会调用loadChapter方法
            if (chapter.getStatus() == Chapter.Status.NO_LOAD) {
                chapter.setStatus(Chapter.Status.IS_LOADING);
                bookLoader.loadChapter(chapter);
                chapter.setStatus(Chapter.Status.NO_SPLIT);
                Log.d(TAG, "requestPreLoad: chapterIndex " + i + " load finished!");
            }
        }
    }

    /**
     * 请求对指定章节进行重新分页（数据层重新分页）
     */
    protected void requestSplitChapter(int chapterIndex) {
        Chapter chapter = book.getChapter(chapterIndex);
        chapter.setPages(pageConfig.getPageFactory().splitPage(chapter));
        chapter.setStatus(Chapter.Status.INITIALIZED);
    }

    /**
     * 请求预切割章节，需要注意： <br/>
     * 1. 所有预切割章节需要已经完成章节内容初始化 <br/>
     * 2. 该方法不会切割已经完成切割的方法 <br/>
     */
    protected void requestPreSplit(int chapterIndex) {
        List<Integer> list = getPreChapterList(chapterIndex);
        for (Integer i : list) {
            Chapter chapter = book.getChapter(i);
            if (chapter.getStatus() == Chapter.Status.NO_SPLIT) {
                chapter.setStatus(Chapter.Status.IS_SPLITTING);
                chapter.setPages(pageConfig.getPageFactory().splitPage(chapter));
                chapter.setStatus(Chapter.Status.INITIALIZED);
                Log.d(TAG, "requestPreSplit chapterIndex " + i + " split finished!");
            }
        }
    }

    /**
     * 刷新当前所有页面
     */
    public void refreshPages() {
        getPageView(0).setPage(getCurPage());
        if (hasNextPage()) {
            PageData nextPageData = getNextPageData();
            getPageView(1).setPage(nextPageData);
        }
        if (hasPrePage()) {
            PageData prePageData = getPrePageData();
            getPageView(-1).setPage(prePageData);
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
     * 对外提供的API接口函数，跳转到指定章节的首页 <br/>
     * @param chapterIndex 将要跳转的章节序号
     */
    public void jumpToChapter(@IntRange(from = 1) int chapterIndex) {
        final int oldChapterIndex = this.chapterIndex;
        final int index = chapterIndex == THE_LAST ? getChapterCount() : chapterIndex;
        if (index == getChapterIndex()) {
            return;
        }
        this.chapterIndex = index;
        this.pageIndex = 1;
        startTask(() -> {
            requestPreLoad(index);           // 完成预加载
            requestPreSplit(index);          // 完成预切割
            refreshPages();                         // 刷新页面
            onChapterChange(oldChapterIndex, chapterIndex);
        });
    }

    /**
     * 跳转到下一章节
     */
    public void nextChapter() {
        jumpToChapter(chapterIndex + 1);
    }

    /**
     * 跳转到上一章节
     */
    public void preChapter() {
        jumpToChapter(chapterIndex - 1);
    }

    // 获取当前章节的分页数量
    public int getPageCount() {
        List<PageData> pageData = book.getChapter(chapterIndex).getPages();
        if (pageData == null) {
            return 1;
        }
        return pageData.size();
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

    protected void startTask(Runnable task) {
        threadPool.submit(task);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        threadPool.shutdown();
    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    public interface OnLoadListener {
        // 该方法将会在加载完Book对象（即目录等相关信息）以后会被调用
        void onLoadFinished(Book book);
    }

    /**
     * 设置pageFactory，用于切割章节、绘制页面
     */
    public void setPageFactory(IPageFactory pageFactory) {
        pageConfig.setPageFactory(pageFactory);
    }

}

package org.anvei.novelreader.widget.read;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import org.anvei.novelreader.bean.Book;
import org.anvei.novelreader.bean.Chapter;
import org.anvei.novelreader.widget.read.interfaces.TaskListener;
import org.anvei.novelreader.widget.read.page.Page;
import org.anvei.novelreader.widget.read.page.PageConfig;
import org.anvei.novelreader.widget.read.page.PageFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.security.auth.DestroyFailedException;

public class ReadView extends BaseReadView<ReadPage> {

    private static final String TAG = "ReadView";
    private PageConfig pageConfig;
    private PageFactory pageFactory;
    private Book book;
    private BookLoader bookLoader;
    private final List<Task> taskList = new ArrayList<>();

    private int chapterIndex = 1;
    private int pageIndex = 1;

    public ReadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addOnFlipOverListener(new OnFlipOverListener() {
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
    }

    @Override
    protected ReadPage createChildView() {
        if (pageConfig == null) {
            pageConfig = new PageConfig();
            pageFactory = new PageFactory(pageConfig);
            pageConfig.pageFactory = pageFactory;
        }
        ReadPage page = new ReadPage(getContext());
        page.setPageConfig(pageConfig);
        return page;
    }

    @Override
    protected boolean hasNextPage() {
        if (isLastChapter()) {
            Chapter chapter = book.getChapter(chapterIndex);
            if (chapter.getPages() == null) {
                return false;
            }
            if (chapter.getPages().size() == pageIndex) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean hasPrePage() {
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
    protected ReadPage getView(ReadPage cacheView, Direction direction) {
        switch (direction) {
            case TO_LEFT:
                if (hasNextPage()) {
                    // 这里返回的nextPage可能为null，是因为这里已经在加载下一章节，但是加载需要时间
                    Page nextPage = getNextPage(new TaskListener() {
                        @Override
                        public void onSuccess() {
                            getView(cacheView, direction);
                        }
                    });
                    cacheView.setPage(nextPage);
                }
                break;
            case TO_RIGHT:
                if (hasPrePage()) {
                    cacheView.setPage(getPrePage(null));
                }
                break;
        }
        return cacheView;
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
        // 先加载完目录信息，再加载当前章节
        Task task = new Task(() -> {
            book = this.bookLoader.getBook();
            int count = book.getChapterCount();
            if (chapterIndex == -1) {
                this.chapterIndex = count;
            }
            Chapter curChapter = book.getChapter(this.chapterIndex);
            bookLoader.loadChapter(curChapter);
        });
        taskList.add(task);
        // 分页
        task.start(new TaskListener() {
            @Override
            public void onSuccess() {
                post(() -> {
                    pageConfig.width = getWidth();
                    pageConfig.height = getHeight();
                    Chapter chapter = book.getChapter(chapterIndex);
                    chapter.setPages(pageFactory.splitPage(chapter.getContent()));
                    curView.setPage(getCurPage(null));
                    if (hasNextPage()) {
                        Page nextPage = getNextPage(new TaskListener() {
                            @Override
                            public void onSuccess() {

                            }
                        });
                        nextView.setPage(nextPage);
                    }
                    if (hasPrePage()) {
                        preView.setPage(getPrePage(null));
                    }
                });
            }
            @Override
            public void onFinished() {
                taskList.remove(task);
            }
        });
    }

    protected Page getCurPage(@Nullable TaskListener taskListener) {
        return getPage(chapterIndex, pageIndex, taskListener);
    }

    protected Page getPrePage(@Nullable TaskListener taskListener) {
        if (pageIndex == 1) {
            if (isFirstChapter()) {
                throw new IllegalStateException("当前没有上一页！");
            } else {
                // 返回上一章的最后一页
                return getPage(chapterIndex - 1, -1, taskListener);
            }
        }
        return getPage(chapterIndex, pageIndex - 1, taskListener);
    }

    protected Page getNextPage(@Nullable TaskListener taskListener) {
        List<Page> pages = book.getChapter(chapterIndex).getPages();
        if (pages == null) {
            throw new IllegalStateException("当前章节还未完成分页！");
        }
        if (pageIndex < Objects.requireNonNull(pages).size()) {
            return getPage(chapterIndex, pageIndex + 1, taskListener);
        } else {
            // 当前分页为本章节的最后一页
            if (isLastChapter()) {
                throw new IllegalStateException("当前没有下一页！");
            } else {
                return getPage(chapterIndex + 1, 1, taskListener);
            }
        }
    }

    /**
     * @param taskListener 只会在调用了requestLoadChapter()方法时才会回调该接口
     */
    protected Page getPage(int chapterIndex, int pageIndex, @Nullable TaskListener taskListener) {
        Log.d(TAG, "getPage: chapterIndex = " + chapterIndex +", pageIndex = " + pageIndex);
        Chapter chapter = book.getChapter(chapterIndex);
        if (chapter.getPages() != null) {
            if (pageIndex == -1) {
                pageIndex = chapter.getPages().size();
            }
            return chapter.getPages().get(pageIndex - 1);
        }
        requestLoadChapter(chapterIndex, taskListener);
        return null;
    }

    /**
     * 请求加载指定章节，并且会完成章节分页处理
     */
    protected void requestLoadChapter(int chapterIndex, @Nullable TaskListener taskListener) {
        Task task = new Task(() -> {
            Chapter chapter = book.getChapter(chapterIndex);
            bookLoader.loadChapter(chapter);
        });
        taskList.add(task);
        task.start(new TaskListener() {
            @Override
            public void onSuccess() {
                post(() -> {
                    pageConfig.width = getWidth();
                    pageConfig.height = getHeight();
                    Chapter chapter = book.getChapter(chapterIndex);
                    chapter.setPages(pageFactory.splitPage(chapter.getContent()));
                    Log.d(TAG, "onSuccess: load chapter " + chapterIndex);

                    if (taskListener != null) {
                        taskListener.onSuccess();
                    }
                });
            }
            @Override
            public void onFinished() {
                taskList.remove(task);
                if (taskListener != null) {
                    taskListener.onFinished();
                }
            }
            @Override
            public void onFailed() {
                if (taskListener != null) {
                    taskListener.onFailed();
                }
            }
        });
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

    // 获取当前章节的分页数量
    public int getPageCount() {
        List<Page> pages = book.getChapter(chapterIndex).getPages();
        if (pages == null) {
            return 1;
        }
        return pages.size();
    }
}

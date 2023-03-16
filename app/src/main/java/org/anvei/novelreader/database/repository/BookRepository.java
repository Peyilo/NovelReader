package org.anvei.novelreader.database.repository;

import androidx.annotation.Nullable;

import org.anvei.novelreader.database.AppDatabase;
import org.anvei.novelreader.database.dao.BookDao;
import org.anvei.novelreader.database.entity.BookItem;

import java.sql.Date;
import java.util.List;

public class BookRepository {

    private static BookDao getDao() {
        return AppDatabase.getInstance().bookDao();
    }

    public static List<BookItem> getAll() {
        return getDao().getAll();
    }

    /**
     * 获取当前书架上的所有书籍
     * TODO: 按照最后阅读时间顺序输出
     */
    public static List<BookItem> getAllBookOnBookshelf() {
        return getDao().getAllBookOnBookshelf();
    }

    public static boolean onBookshelf(int loaderUID, String link) {
        return getDao().queryOnBookshelf(loaderUID, link) != null;
    }

    public static BookItem queryOnBookshelf(int loaderUID, String link) {
        return getDao().queryOnBookshelf(loaderUID, link);
    }

    public static BookItem query(int loaderUID, String link) {
        return getDao().query(loaderUID, link);
    }

    public static void addBookInBookshelf(BookItem bookItem) {
        insertBook(bookItem);
    }

    public static void insertBook(BookItem bookItem) {
        getDao().insertBook(bookItem);
    }

    public static void updateBook(BookItem bookItem) {
        getDao().updateBook(bookItem);
    }

    public static void deleteBook(BookItem bookItem) {
        getDao().deleteBook(bookItem);
    }

    public static void updateAllBook(@Nullable List<BookItem> bookItems) {
        if (bookItems == null)
            return;
        for (BookItem bookItem : bookItems) {
            updateBook(bookItem);
        }
    }

    // 清空书架上的全部书籍
    public static void clearAllBook() {
        List<BookItem> allBook = getAllBookOnBookshelf();
        for (BookItem bookItem : allBook) {
            bookItem.setOnBookshelf(false);
            updateBook(bookItem);
        }
    }

    // 清空阅读历史记录
    public static void clearHistory() {
        List<BookItem> history = getHistory();
        for (BookItem bookItem : history) {
            bookItem.setHasHistory(false);
            updateBook(bookItem);
        }
    }

    // 获取在历史记录表中的全部书籍
    public static List<BookItem> getHistory() {
        return getDao().getHistory();
    }
}

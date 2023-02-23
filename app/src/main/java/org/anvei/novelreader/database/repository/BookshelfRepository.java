package org.anvei.novelreader.database.repository;

import org.anvei.novel.utils.TextUtils;
import org.anvei.novelreader.App;
import org.anvei.novelreader.database.AppDatabase;
import org.anvei.novelreader.database.dao.BookDao;
import org.anvei.novelreader.entity.BookItem;
import org.anvei.novelreader.entity.Bookshelf;
import org.anvei.novelreader.entity.BookshelfConfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class BookshelfRepository {

    private static final File parent =  new File(App.getContext().getFilesDir(), "bookshelf");
    private static final File config = new File(parent, "config.json");
    private static final File list = new File(parent, "list");

    /**
     * 首次调用该函数时，会创建配置文件，并返回null <br/>
     * 否则，将会返回书架列表 <br/>
     */
    public static List<Bookshelf> getAllBookshelf() {
        if (!config.exists()) {
            updateBookshelfConfig(new BookshelfConfig());
            return null;
        }
        try {
            return TextUtils.getGson().fromJson(new BufferedReader(new FileReader(config)),
                    BookshelfConfig.class).bookshelfList;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据bookshelf查询该书架上的书籍
     */
    public static List<BookItem> getBooksByBookshelf(Bookshelf bookshelf) {
        bookshelf.bookList.clear();
        File file = new File(list, bookshelf.name + ".txt");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            if (line != null) {
                BookDao bookDao = AppDatabase.getInstance().bookDao();
                for (String s : line.split(",")) {
                    if (s.length() != 0) {
                        int uid = Integer.parseInt(s);
                        BookItem book = bookDao.getBookByUid(uid);
                        if (book != null) {
                            bookshelf.bookList.add(book);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookshelf.bookList;
    }

    /**
     * 更新书架配置文件的内容
     */
    public static void updateBookshelfConfig(BookshelfConfig bookshelfConfig) {
        String s = TextUtils.toPrettyFormat(bookshelfConfig);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(config));
            writer.write(s);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

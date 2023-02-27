package org.anvei.novelreader.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = BookshelfItem.tableName)
public class BookshelfItem {
    @Ignore
    public static final String tableName = "Bookshelf";
    @PrimaryKey
    public int              uid;                    // 书架uid
    public String           name;                   // 暑假名称
    public int priority;                            // 书架顺序
    @ColumnInfo(name = "book_item_uid_list")
    public List<Integer>    bookItemUidList;        // 书籍uid列表

    @Ignore
    public List<BookItem> bookList = new ArrayList<>();
}

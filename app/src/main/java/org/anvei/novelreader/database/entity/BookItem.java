package org.anvei.novelreader.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.sql.Date;

/**
 * 表示书架上的一本小说
 */
@Entity(tableName = BookItem.tableName)
public class BookItem {
    @Ignore
    public static final String tableName = "Book";
    @PrimaryKey
    public int      uid;                // 小说uid
    @ColumnInfo(name = "loader_uid")
    public int      loaderUid;          // 加载器uid
    public String   title;              // 小说标题
    public String   author;             // 小说作者
    public String   link;               // 链接
    public String   path;               // 本地缓存路径
    @ColumnInfo(name = "last_read_time")
    public Date     lastReadTime;       // 最后阅读时间
    @ColumnInfo(name = "on_bookshelf")
    public boolean  onBookshelf;        // 是否在暑假
    @ColumnInfo(name = "has_history")
    public boolean  hasHistory;         // 是否处于历史记录列表中
}

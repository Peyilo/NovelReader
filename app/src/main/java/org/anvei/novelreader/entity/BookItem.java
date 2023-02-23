package org.anvei.novelreader.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = BookItem.tableName)
public class BookItem {
    public static final String tableName = "Book";
    @PrimaryKey
    public int      uid;
    public String   source;             // 书源标识
    public String   title;
    public String   author;
    public String   link;               // 链接
    public String   path;               // 本地缓存路径
}

package org.anvei.novelreader.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.sql.Date

@Parcelize
@Entity(tableName = BookItem.tableName)
data class BookItem(
    var title: String = "",
    var author: String = "",
    var link: String? = null,
    @PrimaryKey(autoGenerate = true)        var uid: Int = 0,
    @ColumnInfo(name = "loader_uid")        var loaderUID: Int = 0,                 // 加载器UID
    @ColumnInfo(name = "cover_link")        var coverLink: String? = null,          // 封面链接
    @ColumnInfo(name = "cache_dir")         var cacheDir: String? = null,           // 缓存文件夹
    @ColumnInfo(name = "page_index")        var pageIndex: Int = 1,                 // 阅读进度
    @ColumnInfo(name = "chap_index")        var chapIndex: Int = 1,                 // 阅读进度
    @ColumnInfo(name = "first_read_time")   var firstReadTime: Date? = null,        // 第一次阅读时间
    @ColumnInfo(name = "last_read_time")    var lastReadTime: Date? = null,         // 最后一次阅读时间
    @ColumnInfo(name = "add_time")          var addTime: Date? = null,              // 添加到书架的时间
    @ColumnInfo(name = "read_time")         var readTime: Int = 0,                 // 阅读时间
    @ColumnInfo(name = "on_bookshelf")      var onBookshelf: Boolean = false,       // 是否处于书架中
    @ColumnInfo(name = "has_history")       var hasHistory: Boolean = false         // 是否阅读过
): Parcelable {
    companion object {
        const val tableName = "Book"
    }
}

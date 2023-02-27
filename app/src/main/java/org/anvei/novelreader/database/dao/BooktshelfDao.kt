package org.anvei.novelreader.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.anvei.novelreader.database.entity.BookshelfItem

@Dao
interface BookshelfDao {
    @Query("select * from ${BookshelfItem.tableName}")
    fun getAll(): List<BookshelfItem>
}
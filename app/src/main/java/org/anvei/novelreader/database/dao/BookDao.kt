package org.anvei.novelreader.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.anvei.novelreader.entity.BookItem

@Dao
interface BookDao {
    @Query("select * from ${BookItem.tableName}")
    fun getAll(): List<BookItem>

    @Query("select * from ${BookItem.tableName} where uid = :uid")
    fun getBookByUid(uid: Int): BookItem?
}
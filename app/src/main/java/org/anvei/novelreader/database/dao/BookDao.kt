package org.anvei.novelreader.database.dao

import androidx.room.*
import org.anvei.novelreader.database.entity.BookItem

@Dao
interface BookDao {
    @Query("select * from ${BookItem.tableName}")
    fun getAll(): List<BookItem>

    @Query("select * from ${BookItem.tableName} where on_bookshelf = 1")
    fun getAllBookOnBookshelf(): List<BookItem>

    @Query("select * from ${BookItem.tableName} where has_history = 1")
    fun getHistory(): List<BookItem>

    @Query("select * from ${BookItem.tableName} where uid = :uid")
    fun getBookByUid(uid: Int): BookItem?

    @Insert
    fun insertBook(bookItem: BookItem)

    @Update
    fun updateBook(bookItem: BookItem)

    @Delete
    fun deleteBook(bookItem: BookItem)

}
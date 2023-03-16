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

    /**
     * 根据给定的loaderUID、author、title在数据库中查找数据，如果存在该条数据就返回true，反之false
     */
    @Query("select * from ${BookItem.tableName} where loader_uid = :loaderUID " +
            "and link = :link and on_bookshelf = 1")
    fun queryOnBookshelf(loaderUID: Int, link: String): BookItem?

    @Query("select * from ${BookItem.tableName} where loader_uid = :loaderUID and link = :link " +
            "and + (on_bookshelf = 1 or has_history = 1)")
    fun query(loaderUID: Int, link: String): BookItem?

    @Insert
    fun insertBook(bookItem: BookItem)

    @Update
    fun updateBook(bookItem: BookItem)

    @Delete
    fun deleteBook(bookItem: BookItem)

}
package org.anvei.novelreader.file

import org.anvei.novel.utils.SecurityUtils
import org.anvei.novel.utils.TextUtils
import org.anvei.novelreader.App
import org.anvei.novelreader.file.bean.TocBean
import java.io.File

object FileManager {

    private val BOOK_CACHE_DIR = App.getContext().getExternalFilesDir(null)!!      // 书籍缓存的目录
    const val TOC_FILE = "toc.json"

    private fun getNameByBookId(bookId: Int): String {
        return SecurityUtils.getMD5Str(bookId.toString())
    }

    private fun getBookCacheDir(bookId: Int = -1): File {
        if (bookId == -1) {
            return BOOK_CACHE_DIR
        }
        val file = File(BOOK_CACHE_DIR, getNameByBookId(bookId))
        if (!file.exists()) {
            file.mkdir()
        }
        return file
    }

    fun readTocFile(bookId: Int): TocBean? {
        val cacheDir = getBookCacheDir(bookId)
        val tocFile = File(cacheDir, TOC_FILE)
        if (!tocFile.exists())
            return null
        val json = FileUtils.readStringFromFile(tocFile)
        return TextUtils.getGson().fromJson(json, TocBean::class.java)
    }

    fun writeTocFile(tocBean: TocBean) {
        val cacheDir = getBookCacheDir(tocBean.bookId)
        val tocFile = File(cacheDir, TOC_FILE)
        if (!tocFile.exists()) {
            tocFile.createNewFile()
        }
        val json = TextUtils.toPrettyFormat(tocBean)
        FileUtils.writeString(tocFile, json)
    }

}
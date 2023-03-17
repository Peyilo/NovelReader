package org.anvei.novelreader.file

import org.anvei.novel.utils.SecurityUtils
import org.anvei.novel.utils.TextUtils
import org.anvei.novelreader.App
import org.anvei.novelreader.file.bean.TocBean
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

object NovelCacheManager {

    private const val BOOK_CACHE_DIR = "bookCache"
    private const val CONTENT_DIR = "content"
    private const val TOC_FILE = "toc.json"
    private const val COVER_FILE = "cover.jpg"

    private val bookCacheDir by lazy {
        File(App.getContext().getExternalFilesDir(null)!!, BOOK_CACHE_DIR).apply {
            if (!this.exists()) {
                this.mkdirs()
            } // 书籍缓存的目录
        }
    }

    private fun getDirNameForBookId(bookId: Int): String {
        return SecurityUtils.getMD5Str(bookId.toString())
    }

    private fun getFileNameForChapLink(link: String): String {
        return SecurityUtils.getMD5Str(link)
    }

    private fun getBookCacheDir(bookId: Int = -1): File {
        if (bookId == -1) {
            return bookCacheDir
        }
        val dir = File(bookCacheDir, getDirNameForBookId(bookId))
        if (!dir.exists()) {
            dir.mkdir()
        }
        return dir
    }

    private fun getContentCacheDir(bookId: Int): File {
        val cacheDir = getBookCacheDir(bookId)
        val dir = File(cacheDir, CONTENT_DIR)
        if (!dir.exists()) {
            dir.mkdir()
        }
        return dir
    }

    fun tocFileExist(bookId: Int): Boolean {
        val cacheDir = getBookCacheDir(bookId)
        val tocFile = File(cacheDir, TOC_FILE)
        return tocFile.exists()
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

    fun contentFileExist(bookId: Int, link: String): Boolean {
        val contentCacheDir = getContentCacheDir(bookId)
        val file = File(contentCacheDir, getFileNameForChapLink(link) + ".txt")
        return file.exists()
    }

    /**
     * 根据指定的书籍id和link值，读取本地缓存的章节内容
     */
    fun readContent(bookId: Int, link: String): String? {
        val contentCacheDir = getContentCacheDir(bookId)
        val file = File(contentCacheDir, getFileNameForChapLink(link) + ".txt")
        if (!file.exists()) return null
        return FileUtils.readStringFromFile(file)
    }

    fun writeContent(bookId: Int, link: String, content: String) {
        val contentCacheDir = getContentCacheDir(bookId)
        val file = File(contentCacheDir, getFileNameForChapLink(link) + ".txt")
        if (!file.exists()) {
            file.createNewFile()
        }
        FileUtils.writeString(file, content)
    }

    /**
     * 读取指定id的封面缓存
     */
    fun getCoverCache(bookId: Int): InputStream? {
        val bookCacheDir = getBookCacheDir(bookId)
        val file = File(bookCacheDir, COVER_FILE)
        if (!file.exists()) return null
        return FileInputStream(file)
    }

    fun saveCover(bookId: Int, inputStream: InputStream) {
        val bookCacheDir = getBookCacheDir(bookId)
        val file = File(bookCacheDir, COVER_FILE)
        if (!file.exists()) {
            file.createNewFile()
        }
        FileUtils.writeStream(
            FileOutputStream(file),
            inputStream
        )
    }

}
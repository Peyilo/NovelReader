package org.anvei.novelreader.loader

import android.util.Log
import org.anvei.novel.api.SfacgAPI
import org.anvei.novelreader.database.repository.BookRepository
import org.anvei.novelreader.file.NovelCacheManager
import org.anvei.novelreader.loader.bean.SearchResultItem
import org.klee.readview.entities.BookData
import org.klee.readview.entities.ChapData

private const val TAG = "SfacgLoader"
class SfacgLoader(novelId: Long = 0) : SearchableLoader(
    LoaderRepository.SfacgLoaderUID, "SfacgAPP"
) {

    init {
        o = novelId
    }

    private val api = SfacgAPI()

    /**
     * 变量link优先于o变量
     * @return 根据当前link、o的值返回一个Long类型的小说id
     */
    private fun getNovelId(): Long {
        return if (link != null) {
            link!!.toLong()
        } else if (o != null) {
            o!! as Long
        } else {
            throw IllegalStateException("当前link、o都为null！")
        }
    }

    override fun search(keyword: String): List<SearchResultItem> {
        val searchResultJson = api.search(keyword)
        val list = ArrayList<SearchResultItem>()
        searchResultJson.data.novels.forEach {
            list.add(
                SearchResultItem(loaderUID).apply {
                    title = it.novelName
                    author = it.authorName
                    link = it.novelId.toString()
                    coverUrl = it.novelCover
                    intro = it.expand.intro
                    charCount = it.charCount
                }
            )
        }
        return list
    }

    override fun requestToc(): BookData {
        val book = BookData()
        val novelId = getNovelId()
        Log.d(TAG, "initToc: $novelId")
        val novelHomeJson = api.getNovelHomeJson(novelId)
        book.apply {                                // 加载小说基本信息
            o = novelHomeJson.data.novelId
            name = novelHomeJson.data.novelName
            author = novelHomeJson.data.authorName
        }
        val chapListJson = api.getChapListJson(novelId)
        var chapIndex = 1
        chapListJson.data.volumeList.forEach { volume ->        // 加载目录信息
            volume.chapterList.forEach { chap ->
                val chapData = ChapData(chapIndex, chap.title)
                chapData.o = chap.chapId
                book.addChapter(chapData)
                chapIndex++
            }
        }
        chapListJson.data
        Log.d(TAG, "initToc: ${book.chapCount}")
        return book
    }

    override fun requestChap(chapData: ChapData) {
        val chapIndex = chapData.o as Int
        val chapContentJson = api.getChapContentJson(chapIndex.toLong())
        chapData.content = chapContentJson.data.expand.content
    }

    override fun hasTocCache(): Boolean {
        val item = BookRepository.queryOnBookshelf(loaderUID, getNovelId().toString()) ?: return false
        return NovelCacheManager.tocFileExist(item.uid)
    }

    override fun requestTocFromCache(): BookData {
        val item = BookRepository.queryOnBookshelf(loaderUID, getNovelId().toString())!!
        val tocCache = NovelCacheManager.readTocFile(item.uid)!!
        val bookData = BookData()
        tocCache.chapList.forEach {
            bookData.addChapter(
                ChapData(it.chapIndex, it.title).apply {
                    o = it.link.toInt()
                }
            )
        }
        return bookData
    }

    override fun hasChapCache(chapData: ChapData): Boolean {
        val item = BookRepository.queryOnBookshelf(loaderUID, getNovelId().toString()) ?: return false
        return NovelCacheManager.contentFileExist(item.uid, chapData.o.toString())
    }

    override fun requestChapFromCache(chapData: ChapData) {
        val item = BookRepository.queryOnBookshelf(loaderUID, getNovelId().toString())!!
        chapData.content = NovelCacheManager.readContent(item.uid, chapData.o.toString())
        Log.d(TAG, "requestChapFromCache: ${chapData.title}")
    }
}
package org.anvei.novelreader.loader

import org.anvei.novel.api.SfacgAPI
import org.anvei.novelreader.loader.bean.SearchResultItem
import org.klee.readview.entities.BookData
import org.klee.readview.entities.ChapData

class SfacgLoader(novelId: Long = 0) : AbsBookLoader(
    LoaderConstant.SfacgLoaderUID, "SfacgAPP"
) {

    init {
        o = novelId
    }

    private val api = SfacgAPI()

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
        TODO("Not yet implemented")
    }

    override fun loadBook(): BookData {
        val book = BookData()
        val novelId = getNovelId()
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
        return book
    }

    override fun loadChapter(chapData: ChapData) {
        val chapIndex = chapData.o as Int
        val chapContentJson = api.getChapContentJson(chapIndex.toLong())
        chapData.content = chapContentJson.data.expand.content
    }

}
package org.anvei.novelreader.loader

import org.anvei.novel.api.SfacgAPI
import org.anvei.novelreader.loader.bean.SearchResultItem
import org.klee.readview.entities.BookData
import org.klee.readview.entities.ChapData

open class SfacgLoader(novelId: Long = 0) : AbsBookLoader(
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
        TODO("Not yet implemented")
    }

    override fun initToc(): BookData {
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
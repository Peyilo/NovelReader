package org.anvei.novelreader.loader

import org.klee.readview.entities.BookData
import org.klee.readview.entities.ChapData
import org.klee.readview.loader.BookLoader

abstract class BaseBookLoader(
    val loaderUID: Int,           // 加载器唯一UID标识
    var name: String        // 加载器名称
) : BookLoader{

    var link: String? = null
    var o: Any? = null

    override fun initToc(): BookData {
        return if (hasTocCache()) {
            requestTocFromCache()!!
        } else {
            requestToc()
        }
    }

    /**
     * 是否有章节目录缓存
     */
    open fun hasTocCache() = false

    /**
     * 是否有章节内容缓存
     */
    open fun hasChapCache(chapData: ChapData) = false

    abstract fun requestToc(): BookData

    open fun requestTocFromCache(): BookData? = null

    open fun requestChap(chapData: ChapData) = Unit

    open fun requestChapFromCache(chapData: ChapData) = Unit

    override fun loadChapter(chapData: ChapData) {
        if (hasChapCache(chapData)) {
            requestChapFromCache(chapData)
        } else {
            requestChap(chapData)
        }
    }
}
package org.anvei.novelreader.loader

import org.anvei.novelreader.loader.bean.SearchResultItem
import org.klee.readview.loader.BookLoader

abstract class AbsBookLoader(
    val uid: Int,           // 加载器唯一UID标识
    var name: String        // 加载器名称
) : BookLoader{

    var link: String? = null
    var o: Any? = null

    /**
     * 小说加载器的搜索功能
     */
    abstract fun search(keyword: String): List<SearchResultItem>

    fun loadBook(link: String) {
        this.link = link
        loadBook()
    }

}
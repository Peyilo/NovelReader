package org.anvei.novelreader.loader

import org.anvei.novelreader.loader.bean.SearchResultItem

interface Searchable {

    /**
     * 小说加载器的搜索功能
     * @param keyword
     * @return 搜索结果
     */
    fun search(keyword: String): List<SearchResultItem>

}
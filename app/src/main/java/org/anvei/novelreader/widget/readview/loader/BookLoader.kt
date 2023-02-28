package org.anvei.novelreader.widget.readview.loader

import org.anvei.novelreader.ui.search.bean.SearchResultItem
import org.anvei.novelreader.widget.readview.ReadView

interface BookLoader : ReadView.BookLoader {

    // 搜索
    fun search(keyword: String): List<SearchResultItem>

}
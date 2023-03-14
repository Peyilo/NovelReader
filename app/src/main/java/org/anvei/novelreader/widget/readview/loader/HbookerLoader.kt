package org.anvei.novelreader.widget.readview.loader

import org.anvei.novel.api.HbookerAPI
import org.anvei.novelreader.loader.bean.SearchResultItem
import org.anvei.novelreader.widget.readview.bean.Book
import org.anvei.novelreader.widget.readview.bean.Chapter

class HbookerLoader : AbsBookLoader("Hbooker", LoaderFactory.HbookerLoaderUID) {
    private val api = HbookerAPI("书客831585069584", "84550c4165e022cafa1bb91bef6d9382")

    override fun loadBook(): Book {
        TODO("Not yet implemented")
    }

    override fun loadChapter(chapter: Chapter?) {
        TODO("Not yet implemented")
    }

    override fun search(keyword: String): List<SearchResultItem> {
        TODO("Not yet implemented")
    }
}
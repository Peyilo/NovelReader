package org.anvei.novelreader.file.bean

data class TocBean(
    var title: String = "",
    var author: String = "",
    var loaderUID: Int = 0,
    var bookId: Int = 0,
    var link: String = "",
    var chapList: MutableList<ChapBean> = ArrayList()
)

data class ChapBean (
    var title: String = "",
    var chapIndex: Int = 1,
    var link: String = "",
    var cache: String? = null
)

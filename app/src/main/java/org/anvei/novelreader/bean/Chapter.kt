package org.anvei.novelreader.bean

import org.anvei.novelreader.widget.read.page.Page

// 普通章节
open class Chapter(val index: Int, val title: String) {
    var what = ""
    var content = ""
    var pages: List<Page>? = null
}

package org.anvei.novelreader.ui.search.bean

// 表示一个搜索结果
class SearchResultItem(val loaderUID: Int) {
    lateinit var url: String
    var coverUrl: String? = null        // 封面url
    lateinit var title: String           // 小说标题
    lateinit var author: String         // 小说作者
    var labels: MutableList<String> = ArrayList()           // 小说标签
    var charCount: Int? = null           // 小说字数
    var intro: String? = null           // 小说简介
}
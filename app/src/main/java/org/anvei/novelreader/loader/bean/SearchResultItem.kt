package org.anvei.novelreader.loader.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// 表示一个搜索结果
@SuppressLint("ParcelCreator")
@Parcelize
data class SearchResultItem(
    val loaderUID: Int,
    var title: String = "",              // 小说标题
    var author: String = "",             // 小说作者
    var link: String = "",
    var coverUrl: String? = null,        // 封面url
    var labels: MutableList<String> = ArrayList(),           // 小说标签
    var charCount: Int = 0,              // 小说字数
    var intro: String? = null            // 小说简介
): Parcelable
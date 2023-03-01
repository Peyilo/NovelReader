package org.anvei.novelreader.widget.readview.page

import android.graphics.Bitmap
import org.anvei.novelreader.widget.readview.bean.Chapter

interface IPageFactory {

    fun splitPage(chapter: Chapter, chapterIndex: Int): List<PageData> {
        return splitPage(chapter, chapterIndex, "本章节为空")
    }

    /**
     * 将章节切割成页面
     */
    fun splitPage(chapter: Chapter, chapterIndex: Int, replace: String): List<PageData>

    /**
     * 根据给定的Page绘制一个bitmap，用着页面的显示
     */
    fun createPage(pageData: PageData): Bitmap

}
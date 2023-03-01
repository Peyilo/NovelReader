package org.anvei.novelreader.widget.readview.page

import android.graphics.Bitmap
import android.graphics.Canvas
import android.text.TextUtils
import org.anvei.novelreader.widget.readview.bean.Chapter

class PageFactory(private val pageConfig: PageConfig) : IPageFactory {

    override fun splitPage(chapter: Chapter, replace: String): List<PageData> {
        var content = chapter.content
        // 如果章节内容为空，就用replace作为替代字符串进行切割显示
        if (TextUtils.isEmpty(content)) {
            content = replace
        }
        // 最终返回的页面数据
        val list: MutableList<PageData> = ArrayList()
        // 先将章节内容切割成段落
        val paras = content.split("\n")
        // 页面的宽高参数
        val width = pageConfig.contentWidth - pageConfig.contentPaddingLeft -
                pageConfig.contentPaddingRight
        val height = pageConfig.contentHeight - pageConfig.contentPaddingTop -
                pageConfig.contentPaddingBottom
        // 第一次需要减去章节标题的空间
        var remainedHeight = height - pageConfig.getTitleSize() - pageConfig.titleMargin
        var remainedWidth = width                       // 剩余的高度和宽度
        val textSize = pageConfig.getTextSize()         // 字符大小
        val textPaint = pageConfig.textPaint            // 绘制章节内容的Paint
        var pageData = PageData()
        var line = Line()
        var isFirst = true
        var dimen: Float
        for (s in paras) {
            // 去除空行
            val para = s.trimEnd()
            if (para.isEmpty()) {
                continue
            }
            // 遍历段落的字符
            for (i in para.indices) {
                val c = para[i]
                if (c == '\r') {
                    continue
                }
                if (remainedWidth < textPaint.measureText(c.toString()).also { dimen = it }) {
                    // 剩余的宽度已经不足以再填充当前字符，所以需要重新new一个Line
                    pageData.add(line)
                    line = Line()
                    remainedWidth = width
                    remainedHeight -= textSize + pageConfig.lineMargin
                    if (remainedHeight < textSize) {
                        // 处理章节首页情形
                        if (isFirst) {
                            pageData.setIsFirstPage(true)
                            pageData.title = chapter.title
                            isFirst = false
                        }
                        list.add(pageData)
                        pageData =
                            PageData()
                        remainedHeight = height
                    }
                }
                line.add(c)
                // 段落的最后一个字符
                if (i == para.length - 1) {
                    line.setIsParaEndLine(true)
                    pageData.add(line)
                    line = Line()
                    remainedWidth = width
                    remainedHeight -= textSize + pageConfig.lineMargin + pageConfig.paraMargin
                    if (remainedHeight < textSize) {
                        if (isFirst) {
                            pageData.setIsFirstPage(true)
                            pageData.title = chapter.title
                            isFirst = false
                        }
                        list.add(pageData)
                        pageData =
                            PageData()
                        remainedHeight = height
                    }
                    break
                }
                remainedWidth -= (dimen + pageConfig.textMargin).toInt()
            }
        }
        if (list.size == 0) {
            if (isFirst) {
                pageData.setIsFirstPage(true)
                pageData.title = chapter.title
            }
            list.add(pageData)
        }
        if (pageData.size() != 0) {
            list.add(pageData)
        }
        return list
    }

    private fun drawPage(pageData: PageData, canvas: Canvas) {
        val textPaint = pageConfig.textPaint
        val lineHeight = pageConfig.getTextSize() + pageConfig.lineMargin
        var base = pageConfig.contentPaddingTop
        var left: Float
        // 绘制标题
        if (pageData.isFirstPage) {
            base += pageConfig.getTitleSize()
            left = pageConfig.contentPaddingLeft
            canvas.drawText(pageData.title, left, base, pageConfig.titlePaint)
            base += pageConfig.titleMargin
        }
        base += pageConfig.getTextSize()
        for (i in 0 until pageData.size()) {
            val line = pageData[i]
            left = pageConfig.contentPaddingLeft
            for (j in 0 until line.size()) {
                val c = line[j]
                canvas.drawText(c.toString(), left, base, textPaint)
                left += (textPaint.measureText(c.toString()) + pageConfig.textMargin).toInt()
            }
            if (line.isParaEndLine) {
                base += pageConfig.paraMargin
            }
            base += lineHeight
        }
    }

    override fun createPage(pageData: PageData): Bitmap {
        // 在背景上绘制文字
        val res = pageConfig.getBackground().copy(Bitmap.Config.RGB_565, true)
        val canvas = Canvas(res)
        drawPage(pageData, canvas)
        return res
    }
}
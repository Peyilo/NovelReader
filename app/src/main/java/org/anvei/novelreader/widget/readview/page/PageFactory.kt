package org.anvei.novelreader.widget.readview.page

import android.graphics.Bitmap
import android.graphics.Canvas
import android.text.TextUtils
import org.anvei.novelreader.widget.readview.bean.Chapter

class PageFactory(private val pageConfig: PageConfig) : IPageFactory {

    override fun splitPage(chapter: Chapter, replace: String): List<Page> {
        var content = chapter.content
        // 如果章节内容为空，就用replace作为替代字符串进行切割显示
        if (TextUtils.isEmpty(content)) {
            content = replace
        }
        // 最终返回的页面数据
        val pages: MutableList<Page> = ArrayList()
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
        var page = Page()
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
                    page.add(line)
                    line = Line()
                    remainedWidth = width
                    remainedHeight -= textSize + pageConfig.lineMargin
                    if (remainedHeight < textSize) {
                        // 处理章节首页情形
                        if (isFirst) {
                            page.setIsFirstPage(true)
                            page.title = chapter.title
                            isFirst = false
                        }
                        pages.add(page)
                        page = Page()
                        remainedHeight = height
                    }
                }
                line.add(c)
                // 段落的最后一个字符
                if (i == para.length - 1) {
                    line.setIsParaEndLine(true)
                    page.add(line)
                    line = Line()
                    remainedWidth = width
                    remainedHeight -= textSize + pageConfig.lineMargin + pageConfig.paraMargin
                    if (remainedHeight < textSize) {
                        if (isFirst) {
                            page.setIsFirstPage(true)
                            page.title = chapter.title
                            isFirst = false
                        }
                        pages.add(page)
                        page = Page()
                        remainedHeight = height
                    }
                    break
                }
                remainedWidth -= (dimen + pageConfig.textMargin).toInt()
            }
        }
        if (pages.size == 0) {
            if (isFirst) {
                page.setIsFirstPage(true)
                page.title = chapter.title
            }
            pages.add(page)
        }
        if (page.size() != 0) {
            pages.add(page)
        }
        return pages
    }

    private fun drawPage(page: Page, canvas: Canvas) {
        val textPaint = pageConfig.textPaint
        val lineHeight = pageConfig.getTextSize() + pageConfig.lineMargin
        var base = pageConfig.contentPaddingTop
        var left: Float
        // 绘制标题
        if (page.isFirstPage) {
            base += pageConfig.getTitleSize()
            left = pageConfig.contentPaddingLeft
            canvas.drawText(page.title, left, base, pageConfig.titlePaint)
            base += pageConfig.titleMargin
        }
        base += pageConfig.getTextSize()
        for (i in 0 until page.size()) {
            val line = page[i]
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

    override fun createPage(page: Page): Bitmap {
        // 在背景上绘制文字
        val res = pageConfig.getBackground().copy(Bitmap.Config.RGB_565, true)
        val canvas = Canvas(res)
        drawPage(page, canvas)
        return res
    }
}
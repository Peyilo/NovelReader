package org.anvei.novelreader.widget.readview.page

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface

class PageConfig {
    var bitmapPaint = Paint()
    // 页面的宽高
    var pageWidth = 0
    var pageHeight = 0
    // contentView的宽高
    var contentWidth = 0
    var contentHeight = 0
    // contentView的文字Paint
    val textPaint: Paint = Paint().apply {
        color = Color.parseColor("#2B2B2B")
        textSize = 54F
    }
    val titlePaint: Paint = Paint().apply {
        color = Color.BLACK
        textSize = 72F
        typeface = Typeface.DEFAULT_BOLD
    }
    // contentView内部尺寸参数
    var titleMargin = 150
    var textMargin = 0          // 字符间隔
    var lineMargin = 25         // 行间隔
    var paraMargin = 35         // 段落的额外间隔

    var bgColor = Color.WHITE
    private var background: Bitmap? = null
    private var pageFactory: IPageFactory? = null

    fun getPageFactory(): IPageFactory {
        if (pageFactory == null) {
            pageFactory = PageFactory(this)
        }
        return pageFactory!!
    }
    fun setPageFactory(pageFactory: IPageFactory) {
        this.pageFactory = pageFactory
    }
    // 设置&获取章节内容文字大小
    fun getTextSize() = textPaint.textSize.toInt()
    fun setTextSize(textSize: Int) {
        textPaint.textSize = textSize.toFloat()
    }
    // 设置&获取章节内容文字颜色
    fun getTextColor(): Int = textPaint.color
    fun setTextColor(textColor: Int) {
        textPaint.color = textColor
    }
    // 设置&获取章节标题文字大小
    fun getTitleSize() = titlePaint.textSize.toInt()
    fun setTitleSize(titleSize: Int) {
        titlePaint.textSize = titleSize.toFloat()
    }
    // 设置&获取章节标题文字颜色
    fun getTitleColor(): Int = titlePaint.color
    fun setTitleColor(titleColor: Int) {
        textPaint.color = titleColor
    }
    fun getBackground(): Bitmap {
        if (background == null) {
            background = Bitmap.createBitmap(contentWidth, contentHeight, Bitmap.Config.RGB_565)
            background!!.eraseColor(bgColor)
        }
        return background!!
    }

    fun setBackground(background: Bitmap) {
        this.background = background
    }


}
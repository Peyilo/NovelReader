package org.anvei.novelreader.widget.readview.page

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface

class PageConfig {
    var bitmapPaint = Paint()
    // contentView的宽高
    private var contentDimenInitialized = false
    var contentWidth = 0
        private set
    var contentHeight = 0
        private set
    // contentView的内边距
    var contentPaddingBottom = 0F
    var contentPaddingTop = 0F
    var contentPaddingLeft = 0F
    var contentPaddingRight = 0F
    // contentView的文字Paint
    val textPaint: Paint = Paint()
    val titlePaint: Paint = Paint().apply {
        typeface = Typeface.DEFAULT_BOLD
    }
    // contentView内部尺寸参数
    var titleMargin = 0F
    var textMargin = 0F          // 字符间隔
    var lineMargin = 0F         // 行间隔
    var paraMargin = 0F         // 段落的额外间隔

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
    fun setTextSize(textSize: Float) {
        textPaint.textSize = textSize
    }
    // 设置&获取章节内容文字颜色
    fun getTextColor(): Int = textPaint.color
    fun setTextColor(textColor: Int) {
        textPaint.color = textColor
    }
    // 设置&获取章节标题文字大小
    fun getTitleSize() = titlePaint.textSize.toInt()
    fun setTitleSize(titleSize: Float) {
        titlePaint.textSize = titleSize
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

    fun initContentDimen(width: Int, height: Int) {
        if (!contentDimenInitialized) {
            contentDimenInitialized = true
            this.contentWidth = width
            this.contentHeight = height
        }
    }

}
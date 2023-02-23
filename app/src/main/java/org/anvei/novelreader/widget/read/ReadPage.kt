package org.anvei.novelreader.widget.read

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.anvei.novelreader.R
import org.anvei.novelreader.widget.read.page.Page

class ReadPage(context: Context, attributeSet: AttributeSet?) : ViewGroup(context, attributeSet) {

    var headerView: View? = null            // 页眉
    var footerView: View? = null            // 页脚
    var titleView: View? = null             // 章节标题视图
    lateinit var contentView: ContentView           // 章节内容视图

    lateinit var view: View

    var title: String = ""                  // 章节标题
    var content: Page? = null               // 章节内容

    constructor(context: Context) : this(context, null)

    init {
        attributeSet?.let {
            val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ReadPage)
            val id = typedArray.getResourceId(R.styleable.ReadPage_layout_child, -1)
            if (id != -1) {
                throw IllegalStateException("child属性是必须的！")
            }
            setView(id)
            typedArray.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (i in 0 until childCount) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            getChildAt(i).apply {
                layout(0, 0, this.measuredWidth, this.measuredHeight)
            }
        }
    }

    fun showTitleView(show: Boolean) {
        titleView?.visibility = if (show) VISIBLE else GONE
    }

    fun setView(view: Int) {
        this.view = LayoutInflater.from(context).inflate(view, this)
    }

    fun getTitleHeight(): Int {
        if (titleView != null) {
            return titleView!!.height
        }
        return 0;
    }
}
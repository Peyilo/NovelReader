package org.anvei.novelreader.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.anvei.novelreader.R
import org.anvei.novelreader.activity.ReadActivity
import org.anvei.novelreader.ui.search.bean.SearchResultItem

/**
 * 搜索结果列表的适配器
 */
class ResultAdapter(val context: Context) : RecyclerView.Adapter<ResultAdapter.Holder>() {

    var resultList: MutableList<SearchResultItem> = ArrayList()

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.result_item_cover)
        val title: TextView = view.findViewById(R.id.result_item_title)
        val author: TextView = view.findViewById(R.id.result_item_author)
        val label: TextView = view.findViewById(R.id.result_item_label)
        val charCount: TextView = view.findViewById(R.id.result_item_char_count)
        val intro: TextView = view.findViewById(R.id.result_item_intro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_result, parent, false)
        return Holder((view))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val result = resultList[position]
        result.apply {
            coverUrl?.apply {
                Glide.with(context).load(this).into(holder.cover)
            }
            holder.title.text = title
            holder.author.text = author
            intro?.apply {
                holder.intro.text = this
            }
            var l = ""
            for (label in labels) {
                l = "$l · $label"
            }
            holder.label.text = l
            charCount?.apply {
                val s: String = if (this > 10000) " · ${this / 10000}万字" else " · ${this}字"
                holder.charCount.text = s
            }
            intro?.apply {
                holder.intro.text = this
            }
        }
        holder.view.setOnClickListener {
            // 启动小说主页页面
            ReadActivity.start(context, result.loaderUID, result.url)
        }
    }

    override fun getItemCount(): Int = resultList.size
}
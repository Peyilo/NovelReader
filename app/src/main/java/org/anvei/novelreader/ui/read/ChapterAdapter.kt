package org.anvei.novelreader.ui.read

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.anvei.novelreader.R
import org.anvei.novelreader.widget.readview.bean.Chapter

class ChapterAdapter(private val chapterList: List<Chapter>) : RecyclerView.Adapter<ChapterAdapter.Holder>() {
    var onItemClickListener: OnItemClickListener? = null

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val chapterTitle: TextView = view.findViewById(R.id.chapter_list_item_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chapter_list_item,
            parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val chapter = chapterList[position]
        holder.chapterTitle.text = chapter.title
        holder.chapterTitle.setOnClickListener {
            // 章节跳转
            onItemClickListener?.onClick(position + 1)
        }
    }

    override fun getItemCount(): Int = chapterList.size

    interface OnItemClickListener {
        fun onClick(chapterIndex: Int)
    }
}
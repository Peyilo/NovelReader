package org.anvei.novelreader.ui.read

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.anvei.novelreader.R
import org.klee.readview.entities.BookData

class ChapterAdapter(private val book: BookData) : RecyclerView.Adapter<ChapterAdapter.Holder>() {
    private var onItemClickListener: OnItemClickListener? = null

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val chapterTitle: TextView = view.findViewById(R.id.chapter_list_item_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chapter_list_item,
            parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val chapter = book.getChapter(position + 1)
        holder.chapterTitle.text = chapter.title
        holder.chapterTitle.setOnClickListener {
            // 章节跳转
            onItemClickListener?.onClick(position + 1)
        }
    }

    override fun getItemCount(): Int = book.chapCount

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    fun setOnItemClickListener(listener: (chapIndex: Int) -> Unit) {
        setOnItemClickListener(object : OnItemClickListener {
            override fun onClick(chapterIndex: Int) {
                listener(chapterIndex)
            }
        })
    }

    interface OnItemClickListener {
        fun onClick(chapterIndex: Int)
    }
}
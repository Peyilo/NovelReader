package org.anvei.novelreader.ui.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.anvei.novelreader.R
import org.anvei.novelreader.database.entity.BookItem
import org.anvei.novelreader.ui.read.ReadActivity

class BookItemAdapter(var list: MutableList<BookItem>) : RecyclerView.Adapter<BookItemAdapter.Holder>() {

    private var context: Context? = null

    inner class Holder(val view: View): RecyclerView.ViewHolder(view) {
        val uid: TextView = view.findViewById(R.id.data_uid)
        val title: TextView = view.findViewById(R.id.data_title)
        val author: TextView = view.findViewById(R.id.data_author)
        val loaderUID: TextView = view.findViewById(R.id.data_loader_uid)
        val link: TextView = view.findViewById(R.id.data_link)
        val coverLink: TextView = view.findViewById(R.id.data_cover_link)
        val cacheDir: TextView = view.findViewById(R.id.data_cache_dir)
        val pageIndex: TextView = view.findViewById(R.id.data_page_index)
        val chapIndex: TextView = view.findViewById(R.id.data_chap_index)
        val firstReadTime: TextView = view.findViewById(R.id.data_first_read_time)
        val lastReadTime: TextView = view.findViewById(R.id.data_last_read_time)
        val readTime: TextView = view.findViewById(R.id.data_read_time)
        val addTime: TextView = view.findViewById(R.id.data_add_time)
        val onBookshelf: TextView = view.findViewById(R.id.data_on_bookshelf)
        val hasHistory: TextView = view.findViewById(R.id.data_has_history)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        if (context == null) {
            context = parent.context
        }
        val view = LayoutInflater.from(context).inflate(R.layout.item_data_book_item,
            parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val bookItem = list[position]
        bookItem.apply {
            holder.uid.text = "uid: $uid"
            holder.title.text = "title: $title"
            holder.author.text = "author: $author"
            holder.loaderUID.text = "loaderUID: $loaderUID"
            holder.link.text = "link: $link"
            holder.coverLink.text = "coverLink: $coverLink"
            holder.cacheDir.text = "cacheDir: $cacheDir"
            holder.pageIndex.text = "pageIndex: $pageIndex"
            holder.chapIndex.text = "chapIndex: $chapIndex"
            holder.firstReadTime.text = "firstReadTime: $firstReadTime"
            holder.lastReadTime.text = "lastReadTime: $lastReadTime"
            holder.readTime.text = "readTime: ${readTime}s"
            holder.addTime.text = "addTime: $addTime"
            holder.onBookshelf.text = "onBookshelf: $onBookshelf"
            holder.hasHistory.text = "hasHistory: $hasHistory"
        }
        holder.view.setOnClickListener {
            ReadActivity.start(context!!, bookItem.loaderUID, bookItem.link!!)
        }
    }

    override fun getItemCount(): Int = list.size
}
package org.anvei.novelreader.ui.bookshelf

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.anvei.novelreader.R
import org.anvei.novelreader.activity.ReadActivity
import org.anvei.novelreader.database.entity.BookItem

class BookshelfAdapter(val context: Context,var books: MutableList<BookItem>? = null)
    : RecyclerView.Adapter<BookshelfAdapter.Holder>() {

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.bookshelf_item_cover)
        val title: TextView = view.findViewById(R.id.bookshelf_item_title)
        val author: TextView = view.findViewById(R.id.bookshelf_item_author)
        val readProcess: TextView = view.findViewById(R.id.bookshelf_item_read_process)
        val lastChapter: TextView = view.findViewById(R.id.bookshelf_item_last_chapter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_bookshelf, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val book = books!![position]
        holder.title.text = book.title
        holder.author.text = book.author
        holder.view.setOnClickListener {
            ReadActivity.start(context, book.loaderUid, book.link)
        }
    }

    override fun getItemCount(): Int {
        books?.let {
            return books!!.size
        }
        return 0
    }

}
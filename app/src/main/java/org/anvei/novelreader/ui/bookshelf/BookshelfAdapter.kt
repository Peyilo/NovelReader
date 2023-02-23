package org.anvei.novelreader.ui.bookshelf

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.anvei.novelreader.R
import org.anvei.novelreader.entity.BookItem

class BookshelfAdapter(private val books: MutableList<BookItem>) : RecyclerView.Adapter<BookshelfAdapter.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.bookshelf_item_cover)
        val title: TextView = view.findViewById(R.id.bookshelf_item_title)
        val author: TextView = view.findViewById(R.id.bookshelf_item_author)
        val updateState: TextView = view.findViewById(R.id.bookshelf_item_update_state)
        val manageBtn: ImageButton = view.findViewById(R.id.bookshelf_item_manage_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bookshelf, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val book = books[position]
        holder.title.text = book.title
        holder.author.text = book.author
    }

    override fun getItemCount(): Int = books.size

}
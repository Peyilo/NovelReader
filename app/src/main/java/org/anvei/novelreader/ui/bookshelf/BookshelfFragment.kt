package org.anvei.novelreader.ui.bookshelf

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import org.anvei.novelreader.R
import org.anvei.novelreader.activity.ReadHistoryActivity
import org.anvei.novelreader.activity.SearchActivity
import org.anvei.novelreader.entity.BookItem

class BookshelfFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bookshelf, container, false)

        // 书架更新
        val recyclerView = view.findViewById<RecyclerView>(R.id.book_list)
        val books = mutableListOf(BookItem().apply {
            title = "来自深渊"
            author = "未知"
        })
        val adapter = BookshelfAdapter(books)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 下拉刷新
        val refresh = view.findViewById<SmartRefreshLayout>(R.id.refresh_bookshelf)
        refresh.setOnRefreshListener {
            refresh.finishRefresh(1000)
        }

        // 筛选按钮
        val filter = view.findViewById<TextView>(R.id.bookshelf_filter_btn)
        val drawer = view.findViewById<DrawerLayout>(R.id.bookshelf_drawer)
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)      // 禁用侧边滑动
        filter.setOnClickListener {
            drawer.openDrawer(GravityCompat.START)
        }
        view.findViewById<ImageButton>(R.id.bookshelf_filter_back_btn).setOnClickListener {
            drawer.closeDrawer(GravityCompat.START)
        }

        // 搜索按钮
        view.findViewById<TextView>(R.id.bookshelf_search_btn).setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            startActivity(intent)
        }
        // 阅读历史记录按钮
        view.findViewById<ImageButton>(R.id.bookshelf_read_history_btn).setOnClickListener {
            val intent = Intent(activity, ReadHistoryActivity::class.java)
            startActivity(intent)
        }
        return view
    }
}
package org.anvei.novelreader.ui.data

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import org.anvei.novelreader.R
import org.anvei.novelreader.activity.BaseActivity
import org.anvei.novelreader.database.repository.BookRepository
import org.anvei.novelreader.databinding.ActivityDataBinding
import org.anvei.novelreader.util.toast

class DataActivity : BaseActivity(hideToolbar = false, fullScreen = false,
    transparent = false, light = false
) {
    private lateinit var binding: ActivityDataBinding

    private val recycler get() = binding.dataRecycler
    private lateinit var adapter: BookItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = BookItemAdapter(ArrayList())
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)
        refresh()
    }

    override fun onRestart() {
        super.onRestart()
        recycler.postDelayed({
            refresh()
        }, 800)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.data_menu, menu)
        return true
    }

    private fun refresh() {
        Thread {
            val list = BookRepository.getAll()
            runOnUiThread {
                synchronized(adapter) {
                    adapter.list.clear()
                    adapter.list.addAll(list)
                    toast("查询到${adapter.list.size}条数据")
                    adapter.notifyItemRangeChanged(0, list.size)
                }
            }
        }.start()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.data_menu_refresh -> {
                refresh()
                true
            }
            else -> false
        }
    }
}
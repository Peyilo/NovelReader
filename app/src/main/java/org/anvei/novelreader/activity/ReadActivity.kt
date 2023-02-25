package org.anvei.novelreader.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.anvei.novelreader.R
import org.anvei.novelreader.databinding.ActivityReadBinding
import org.anvei.novelreader.entity.Source
import org.anvei.novelreader.ui.read.ChapterAdapter
import org.anvei.novelreader.util.StatusBarUtils
import org.anvei.novelreader.widget.readview.loader.AbsBookLoader
import org.anvei.novelreader.widget.readview.loader.LoaderFactory

private const val TAG = "ReadViewTest"

class ReadActivity : BaseActivity() {

    private lateinit var binding: ActivityReadBinding
    private lateinit var loader: AbsBookLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtils.requestFullScreen(window, binding.root, true, true) // 沉浸式状态栏
        binding.readDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        val source = intent.getSerializableExtra(SOURCE) as Source
        loader = LoaderFactory.getLoader(source)
        loader.link = intent.getStringExtra(LINK)
        binding.readView.openBook(loader)
        binding.readView.setOnLoadListener {
            // 小说加载完成以后，更新章节列表
            val recyclerView = binding.readDrawer.findViewById<RecyclerView>(R.id.read_chap_list_recycler)
            Log.d(TAG, "onCreate: count" + it.chapterCount)
            recyclerView.adapter = ChapterAdapter(it.chapters).apply {
                onItemClickListener = object : ChapterAdapter.OnItemClickListener {
                    override fun onClick(chapterIndex: Int) {
                        binding.readView.jumpToChapter(chapterIndex)
                        Log.d(TAG, "onClick: onItemClickListener $chapterIndex")
                        binding.readDrawer.closeDrawer(GravityCompat.START)
                    }
                }
            }
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
        binding.readView.setOnClickListener {
            binding.readDrawer.openDrawer(GravityCompat.START)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.readView.destroy()
    }

    companion object {
        const val SOURCE = "SOURCE"
        const val LOADER_ID = "LOADER_ID"
        const val LINK = "LINK"

        fun start(context: Context, source: Source, link: String) {
            val intent = Intent(context, ReadActivity::class.java)
            intent.putExtra(SOURCE, source)
            intent.putExtra(LINK, link)
            context.startActivity(intent)
        }

    }
}
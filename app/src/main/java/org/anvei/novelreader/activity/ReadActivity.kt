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
import org.anvei.novelreader.ui.read.ChapterAdapter
import org.anvei.novelreader.util.StatusBarUtils
import org.anvei.novelreader.widget.readview.ReadPage
import org.anvei.novelreader.widget.readview.loader.AbsBookLoader

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
        initReadView()
    }

    private fun initReadView() {
        // 初始化小说加载器
        val loaderUId = intent.getIntExtra(LOADER_UID, 0)
        loader = getLoaderFactory().getLoader(loaderUId)
        loader.link = intent.getStringExtra(LINK)
        // 完成ReadPage的初始化
        binding.readView.setPageInitializer {
            it.initLayout(R.layout.item_view_page, R.id.page_content, R.id.page_header, R.id.page_footer)
        }
        // 启用阴影绘制
        binding.readView.enableShadow(true)
        binding.readView.shadowWidth = 18
        // 开始加载小说
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

    companion object {
        const val LOADER_UID = "LOADER_UID"
        const val LINK = "LINK"
        // 启动ReadActivity
        fun start(context: Context, loaderUID: Int, link: String) {
            val intent = Intent(context, ReadActivity::class.java)
            intent.putExtra(LOADER_UID, loaderUID)
            intent.putExtra(LINK, link)
            context.startActivity(intent)
        }

    }
}
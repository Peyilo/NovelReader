package org.anvei.novelreader.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.anvei.novelreader.R
import org.anvei.novelreader.databinding.ActivityReadBinding
import org.anvei.novelreader.loader.AbsBookLoader
import org.anvei.novelreader.loader.LoaderRepository
import org.anvei.novelreader.ui.read.ChapterAdapter
import org.klee.readview.entities.BookData
import org.klee.readview.entities.ChapData
import org.klee.readview.entities.ChapterStatus
import org.klee.readview.widget.FlipMode
import org.klee.readview.widget.PageView
import org.klee.readview.widget.ReadView
import org.klee.readview.widget.api.ReadViewCallback

private const val TAG = "ReadViewTest"

class ReadActivity : BaseActivity() {

    private lateinit var binding: ActivityReadBinding
    private lateinit var loader: AbsBookLoader
    private val readView: ReadView get() = binding.readView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prepareLoader()
        initReadView()
        initSettingView()
    }

    /**
     * 根据LoaderUID从加载器仓库获取BookLoader，并完成BookLoader的初始化
     */
    private fun prepareLoader() {
        val loaderUId = intent.getIntExtra(LOADER_UID, -1)
        loader = LoaderRepository.getLoader(loaderUId)
        loader.link = intent.getStringExtra(LINK)
    }

    private fun initReadView() {
        readView.initPage { pageView, _ ->          // 初始化PageView
            pageView.initLayout(R.layout.item_view_page, R.id.page_content,
                R.id.page_header, R.id.page_footer)
        }
        readView.setCallback(object : ReadViewCallback {
            override fun onTocInitSuccess(book: BookData) {
                // 小说加载完成以后，更新章节列表
                val recyclerView = binding.readDrawer.findViewById<RecyclerView>(R.id.read_chap_list_recycler)
                recyclerView.adapter = ChapterAdapter(book).apply {
                    onItemClickListener = object : ChapterAdapter.OnItemClickListener {
                        override fun onClick(chapterIndex: Int) {
                            binding.readView.setProcess(chapterIndex)
                            Log.d(TAG, "onClick: onItemClickListener $chapterIndex")
                            binding.readDrawer.closeDrawer(GravityCompat.START)
                        }
                    }
                }
                recyclerView.layoutManager = LinearLayoutManager(this@ReadActivity)
            }
            override fun onUpdatePage(convertView: PageView, newChap: ChapData, newPageIndex: Int) {
                val header = convertView.header!! as TextView
                header.text = newChap.title
                val process = convertView.footer!!.findViewById(R.id.page_footer_process) as TextView
                process.text = if (newChap.status == ChapterStatus.FINISHED) {
                    "${newPageIndex}/${newChap.pageCount}"
                } else {
                    "loading"
                }
            }

        })
        readView.flipMode = FlipMode.Cover
        // 开始加载小说
        readView.openBook(loader)
        // 控制设置面板的开关
        readView.setOnClickRegionListener { xPercent, _ ->
            return@setOnClickRegionListener when (xPercent) {
                in 0..30 -> {
                    readView.prevPage()
                }
                in 70..100 -> {
                    readView.nextPage()
                }
                in 30..70 -> {
                    Log.d(TAG, "open setting view")
                    Toast.makeText(applicationContext, "show setting view", Toast.LENGTH_SHORT).show()
                    /*if (binding.rvSettingLinear.visibility == View.GONE) {
                        if (binding.rpSettingFontLinear.visibility == View.GONE) {
                            openSetting(true)
                        } else {
                            openFontSetting(false)
                        }
                    } else {
                        openSetting(false)
                    }*/
                    true
                }
                else -> false
            }
        }
    }

    private fun openSetting(open: Boolean) {
        if (open) {
            binding.rvSettingLinear.visibility = View.VISIBLE
        } else {
            binding.rvSettingLinear.visibility = View.GONE
        }
    }

    private fun openFontSetting(open: Boolean) {
        if (open) {
            binding.rpSettingFontLinear.visibility = View.VISIBLE
        } else {
            binding.rpSettingFontLinear.visibility = View.GONE
        }
    }

    private fun initSettingView() {
        // 打开目录
        binding.chapterListDisplayBtn.setOnClickListener {
            // 滑动到当前章节处
            binding.readChapListRecycler.scrollToPosition(readView.curChapIndex - 1)
            binding.readDrawer.openDrawer(GravityCompat.START)
        }
        // 打开字体设置面板
        binding.chapterFontSettingBtn.setOnClickListener {
            openSetting(false)
            openFontSetting(true)
        }
        // 设置字体大小更改
        binding.rpFontSizeAdd.setOnClickListener {

        }
        binding.rpFontSizeSubtract.setOnClickListener {

        }
        // 设置上下章节按钮
        binding.nextChapter.setOnClickListener {
            if (readView.hasNextChap()) {
                readView.nextChap()
            } else {
                Toast.makeText(this, "当前已经是最后一章节！", Toast.LENGTH_SHORT).show()
            }
        }
        binding.lastChapter.setOnClickListener {
            if (readView.hasPrevChap()) {
                readView.prevPage()
            } else {
                Toast.makeText(this, "当前没有上一章！", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val LOADER_UID = "LOADER_UID"
        const val LINK = "LINK"
        // 启动ReadActivity
        fun start(context: Context, loaderUID: Int, what: String) {
            val intent = Intent(context, ReadActivity::class.java)
            intent.putExtra(LOADER_UID, loaderUID)
            intent.putExtra(LINK, what)
            context.startActivity(intent)
        }
    }
}
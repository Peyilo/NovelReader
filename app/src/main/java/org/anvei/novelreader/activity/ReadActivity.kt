package org.anvei.novelreader.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.anvei.novelreader.R
import org.anvei.novelreader.databinding.ActivityReadBinding
import org.anvei.novelreader.loader.BaseBookLoader
import org.anvei.novelreader.loader.LoaderRepository
import org.anvei.novelreader.ui.read.ChapterAdapter
import org.klee.readview.entities.BookData
import org.klee.readview.entities.ChapData
import org.klee.readview.entities.ChapterStatus
import org.klee.readview.utils.invisible
import org.klee.readview.utils.visible
import org.klee.readview.widget.FlipMode
import org.klee.readview.widget.PageView
import org.klee.readview.widget.ReadView
import org.klee.readview.widget.api.ReadViewCallback

private const val TAG = "ReadViewTest"

class ReadActivity : BaseActivity() {

    private lateinit var binding: ActivityReadBinding
    private lateinit var loader: BaseBookLoader

    private val drawer get() = binding.readDrawer

    private val readView: ReadView get() = binding.readView
    private val bottomView get() = binding.readBottomLinear
    private val settingView get() = binding.readSettingLinear

    private val chapSeekBar get() = binding.chapSeekbar
    private val nextChapterBtn get() = binding.nextChapBtn
    private val prevChapBtn get() = binding.prevChapBtn
    private val showTocBtn get() = binding.showTocBtn
    private val switchOrientationBtn get() = binding.switchOrientationBtn
    private val settingBtn get() = binding.settingBtn

    private val tocRecycler get() = binding.tocRecycler

    private val reduceFontSizeBtn get() = binding.reduceFontSizeBtn
    private val increaseFontSizeBtn get() = binding.increaseFontSizeBtn
    private val revertFontSizeBtn get() = binding.revertFontSizeBtn

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prepareLoader()
        initReadView()
        initSettingView()
        configReadView()
        openBook()
    }

    /**
     * 根据LoaderUID从加载器仓库获取BookLoader，并完成BookLoader的初始化
     */
    private fun prepareLoader() {
        val loaderUId = intent.getIntExtra(LOADER_UID, -1)
        loader = LoaderRepository.getLoader(loaderUId)
        loader.link = intent.getStringExtra(LINK)
    }

    /**
     * 完成ReadView的参数配置
     */
    private fun configReadView() {
        readView.flipMode = FlipMode.Cover
    }

    /**
     * 初始化ReadView，并设置回调监听
     */
    private fun initReadView() {
        readView.initPage { pageView, _ ->          // 初始化PageView
            pageView.initLayout(R.layout.item_view_page, R.id.page_content,
                R.id.page_header, R.id.page_footer)
        }
        readView.setCallback(object : ReadViewCallback {
            // 小说加载完成以后，刷新章节列表
            override fun onTocInitSuccess(book: BookData) {
               runOnUiThread {
                   val recyclerView = binding.readDrawer.findViewById<RecyclerView>(R.id.toc_recycler)
                   recyclerView.adapter = ChapterAdapter(book).apply {
                       setOnItemClickListener {
                           binding.readView.setProcess(it)
                           binding.readDrawer.closeDrawer(GravityCompat.START)
                       }
                   }
                   recyclerView.layoutManager = LinearLayoutManager(this@ReadActivity)
               }
            }
            // 绑定视图
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
        readView.setOnClickRegionListener { xPercent, _ ->
            return@setOnClickRegionListener when (xPercent) {
                in 0..30 -> {
                    readView.prevPage()           // 屏幕左侧点击触发上一页
                }
                in 70..100 -> {             // 屏幕右侧点击触发下一页
                    readView.nextPage()
                }
                in 30..70 -> {              // 中间区域点击触发设置面板
                    onMiddleRegionClick()
                    true
                }
                else -> false
            }
        }
    }

    // 开始加载小说
    private fun openBook() {
        readView.openBook(loader)
    }

    /**
     * 根据当前视图状态控制点击对应的行为
     */
    private fun onMiddleRegionClick() {
        if (!bottomView.isVisible) {
            if (!settingView.isVisible) {
                openSetting(true)
            } else {
                openFontSetting(false)
            }
        } else {
            openSetting(false)
        }
    }

    /**
     * 控制设置面板的开关
     * @param open true表示打开控制面板，反之为关闭
     */
    private fun openSetting(open: Boolean) {
        if (open) {
            bottomView.visible()
        } else {
            bottomView.invisible()
        }
    }

    /**
     * 控制字体大小设置面板的开关
     * @param open true表示打开控制面板，反之为关闭
     */
    private fun openFontSetting(open: Boolean) {
        if (open) {
            settingView.visible()
        } else {
            settingView.invisible()
        }
    }

    private fun initSettingView() {
        showTocBtn.setOnClickListener {
            // 滑动到当前章节处
            tocRecycler.scrollToPosition(readView.curChapIndex - 1)
            binding.readDrawer.openDrawer(GravityCompat.START)
            openSetting(false)
        }
        // 打开字体设置面板
        settingBtn.setOnClickListener {
            openSetting(false)
            openFontSetting(true)
        }
        // 设置字体大小更改
        increaseFontSizeBtn.setOnClickListener {
            readView.apply {
                if (!setContentSize(getContentSize() + 2)) {
                    toast("当前字体大小已经是最大值！")
                }
            }
        }
        reduceFontSizeBtn.setOnClickListener {
            readView.apply {
                if (!setContentSize(getContentSize() - 2)) {
                    toast("当前字体大小已经是最小值！")
                }
            }
        }
        // 设置上下章节按钮
        nextChapterBtn.setOnClickListener {
            if (readView.hasNextChap()) {
                readView.nextChap()
            } else {
                toast("当前已经是最后一章节！")
            }
        }
        prevChapBtn.setOnClickListener {
            if (readView.hasPrevChap()) {
                readView.prevPage()
            } else {
                toast("当前已经是第一章！")
            }
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    companion object {

        const val LOADER_UID = "LOADER_UID"         // BookLoader的UID
        const val LINK = "LINK"                     // 待加载小说的链接

        /**
         * 启动ReadActivity
         */
        fun start(context: Context, loaderUID: Int, link: String) {
            val intent = Intent(context, ReadActivity::class.java)
            intent.putExtra(LOADER_UID, loaderUID)
            intent.putExtra(LINK, link)
            context.startActivity(intent)
        }
    }
}
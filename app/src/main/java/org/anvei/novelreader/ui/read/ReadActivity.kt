package org.anvei.novelreader.ui.read

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.anvei.novelreader.R
import org.anvei.novelreader.activity.BaseActivity
import org.anvei.novelreader.database.entity.BookItem
import org.anvei.novelreader.database.repository.BookRepository
import org.anvei.novelreader.databinding.ActivityReadBinding
import org.anvei.novelreader.file.NovelCacheManager
import org.anvei.novelreader.file.bean.ChapBean
import org.anvei.novelreader.file.bean.TocBean
import org.anvei.novelreader.loader.BaseBookLoader
import org.anvei.novelreader.loader.LoaderRepository
import org.anvei.novelreader.ui.read.api.IReadPresenter
import org.anvei.novelreader.ui.read.api.IReadView
import org.anvei.novelreader.util.toast
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
class ReadActivity : BaseActivity(), IReadView, ReadViewCallback {

    private lateinit var binding: ActivityReadBinding

    private val presenter: IReadPresenter by lazy {
        ReadPresenter(this, ReadModel(bookItem))
    }

    private lateinit var loader: BaseBookLoader
    private lateinit var bookItem: BookItem
    private var bookItemInitialized = false

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

    private var revertedSize: Float = 0F      // 在一轮字体更新的点击事件开始之前的字体大小，用于恢复最初的字体大小

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prepareLoader()
        initReadView()
        initSettingView()
        configReadView()
        readView.openBook(loader)
    }

    /**
     * 根据LoaderUID从加载器仓库获取BookLoader，并完成BookLoader的初始化
     */
    private fun prepareLoader() {
        val loaderUID = intent.getIntExtra(LOADER_UID, 0)
        val link = intent.getStringExtra(LINK)
        loader = LoaderRepository.getLoader(loaderUID)
        loader.link = link
    }

    /**
     * 完成ReadView的参数配置
     */
    private fun configReadView() {
        readView.flipMode = FlipMode.Cover
        readView.setPreprocessParas(1, 1)
    }

    /**
     * 初始化ReadView，并设置回调监听
     */
    private fun initReadView() {
        readView.initPage { pageView, _ ->          // 初始化PageView
            pageView.initLayout(
                R.layout.item_view_page, R.id.page_content,
                R.id.page_header, R.id.page_footer
            )
        }
        readView.setCallback(this)
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

    /**
     * 根据当前视图状态控制点击对应的行为
     */
    private fun onMiddleRegionClick() {
        if (!bottomView.isVisible) {
            if (!settingView.isVisible) {
                openBottomView(true)
            } else {
                openFontSetting(false)
            }
        } else {
            openBottomView(false)
        }
    }

    /**
     * 控制设置面板的开关
     * @param open true表示打开控制面板，反之为关闭
     */
    private fun openBottomView(open: Boolean) {
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
            revertedSize = readView.getContentSize()
        } else {
            settingView.invisible()
        }
    }

    private fun initSettingView() {
        showTocBtn.setOnClickListener {
            // 滑动到当前章节处
            tocRecycler.scrollToPosition(readView.curChapIndex - 1)
            binding.readDrawer.openDrawer(GravityCompat.START)
            openBottomView(false)
        }
        // 打开字体设置面板
        settingBtn.setOnClickListener {
            openBottomView(false)
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
        revertFontSizeBtn.setOnClickListener {
            readView.apply {        // 恢复最开始的字体大小
                setContentSize(revertedSize)
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
        switchOrientationBtn.setOnClickListener {       // 切换屏幕方向
            requestedOrientation =
                if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            // TODO: 当处理滑动事件，浮动面板需要关闭
            /*if (bottomView.isVisible) {
                openBottomView(false)
            } else if (settingView.isVisible) {
                openFontSetting(false)
            }*/
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onTocInitSuccess(book: BookData) {
        runOnUiThread {
            val recyclerView = drawer.findViewById<RecyclerView>(R.id.toc_recycler)
            recyclerView.adapter = ChapterAdapter(book).apply {
                setOnItemClickListener {
                    readView.setProcess(it)
                    drawer.closeDrawer(GravityCompat.START)
                }
            }
            recyclerView.layoutManager = LinearLayoutManager(this@ReadActivity)
        }
        bookItem = BookRepository.query(loader.loaderUID, loader.link)
        readView.setProcess(bookItem.chapIndex, bookItem.pageIndex)
        bookItemInitialized = true
        presenter.startReadTimer()
        // 更新目录缓存
        Thread {
            NovelCacheManager.writeTocFile(generateTocBean(book))
        }.start()
    }

    override fun onUpdatePage(convertView: PageView, newChap: ChapData, newPageIndex: Int) {
        val header = convertView.header!! as TextView
        header.text = newChap.title
        val process =
            convertView.footer!!.findViewById(R.id.page_footer_process) as TextView
        process.text = if (newChap.status == ChapterStatus.FINISHED) {
            "${newPageIndex}/${newChap.pageCount}"
        } else {
            "loading"
        }
    }

    override fun onLoadChap(chap: ChapData, success: Boolean) {
        if (success && !TextUtils.isEmpty(chap.content)) {
            // 更新章节内容缓存
            Thread {
                NovelCacheManager.writeContent(bookItem.uid, chap.o.toString(), chap.content!!)
            }.start()
        }
    }

    private fun generateTocBean(book: BookData): TocBean {
        val tocBean = TocBean().apply {
            title = bookItem.title
            author = bookItem.author
            loaderUID = bookItem.loaderUID
            bookId = bookItem.uid
            link = bookItem.link!!
        }
        for (i in 1..book.chapCount) {
            val chap = book.getChapter(i)
            tocBean.chapList.add(
                ChapBean().apply {
                    title = chap.title
                    chapIndex = chap.chapIndex
                    link = chap.o.toString()
                }
            )
        }
        return tocBean
    }

    override fun onResume() {
        super.onResume()
        if (bookItemInitialized && presenter.timerIsStop()) {
            presenter.startReadTimer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (bookItemInitialized && !presenter.timerIsStop()) {
            presenter.stopReadTimer()
        }
    }
    override fun onStop() {
        super.onStop()
        presenter.onExit()
    }

    override fun getCurChapIndex() = readView.curChapIndex

    override fun getCurPageIndex() = readView.curPageIndex

    companion object {

        const val LOADER_UID = "LOADER_UID"
        const val LINK = "LINK"

        fun start(context: Context, loaderUID: Int, link: String) {
            val intent = Intent(context, ReadActivity::class.java).apply {
                putExtra(LOADER_UID, loaderUID)
                putExtra(LINK, link)
            }
            context.startActivity(intent)
        }
    }
}
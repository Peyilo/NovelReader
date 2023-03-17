package org.anvei.novelreader.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.bumptech.glide.Glide
import org.anvei.novelreader.database.entity.BookItem
import org.anvei.novelreader.database.repository.BookRepository
import org.anvei.novelreader.databinding.ActivityNovelHomeBinding
import org.anvei.novelreader.loader.BaseBookLoader
import org.anvei.novelreader.loader.LoaderRepository
import org.anvei.novelreader.loader.bean.SearchResultItem
import org.anvei.novelreader.ui.read.ReadActivity
import org.anvei.novelreader.util.getParcelableExtra
import org.anvei.novelreader.util.toast
import java.sql.Date

private const val TAG = "NovelHomeActivity"
class NovelHomeActivity : BaseActivity() {
    private lateinit var binding: ActivityNovelHomeBinding
    private lateinit var loader: BaseBookLoader

    private var fromSearch: Boolean = false
    private lateinit var bookItem: BookItem

    private val cover get() = binding.novelHomeCover
    private val title get() = binding.novelHomeTitle
    private val author get() = binding.novelHomeAuthor
    private val intro get() = binding.novelHomeIntro
    private val likeBtn get() = binding.novelHomeLikeBtn
    private val readBtn get() = binding.novelHomeReadBtn
    private val downloadBtn get() = binding.novelHomeDownloadBtn

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovelHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prepareLoader()
        initHomeView()
    }

    private fun prepareLoader() {
        val loaderUId = intent.getIntExtra(LOADER_UID, -1)
        loader = LoaderRepository.getLoader(loaderUId)
        loader.link = intent.getStringExtra(LINK)
        fromSearch = intent.getBooleanExtra(FROM_SEARCH, true)
        if (fromSearch) {
            val result = getParcelableExtra(RESULT, SearchResultItem::class.java)!!
            bookItem = BookItem().apply {
                title = result.title
                author = result.author
                loaderUID = result.loaderUID
                link = result.link
                coverLink = result.coverUrl
            }
        }
    }

    private fun  initHomeView() {
        Glide.with(applicationContext).load(bookItem.coverLink).into(cover)
        title.text = bookItem.title
        author.text = bookItem.author
        likeBtn.setOnClickListener {
            // 先在数据库中查询
            Thread {
                val query = BookRepository.query(bookItem.loaderUID, bookItem.link)
                if (query == null) {
                    // 没有阅读过、也不在书架中
                    bookItem.onBookshelf = true
                    bookItem.addTime = Date(System.currentTimeMillis())
                    BookRepository.addBookInBookshelf(bookItem)
                    toast("加入书架成功！")
                } else {
                    if (query.onBookshelf) {
                        toast("已经在书架中！")
                    } else {
                        query.onBookshelf = true
                        query.addTime = Date(System.currentTimeMillis())
                        BookRepository.updateBook(query)
                        toast("加入书架成功！")
                    }
                }
            }.start()
        }
        readBtn.setOnClickListener {
            // 启动阅读界面
            Thread {
                val item = BookRepository.query(bookItem.loaderUID, bookItem.link)
                if (item == null) {
                    bookItem.hasHistory = true
                    bookItem.firstReadTime = Date(System.currentTimeMillis())
                    BookRepository.addBookInBookshelf(bookItem)
                } else if (!item.hasHistory){
                    item.hasHistory = true
                    item.firstReadTime = Date(System.currentTimeMillis())
                    BookRepository.updateBook(bookItem)
                }
                ReadActivity.start(this, bookItem.loaderUID, bookItem.link!!)
            }.start()
        }
        downloadBtn.setOnClickListener {

        }
    }

    companion object {

        const val LOADER_UID = "LOADER_UID"
        const val LINK = "LINK"
        const val RESULT = "RESULT"
        const val FROM_SEARCH = "FROM_SEARCH"

        fun startFromSearch(context: Context, loaderUID: Int, link: String, result: SearchResultItem) {
            val intent = Intent(context, NovelHomeActivity::class.java)
            intent.putExtra(LOADER_UID, loaderUID)
            intent.putExtra(LINK, link)
            intent.putExtra(RESULT, result)
            intent.putExtra(FROM_SEARCH, true)
            context.startActivity(intent)
        }

    }

}
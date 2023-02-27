package org.anvei.novelreader.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import org.anvei.novel.api.SfacgAPI
import org.anvei.novelreader.databinding.ActivitySearchResultBinding
import org.anvei.novelreader.ui.search.ResultAdapter
import org.anvei.novelreader.ui.search.bean.SearchResultItem
import org.anvei.novelreader.util.StatusBarUtils
import org.anvei.novelreader.widget.readview.loader.LoaderFactory

class SearchResultActivity : BaseActivity() {
    private lateinit var binding: ActivitySearchResultBinding
    private lateinit var adapter: ResultAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtils.requestFullScreen(window, binding.root, true, false)
        val keyword = intent.getStringExtra(KEYWORD)
        search(keyword!!)
        binding.resultBar.setText(keyword)
        adapter = ResultAdapter(this)
        binding.resultRecycler.adapter = adapter
        binding.resultRecycler.layoutManager = LinearLayoutManager(this)
        binding.resultCancelBtn.setOnClickListener {
            finish()
        }
        binding.resultBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                adapter.resultList.clear()
                search(binding.resultBar.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun search(keyword: String) {
        // 开始发起请求
        Thread{
            val api = SfacgAPI.getInstance()
            val resultJson = api.search(keyword)
            val start = adapter.resultList.size
            for (novel in resultJson.data.novels) {
                adapter.resultList.add(SearchResultItem(LoaderFactory.SfacgLoaderUID).apply {
                    title = novel.novelName
                    author = novel.authorName
                    coverUrl = novel.novelCover
                    charCount = novel.charCount
                    url = novel.novelId.toString()
                    intro = novel.expand.intro
                })
            }
            val end = adapter.resultList.size
            if (start != end) {
                runOnUiThread {
                    adapter.notifyItemRangeChanged(start, end - start)
                }
            }
        }.start()
    }

    companion object {
        private const val KEYWORD = "KEYWORD"
        fun start(context: Context, keyword: String) {
            val intent = Intent(context, SearchResultActivity::class.java)
            intent.putExtra(KEYWORD, keyword)
            context.startActivity(intent)
        }
    }
}
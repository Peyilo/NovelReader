package org.anvei.novelreader.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import org.anvei.novelreader.databinding.ActivitySearchResultBinding
import org.anvei.novelreader.loader.LoaderRepository
import org.anvei.novelreader.ui.search.ResultAdapter

class SearchResultActivity : BaseActivity() {

    private lateinit var binding: ActivitySearchResultBinding
    private lateinit var adapter: ResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val keyword = intent.getStringExtra(KEYWORD)
        binding.resultBar.setText(keyword)
        adapter = ResultAdapter(this)
        binding.resultRecycler.adapter = adapter
        binding.resultRecycler.layoutManager = LinearLayoutManager(this)
        search(keyword!!)
        binding.resultCancelBtn.setOnClickListener {
            finish()
        }
        binding.resultBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // 先清空，再进行搜索
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
            val start = adapter.resultList.size
            getAvailableLoaderUIDList().forEach {
                val searchable = LoaderRepository.getSearchableLoader(it)
                adapter.resultList.addAll(
                    searchable.search(keyword)
                )
            }
            val end = adapter.resultList.size
            if (start != end) {
                runOnUiThread {
                    adapter.notifyItemRangeChanged(start, end - start)
                }
            }
        }.start()
    }

    private fun getAvailableLoaderUIDList(): Array<Int> {
        return arrayOf(LoaderRepository.SfacgLoaderUID)
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
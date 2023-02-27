package org.anvei.novelreader.activity

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import org.anvei.novelreader.databinding.ActivitySearchBinding
import org.anvei.novelreader.util.StatusBarUtils

private const val TAG = "SearchActivity"
class SearchActivity : BaseActivity() {
    private lateinit var binding: ActivitySearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtils.requestFullScreen(window, binding.root, true, false)

        // 返回上一个Activity
        binding.searchCancelBtn.setOnClickListener {
            finish()
        }
        binding.searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // 进入SearchResultActivity界面
                val text = binding.searchBar.text
                if (text.isEmpty() || text.isBlank()) {
                    Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show()
                } else {
                    SearchResultActivity.start(this, text.toString())
                }
                true
            } else {
                false
            }
        }
    }
}
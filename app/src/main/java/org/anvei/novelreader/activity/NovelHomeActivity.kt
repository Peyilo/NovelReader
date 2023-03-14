package org.anvei.novelreader.activity

import android.os.Bundle
import org.anvei.novelreader.databinding.ActivityNovelHomeBinding

private const val TAG = "NovelHomeActivity"
class NovelHomeActivity : BaseActivity() {
    private lateinit var binding: ActivityNovelHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovelHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}
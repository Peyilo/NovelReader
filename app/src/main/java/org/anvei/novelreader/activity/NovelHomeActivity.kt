package org.anvei.novelreader.activity

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import org.anvei.novelreader.R
import org.anvei.novelreader.databinding.ActivityNovelHomeBinding

private const val TAG = "NovelHomeActivity"
class NovelHomeActivity : BaseActivity() {
    lateinit var binding: ActivityNovelHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovelHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}
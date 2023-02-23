package org.anvei.novelreader.activity

import android.os.Bundle
import android.util.Log
import org.anvei.novel.api.SfacgAPI
import org.anvei.novelreader.bean.Book
import org.anvei.novelreader.bean.Chapter
import org.anvei.novelreader.bean.IndexBean
import org.anvei.novelreader.bean.Volume
import org.anvei.novelreader.bean.VolumeBook
import org.anvei.novelreader.databinding.ActivityReadBinding
import org.anvei.novelreader.util.StatusBarUtils
import org.anvei.novelreader.widget.read.ReadView

private const val TAG = "ReadViewTest"
class ReadActivity : BaseActivity() {
    private lateinit var binding: ActivityReadBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtils.requestFullScreen(window, binding.root, true, true) // 沉浸式状态栏
        val api = SfacgAPI()
        binding.readView.openBook(object : ReadView.BookLoader {
            override fun getBook(): Book {
                val chapListJson = api.getChapListJson(591785)
                var size = 0
                val volumeList = ArrayList<Volume>()
                for (volume in chapListJson.data.volumeList) {
                    val startIndex = size + 1
                    for (chapter in volume.chapterList) {
                        size++
                    }
                    val endIndex = size + 1
                    volumeList.add(Volume(volume.title, IndexBean(startIndex, endIndex)))
                }
                Log.d(TAG, "getBook: size = $size")
                val book = VolumeBook("SfacgAPP", size, volumeList)
                var index = 0
                for (volume in chapListJson.data.volumeList) {
                    for (chapter in volume.chapterList) {
                        index++
                        book.addChapter(Chapter(index, chapter.title).apply {
                            what = chapter.chapId.toString()
                        })
                    }
                }
                return book
            }
            override fun loadChapter(chapter: Chapter) {
                chapter.what.let {
                    val chapContentJson = api.getChapContentJson(it.toLong())
                    chapter.content = chapContentJson.content
                }

            }
        }, 2, 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.readView.destroy()
    }

}
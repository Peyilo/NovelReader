package org.anvei.novelreader.widget.readview.loader

import org.anvei.novel.api.SfacgAPI
import org.anvei.novelreader.bean.*

class SfacgLoader : AbsBookLoader() {
    val api: SfacgAPI = SfacgAPI()

    override fun getBook(): Book {
        val chapListJson = api.getChapListJson(link.toLong())
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
}
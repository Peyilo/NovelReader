package org.anvei.novelreader.widget.readview.loader

import org.anvei.novelreader.widget.readview.bean.Book
import org.anvei.novelreader.widget.readview.bean.Chapter

interface BookLoader {

    // 该方法需要完成小说目录的加载
    fun getBook(): Book

    // 加载指定章节
    fun loadChapter(chapter: Chapter)

}
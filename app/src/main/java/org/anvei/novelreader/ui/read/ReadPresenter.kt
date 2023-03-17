package org.anvei.novelreader.ui.read

import org.anvei.novelreader.database.repository.BookRepository
import org.anvei.novelreader.ui.read.api.IReadModel
import org.anvei.novelreader.ui.read.api.IReadPresenter
import org.anvei.novelreader.ui.read.api.IReadView
import java.sql.Date

class ReadPresenter(
    private val view: IReadView,
    private val model: IReadModel
): IReadPresenter {

    /**
     * 更新数据库中的信息
     */
    override fun upBookItem() {
        Thread {
            // 更新最后阅读时间、阅读进度
            val bookItem = model.getBookItem()
            bookItem.apply {
                    chapIndex = view.getCurChapIndex()
                    pageIndex = view.getCurPageIndex()
                    lastReadTime = Date(System.currentTimeMillis())
            }
            BookRepository.updateBook(bookItem)
        }.start()
    }

    fun refreshToc() {

    }

    override fun onExit() {
        upBookItem()
        // upBookTocCache()
    }

    private var startTime = 0
    private var isStop = true

    @Synchronized override fun startReadTimer() {
        startTime = (System.currentTimeMillis() / 1000).toInt()
        isStop = false
    }

    @Synchronized override fun stopReadTimer() {
        val readTime = (System.currentTimeMillis() / 1000).toInt() - startTime
        model.getBookItem().readTime += readTime
        isStop = true
    }

    override fun timerIsStop(): Boolean = isStop
}
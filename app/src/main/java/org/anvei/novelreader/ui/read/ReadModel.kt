package org.anvei.novelreader.ui.read

import org.anvei.novelreader.database.entity.BookItem
import org.anvei.novelreader.ui.read.api.IReadModel

class ReadModel(private val bookItem: BookItem): IReadModel {

    override fun getBookItem(): BookItem = bookItem

}
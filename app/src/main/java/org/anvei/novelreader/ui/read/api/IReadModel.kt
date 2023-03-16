package org.anvei.novelreader.ui.read.api

import org.anvei.novelreader.database.entity.BookItem

interface IReadModel {

    fun getBookItem(): BookItem

}
package org.anvei.novelreader.ui.read.api

import org.anvei.novelreader.file.bean.TocBean

interface IReadView {

    fun getCurChapIndex(): Int

    fun getCurPageIndex(): Int

    fun getTocBean(): TocBean

}
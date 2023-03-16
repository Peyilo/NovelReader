package org.anvei.novelreader.ui.read.api

interface IReadPresenter {

    fun upBookItem()

    fun onExit()

    fun startReadTimer()

    fun stopReadTimer()

    fun timerIsStop(): Boolean

}
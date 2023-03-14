package org.anvei.novelreader.activity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.anvei.novelreader.util.fullScreen
import org.anvei.novelreader.util.setStatusColor

open class BaseActivity(
    private val fullScreen: Boolean = true,             // 是否全屏
    private val hideToolbar: Boolean = true,            // 是否隐藏工具栏
    private val transparent: Boolean = true,            // 状态栏是否透明
    private val light: Boolean = true)
    : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initStatusBar()
        if (hideToolbar) {
            supportActionBar?.hide()
        }
    }

    // 完成状态栏的初始化
    private fun initStatusBar() {
        setStatusColor(light)
        if (fullScreen) {
            fullScreen()
        }
        if (transparent) {
            window.statusBarColor = Color.TRANSPARENT
        }
    }

}
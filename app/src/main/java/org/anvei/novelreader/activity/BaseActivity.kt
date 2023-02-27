package org.anvei.novelreader.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.anvei.novelreader.App
import org.anvei.novelreader.widget.readview.loader.LoaderFactory

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (hideToolbar()) {
            supportActionBar?.hide()
        }
    }
    /**
     * 是否隐藏工具栏
     */
    protected fun hideToolbar() : Boolean = true

    fun getLoaderFactory(): LoaderFactory = App.getLoaderFactory()

}
package org.anvei.novelreader.loader

import org.klee.readview.loader.BookLoader

abstract class BaseBookLoader(
    val uid: Int,           // 加载器唯一UID标识
    var name: String        // 加载器名称
) : BookLoader{

    var link: String? = null
    var o: Any? = null

}
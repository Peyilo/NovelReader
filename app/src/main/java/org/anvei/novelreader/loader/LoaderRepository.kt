package org.anvei.novelreader.loader

/**
 * 小说加载器仓库
 */
object LoaderRepository {

    const val NativeLoaderUID = 1
    const val SfacgLoaderUID = 2

    private val nativeLoader by lazy {  }
    private val sfacgLoader by lazy { SfacgLoader() }

    fun getLoader(loadUID: Int): AbsBookLoader {
        return when (loadUID) {
            SfacgLoaderUID -> {
                sfacgLoader
            }
            else -> {
                throw IllegalStateException()
            }
        }
    }

}
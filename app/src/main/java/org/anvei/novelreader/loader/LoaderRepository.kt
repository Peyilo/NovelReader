package org.anvei.novelreader.loader

/**
 * 小说加载器仓库
 */
object LoaderRepository {

    const val NativeLoaderUID = 1
    const val SfacgLoaderUID = 2

    private val nativeLoader by lazy { NativeLoader() }
    private val sfacgLoader by lazy { SfacgLoader() }

    fun getLoader(loadUID: Int): BaseBookLoader {
        return when (loadUID) {
            NativeLoaderUID -> {
                nativeLoader
            }
            SfacgLoaderUID -> {
                sfacgLoader
            }
            else -> {
                throw IllegalStateException()
            }
        }
    }

    fun getSearchableLoader(loadUID: Int): Searchable {
        val loader = getLoader(loadUID)
        if (loader is Searchable) {
            return loader
        }
        throw IllegalArgumentException("$loadUID 对应的小说加载器并没有实现Searchable接口！" )
    }

}
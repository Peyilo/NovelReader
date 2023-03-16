package org.anvei.novelreader.loader

import org.anvei.novelreader.App
import org.klee.readview.entities.BookData
import org.klee.readview.loader.DefaultNativeLoader
import java.io.File

class NativeLoader : BaseBookLoader(
    LoaderRepository.NativeLoaderUID, "Native") {

    private val loader = DefaultNativeLoader()

    override fun requestToc(): BookData {
        loader.file = File(App.getContext().externalCacheDir, link!!)
        return loader.initToc()
    }
}
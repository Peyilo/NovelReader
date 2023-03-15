package org.anvei.novelreader.loader

import org.klee.readview.entities.BookData

class NativeLoader() : BaseBookLoader(
    LoaderRepository.NativeLoaderUID, "Native") {

    private val loader = org.klee.readview.loader.NativeLoader()

    override fun initToc(): BookData {
        require(link != null)
        TODO()
    }

}
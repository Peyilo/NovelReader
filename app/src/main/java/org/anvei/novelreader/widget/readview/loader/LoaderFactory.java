package org.anvei.novelreader.widget.readview.loader;

import org.anvei.novelreader.entity.Source;

public class LoaderFactory {
    private volatile static SfacgLoader sfacgLoader;

    public static AbsBookLoader getLoader(Source source) {
        if (sfacgLoader == null) {
            synchronized (SfacgLoader.class) {
                if (sfacgLoader == null) {
                    sfacgLoader = new SfacgLoader();
                }
            }
        }
        return sfacgLoader;
    }
}

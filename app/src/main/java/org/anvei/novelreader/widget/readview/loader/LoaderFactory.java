package org.anvei.novelreader.widget.readview.loader;

public class LoaderFactory {
    public LoaderFactory() {
    }

    public static final int SfacgLoaderUID = 0x23363146;
    private volatile SfacgLoader sfacgLoader;

    public AbsBookLoader getLoader(int loadUID) {
        switch (loadUID) {
            case SfacgLoaderUID: {
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
        throw new IllegalArgumentException("没有该loaderUID对应的小说加载器!");
    }
}

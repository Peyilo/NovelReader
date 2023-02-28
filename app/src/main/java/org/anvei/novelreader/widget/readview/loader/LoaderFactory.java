package org.anvei.novelreader.widget.readview.loader;

public class LoaderFactory {
    public LoaderFactory() {
    }

    public static final int SfacgLoaderUID      = 0x23363146;
    public static final int HbookerLoaderUID    = 0x58458499;
    private volatile SfacgLoader sfacgLoader;
    private volatile HbookerLoader hbookerLoader;

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
            case HbookerLoaderUID: {
                if (hbookerLoader == null) {
                    synchronized (HbookerLoader.class) {
                        if (hbookerLoader == null) {
                            hbookerLoader = new HbookerLoader();
                        }
                    }
                }
            }
        }
        throw new IllegalArgumentException("没有该loaderUID对应的小说加载器!");
    }
}

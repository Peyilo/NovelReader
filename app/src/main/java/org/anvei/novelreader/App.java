package org.anvei.novelreader;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import org.anvei.novelreader.widget.readview.loader.LoaderFactory;

public class App extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private final static LoaderFactory loaderFactory = new LoaderFactory();

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContext() {
        return context;
    }

    /**
     * 获取一个全局的小说加载器工厂
     */
    public static LoaderFactory getLoaderFactory() {
        return loaderFactory;
    }
}

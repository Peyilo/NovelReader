package org.anvei.novelreader;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class App extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    public static void startTask(Runnable task) {
        threadPool.submit(task);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        try {
            threadPool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

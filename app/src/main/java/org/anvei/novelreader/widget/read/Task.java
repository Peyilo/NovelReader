package org.anvei.novelreader.widget.read;

import androidx.annotation.Nullable;

import org.anvei.novelreader.widget.read.interfaces.TaskLifecycle;
import org.anvei.novelreader.widget.read.interfaces.TaskListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.security.auth.DestroyFailedException;

/**
 * 一次性加载任务
 */
public class Task implements TaskLifecycle {

    private volatile boolean loading = false;

    private boolean isDestroyed = false;

    private ExecutorService mainTask;

    private Runnable runnable;

    public Task() {
        mainTask = Executors.newSingleThreadExecutor();
    }

    public Task(Runnable runnable) {
        this();
        this.runnable = runnable;
    }

    @Override
    public boolean isLoading() {
        return loading;
    }

    @Override
    public synchronized void start(@Nullable TaskListener taskListener) {
        if (loading) {
            throw new IllegalStateException("不能重复调用start()!");
        }
        loading = true;
        mainTask.submit(() -> {
            try {
                run();
                if (taskListener != null) {
                    taskListener.onSuccess();
                }
            } catch (Exception e) {
                if (taskListener != null) {
                    taskListener.onFailed();
                }
            }
            loading = false;
            if (taskListener != null) {
                taskListener.onFinished();
            }
        });
    }

    public void start() {
        start(null);
    }

    // 将在子线程执行
    protected void run() {
        if (runnable != null) {
            runnable.run();
        }
    }

    @Override
    public void stop() {
        mainTask.shutdown();
    }

    /**
     * 销毁任务，调用之前需要先调用stop()停止任务 <br/>
     * 需要在该函数内完成一些内存释放操作 <br/>
     */
    @Override
    public void destroy() throws DestroyFailedException {
        try {
            mainTask = null;
            isDestroyed = true;
        } catch (Exception e) {
            throw new DestroyFailedException();
        }
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

}

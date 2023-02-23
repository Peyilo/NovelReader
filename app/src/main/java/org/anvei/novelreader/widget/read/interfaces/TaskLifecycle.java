package org.anvei.novelreader.widget.read.interfaces;

import javax.security.auth.Destroyable;

public interface TaskLifecycle extends Destroyable {

    /**
     * 如果任务还在运行，就返回true，否则返回false
     */
    boolean isLoading();

    /**
     * 启动任务
     * @param taskListener 加载监听回调
     */
    void start(TaskListener taskListener);

    /**
     * 停止LoadTask
     */
    void stop();

}

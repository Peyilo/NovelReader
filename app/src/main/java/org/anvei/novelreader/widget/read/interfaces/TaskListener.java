package org.anvei.novelreader.widget.read.interfaces;

public interface TaskListener {

    default void onSuccess() {}

    default void onFailed() {}

    default void onFinished() {}

}

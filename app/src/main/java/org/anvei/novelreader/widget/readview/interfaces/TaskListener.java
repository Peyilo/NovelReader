package org.anvei.novelreader.widget.readview.interfaces;

public interface TaskListener {

    default void onSuccess() {}

    default void onFailed() {}

    default void onFinished() {}

}

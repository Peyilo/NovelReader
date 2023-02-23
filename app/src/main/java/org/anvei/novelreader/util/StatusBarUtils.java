package org.anvei.novelreader.util;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class StatusBarUtils {

    public static void requestFullScreen(Window window, View root, boolean light, boolean hide) {
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, root);
        window.setStatusBarColor(Color.TRANSPARENT);
        controller.setAppearanceLightStatusBars(light);
        if (hide) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                controller.hide(WindowInsetsCompat.Type.statusBars());
            }
        }
        WindowCompat.setDecorFitsSystemWindows(window, false);
    }

}

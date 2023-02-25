package org.anvei.novelreader.widget.readview.flip;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.View;

/**
 * 负责控制页面的翻页效果
 */
public class PageFlipper {

    private FlipMode mode = FlipMode.NONE;
    private int duration = 250;

    public PageFlipper() {
    }

    // 设置翻页模式
    public void setFlipMode(FlipMode mode) {
        this.mode = mode;
    }

    // 设置翻页动画持续时间
    public void setFlipDuration(int duration) {
        this.duration = duration;
    }

    public FlipMode getMode() {
        return mode;
    }

    public int getDuration() {
        return duration;
    }

    public PageDirection getDirection(PointF start, PointF end) {
        return PageDirection.NONE;
    }

    // 开始翻页动画，isForward表示翻页方向，nextPageBitmap表示下一页的位图。
    public void flipPage(PageDirection direction, Bitmap nextPageBitmap) {

    }

    /**
     * 将由PageFlipper负责管理子View的layout过程
     * @param v 子view
     * @param position 该子view的位置，例如当前需要显示的子view的position为0，
     *                 上一页的子view为-1，下一页的子view为1，以此类推
     * @param width 容器的宽度，这里默认子view的width、height全部与容器相同
     * @param height 容器的高度
     */
    public void layoutPage(View v, int position, int width, int height) {
        if (position == 0) {
            layoutCurPage(v, width, height);
        } else if (position > 0) {
            layoutNextPage(v, width, height);
        } else {
            layoutPrevPage(v, width, height);
        }
    }

    private void layoutCurPage(View v, int width, int height) {
        v.layout(0, 0, width, height);
    }

    private void layoutPrevPage(View v, int width, int height) {
        switch (mode) {
            case NONE:
            case COVER:
            case SIMULATE:
            case HORIZONTAL_SLIP:
                v.layout(-width, 0, 0, height);
                break;
            case SLIP:
                v.layout(0, -height, width, 0);
                break;
        }
    }

    private void layoutNextPage(View v, int width, int height) {
        switch (mode) {
            case NONE:
            case COVER:
            case SIMULATE:
            case HORIZONTAL_SLIP:
                v.layout(width, 0, width * 2, height);
                break;
            case SLIP:
                v.layout(0, height, width, height * 2);
                break;
        }
    }

    public GestureDetector getGestureDetector() {
        return null;
    }
}

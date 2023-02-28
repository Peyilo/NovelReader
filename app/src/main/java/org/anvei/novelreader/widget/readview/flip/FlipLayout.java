package org.anvei.novelreader.widget.readview.flip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

/**
 * 提供翻页动画的布局 <br/>
 * 当前支持的翻页模式为覆盖模式
 */
public abstract class FlipLayout extends ViewGroup {

    private static final String TAG = "FlipLayout";

    // 弹性滑动对象，实现过渡效果的滑动
    private final Scroller scroller;

    private final List<OnFlipListener> onFlipListenerList = new ArrayList<>();

    // 商定这个滑动是否有效的距离
    private static final int minFlipDistance = 50;
    // 翻页动画时间
    private int flipDuration = 450;

    private float startX = 0;

    private PageDirection initialDirection = PageDirection.NONE;    // 初始滑动方向
    // 滑动的view
    private View scrolledView;
    private int curPagePointer = 0;

    // 绘制阴影相关参数
    private boolean enableShadow = false;
    private final Paint shadowPaint;
    private int shadowWidth = 0;
    private final int[] gradientColors = {0x8F000000, 0x00000000};
    private final float[] gradientPositions = {0.0f, 1.0f};

    public FlipLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(context);
        shadowPaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode =MeasureSpec.getMode(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
        Log.d(TAG, "onMeasure: parentWidth = " + widthSize);
        Log.d(TAG, "onMeasure: parentHeight = " + heightSize);
        // 设置view的测量大小
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(0);
            if (child.getVisibility() != GONE) {
                int childWidth = Math.max(0, widthSize - getPaddingLeft() - getPaddingRight());
                int childHeight = Math.max(0, heightSize - getPaddingTop() - getPaddingBottom());
                child.measure(MeasureSpec.makeMeasureSpec(childWidth, widthMode),
                        MeasureSpec.makeMeasureSpec(childHeight, heightMode));
                Log.d(TAG, "onMeasure: width = " + childWidth);
                Log.d(TAG, "onMeasure: height = " + childHeight);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int height = child.getMeasuredHeight();
            int width = child.getMeasuredWidth();
            // 子view全部叠放在一起，但是最顶层的子view被设置了scrollX，所以滑出了屏幕
            child.layout(getPaddingLeft(), getPaddingTop(),
                    getPaddingLeft() + width, getPaddingTop() + height);
            // curPageIndex之前的页面全部需要滑动到主视图之外
            if (i > curPagePointer) {
                child.scrollTo(-getPrevScrollWidth(), 0);
            }
        }
    }

    private int getPrevScrollWidth() {
        if (enableShadow) {
            return getWidth() + getShadowWidth();
        }
        return getWidth();
    }

    /**
     * 获取指定的子view
     *
     * @param offset 偏移量，offset为0则返回当前显示的子view，offset为-1时返回上一页子view
     */
    protected View getPageView(int offset) {
        return getChildAt(curPagePointer - offset);
    }

    // 在当前显示view之后的子view的数量
    protected int getNextViewCount() {
        return curPagePointer;
    }

    // 在当前显示view之前的子view数量
    protected int getPrevViewCount() {
        return getChildCount() - curPagePointer - 1;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getChildCount() > 0 && isScrolling()) {
            // 如果还在执行翻页动画，就直接强制结束
            scroller.forceFinished(true);
            scrolledView.scrollTo(scroller.getFinalX(), scroller.getFinalY());
        }
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            startX = ev.getX();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 父类的方法中包含了点击监听相关代码，需要调用以支持点击监听
        final float distance = startX - event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // 计算滑动距离，先确定本系列事件中最初的滑动方向，并确定被拖动的子view
                if (initialDirection == PageDirection.NONE) {
                    if (distance > 0 && hasNextPage() && getNextViewCount() > 0) {
                        initialDirection = PageDirection.TO_NEXT;
                        scrolledView = getPageView(0);
                    } else if (distance < 0 && hasPrePage() && getPrevViewCount() > 0) {
                        initialDirection = PageDirection.TO_PREV;
                        scrolledView = getPageView(-1);
                    } else {
                        break;
                    }
                }
                // 控制实时滑动效果
                if (initialDirection == PageDirection.TO_NEXT) {
                    if (distance > 0) {
                        scrolledView.scrollTo((int) distance, 0);
                    }
                } else {
                    if (distance < 0) {
                        scrolledView.scrollTo(getPrevScrollWidth() + (int) distance, 0);
                    }
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (initialDirection != PageDirection.NONE) {
                    int scrollX = scrolledView.getScrollX();
                    int dx;
                    // 如果滑动的距离没有超过minFlipDistance，就不进行翻页，应将页面复位
                    // 最终页面的滑动方向
                    PageDirection endDirection;
                    if (Math.abs(distance) < minFlipDistance) {
                        endDirection = PageDirection.NONE;
                        if (initialDirection == PageDirection.TO_NEXT) {
                            dx = -scrollX;
                        } else {
                            dx = getPrevScrollWidth() - scrollX;
                        }
                    } else { // 完成翻页，并且加载新的视图
                        if (distance > 0) {
                            dx = getPrevScrollWidth() - scrollX;
                            endDirection = PageDirection.TO_NEXT;
                        } else {
                            dx = -scrollX;
                            endDirection = PageDirection.TO_PREV;
                        }
                    }
                    scroller.startScroll(scrollX, 0, dx, 0, flipDuration);
                    postInvalidate();
                    initialDirection = PageDirection.NONE;
                    if (endDirection == PageDirection.TO_NEXT) {
                        for (OnFlipListener onFlipListener : onFlipListenerList) {
                            onFlipListener.onNext();
                        }
                        // 将距离当前页面最远的页面移除，再进行复用
                        View convertView = getPageView(-getPrevViewCount());
                        View newView = getView(convertView, endDirection);
                        if (newView != null) {
                            removeView(convertView);
                            newView.scrollTo(0, 0);
                            addView(newView, 0);
                        }
                    } else if (endDirection == PageDirection.TO_PREV) {
                        for (OnFlipListener onFlipListener : onFlipListenerList) {
                            onFlipListener.onPre();
                        }
                        View convertView = getPageView(getNextViewCount());
                        View newView = getView(convertView, endDirection);
                        if (newView != null) {
                            removeView(convertView);
                            newView.scrollTo(getPrevScrollWidth(), 0);
                            addView(newView);
                        }
                    }
                    return true;
                }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrolledView.scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (enableShadow && scrolledView != null) {
            // 绘制阴影
            int x = getWidth() - scrolledView.getScrollX();
            int min = -getShadowWidth();
            if (x > min && x < getWidth()) {
                LinearGradient gradient = new LinearGradient(x, 0, x + getShadowWidth(), 0,
                        gradientColors, gradientPositions, Shader.TileMode.CLAMP);
                shadowPaint.setShader(gradient);
                canvas.drawRect(x, 0, x + getShadowWidth(), getHeight(), shadowPaint);
            }
        }
    }

    protected abstract boolean hasNextPage();

    protected abstract boolean hasPrePage();

    protected abstract View getView(View convertView, PageDirection direction);

    public void addOnFlipOverListener(OnFlipListener onFlipListener) {
        onFlipListenerList.add(onFlipListener);
    }

    public void removeFlipOverListener(OnFlipListener onFlipListener) {
        onFlipListenerList.remove(onFlipListener);
    }

    public void removeFlipOverListener(int index) {
        onFlipListenerList.remove(index);
    }

    /**
     * 翻页监听接口
     */
    public interface OnFlipListener {
        /**
         * 翻向下一页时的回调函数，该方法会在getView之前被调用
         */
        void onNext();

        /**
         * 翻向上一页时的回调函数，该方法会在getView之前被调用
         */
        void onPre();
    }

    protected final boolean isScrolling() {
        return !scroller.isFinished();
    }

    public int getFlipDuration() {
        return flipDuration;
    }

    public void setFlipDuration(int flipDuration) {
        this.flipDuration = flipDuration;
    }

    public void setCurPagePointer(int curPagePointer) {
        this.curPagePointer = curPagePointer;
    }

    public void setShadowWidth(int shadowWidth) {
        this.shadowWidth = shadowWidth;
    }

    public int getShadowWidth() {
        return shadowWidth;
    }

    public void enableShadow(boolean enable) {
        this.enableShadow = enable;
    }
}

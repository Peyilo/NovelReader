package org.anvei.novelreader.widget.read;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseReadView<E extends View> extends ViewGroup {

    private static final String TAG = "BaseReadView";

    // 弹性滑动对象，实现过渡效果的滑动
    private final Scroller scroller;

    private final List<OnFlipOverListener> onFlipOverListenerList = new ArrayList<>();

    // 商定这个滑动是否有效的距离
    private static final int minScrollDistance = 0;
    // 翻页动画时间
    private static final int flipOverTime = 250;

    private final int screenWidth;


    private float startX = 0;
    private float curX = 0;

    private Direction initialDirection = Direction.NONE;    // 初始滑动方向
    private Direction curDirection = Direction.NONE;        // 实时滑动方向
    private Direction endDirection = Direction.NONE;        // 最终页面的滑动方向
    // 滑动的view
    private E scrolledView;

    protected E preView;
    protected E curView;
    protected E nextView;

    public BaseReadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initChildView();
        scroller = new Scroller(context);
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
    }

    // 在这里完成子view的初始化
    protected void initChildView() {
        this.nextView = createChildView();
        this.curView = createChildView();
        this.preView = createChildView();
        addView(nextView);
        addView(curView);
        addView(preView);
        // 默认将最上层的view滑动到边缘（用于查看上一页）
        preView.scrollTo(-screenWidth, 0);
    }

    protected abstract E createChildView();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        // 设置view的测量大小
        setMeasuredDimension(widthSize, heightSize);
        for (int i = 0; i < getChildCount(); i++) {
            // 子view的MeasureSpec和FlipperLayout相同
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int height = child.getMeasuredHeight();
            int width = child.getMeasuredWidth();
            // 子view全部叠放在一起，但是最顶层的子view被设置了scrollX，所以滑出了屏幕
            child.layout(0, 0, width, height);
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isScrolling() && ev.getAction() == MotionEvent.ACTION_DOWN) {
            startX = ev.getX();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        if (isScrolling()) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float oldX = curX;
                curX = event.getX();
                // 计算滑动距离，先确定本系列事件中最初的滑动方向，并确定被拖动的子view
                final float distance = startX - curX;
                if (initialDirection == Direction.NONE) {
                    if (distance > 0 && hasNextPage()) {
                        initialDirection = Direction.TO_LEFT;
                        scrolledView = curView;
                    } else if (distance < 0 && hasPrePage()) {
                        initialDirection = Direction.TO_RIGHT;
                        scrolledView = preView;
                    } else {
                        break;
                    }
                }
                // 确定本次MOVE事件相对于上次MOVE事件的滑动方向
                float relatedDistance = oldX - curX;
                if (relatedDistance > 0) {
                    curDirection = Direction.TO_LEFT;
                } else if (relatedDistance < 0) {
                    curDirection = Direction.TO_RIGHT;
                }
                if (initialDirection == Direction.TO_LEFT) {
                    if (distance > 0) {
                        scrolledView.scrollTo((int) distance, 0);
                    }
                } else {
                    if (distance < 0) {
                        scrolledView.scrollTo(screenWidth + (int) distance, 0);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (initialDirection != Direction.NONE && curDirection != Direction.NONE) {
                    int scrollX = scrolledView.getScrollX();
                    int dx;
                    // 最初的翻页方向和最终翻页方向不同，所以不应翻页，应当将视图复位
                    if (initialDirection != curDirection) {
                        endDirection = Direction.NONE;
                        if (initialDirection == Direction.TO_LEFT) {
                            dx = -scrollX;
                        } else {
                            dx = screenWidth - scrollX;
                        }
                    } else { // 完成翻页，并且加载新的视图
                        endDirection = initialDirection;
                        if (initialDirection == Direction.TO_LEFT) {
                            dx = screenWidth - scrollX;
                        } else {
                            dx = -scrollX;
                        }
                    }
                    scroller.startScroll(scrollX, 0, dx, 0, flipOverTime);
                    postInvalidate();
                    initialDirection = Direction.NONE;
                    curDirection = Direction.NONE;
                }
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrolledView.scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        } else if (endDirection != Direction.NONE) {
            if (endDirection == Direction.TO_LEFT) {
                for (OnFlipOverListener onFlipOverListener : onFlipOverListenerList) {
                    onFlipOverListener.onNext();
                }
                removeView(preView);
                E cacheView = preView;
                preView = curView;
                curView = nextView;
                nextView = getView(cacheView, endDirection);
                if (nextView != null) {
                    nextView.scrollTo(0, 0);
                    addView(nextView, 0);
                }
            } else {
                for (OnFlipOverListener onFlipOverListener : onFlipOverListenerList) {
                    onFlipOverListener.onPre();
                }
                removeView(nextView);
                E cacheView = nextView;
                nextView = curView;
                curView = preView;
                preView = getView(cacheView, endDirection);
                if (preView != null) {
                    preView.scrollTo(-screenWidth, 0);
                    addView(preView);
                }
            }
            endDirection = Direction.NONE;
        }
    }

    protected abstract boolean hasNextPage();

    protected abstract boolean hasPrePage();

    protected abstract E getView(E cacheView, Direction direction);

    public void addOnFlipOverListener(OnFlipOverListener onFlipOverListener) {
        onFlipOverListenerList.add(onFlipOverListener);
    }

    public void removeFlipOverListener(OnFlipOverListener onFlipOverListener) {
        onFlipOverListenerList.remove(onFlipOverListener);
    }

    public void removeFlipOverListener(int index) {
        onFlipOverListenerList.remove(index);
    }

    // 翻页监听
    public interface OnFlipOverListener {

        void onNext();

        void onPre();
    }

    protected boolean isScrolling() {
        return !scroller.isFinished();
    }
}

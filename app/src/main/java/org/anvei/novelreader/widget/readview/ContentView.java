package org.anvei.novelreader.widget.readview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.anvei.novelreader.R;
import org.anvei.novelreader.widget.readview.page.PageData;
import org.anvei.novelreader.widget.readview.page.PageConfig;

/**
 * 负责绘制章节内容主题内容
 */
public class ContentView extends View {

    private static final String TAG = "ContentView";
    private PageData pageData;
    private Bitmap cache;
    private boolean needRecreate = false;
    private PageConfig pageConfig;

    public ContentView(Context context) {
        this(context, null);
    }

    public ContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    /**
     * 初始化PageConfig的配置
     */
    private void initPageConfig() {
        TypedArray typedArray = getContext().obtainStyledAttributes(R.styleable.ContentView);
        pageConfig.setContentPaddingTop(getPaddingTop());
        pageConfig.setContentPaddingBottom(getPaddingBottom());
        pageConfig.setContentPaddingLeft(getPaddingLeft());
        pageConfig.setContentPaddingRight(getPaddingRight());
        // TODO: 有bug
        // 初始化contentView的文字配置
        pageConfig.setTitleSize(typedArray.getDimension(
                R.styleable.ContentView_titleSize, 72F));
        pageConfig.setTextSize(typedArray.getDimension(
                R.styleable.ContentView_textSize, 54F));
        pageConfig.setTitleColor(typedArray.getColor(
                R.styleable.ContentView_titleColor, Color.BLACK));
        pageConfig.setTextColor(typedArray.getColor(
                R.styleable.ContentView_textColor, Color.parseColor("#2B2B2B")));
        // 初始化文字间距
        pageConfig.setTextMargin(typedArray.getDimension(
                R.styleable.ContentView_textMargin, 0F));
        pageConfig.setLineMargin(typedArray.getDimension(
                R.styleable.ContentView_lineMargin, 25F));
        pageConfig.setParaMargin(typedArray.getDimension(
                R.styleable.ContentView_paraMargin, 35F));
        pageConfig.setTitleMargin(typedArray.getDimension(
                R.styleable.ContentView_titleMargin, 150F));
        typedArray.recycle();
    }

    public void setPage(PageData pageData) {
        if (this.pageData == null || !this.pageData.equals(pageData)) {
            this.pageData = pageData;
            requestReCreate();
        }
    }

    public void requestReCreate() {
        Log.d(TAG, "requestReCreate: called");
        needRecreate = true;
        postInvalidate();
    }

    public void setPageConfig(PageConfig pageConfig) {
        this.pageConfig = pageConfig;
        initPageConfig();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        pageConfig.initContentDimen(getWidth(), getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (pageData != null) {
            if (needRecreate) {
                needRecreate = false;
                cache = pageConfig.getPageFactory().createPage(this.pageData);
            } else if (cache == null) {
                cache = pageConfig.getPageFactory().createPage(this.pageData);
            }
            canvas.drawBitmap(cache, 0F, 0F, pageConfig.getBitmapPaint());
        } else {
            Log.d(TAG, "onDraw: needRecreate " + needRecreate);
            canvas.drawBitmap(pageConfig.getBackground(), 0F, 0F, pageConfig.getBitmapPaint());
        }
    }
}

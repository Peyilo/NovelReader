package org.anvei.novelreader.widget.readview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.anvei.novelreader.widget.readview.page.Page;
import org.anvei.novelreader.widget.readview.page.PageConfig;

/**
 * 负责绘制章节内容主题内容
 */
public class ContentView extends View {

    private static final String TAG = "ContentView";
    private Page page;
    private Bitmap cache;
    private boolean needRecreate = false;

    private PageConfig pageConfig;

    public ContentView(Context context) {
        super(context);
    }

    public ContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPage(Page page) {
        if (this.page == null || !this.page.equals(page)) {
            this.page = page;
            requestReCreate();
        }
    }

    public void requestReCreate() {
        Log.d(TAG, "requestReCreate: called");
        needRecreate = true;
        postInvalidate();
    }

    public PageConfig getPageConfig() {
        return pageConfig;
    }

    public void setPageConfig(PageConfig pageConfig) {
        this.pageConfig = pageConfig;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        pageConfig.setWidth(getWidth());
        pageConfig.setHeight(getHeight());
        if (page != null) {
            if (needRecreate) {
                needRecreate = false;
                cache = pageConfig.getPageFactory().createPage(this.page);
            } else if (cache == null) {
                cache = pageConfig.getPageFactory().createPage(this.page);
            }
            canvas.drawBitmap(cache, 0F, 0F, pageConfig.getBitmapPaint());
        } else {
            Log.d(TAG, "onDraw: needRecreate " + needRecreate);
            canvas.drawBitmap(pageConfig.getBackground(), 0F, 0F, pageConfig.getBitmapPaint());
        }
    }
}

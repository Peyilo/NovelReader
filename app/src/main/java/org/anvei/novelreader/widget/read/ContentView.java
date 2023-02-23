package org.anvei.novelreader.widget.read;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.anvei.novelreader.widget.read.page.Page;
import org.anvei.novelreader.widget.read.page.PageConfig;

/**
 * 负责绘制章节内容主题内容
 */
public class ContentView extends View {

    private static final String TAG = "ContentView";
    private Page page;
    private Bitmap bitmap;
    private boolean needRecreate = false;

    private PageConfig pageConfig;

    public ContentView(Context context) {
        super(context);
    }

    public ContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPage(Page page) {
        this.page = page;
        requestReCreate();
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
        if (page != null) {
            if (pageConfig.width == 0 || pageConfig.height == 0) {
                pageConfig.width = getWidth();
                pageConfig.height = getHeight();
            }
            if (needRecreate) {
                needRecreate = false;
                bitmap = pageConfig.pageFactory.createPage(this.page);
            } else if (bitmap == null) {
                bitmap = pageConfig.pageFactory.createPage(this.page);
            }
            canvas.drawBitmap(bitmap, 0F, 0F, pageConfig.paint);
        }
    }
}

package org.anvei.novelreader.widget.read;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import org.anvei.novelreader.widget.read.page.Page;
import org.anvei.novelreader.widget.read.page.PageConfig;

/**
 * 负责绘制章节内容主题内容
 */
public class ContentView extends View {

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
            if (bitmap == null || needRecreate) {
                if (pageConfig.width == 0 || pageConfig.height == 0) {
                    pageConfig.width = getWidth();
                    pageConfig.height = getHeight();
                }
                bitmap = pageConfig.pageFactory.createPage(this.page);
                if (needRecreate) {
                    needRecreate = false;
                }
            }
            canvas.drawBitmap(bitmap, 0F, 0F, pageConfig.paint);
        }
    }
}

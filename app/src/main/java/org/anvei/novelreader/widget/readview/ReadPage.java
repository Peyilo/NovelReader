package org.anvei.novelreader.widget.readview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

import org.anvei.novelreader.widget.readview.page.Page;
import org.anvei.novelreader.widget.readview.page.PageConfig;

/**
 * 该视图提供了页眉自定义、页脚自定义API
 */
public class ReadPage extends ViewGroup {

    public static final int NONE = -1;      // 设置为NONE表示无

    private View layout;
    private ContentView content;            // 章节内容视图
    private View header;                    // 页眉视图
    private View footer;                    // 页脚视图

    public ReadPage(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).layout(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * 初始化ReadPage的子view
     */
    public void initLayout(@LayoutRes int layoutId, @IdRes int contentId) {
        initLayout(layoutId, contentId, NONE, NONE);
    }

    public void initLayout(@LayoutRes int layoutId, @IdRes int contentId,
        @IdRes int headerId, @IdRes int footerId) {
        layout = LayoutInflater.from(getContext()).inflate(layoutId, this);
        assert contentId != NONE;
        content = layout.findViewById(contentId);
        if (headerId != NONE) {
            header = layout.findViewById(contentId);
        }
        if (footerId != NONE) {
            footer = layout.findViewById(footerId);
        }
    }

    public View getLayout() {
        return layout;
    }

    public ContentView getContent() {
        return content;
    }

    public View getHeader() {
        return header;
    }

    public View getFooter() {
        return footer;
    }

    void setPageConfig(PageConfig pageConfig) {
        this.content.setPageConfig(pageConfig);
    }

    public void setPage(Page page) {
        content.setPage(page);
    }
}

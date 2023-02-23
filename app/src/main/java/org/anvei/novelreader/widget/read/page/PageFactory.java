package org.anvei.novelreader.widget.read.page;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class PageFactory {

    private final PageConfig pageConfig;
    public PageFactory(PageConfig pageConfig) {
        this.pageConfig = pageConfig;
    }

    public List<Page> splitPage(String content) {
        if (TextUtils.isEmpty(content)) {
            content = "  带加载中...";
        }
        List<Page> pages = new ArrayList<>();
        String[] splits = content.split("\\n");
        final int WIDTH = pageConfig.width - pageConfig.paddingLeft - pageConfig.paddingRight;
        final int HEIGHT = pageConfig.height - pageConfig.paddingTop - pageConfig.paddingBottom
                - pageConfig.headerHeight - pageConfig.footerHeight;
        int remainedHeight = HEIGHT - pageConfig.titleHeight; // 剩余的高度
        int remainedWidth = WIDTH; // 剩余的宽度
        float textSize = pageConfig.getTextSize();   // 字符大小
        Paint paint = pageConfig.getTextPaint();
        Page page = new Page();
        Line line = new Line();
        boolean isFirst = true;
        float dimen;
        for (String split : splits) {
            // 去除空行
            split = split.stripTrailing();
            if (split.length() == 0) {
                continue;
            }
            for (int i = 0; i < split.length(); i++) {
                char c = split.charAt(i);
                if (c == '\r') {
                    continue;
                }
                if (remainedWidth < (dimen = paint.measureText(String.valueOf(c)))) {
                    // 剩余的宽度已经不足以再填充当前字符，所以需要重新new一个Line
                    page.add(line);
                    line = new Line();
                    remainedWidth = WIDTH;
                    remainedHeight -= textSize + pageConfig.lineMargin;
                    if (remainedHeight < textSize) {
                        if (isFirst) {
                            page.setIsFirstPage(true);
                            isFirst = false;
                        }
                        pages.add(page);
                        page = new Page();
                        remainedHeight = HEIGHT;
                    }
                }
                line.add(c);
                // 段落的最后一个字符
                if (i == split.length() - 1) {
                    line.setIsParaEndLine(true);
                    page.add(line);
                    line = new Line();
                    remainedWidth = WIDTH;
                    remainedHeight -= textSize + pageConfig.lineMargin + pageConfig.paraMargin;
                    if (remainedHeight < textSize) {
                        if (isFirst) {
                            page.setIsFirstPage(true);
                            isFirst = false;
                        }
                        pages.add(page);
                        page = new Page();
                        remainedHeight = HEIGHT;
                    }
                    break;
                }
                remainedWidth -= dimen + pageConfig.textMargin;
            }
        }
        if (pages.size() == 0) {
            if (isFirst) {
                page.setIsFirstPage(true);
            }
            pages.add(page);
        }
        if (page.size() != 0) {
            pages.add(page);
        }
        return pages;
    }

    public void drawPage(Page page, Canvas canvas) {
        drawPage(page, canvas, pageConfig.getTextPaint());
    }

    public void drawPage(Page page, Canvas canvas, Paint paint) {
        int textSize = pageConfig.getTextSize();
        int lineHeight = textSize + pageConfig.lineMargin;
        int base = pageConfig.paddingTop + textSize;
        int left;
        for (int i = 0; i < page.size(); i++) {
            Line line = page.get(i);
            left = pageConfig.paddingLeft;
            for (int j = 0; j < line.size(); j++) {
                char c = line.get(j);
                canvas.drawText(String.valueOf(c), left, base, paint);
                left += paint.measureText(String.valueOf(c)) + pageConfig.textMargin;
            }
            if (line.isParaEndLine()) {
                base += pageConfig.paraMargin;
            }
            base += lineHeight;
        }
    }

    public Bitmap createPage(Page page) {
        return createPage(page, pageConfig.getTextPaint());
    }

    public Bitmap createPage(Page page, Paint paint) {
        // 在背景上绘制文字
        Bitmap res = pageConfig.getBackground().copy(Bitmap.Config.RGB_565, true);
        Canvas canvas = new Canvas(res);
        drawPage(page, canvas, paint);
        return res;
    }


}

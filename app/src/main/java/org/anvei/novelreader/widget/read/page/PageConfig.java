package org.anvei.novelreader.widget.read.page;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;

public class PageConfig {

    public PageConfig() {
        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
    }

    public Paint paint = new Paint();
    public int width;
    public int height;

    // 内边距
    public int paddingLeft = 30;
    public int paddingBottom = 40;
    public int paddingTop = 20;
    public int paddingRight = 30;

    private int textColor = Color.parseColor("#4a453a");
    private final Paint textPaint;
    private int textSize = 55;

    public int textMargin = 5;       // 字符间隔
    public int lineMargin = 25;      // 行间隔
    public int paraMargin = 20;      // 段落间隔

    public int bgColor = Color.WHITE;
    private Bitmap background;
    public PageFactory pageFactory;

    public Bitmap getBackground() {
        if (background == null) {
            background = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            background.eraseColor(bgColor);
        }
        return background;
    }

    public void setBackground(Bitmap background) {
        this.background = background;
    }

    public Paint getTextPaint() {
        return textPaint;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        textPaint.setTextSize(textSize);
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        textPaint.setColor(textColor);
    }
}

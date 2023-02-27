package org.anvei.novelreader.widget.readview.bean;

// 表示一个从startIndex到endIndex-1的区间（不包含endIndex）
public class IndexBean {
    private int startIndex;
    private int endIndex;

    public IndexBean(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public int size() {
        return endIndex - startIndex;
    }
}

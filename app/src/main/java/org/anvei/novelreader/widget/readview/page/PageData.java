package org.anvei.novelreader.widget.readview.page;

import java.util.ArrayList;
import java.util.List;

public class PageData {

    private final int chapIndex;
    private final int pageIndex;

    private final List<Line> lineList;

    private String title;

    private boolean isFirstPage = false;

    public PageData(int chapIndex, int pageIndex) {
        this.chapIndex = chapIndex;
        this.pageIndex = pageIndex;
        this.lineList = new ArrayList<>();
    }

    public void set(int index, Line line) {
        lineList.set(index, line);
    }

    public void add(Line line) {
        lineList.add(line);
    }

    public Line get(int index) {
        return lineList.get(index);
    }

    public int size() {
        return lineList.size();
    }

    public void addLine(String string) {
        Line line = new Line();
        line.add(string);
        add(line);
    }

    public int getChapIndex() {
        return chapIndex;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public boolean isFirstPage() {
        return isFirstPage;
    }

    public void setIsFirstPage(boolean firstPage) {
        isFirstPage = firstPage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

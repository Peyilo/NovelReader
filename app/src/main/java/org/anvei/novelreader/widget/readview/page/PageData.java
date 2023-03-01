package org.anvei.novelreader.widget.readview.page;

import java.util.ArrayList;
import java.util.List;

public class PageData {

    private final List<Line> lineList;

    private String title;

    private boolean isFirstPage = false;
    private boolean isLoading = false;

    public PageData() {
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

    public boolean isFirstPage() {
        return isFirstPage;
    }

    public void setIsFirstPage(boolean firstPage) {
        isFirstPage = firstPage;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setIsLoading(boolean loading) {
        isLoading = loading;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

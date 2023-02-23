package org.anvei.novelreader.widget.read.page;

import java.util.ArrayList;
import java.util.List;

public class Page {

    private final List<Line> lineList;

    public Page() {
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

}

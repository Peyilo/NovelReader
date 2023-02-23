package org.anvei.novelreader.bean;

import androidx.annotation.IntRange;

import java.util.ArrayList;
import java.util.List;

public class Book {

    private final List<Chapter> chapters;
    private final String source;

    private String author;
    private String title;
    private String what;

    public Book(String source, int size) {
        this.source = source;
        chapters = new ArrayList<>();
    }

    // 是否具有分卷
    public boolean hasMultiVolume() {
        return false;
    }

    // 获取指定index的章节
    public Chapter getChapter(@IntRange(from = 1) int index) {
        return chapters.get(index - 1);
    }

    // 获取章节总数
    public int getChapterCount() {
        return chapters.size();
    }

    public void addChapter(Chapter chapter) {
        chapters.add(chapter);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public String getSource() {
        return source;
    }
}

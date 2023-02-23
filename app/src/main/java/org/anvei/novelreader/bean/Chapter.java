package org.anvei.novelreader.bean;

import org.anvei.novelreader.widget.read.page.Page;

import java.util.List;

/**
 * 章节有三种状态：
 * 1. 未加载
 * 2. 未分页
 * 3. 初始化完成
 */
public class Chapter {
    private final String title;
    private final int index;
    private String content;

    private String what;
    private List<Page> pages;

    private Status status = Status.NO_CONTENT;

    public enum Status {
        NO_CONTENT,     // 章节还没有加载
        NO_SPLIT,       // 章节完成了加载但是没有完成分页
        IS_LOADING,     // 在加载中
        INITIALIZED     // 章节完成了加载和分页
    }

    public Chapter(int index, String title) {
        this.title = title;
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public int getIndex() {
        return index;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (status == Status.NO_CONTENT) {
            status = Status.NO_SPLIT;
        }
        this.content = content;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        if (pages == null) {
            status = Status.NO_SPLIT;
        }
        if (pages != null && status != Status.INITIALIZED) {
            status = Status.INITIALIZED;
        }
        this.pages = pages;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

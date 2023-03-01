package org.anvei.novelreader.widget.readview.bean;

import org.anvei.novelreader.widget.readview.page.PageData;

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
    private List<PageData> pageData;

    private Status status = Status.NO_LOAD;

    public enum Status {
        NO_LOAD,        // 章节还没有加载
        NO_SPLIT,       // 章节完成了加载但是没有完成分页
        IS_LOADING,     // 在加载中
        IS_SPLITTING,   // 在切割中
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
        this.content = content;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public List<PageData> getPages() {
        return pageData;
    }

    public void setPages(List<PageData> pageData) {
        this.pageData = pageData;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

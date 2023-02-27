package org.anvei.novelreader.widget.readview.bean;

import androidx.annotation.Nullable;

public class Volume {
    private final String title;
    private final IndexBean indexBean;

    private String what;

    public Volume(@Nullable String title, IndexBean indexBean) {
        this.indexBean = indexBean;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public IndexBean getIndexBean() {
        return indexBean;
    }

    public int size() {
        return indexBean.size();
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }
}

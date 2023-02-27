package org.anvei.novelreader.widget.readview.loader;

public abstract class AbsBookLoader implements BookLoader {

    private String link;

    private String sourceName;          // 小说加载器书源名称
    private final int uid;              // 小说加载器唯一UID标识

    public AbsBookLoader(String sourceName, int uid) {
        this.sourceName = sourceName;
        this.uid = uid;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public int getUid() {
        return uid;
    }

}

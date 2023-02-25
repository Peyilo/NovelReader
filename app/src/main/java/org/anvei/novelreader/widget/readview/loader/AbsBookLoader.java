package org.anvei.novelreader.widget.readview.loader;

public abstract class AbsBookLoader implements BookLoader {

    private String link;

    private String sourceName;

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
}

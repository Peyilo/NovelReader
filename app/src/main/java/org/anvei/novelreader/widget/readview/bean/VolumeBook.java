package org.anvei.novelreader.widget.readview.bean;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import java.util.List;

/**
 * 在Book的基础之上添加了分卷功能
 */
public class VolumeBook extends Book {

    private final List<Volume> volumeList;

    public VolumeBook(String source, int size, @NonNull List<Volume> volumeList) {
        super(source, size);
        this.volumeList = volumeList;
    }

    @Override
    public boolean hasMultiVolume() {
        return true;
    }

    // 获取指定分卷
    public Volume getVolume(@IntRange(from = 1) int index) {
        return volumeList.get(index - 1);
    }

    /**
     * 给定一个章节序号，返回该章节所属的分卷序号，如果返回的为-1表示该index不属于任何一个分卷
     */
    public int getVolumeIndex(int index) {
        int res = -1;
        for (int i = 0; i < volumeList.size(); i++) {
            IndexBean indexBean = volumeList.get(i).getIndexBean();
            if (index >= indexBean.getStartIndex() && index < indexBean.getEndIndex()) {
                res = i + 1;
                break;
            }
        }
        return res;
    }


}

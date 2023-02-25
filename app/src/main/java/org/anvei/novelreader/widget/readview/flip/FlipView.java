package org.anvei.novelreader.widget.readview.flip;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class FlipView extends FlipLayout {

    public FlipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCurPagePointer(1);
    }

    @Override
    protected boolean hasNextPage() {
        return true;
    }

    @Override
    protected boolean hasPrePage() {
        return true;
    }

    @Override
    protected View getView(View convertView, PageDirection direction) {
        return convertView;
    }
}

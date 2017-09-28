package com.thinkjoy.zhthinkjoyfacedetect;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by thinkjoy on 17-7-27.
 */

public class SameSizeImageView extends ImageView {
    public SameSizeImageView(Context context) {
        super(context);
    }
    public SameSizeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = Math.round(width * 1);
        setMeasuredDimension(width, height);
    }

}

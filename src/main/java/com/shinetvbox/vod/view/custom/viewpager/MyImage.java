package com.shinetvbox.vod.view.custom.viewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.shinetvbox.vod.utils.KtvLog;

/**
 * Created by hrblaoj on 2019/1/14.
 */

@SuppressLint("AppCompatCustomView")
public class MyImage extends ImageView {


    public MyImage(Context context) {
        super(context);
    }

    public MyImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rec=new Rect();
        this.getLocalVisibleRect(rec);
        KtvLog.d("onDraw btnVideo33 w is " + rec.width());
    }


//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//        Rect rec=new Rect();
//        this.getLocalVisibleRect(rec);
//        KtvLog.d("onLayout btnVideo33 w is " + rec.width());
//    }
}

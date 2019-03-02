package com.shinetvbox.vod.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class MyRecyclerView extends RecyclerView {

    private int mSelectedPosition = 0;

    public MyRecyclerView(Context context) {
        super( context );
        init();
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super( context, attrs );
        init();
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super( context, attrs, defStyle );
        init();
    }

    private void init() {
        //启用子视图排序功能
        setChildrenDrawingOrderEnabled( true );
    }

    @Override
    public void onDraw(Canvas c) {
        //mSelectedPosition = getChildAdapterPosition(getFocusedChild());
        mSelectedPosition = indexOfChild( getFocusedChild() );
        super.onDraw( c );
    }


    /**
     * 使子view位置在上层
     *
     * @param childCount
     */
    //@Override
    //public void bringChildToFront(View child) { //重写，不调用父类方法；获取child的实际position
    // // super.bringChildToFront(child);
    // mSelectedPosition = indexOfChild(child);
    // invalidate();
    //}
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        int position = mSelectedPosition;
        if (position < 0) {
            return i;
        } else {
            if (i == childCount - 1) {
                if (position > i) {
                    position = i;
                }
                return position;
            }
            if (i == position) {
                return childCount - 1;
            }
        }
        return i;
    }
}
package com.shinetvbox.vod.view.custom.viewpager;

/**
 * Created by Administrator on 2018/3/14.
 */

import android.content.Context;
import android.widget.RelativeLayout;

public class MyLtViewPager extends LtViewPager {

    public MyLtViewPager(Context context,RelativeLayout body) {
        super(context);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) body.getLayoutParams();
        RelativeLayout.LayoutParams par = new RelativeLayout.LayoutParams(params.width-10, params.height);
        par.leftMargin = 0;
        par.topMargin = 0;
        this.setLayoutParams(par);
        body.addView(this);
    }
}
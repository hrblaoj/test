package com.shinetvbox.vod.utils;

import android.graphics.Point;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class AnimationUtil {
    public static final int TYPE_TOP = 0;
    public static final int TYPE_BOTTOM = 1;
    public static final int TYPE_LEFT = 2;
    public static final int TYPE_RIGHT = 3;
    public static final int TYPE_SCALE = 4;
    public static final int TYPE_SCALE_CENTER = 5;
    public static final int TYPE_SCALE_CUSTOM = 6;

    public static final int TIME_ANIMATION = 300;

    public static void setViewVisible(View vi, boolean isShow, int type) {
        setViewVisible(vi,isShow,type,null);
    }
    public static void setViewVisible(View vi, boolean isShow, int type, Point point) {
        if(isShow){
            if(vi.getVisibility() == View.VISIBLE) return;
        }else {
            if(vi.getVisibility() == View.GONE || vi.getVisibility() == View.INVISIBLE) return;
        }
        switch (type) {
            case TYPE_TOP:
                if (isShow) {
                    vi.setVisibility( View.VISIBLE );
                    vi.setAnimation( topShow() );
                } else {
                    vi.setVisibility( View.GONE );
                    vi.setAnimation( topHide() );
                }
                break;
            case TYPE_BOTTOM:
                if (isShow) {
                    vi.setVisibility( View.VISIBLE );
                    vi.setAnimation( bottomShow() );
                } else {
                    vi.setVisibility( View.GONE );
                    vi.setAnimation( bottomHide() );
                }
                break;
            case TYPE_LEFT:
                if (isShow) {
                    vi.setVisibility( View.VISIBLE );
                    vi.setAnimation( leftShow() );
                } else {
                    vi.setVisibility( View.GONE );
                    vi.setAnimation( leftHide() );
                }
                break;
            case TYPE_RIGHT:
                if (isShow) {
                    vi.setVisibility( View.VISIBLE );
                    vi.setAnimation( rightShow() );
                } else {
                    vi.setVisibility( View.GONE );
                    vi.setAnimation( rightHide() );
                }
                break;
            case TYPE_SCALE:
                if (isShow) {
                    vi.setVisibility( View.VISIBLE );
                    vi.setAnimation( scaleShow() );
                } else {
                    vi.setVisibility( View.GONE );
                    vi.setAnimation( scaleHide() );
                }
                break;
            case TYPE_SCALE_CENTER:
                if (isShow) {
                    vi.setVisibility( View.VISIBLE );
                    vi.setAnimation( scaleShow(vi.getMeasuredWidth()/2,vi.getMeasuredHeight()/2) );
                } else {
                    vi.setVisibility( View.GONE );
                    vi.setAnimation( scaleHide(vi.getMeasuredWidth()/2,vi.getMeasuredHeight()/2) );
                }
                break;
            case TYPE_SCALE_CUSTOM:
                if(point == null) {
                    point = new Point( vi.getMeasuredWidth()/2,vi.getMeasuredHeight()/2 );
                }
                if (isShow) {
                    vi.setVisibility( View.VISIBLE );
                    vi.setAnimation( scaleShow(point.x,point.y) );
                } else {
                    vi.setVisibility( View.GONE );
                    vi.setAnimation( scaleHide(point.x,point.y) );
                }
                break;
        }
    }
    /**
     * 顶部控件显示
     * @return
     */
    private static TranslateAnimation topShow() {
        TranslateAnimation mHiddenAction = new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                -1, Animation.RELATIVE_TO_SELF, 0 );
        mHiddenAction.setDuration( TIME_ANIMATION );
        return mHiddenAction;
    }

    /**
     * 顶部控件隐藏
     * @return
     */
    private static TranslateAnimation topHide() {
        TranslateAnimation mHiddenAction = new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                0, Animation.RELATIVE_TO_SELF, -1 );
        mHiddenAction.setDuration( TIME_ANIMATION );
        return mHiddenAction;
    }

    /**
     * 底部控件显示
     * @return
     */
    private static TranslateAnimation bottomShow() {
        TranslateAnimation mHiddenAction = new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                1, Animation.RELATIVE_TO_SELF, 0 );
        mHiddenAction.setDuration( TIME_ANIMATION );
        return mHiddenAction;
    }

    /**
     * 底部控件隐藏
     * @return
     */
    private static TranslateAnimation bottomHide() {
        TranslateAnimation mHiddenAction = new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                0, Animation.RELATIVE_TO_SELF, 1 );
        mHiddenAction.setDuration( TIME_ANIMATION );
        return mHiddenAction;
    }

    /**
     * 左侧控件显示
     * @return
     */
    private static TranslateAnimation leftShow() {
        TranslateAnimation mHiddenAction = new TranslateAnimation( Animation.RELATIVE_TO_SELF, -1,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                0, Animation.RELATIVE_TO_SELF, 0 );
        mHiddenAction.setDuration( TIME_ANIMATION );
        return mHiddenAction;
    }

    /**
     * 左侧控件隐藏
     * @return
     */
    private static TranslateAnimation leftHide() {
        TranslateAnimation mHiddenAction = new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF,
                0, Animation.RELATIVE_TO_SELF, 0 );
        mHiddenAction.setDuration( TIME_ANIMATION );
        return mHiddenAction;
    }

    /**
     * 右侧控件显示
     * @return
     */
    private static TranslateAnimation rightShow() {
        TranslateAnimation mHiddenAction = new TranslateAnimation( Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                0, Animation.RELATIVE_TO_SELF, 0 );
        mHiddenAction.setDuration( TIME_ANIMATION );
        return mHiddenAction;
    }

    /**
     * 右侧控件隐藏
     * @return
     */
    private static TranslateAnimation rightHide() {
        TranslateAnimation mHiddenAction = new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF,
                0, Animation.RELATIVE_TO_SELF, 0 );
        mHiddenAction.setDuration( TIME_ANIMATION );
        return mHiddenAction;
    }
    /**
     * 缩放显示
     * @return
     */
    private static ScaleAnimation scaleShow() {
        return scaleShow(0,0);
    }
    private static ScaleAnimation scaleShow(int pivotX, int pivotY) {
        ScaleAnimation mHiddenAction = new ScaleAnimation( 0,1,0,1 ,pivotX,pivotY );
        mHiddenAction.setDuration( TIME_ANIMATION );
        return mHiddenAction;
    }

    /**
     * 缩放隐藏
     * @return
     */
    private static ScaleAnimation scaleHide() {
        return scaleHide(0,0);
    }
    private static ScaleAnimation scaleHide(int pivotX, int pivotY) {
        ScaleAnimation mHiddenAction = new ScaleAnimation(1,0,1,0,pivotX,pivotY );
        mHiddenAction.setDuration( TIME_ANIMATION );
        return mHiddenAction;
    }
}
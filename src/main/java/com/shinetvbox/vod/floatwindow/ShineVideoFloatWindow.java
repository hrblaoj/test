package com.shinetvbox.vod.floatwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shinetvbox.vod.R;
import com.shinetvbox.vod.VideoView;
import com.shinetvbox.vod.certercontrol.ControlCenter;
import com.shinetvbox.vod.certercontrol.ControlCenterConstants;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.manager.KeyDownManager;
import com.shinetvbox.vod.status.PlayStatus;
import com.shinetvbox.vod.utils.OnclickUtil;
import com.shinetvbox.vod.utils.volume.VolumeConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by hrblaoj on 2019/1/9.
 */

public class ShineVideoFloatWindow {


    private static final int hideW = 4;//4
    private static final int hideH = 4;//8
    public final static int SCREEN_FULL = 0;// 全屏状态
    public final static int SCREEN_DEFAULT = 1;// 默认状态
    public final static int SCREEN_HIDE = 2;// 隐藏

    private int mScreenStatus = SCREEN_DEFAULT;


    public static int DEFAULT_ZOOM_STATE_X = -10;
    public static int DEFAULT_ZOOM_STATE_Y = -10;
    public static int DEFAULT_ZOOM_STATE_WIDTH = 2;
    public static int DEFAULT_ZOOM_STATE_HEIGHT = 2;


    public static ShineVideoFloatWindow floatWindow;

    public static ShineVideoFloatWindow getIntance(){
        return floatWindow;
    }


    private Context mContext;
    WindowManager wm = null;
    FloatVideoLayout screenControlView = null;
    RelativeLayout floatingViewObj = null;
    public VideoView mVideoView;
    private int mScreenWidth = 0;
    private int mScreenHeight = 0;


    public ShineVideoFloatWindow(Context context){
        mContext = context;
        floatWindow = this;
        EventBus.getDefault().register( this );
        floatingViewObj = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.float_layout, null);

        screenControlView = floatingViewObj.findViewById(R.id.float_screen_control);
        floatingViewObj.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(OnclickUtil.isFastDoubleClickDelay(100)) return false;
//                if(mVideoView.isPlaying()){
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                   if(mScreenStatus == SCREEN_DEFAULT){
                       setVideoScale(SCREEN_FULL, true);
                   }else if(mScreenStatus == SCREEN_FULL){
                       showScreenControl(true);
                   }
//                   else {
//                        setVideoScale(SCREEN_DEFAULT, true);
//                    }
                }
                return false;
            }
        });


        mVideoView = (VideoView) floatingViewObj.findViewById(R.id.video_float_view);
        Point outsize = new Point(  );
        wm = (WindowManager) mContext.getSystemService(Activity.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize( outsize );
        mScreenWidth = outsize.x;
        mScreenHeight = outsize.y;
//        KtvLog.d( "mScreenWidth is " + mScreenWidth + " mScreenWidth is " + mScreenHeight);
        init();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusMessage msg) {
        switch (msg.what) {
            case EventBusConstants.FLOAT_WINDOW_CHANGE:

                break;
        }
    }
    private void init() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
/*		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
				| WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;*/
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION;

        // 设定内部文字对齐方式
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.height = 0;
        params.width = 0;
        params.x = 0;//DEFAULT_ZOOM_STATE_X
        params.y = mScreenHeight;//DEFAULT_ZOOM_STATE_Y

        wm.addView(floatingViewObj, params);
        setVideoScale(SCREEN_HIDE, true);
        floatingViewObj.setFocusable( true );
        floatingViewObj.setFocusableInTouchMode( true );
        floatingViewObj.setOnKeyListener( KeyListener);
    }

    private View.OnKeyListener KeyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
//            int vol = 0;
            switch (keyCode) {
                //模拟器测试时键盘中的的Enter键，模拟ok键（推荐TV开发中使用蓝叠模拟器）
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    showScreenControl(true);
                    break;
                case KeyEvent.KEYCODE_BACK:
                    setVideoScale(SCREEN_DEFAULT, true);
                    break;
                case KeyEvent.KEYCODE_HOME:///**** 获取不到home事件，得监听广播
                    break;
//                case KeyEvent.KEYCODE_VOLUME_UP:
//                    vol = VolumeConfig.setHoldVolume();
//                    if(PlayStatus.getInstance().getIsMute())
//                        ControlCenter.sendEmptyMessage( ControlCenterConstants.MUSIC_VOLUME_UNMUTE );
//                    break;
//                case KeyEvent.KEYCODE_VOLUME_DOWN:
//                    vol = VolumeConfig.setHoldVolume();
//                    if(!PlayStatus.getInstance().getIsMute() && (0 == vol))
//                        ControlCenter.sendEmptyMessage( ControlCenterConstants.MUSIC_VOLUME_MUTE );
//                    break;
                default:
                    KeyDownManager.customKeyDown( keyCode,event );
                    break;
            }
            return false;
        }
    };
    public void setVideoScale(int localZoomState, boolean isTop) {

//        if(!ControlCenter.getMediaPlayerIsOpening()) return;
//        KtvLog.d("setVideoScale localZoomState is " + localZoomState);

        WindowManager.LayoutParams params = (WindowManager.LayoutParams) floatingViewObj.getLayoutParams();

        if(null == mVideoView)
            return;
//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
        switch (localZoomState) {
            case SCREEN_FULL:
                showScreenControl(true);
                mScreenStatus =  SCREEN_FULL;
                params.flags =  WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN;

                params.width = mScreenWidth;
                params.height = mScreenHeight;
                params.x = 0;
                params.y = 0;
                wm.updateViewLayout(floatingViewObj, params);
                floatingViewObj.requestLayout();
                break;
            case SCREEN_DEFAULT:
                showScreenControl(false);
                mScreenStatus = SCREEN_DEFAULT;
                params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_FULLSCREEN;

                params.width = DEFAULT_ZOOM_STATE_WIDTH;
                params.height = DEFAULT_ZOOM_STATE_HEIGHT;
                params.x = DEFAULT_ZOOM_STATE_X;
                params.y = DEFAULT_ZOOM_STATE_Y;
                wm.updateViewLayout(floatingViewObj, params);
                floatingViewObj.requestLayout();
                break;
            case SCREEN_HIDE:
                mScreenStatus = SCREEN_HIDE;
                params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_FULLSCREEN;
                if(isTop){
                    params.y = -mScreenHeight;
                }else{
                    params.x = -mScreenWidth;
                }
                params.height = hideH;
                params.width = hideW;
                wm.updateViewLayout(floatingViewObj, params);
                break;
        }
    }

    private void showScreenControl(boolean isShow){
        if(screenControlView!=null)
        screenControlView.show(isShow);
    }

    public void setWindowsPoint(ImageView iv) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) iv.getLayoutParams();
        int[] location = new int[2] ;
        iv.getLocationOnScreen(location);
        DEFAULT_ZOOM_STATE_X = location[0];
        DEFAULT_ZOOM_STATE_Y = location[1];
        DEFAULT_ZOOM_STATE_WIDTH = lp.width;
        DEFAULT_ZOOM_STATE_HEIGHT = lp.height;
        setVideoScale(SCREEN_DEFAULT, true);
    }
}

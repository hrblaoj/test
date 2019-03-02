package com.shinetvbox.vod;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import com.shinetvbox.vod.certercontrol.ControlCenter;
import com.shinetvbox.vod.certercontrol.ControlCenterConstants;
import com.shinetvbox.vod.certercontrol.ToastCenter;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.floatwindow.ShineVideoFloatWindow;
import com.shinetvbox.vod.manager.EventBusManager;
import com.shinetvbox.vod.manager.KeyDownManager;
import com.shinetvbox.vod.manager.MyViewManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.utils.KtvLog;
import com.shinetvbox.vod.utils.updateapp.UpdateAppUtil;

import java.util.Timer;
import java.util.TimerTask;
public class MainActivity extends AppCompatActivity {

    public int ShinePort;
    public static VideoView mVideoView = null;
    public ShineVideoFloatWindow floatWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        MyViewManager.getInstance().init( this );
        ShinePort = GetShineHttpServerPort();
        floatWindow = new ShineVideoFloatWindow(this);

        mVideoView = floatWindow.mVideoView;

        ControlCenter.init(this,ShinePort );

        UpdateAppUtil.init( this );
    }

    @Override
    protected void onResume() {
        super.onResume();
        KtvLog.d( "myMainActivity onResume" );

        //mMainActivityView.onResume();
//恢复视频窗大小
        KtvLog.d("JGGGGG onResume");
        if (null != mVideoView ) {
//            mMainActivityView.mAwFloatingWindow.setVideoScaleDefault();
//            mMainActivityView.mAwFloatingWindow.setVideoScaleHide();
//            ControlCenter.sendEmptyMessageDelay( ControlCenterConstants.SONG_PLAY_NEXT_DEFUALT,100 );

        }
    }

    public native int GetShineHttpServerPort();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        EventBusMessage msg = new EventBusMessage();
//        int vol = 0;
//        KtvLog.d("onKeyDown keyCode is  " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if(MyViewManager.getInstance().getStackListCount()==0) {
                    exitBy2Click();
                }else{
                    msg.what = EventBusConstants.PAGE_BACK;
                    EventBusManager.sendMessage( msg );
                }
                Log.i( "222222222222222","KeyEvent=========================back" );
                return false;
            case KeyEvent.KEYCODE_HOME:///**** 获取不到home事件，得监听广播
//                Log.i( "222222222222222","KeyEvent=========================home" );
                return false;
//            case KeyEvent.KEYCODE_VOLUME_UP:
//                vol = VolumeConfig.setHoldVolume();
//                if(PlayStatus.getInstance().getIsMute())
//                    ControlCenter.sendEmptyMessage( ControlCenterConstants.MUSIC_VOLUME_UNMUTE );
//                break;
//            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                vol = VolumeConfig.setHoldVolume();
//                if(!PlayStatus.getInstance().getIsMute() && (0 == vol))
//                    ControlCenter.sendEmptyMessage( ControlCenterConstants.MUSIC_VOLUME_MUTE );
//                break;
            default:
                KeyDownManager.customKeyDown( keyCode,event );
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private Timer tExit = null;
    private boolean isExit = false;
    /**
     * 双击退出
     */
    private void exitBy2Click() {
        if (!isExit) {
            isExit = true; // 准备退出
            ToastCenter.getInTance().sendToastEvent( ResManager.getInstance()
                    .getStringById( R.string.system_hint_exit ) );
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                    tExit.cancel();
                    tExit = null;
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            if(tExit!=null){
                tExit.cancel();
                tExit = null;
            }
            this.finish();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().onDestroy();
        MyViewManager.getInstance().onDestroy();
        android.os.Process.killProcess( android.os.Process.myPid() );
    }
}

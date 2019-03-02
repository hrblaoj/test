package com.shinetvbox.vod.floatwindow;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.shinetvbox.vod.MyApplication;
import com.shinetvbox.vod.R;
import com.shinetvbox.vod.certercontrol.ControlCenter;
import com.shinetvbox.vod.certercontrol.ControlCenterConstants;
import com.shinetvbox.vod.manager.MyViewManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.status.PlayStatus;
import com.shinetvbox.vod.utils.AnimationUtil;
import com.shinetvbox.vod.utils.KtvLog;

/**
 * Created by hrblaoj on 2019/1/14.
 */

public class FloatVideoLayout extends RelativeLayout implements PlayStatus.OnStatuChange {
    private PlayStatus mPlayStatus = null;
    private View mScreemView;
    private Button btn_next;
    private Button btn_yuanchang;
    private Button btn_banchang;
    private Button btn_play;
    private Button btn_pause;
    private Button btn_mute;
    private Button btn_unmute;
    private Button btn_replay;

    Context mContext;
    public FloatVideoLayout(Context context) {
        super(context);
        KtvLog.d("FloatVideoLayout 111");
        mContext = context;
        init();
    }

    public FloatVideoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        KtvLog.d("FloatVideoLayout 222");
        mContext = context;
        init();
    }

    public FloatVideoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        KtvLog.d("FloatVideoLayout 333");
        mContext = context;
        init();
    }

    void init(){
//        KtvLog.d("FloatVideoLayout init");
        mScreemView = LayoutInflater.from(MyApplication.getInstance()).inflate(R.layout.screen_control, null);
        if (mScreemView == null) return;
        btn_next = mScreemView.findViewById(R.id.screen_control_btn_playnext );
        btn_next.setOnClickListener(onClick);
        btn_next.setOnFocusChangeListener(onFocusChange);
        btn_yuanchang = mScreemView.findViewById(R.id.screen_control_btn_yuanchang );
        btn_yuanchang.setOnClickListener(onClick);
        btn_yuanchang.setOnFocusChangeListener(onFocusChange);
        btn_banchang = mScreemView.findViewById(R.id.screen_control_btn_banchang );
        btn_banchang.setOnClickListener(onClick);
        btn_banchang.setOnFocusChangeListener(onFocusChange);
        btn_play = mScreemView.findViewById(R.id.screen_control_btn_play );
        btn_play.setOnClickListener(onClick);
        btn_play.setOnFocusChangeListener(onFocusChange);
        btn_pause = mScreemView.findViewById(R.id.screen_control_btn_pause );
        btn_pause.setOnClickListener(onClick);
        btn_pause.setOnFocusChangeListener(onFocusChange);
        btn_mute = mScreemView.findViewById(R.id.screen_control_btn_mutex );
        btn_mute.setOnClickListener(onClick);
        btn_mute.setOnFocusChangeListener(onFocusChange);
        btn_unmute = mScreemView.findViewById(R.id.screen_control_btn_unmutex );
        btn_unmute.setOnClickListener(onClick);
        btn_unmute.setOnFocusChangeListener(onFocusChange);
        btn_replay = mScreemView.findViewById(R.id.screen_control_btn_replay );
        btn_replay.setOnClickListener(onClick);
        btn_replay.setOnFocusChangeListener(onFocusChange);

        this.addView(mScreemView);

        ResManager.getInstance().register( mScreemView );

        mPlayStatus = PlayStatus.getInstance();
        mPlayStatus.registerOnStatuList(this);

        if(mPlayStatus.getIsAccompany()){
            setaccompany();
        }else{
            setoriginal();
        }
        if(mPlayStatus.getIsMute()){
            setmute();
        }else{
            setunmute();
        }
        if(mPlayStatus.getIsPause()){
            setpause();
        }else{
            setcancelpause();
        }
    }

    public void show(boolean isShow){
        if(isShow){
            if(mScreemView.getVisibility()!=VISIBLE){
                MyViewManager.getInstance().requestFocus( btn_next );
            }
            startDownTimer();
        }else{
            closeDownTimer();
        }
        AnimationUtil.setViewVisible( mScreemView,isShow,AnimationUtil.TYPE_TOP );
    }

    @Override
    public void setmute() {
        btn_mute.setVisibility( VISIBLE );
        btn_unmute.setVisibility( GONE );
        if(mScreemView.getVisibility() == VISIBLE){
            MyViewManager.getInstance().requestFocus( btn_mute );
        }
    }

    @Override
    public void setunmute() {
        btn_mute.setVisibility( GONE );
        btn_unmute.setVisibility( VISIBLE );
        if(mScreemView.getVisibility() == VISIBLE){
            MyViewManager.getInstance().requestFocus( btn_unmute );
        }
    }

    @Override
    public void setpause() {
        btn_play.setVisibility( VISIBLE );
        btn_pause.setVisibility( GONE );
        if(mScreemView.getVisibility() == VISIBLE){
            MyViewManager.getInstance().requestFocus( btn_play );
        }
    }

    @Override
    public void setcancelpause() {
        btn_play.setVisibility( GONE );
        btn_pause.setVisibility( VISIBLE );
        if(mScreemView.getVisibility() == VISIBLE){
            MyViewManager.getInstance().requestFocus( btn_pause );
        }
    }

    @Override
    public void setaccompany() {
        btn_yuanchang.setVisibility( VISIBLE );
        btn_banchang.setVisibility( GONE );
        if(mScreemView.getVisibility() == VISIBLE){
            MyViewManager.getInstance().requestFocus( btn_yuanchang );
        }
    }

    @Override
    public void setoriginal() {
        btn_yuanchang.setVisibility( GONE );
        btn_banchang.setVisibility( VISIBLE );
        if(mScreemView.getVisibility() == VISIBLE){
            MyViewManager.getInstance().requestFocus( btn_banchang );
        }
    }

    @Override
    public void changevolume(int vol) {

    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            show( false );
            return true;
        }
        return super.dispatchKeyEvent( event );
    }

    private View.OnFocusChangeListener onFocusChange = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                startDownTimer();
            }
        }
    };
    private View.OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getTag() == null) return;
            switch (v.getTag().toString()){
                case "screen_control_btn_playnext":
                    ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_PLAY_NEXT );
                    break;
                case "screen_control_btn_yuanchang":
                    ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_ORIGINAL );
                    break;
                case "screen_control_btn_banchang":
                    ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_ACCOMPANY );
                    break;
                case "screen_control_btn_play":
                    ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_PLAY );
                    break;
                case "screen_control_btn_pause":
                    ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_PAUSE );
                    break;
                case "screen_control_btn_mutex":
                    ControlCenter.sendEmptyMessage( ControlCenterConstants.MUSIC_VOLUME_UNMUTE );
                    break;
                case "screen_control_btn_unmutex":
                    ControlCenter.sendEmptyMessage( ControlCenterConstants.MUSIC_VOLUME_MUTE );
                    break;
                case "screen_control_btn_replay":
                    ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_REPLAY );
                    break;
            }
        }
    };

    private CountDownTimer countDownTimer;
    private void startDownTimer(){
        closeDownTimer();
        countDownTimer = new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                show( false );
            }
        }.start();
    }
    private void closeDownTimer(){
        if(countDownTimer!=null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        closeDownTimer();
    }
}

package com.shinetvbox.vod;

/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;


import com.shinetvbox.vod.dao.PlaySong;
import com.shinetvbox.vod.mycomponents.MyMediaPlayer;
import com.shinetvbox.vod.status.PlayStatus;
import com.shinetvbox.vod.utils.FileUtil;
import com.shinetvbox.vod.utils.KtvLog;
import com.shinetvbox.vod.utils.threadfactory.ThreadFactory;
import com.shinetvbox.vod.utils.volume.VolumeConfig;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static android.media.MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO;
import static android.media.MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_UNKNOWN;


public class VideoView extends SurfaceView implements MediaPlayerControl, PlayStatus.OnStatuChange {
    private String TAG = "VideoView";
    private boolean VIDEO_DBG = true;

//    private int [] BackgroundResArray = {R.drawable.p2, R.drawable.p3, R.drawable.p4,
//    		R.drawable.p5, R.drawable.p6, R.drawable.p7, R.drawable.p8, R.drawable.p9,
//    		R.drawable.p10, R.drawable.p11};

    public static final int KTV = 0;//专业
    public static final int ODEUM = 1;
    public static final int CONCERT = 2;//,音乐会，流行
    public static final int SUBTLE = 3;//,说唱，摇滚
    public static final int STADIUM = 4;//,体育场
    public static final int CUPBOARD = 5;//,橱柜，室内，抒情
    public static final int DARK = 6;//,黑暗
    public static final int HALVES = 7;//,
    public static final int TEMPTEST = 8;//,

    private Context mContext;
    private Uri mUri;

    private int         mDuration;
    private SurfaceHolder mSurfaceHolder = null;
    private MediaPlayer mBgMediaPlayer = null;
    private MyMediaPlayer mMediaPlayer = null;
//    private MediaPlayer mMediaPlayer = null;
    private MyMediaPlayer mAudioMediaPlayer = null;
    private boolean     mIsPrepared = false;
    private int         mVideoWidth;
    private int         mVideoHeight;
    private MediaController mMediaController;
    private OnCompletionListener mOnCompletionListener;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private int         mCurrentBufferPercentage;
    private OnErrorListener mOnErrorListener;
    private boolean     mStartWhenPrepared;
    private int         mSeekWhenPrepared;

    private DisplayManager mDisplayManager;
    //远端显示屏播放视频的界面


    private PlayStatus mPlayStatus = null;
    private Timer timer;
    private TimerTask timerTask;


    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        KtvLog.d("VideoView1");
        mContext = context;
        initVideoView();
        initLogoAndPhoneQrcodeTimer();
    }

    public VideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        KtvLog.d("VideoView2");
        mContext = context;
        // initVideoView();
    }

    public void setVideoScale(int width , int height){
        LayoutParams lp = getLayoutParams();
        lp.height = height;
        lp.width = width;
        setLayoutParams(lp);
    }

    public Bitmap getCurFrame(){
        Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = mSurfaceHolder.lockCanvas();
        if(null == mCanvas) {
            KtvLog.d("mCanvas is null");
            return null;
        }
        mCanvas.drawBitmap(bitmap,0,0,new Paint());
        mSurfaceHolder.unlockCanvasAndPost(mCanvas);



        return bitmap;
    }

    private void initVideoView() {

        KtvLog.d("initVideoView");
        mVideoWidth = 0;
        mVideoHeight = 0;

//        setZOrderOnTop(true);
//        setZOrderMediaOverlay(true);
        mPlayStatus = PlayStatus.getInstance();
        mPlayStatus.registerOnStatuList(this);

        mMediaPlayer = new MyMediaPlayer();
//        mMediaPlayer = new MediaPlayer();
        mAudioMediaPlayer = new MyMediaPlayer();
        getHolder().addCallback(mSHCallback);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        setFocusable(true);
//        setFocusableInTouchMode(true);
        requestFocus();

        mDisplayManager = (DisplayManager) mContext.getSystemService(Context.DISPLAY_SERVICE);


//        requestLayout();
//        invalidate();
    }

    private boolean isShowLogo = true;
    private void initLogoAndPhoneQrcodeTimer(){
        @SuppressLint("HandlerLeak") final Handler handler = new Handler(  ){
            @Override
            public void handleMessage(Message msg) {
//                if(ConfigUtil.isEnableQrcode()){
//                    if(isShowLogo){
//                        showLogo();
//                        hideWebView();
//                        isShowLogo = false;
//                    }else{
//                        hideLogo();
//                        showWebView();
//                        isShowLogo = true;
//                    }
//                }else{
//                    if(isShowLogo){
//                        showLogo();
//                        hideWebView();
//                        isShowLogo = false;
//                    }
//                }
            }
        };

//        timer = new Timer();
//        timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                handler.sendEmptyMessage( 0 );
//            }
//        };
//        timer.schedule( timerTask,10000,10000 );
    }

    public void setPlayOrPause()
    {
        if(isVideoOpening)
            return;

        if(mMediaPlayer.isPlaying())
        {
            mMediaPlayer.pause();
        }
        else
        {
            mMediaPlayer.start();
        }
    }

    public void playrecordaudio(final String mp3path){
        ThreadFactory.getNormalPool().execute(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {

                    KtvLog.d(" setDataSource mp3path " + mp3path);
                    mAudioMediaPlayer.setDataSource(mp3path);
                    mAudioMediaPlayer.prepare();
                    Thread.sleep(2000);

                    int seekTime = getCurrentPosition();
                    KtvLog.d("playrecordaudio seekTime is " + seekTime );
                    mAudioMediaPlayer.seekTo(seekTime + 200);
                    mAudioMediaPlayer.start();


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }});
    }

    public void replayvideo()
    {
        playvideo(mUri);
    }

    public void setVideoPath(String path) {
        //setVideoURI(Uri.parse(path), true);
        playvideo(Uri.parse(path));
    }

    public void setVideoPath(String path, String mp3path) {
        //setVideoURI(Uri.parse(path), true);
        playvideoandaudio(Uri.parse(path), mp3path);
    }





    private  void playvideoandaudiothread(final String mp3path) {
        if (mUri == null || mSurfaceHolder == null) {
            KtvLog.d("openVideo mUri == null || mSurfaceHolder == null");
            sendPendingUriMessage(0);
            return;
        }

        boolean bture = Looper.getMainLooper() == Looper.myLooper();
        KtvLog.d("openVideo bture is "+ bture);

        sendPendingUriMessage(4000);//延迟发送这个广播，避免执行prepare后，没有调用mPreparedListener

        //广播停止其他音乐
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        mContext.sendBroadcast(i);

        try {
            if(mMediaPlayer == null) {
                initVideoView();
            } else {
                if(mMediaPlayer.isPlaying()) {
//        			this.setAudioTone(5);
                    mMediaPlayer.stop();
                }
                mMediaPlayer.reset();

                if(mAudioMediaPlayer.isPlaying()) {
                    mAudioMediaPlayer.stop();
                }
                mAudioMediaPlayer.reset();
            }

            mMediaPlayer.setOnPreparedListener(mPreparedListener);
//            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);


            if (mUri == null || mSurfaceHolder == null) {
                sendPendingUriMessage(0);
                return;
            }

            String path = mUri.getPath();

            //mMediaPlayer.setDataSource("file://"+path);
            mMediaPlayer.setDataSource(mUri.toString());
//            	mMediaPlayer.setDataSource(mContext, mUri);




            //设置辅助显示
            if(mUri != null && !FileUtil.isMusicFile(mUri.getPath())) {

                mMediaPlayer.setDisplay(mSurfaceHolder);
            }


            if (mUri == null || mSurfaceHolder == null) {
                if(mMediaPlayer != null){
                    mMediaPlayer.reset();
                }
                sendPendingUriMessage(0);
                return;
            }
            mMediaPlayer.setVolume(1.0f, 1.0f);
            KtvLog.d("mMediaPlayer.prepare()  mVideoOpening"+isVideoOpening);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setVolume(0,0);
            mHandler.sendEmptyMessage(SHOW_MEDIA_CONTROLL);
            playrecordaudio(mp3path);

        } catch (Exception ex) {
            sendPendingUriMessage(0);

            Log.w(TAG, "Unable to open content: " + mUri, ex);
            return;
        }

    }




    private  void playvideothread() {
        if (mUri == null || mSurfaceHolder == null) {
            KtvLog.d("openVideo mUri == null || mSurfaceHolder == null");
            sendPendingUriMessage(0);
            return;
        }

        boolean bture = Looper.getMainLooper() == Looper.myLooper();
        KtvLog.d("openVideo bture is "+ bture);


        sendPendingUriMessage(4000);//延迟发送这个广播，避免执行prepare后，没有调用mPreparedListener

        //广播停止其他音乐
//        Intent i = new Intent("com.android.music.musicservicecommand");
//        i.putExtra("command", "pause");
//        mContext.sendBroadcast(i);

        try {
            if(mMediaPlayer == null) {
                initVideoView();
            } else {
                if(mMediaPlayer.isPlaying()) {
//        			this.setAudioTone(5);
                    mMediaPlayer.stop();
                }

                mMediaPlayer.reset();

                if(mAudioMediaPlayer.isPlaying()) {
                    mAudioMediaPlayer.stop();
                }
                mAudioMediaPlayer.reset();

            }

            KtvLog.d("playvideothread 00000");
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
//            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);

            KtvLog.d("playvideothread 111111");
            if (mUri == null || mSurfaceHolder == null) {
                KtvLog.d("mUri is " + mUri + " mSurfaceHolder is " + mSurfaceHolder);
                sendPendingUriMessage(0);
                return;
            }

            String path = mUri.getPath();

            KtvLog.d("playvideothread 22222");
            //mMediaPlayer.setDataSource("file://"+path);
            mMediaPlayer.setDataSource(mUri.toString());
//            String jjkkk = Environment.getExternalStorageDirectory() + "/shinedir/shinesong/874337.mpg";
//            KtvLog.d("jjkkkjjkkk is " + jjkkk);
//            mMediaPlayer.setDataSourceLocal(jjkkk);

//            	mMediaPlayer.setDataSource(mContext, mUri);

            KtvLog.d("playvideothread 33333");
            //设置辅助显示

            //设置辅助显示
            if(mUri != null && !FileUtil.isMusicFile(mUri.getPath())) {

                mMediaPlayer.setDisplay(mSurfaceHolder);

//                mMediaPlayer.setSurface(MySurfaceView.getInstance().getSurface());
            }


            if (mUri == null || mSurfaceHolder == null) {
                if(mMediaPlayer != null){
                    mMediaPlayer.reset();
                }
                sendPendingUriMessage(0);
                return;
            }
            mMediaPlayer.setVolume(1.0f, 1.0f);
            KtvLog.d("mMediaPlayer.prepare()  mVideoOpening"+isVideoOpening);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
            KtvLog.d("playvideothread 44444");
            mHandler.sendEmptyMessage(SHOW_MEDIA_CONTROLL);
            KtvLog.d("playvideothread 55555");

        } catch (Exception ex) {
            sendPendingUriMessage(0);

            Log.w(TAG, "Unable to open content: " + mUri, ex);
            return;
        }

    }


    public synchronized void playvideo(Uri uri) {

        //KtvLog.d("uriuriuristing is " + uri.toString() + "uri is " + uri);
        if(isVideoOpening) {
            KtvLog.d("playvideo isVideoOpening return");
            return;
        }
        else {
            KtvLog.d("playvideo isVideoOpening pre");
        }

        mUri = uri;
        if(mUri != null){
            readyOpenVideo();
        }
        ThreadFactory.getNormalPool().execute(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                playvideothread();
            }});


        //playvideothread();
    }

    public synchronized void playvideoandaudio(Uri uri, final String mp3path) {

        KtvLog.d("uriuriuri is " + uri.toString());
        if(isVideoOpening) {
            KtvLog.d("playvideo isVideoOpening return");
            return;
        }
        else {
            KtvLog.d("playvideo isVideoOpening pre");
        }

        mUri = uri;
        if(mUri != null){
            readyOpenVideo();
        }
        ThreadFactory.getNormalPool().execute(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                playvideoandaudiothread(mp3path);
            }});

        //playvideothread();
    }

    private boolean isLoop = true;




    private boolean isSettingAudioTrack;
    private boolean isSettingAudioChannel;
    private final static int CHANNEL_LEFT = 0;
    private final static int CHANNEL_RIGHT = 1;
    private final static int CHANNEL_CENTER = 2;
    private void setAudioChannel(final int channel){
        if(mMediaPlayer == null) return;
        if(isSettingAudioChannel)return;
        isSettingAudioChannel = true;
        try {
            Log.d(TAG, "setAudioChannel start");
//            Invoke.invokeMethod(mMediaPlayer, "switchChannel", new Object[]{channel});
            if(0 == channel)
                mMediaPlayer.setVolume(1.0f, 0.0f);
            else
                mMediaPlayer.setVolume(0.0f, 1.0f);
            Log.d(TAG, "setAudioChannel end");
        }catch(Exception e){
            e.printStackTrace();
        } finally {
            isSettingAudioChannel = false;
        }
//        ThreadFactory.getNormalPool().execute(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    Log.d(TAG, "setAudioChannel start");
//                    Invoke.invokeMethod(mMediaPlayer, "switchChannel", new Object[]{channel});
//                    Log.d(TAG, "setAudioChannel end");
//                }catch(Exception e){
//                    e.printStackTrace();
//                } finally {
//                    isSettingAudioChannel = false;
//                }
//            }
//        });

    }



    OnSeekCompleteListener mSeekCompleteListener = new OnSeekCompleteListener() {

        @Override
        public void onSeekComplete(MediaPlayer mp) {
            // TODO Auto-generated method stub
            KtvLog.d("onSeekComplete");
            sendPendingSeekMessage(0);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean changeTrack() {
        int track = 0;
        track = mMediaPlayer.getSelectedTrack(MEDIA_TRACK_TYPE_AUDIO);
        KtvLog.d("getTrack index is " +  track);

        if(1 == track)
            track = 2;
        else if(2 == track)
            track = 1;

        if (mMediaPlayer != null && mMediaPlayer.isPlaying() && !isVideoOpening) {
            try {
                MediaPlayer.TrackInfo[] trackInfosLocal = mMediaPlayer
                        .getTrackInfo();

                if (trackInfosLocal != null && trackInfosLocal.length > 0) {
                    for (int j = 0; j < trackInfosLocal.length; j++) {
                        Log.d(TAG, "type=" + trackInfosLocal[j].getTrackType()+ "  track=" + j);
                    }
                    if (trackInfosLocal.length < 3) {// 如果长度小于3，则track音轨值不是1,2，则认为是3,4
                        Log.d(TAG, "setAudioChannel track=" + track);
                        if (track == 1) {
                            setAudioChannel(CHANNEL_LEFT);
                        } else {
                            setAudioChannel(CHANNEL_RIGHT);
                        }
                        Log.d(TAG, "setAudioChannel end");
                        return true;
                    }
                    boolean found = false;
                    // 查找歌曲的音轨，并匹配要设置的音轨。
                    for (int j = 0; j < trackInfosLocal.length; j++) {
                        if (MEDIA_TRACK_TYPE_AUDIO == trackInfosLocal[j]
                                .getTrackType() && track == j) {
                            Log.d(TAG, "selectTrack track=" + track);
                            final int trace1 = j;
                            if (isSettingAudioTrack) {
                                Log.d(TAG, "isSettingAudioTrack");
                                return false;
                            }
                            isSettingAudioTrack = true;
                            ThreadFactory.getNormalPool().execute(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        Log.d(TAG, "selectTrack start");
                                        mMediaPlayer.selectTrack(trace1);
                                        Log.d(TAG, "selectTrack end");
                                    } catch(Exception e){
                                        e.printStackTrace();
                                    } finally {
                                        isSettingAudioTrack = false;
                                    }
                                }
                            });

                            found = true;
                            break;
                        }
                    }
                    // 如果没有找到音轨
                    if (!found) {
                        return false;
                    } else {
                        return true;
                    }

                } else {
                    Log.v(TAG, "setAudioTrack get AudioTrack is null");
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;

        }

        return false;
    }

    public boolean setTrack(int track){
        if (mMediaPlayer != null && mMediaPlayer.isPlaying() && !isVideoOpening) {
            try {
                MediaPlayer.TrackInfo[] trackInfosLocal = mMediaPlayer
                        .getTrackInfo();

                if (trackInfosLocal != null && trackInfosLocal.length > 0) {
                    for (int j = 0; j < trackInfosLocal.length; j++) {
                        Log.d(TAG, "type=" + trackInfosLocal[j].getTrackType()+ "  track=" + j);
                    }
                    Log.d(TAG,"trackInfosLocal.length is " + trackInfosLocal.length);
                    if (trackInfosLocal.length < 3) {// 如果长度小于3，则track音轨值不是1,2，则认为是3,4
                        Log.d(TAG, "setAudioChannel track=" + track);
                        if (track == 1) {
                            setAudioChannel(CHANNEL_LEFT);
                        } else {
                            setAudioChannel(CHANNEL_RIGHT);
                        }
                        Log.d(TAG, "setAudioChannel end");
                        return true;
                    }
                    boolean found = false;
                    // 查找歌曲的音轨，并匹配要设置的音轨。
                    for (int j = 0; j < trackInfosLocal.length; j++) {
                        Log.d(TAG, "for track =" + trackInfosLocal[j]);
                        if (MEDIA_TRACK_TYPE_AUDIO == trackInfosLocal[j]
                                .getTrackType()&& track == j) {
                            Log.d(TAG, "selectTrack track=" + track);
                            final int trace1 = j;
                            if (isSettingAudioTrack) {
                                Log.d(TAG, "isSettingAudioTrack");
                                return false;
                            }
                            isSettingAudioTrack = true;
//                            ThreadFactory.getNormalPool().execute(new Runnable() {
//
//                                @Override
//                                public void run() {
//                                    try {
//                                        Log.d(TAG, "selectTrack start time is " + System.currentTimeMillis());
//                                        mMediaPlayer.selectTrack(trace1);
//                                        Log.d(TAG, "selectTrack end time is " + System.currentTimeMillis());
//                                    } catch(Exception e){
//                                        e.printStackTrace();
//                                    } finally {
//                                        isSettingAudioTrack = false;
//                                    }
//                                }
//                            });
                            try {
                                Log.d(TAG, "selectTrack start time is " + System.currentTimeMillis());
                                mMediaPlayer.selectTrack(trace1);
                                Log.d(TAG, "selectTrack end time is " + System.currentTimeMillis());
                            } catch(Exception e){
                                e.printStackTrace();
                            } finally {
                                isSettingAudioTrack = false;
                            }

                            found = true;
                            break;
                        }
                    }
                    // 如果没有找到音轨
                    if (!found) {
                        return false;
                    } else {
                        return true;
                    }

                } else {
                    Log.v(TAG, "setAudioTrack get AudioTrack is null");
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;

        } else {
            Log.v(TAG, "setAudioTrack track=" + track + "  mediaPlayer is not playing. set fail.");
            return false;
        }
    }
    MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            try{
                KtvLog.d("onPrepared mStartWhenPrepared="+mStartWhenPrepared);

                boolean bture = Looper.getMainLooper() == Looper.myLooper();
                KtvLog.d("onPrepared bture is "+ bture);
                //mIsPrepared = true;
                if (mOnPreparedListener != null) {
                    mOnPreparedListener.onPrepared(mMediaPlayer);
                }
                if (mMediaController != null) {
                    mMediaController.setEnabled(true);
                }

//                setMediaPlayer(mMediaPlayer);

                //mp.start();
//	            mVideoWidth = mp.getVideoWidth();
//	            mVideoHeight = mp.getVideoHeight();
//	            if (mVideoWidth != 0 && mVideoHeight != 0 && mUri != null && !FileUtil.isMusicFile(mUri.getPath())) {
//	            	if(mAwFloatingWindow.videoIsFullStatus()){
//	            		getHolder().setFixedSize(mVideoWidth, mVideoHeight);
//	            	} else if(mAwFloatingWindow.videoIsHideStatus()){
//	            		VideoView.this.setVideoScale(8, 4);
//	            		getHolder().setFixedSize(8, 4);
//	            	}else {
//	            		VideoView.this.setVideoScale(AwFloatingWindow.DEFAULT_VIDEO_WIDTH, AwFloatingWindow.DEFAULT_VIDEO_HEIGHT);
//	            		getHolder().setFixedSize(AwFloatingWindow.DEFAULT_VIDEO_WIDTH, AwFloatingWindow.DEFAULT_VIDEO_HEIGHT);
//	            	}
//	            	mHandler.sendEmptyMessage(FIX_SIZE);
//	                //if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
//	                    if (mSeekWhenPrepared != 0) {
//	                        mMediaPlayer.seekTo(mSeekWhenPrepared);
//	                        mSeekWhenPrepared = 0;
//	                    }
//	                    if (mStartWhenPrepared) {
//	                        mMediaPlayer.start();
//	                        mStartWhenPrepared = false;
//	                        if (mMediaController != null) {
//	                            mMediaController.show();
//	                        }
//	                    } else if (!isPlaying() &&
//	                            (mSeekWhenPrepared != 0 || getCurrentPosition() > 0)) {
//	                       if (mMediaController != null) {
//	                           mMediaController.show(0);
//	                       }
//	                   }
//	                //}
//	            } else {
//	                if (mSeekWhenPrepared != 0) {
//	                    mMediaPlayer.seekTo(mSeekWhenPrepared);
//	                    mSeekWhenPrepared = 0;
//	                }
//	                if (mStartWhenPrepared) {
//						KtvLog.d("mStartWhenPrepared start");
//	                    mMediaPlayer.start();
//	                    mStartWhenPrepared = false;
//	                }
//	            }
            }finally{
                sendPendingUriMessage(0);
            }
        }
    };

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {

            KtvLog.d("onCompletiononCompletion");
            if (mMediaController != null) {
                mMediaController.hide();
            }
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion(mMediaPlayer);
            }
        }
    };

    static int fileindex = 0;
    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
                public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {

                    Log.d(TAG, "mErrorListener Error:  " + framework_err + "," + impl_err);
                    File file = new File("/sdcard/mErrorListener" + fileindex);
                    //fileindex++;
                    if (!file.exists()) {

                        try {
                            file.createNewFile();
                        } catch (IOException b) {
                            b.printStackTrace();
                        }
                    }
                    if (mMediaController != null) {
                        mMediaController.hide();
                    }

//                    try {
//                        mp.reset();
//                        //sendPendingUriMessage(0);
//                    }catch(Exception e){
//                        e.printStackTrace();
//                    }
//                    finally {
//
//                    }
                    if (mOnErrorListener != null) {
                        if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
                            //mp.reset();
                            //sendPendingUriMessage(0);
                            return true;
                        }
                    }


                    //sendPendingUriMessage(0);
                    return true;
                }
            };


//    DateUtil.frame myFrame = DateUtil.getInstance().new frame("video");


    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
            new MediaPlayer.OnBufferingUpdateListener() {
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    mCurrentBufferPercentage = percent;
                }
            };

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l)
    {
        mOnPreparedListener = l;
    }

    public void setOnCompletionListener(OnCompletionListener l)
    {
        mOnCompletionListener = l;
    }

    public void setOnErrorListener(OnErrorListener l)
    {
        mOnErrorListener = l;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent receivedIt) {
            String action = receivedIt.getAction();
            KtvLog.d("BroadcastReceiver recv is " + action);
            if (action.equals("android.intent.action.HDMI_PLUGGED")) {
                boolean state = receivedIt.getBooleanExtra("state", false);
                if (state) {
                    //isHdmiConnect = true;
                    //updateContents();
                    Message m = new Message();


                    m.what = HDMI_ADD;

                    //4、发送HDMI_ADD重新启动视频
                    mHandler.sendMessageDelayed(m,2000);
                    KtvLog.d("isis inin HDMI_PLUGGED");
                } else {
                    //isHdmiConnect = false;
                    KtvLog.d("isis outout HDMI_PLUGGED");
                }
            }
        }
    };

    public static volatile boolean vgaSurfaceComplete = false;
    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback()
    {
        public void surfaceChanged(SurfaceHolder holder, int format,
                                   int w, int h)
        {
            KtvLog.d("vga surfaceChanged");
//            if (mMediaPlayer != null && mIsPrepared && mVideoWidth == w && mVideoHeight == h) {
//                KtvLog.d("vga surfaceChanged process");
//                if (mSeekWhenPrepared != 0) {
//                    mMediaPlayer.seekTo(mSeekWhenPrepared);
//                    mSeekWhenPrepared = 0;
//                }
//                readyOpenVideo();
//                ThreadFactory.getNormalPool().execute(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        try {
//                            mMediaPlayer.start();
//                        } catch(Exception e){
//                            e.printStackTrace();
//                        } finally {
//                            sendPendingUriMessage(0);
//                        }
//                    }
//                });
//
//                if (mMediaController != null) {
//                    mMediaController.show();
//                }
//            }
        }

        public void surfaceCreated(SurfaceHolder holder)
        {
            Log.d("JG", "vga surfaceCreated");

            if(mMediaPlayer == null){
                initVideoView();
            }
            mSurfaceHolder = holder;

            KtvLog.d("surfaceCreated updateContents");
            updateContents();
            mMediaPlayer.stop();
            mMediaPlayer.reset();

            //mDisplayManager.registerDisplayListener(mDisplayListener, null);
            KtvLog.d("surfaceCreated after registerDisplayListener");

            IntentFilter filter=new IntentFilter();
            filter.addAction("android.intent.action.HDMI_PLUGGED");
            vgaSurfaceComplete = true;
            mContext.registerReceiver(mReceiver,filter);



//            if(mMediaPlayer.isPlaying()) {
//                Message m = new Message();
//
//                //1、记录当前播放位置
////                m.arg1 = mMediaPlayer.getCurrentPosition();
////                m.what = HDMI_ADD;
////                m.obj = true;//播放中
//
//                //2、停止视频播放
//                mMediaPlayer.stop();
//                mMediaPlayer.reset();
//
//                //3、更新辅助显示
////    	        updateContents();
//
//                //4、发送HDMI_ADD重新启动视频
//               // mHandler.sendMessage(m);
//            } else {
//                mMediaPlayer.stop();
//                mMediaPlayer.reset();
//
//
////                //mDisplayManager.registerDisplayListener(mDisplayListener, null);
////                //更新辅助显示屏
////                updateContents();
////                if(mUri != null && !FileUtil.isMidiFile(mUri.getPath())){
////                	openVideo();
////                }
//            }
        }



        public void surfaceDestroyed(SurfaceHolder holder)
        {
            KtvLog.d("vga surfaceDestroyed");
            mSurfaceHolder = null;
            if (mMediaController != null) mMediaController.hide();
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }

            hidePresentation();
            KtvLog.d("surfaceDestroyed");
            mDisplayManager.unregisterDisplayListener(mDisplayListener);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIsPrepared && mMediaPlayer != null && mMediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (mIsPrepared && mMediaPlayer != null && mMediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        Log.d("JG", "onKeyDown VideoView ");
////        if (mIsPrepared &&
////                keyCode != KeyEvent.KEYCODE_BACK &&
////                keyCode != KeyEvent.KEYCODE_VOLUME_UP &&
////                keyCode != KeyEvent.KEYCODE_VOLUME_DOWN &&
////                keyCode != KeyEvent.KEYCODE_MENU &&
////                keyCode != KeyEvent.KEYCODE_CALL &&
////                keyCode != KeyEvent.KEYCODE_ENDCALL &&
////                mMediaPlayer != null &&
////                mMediaController != null) {
////            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK ||
////                    keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
////                if (mMediaPlayer.isPlaying()) {
////                    pause();
////                    mMediaController.show();
////                } else {
////                    start();
////                    mMediaController.hide();
////                }
////                return true;
////            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
////                    && mMediaPlayer.isPlaying()) {
////                pause();
////                mMediaController.show();
////            } else {
////                toggleMediaControlsVisiblity();
////            }
////        }
//
//        return super.onKeyDown(keyCode, event);
//    }

    private void toggleMediaControlsVisiblity() {
        if (mMediaController.isShowing()) {
            mMediaController.hide();
        } else {
            mMediaController.show();
        }
    }


    public void start() {
        try {
            mMediaPlayer.start();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            sendPendingUriMessage(0);
        }

        if(null != mAudioMediaPlayer){
            if(mAudioMediaPlayer.isPlaying()){
                mAudioMediaPlayer.start();
            }
        }

//        if (mMediaPlayer != null && !isVideoOpening) {
//            readyOpenVideo();
//
//            try {
//                mMediaPlayer.start();
//            } catch (Exception e){
//                e.printStackTrace();
//            } finally {
//                sendPendingUriMessage(0);
//            }
//        }
    }
//    public void start() {
//    	if(mUri != null && FileUtil.isMidiFile(mUri.getPath())) {
////    		openMidi();
//    	} else {
//		    if (mMediaPlayer != null && !isVideoOpening) {
//		    	readyOpenVideo();
//		    	ThreadFactory.getNormalPool().execute(new Runnable() {
//
//					@Override
//					public void run() {
//						try {
//							mMediaPlayer.start();
//						} catch (Exception e){
//							e.printStackTrace();
//						} finally {
//							sendPendingUriMessage(0);
//						}
//					}
//				});
//		    	mStartWhenPrepared = false;
//		    } else {
//		        mStartWhenPrepared = true;
//		    }
//    	}
//    }

    public  boolean isVideoOpening(){
        return isVideoOpening;
    }

    public void pause() {
        if (mMediaPlayer != null) {
            if (!isVideoOpening && mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }

        if(null != mAudioMediaPlayer){
            if(mAudioMediaPlayer.isPlaying()){
                mAudioMediaPlayer.pause();
            }
        }
//        mStartWhenPrepared = false;
    }

    public int getDuration() {
        if (mMediaPlayer != null && mIsPrepared/* && !mVideoOpening*/) {
            if (mDuration > 0) {
                return mDuration;
            }
            mDuration = mMediaPlayer.getDuration();
            return mDuration;
        }
        mDuration = -1;
        return mDuration;
    }

    public int getCurrentPosition() {
//        if (mMediaPlayer != null && mIsPrepared && !isVideoOpening) {
        if (mMediaPlayer != null  && !isVideoOpening) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void seekTo(final int msec) {
        if(msec < 0){
            return;
        }
        if (mMediaPlayer != null) {
            //KtvLog.d("seekTo mesc="+msec);
            if(isSeeking){
                mPenddingSeek = msec;
                return;
            }

            if(!mIsPrepared || isVideoOpening){
                mPenddingSeek = msec;
                sendPendingSeekMessage(1000);
                return;
            }

            isSeeking = true;
            ThreadFactory.getNormalPool().execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        mMediaPlayer.seekTo(msec);
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                    sendPendingSeekMessage(1000);
                }
            });

        } else {
            mSeekWhenPrepared = msec;
        }
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null && !isVideoOpening) {
            try{
//        	    KtvLog.d("mMediaPlayer.isPlaying(); is " + mMediaPlayer.isPlaying());
                return mMediaPlayer.isPlaying();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    public boolean canPause() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canSeekBackward() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canSeekForward() {
        return false;
    }

    public int getAudioSessionId() {
        return 0;
    }

    /*************************************************************************************/
    /*         处理等待中的时间        */
    /*************************************************************************************/
    private Uri mPenddingUri;
    private volatile boolean 	isVideoOpening;//视频是否在打开中
    private int 		mPenddingSeek = -1;
    private boolean 	isSeeking;//视频是否在调节进度中
    private int 		mPendingTone = -1;
    private boolean 	isSettingTone;//是否在调节音调中

    private final static int HANDLER_PENDDING_URI = 1;
    private final static int HANDLER_PENDDING_SEEK = 2;
    private final static int HANDLER_PENDDING_TONE = 3;
    private Handler mPenddingHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case HANDLER_PENDDING_URI:
                    handlerPendingUri();
                    break;
                case HANDLER_PENDDING_SEEK:
                    handlerPenddingSeek();
                    break;
                case HANDLER_PENDDING_TONE:
                    handlerPenddingTone();
                    break;
            }
        };
    };

    private void sendPendingUriMessage(int delay){
        KtvLog.d("sendPendingUriMessage   " + delay);
        if(mPenddingHandler.hasMessages(HANDLER_PENDDING_URI)){
            mPenddingHandler.removeMessages(HANDLER_PENDDING_URI);
        }
        mPenddingHandler.sendEmptyMessageDelayed(HANDLER_PENDDING_URI, delay);
    }

    private void handlerPendingUri(){
        //.d("handlerPendingUri mVideoOpening="+isVideoOpening+"  mPenddingUri="+mPenddingUri);"
        KtvLog.d("handlerPendingUri");
        openVideoFinish();
//        if(mPenddingUri != null) {
//            setVideoURI(mPenddingUri, true);
//            mPenddingUri = null;
//        }
    }

    private void sendPendingToneMessage(int delay){
        if(mPenddingHandler.hasMessages(HANDLER_PENDDING_TONE)){
            mPenddingHandler.removeMessages(HANDLER_PENDDING_TONE);
        }
        mPenddingHandler.sendEmptyMessageDelayed(HANDLER_PENDDING_TONE, delay);
    }

    private void handlerPenddingTone(){
        //KtvLog.d("mPendingTone="+mPendingTone);
        isSettingTone = false;
        if(mPendingTone >= 0) {
//			setAudioTone(mPendingTone);
            mPendingTone = -1;
        }
    }

    private void sendPendingSeekMessage(int delay){
        if(mPenddingHandler.hasMessages(HANDLER_PENDDING_SEEK)){
            mPenddingHandler.removeMessages(HANDLER_PENDDING_SEEK);
        }
        mPenddingHandler.sendEmptyMessageDelayed(HANDLER_PENDDING_SEEK, delay);
    }

    private void handlerPenddingSeek(){
        //KtvLog.d("mPenddingSeek="+mPenddingSeek);
        isSeeking = false;
        if(mPenddingSeek >= 0) {
            seekTo(mPenddingSeek);
            mPenddingSeek = -1;
        }
    }



/*************************************************************************************/
    /*           辅助显示        */
    /*************************************************************************************/
    //HDMI热插拔事件
    private final static int HDMI_REMOVE = 1;
    public final static int HDMI_ADD = 2;
    private final static int SHOW_MEDIA_CONTROLL = 3;
    private final static int FIX_SIZE = 4;

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            //如果视频路径不存在，则不用处理。
            if(mUri == null)
                return;

            switch(msg.what) {
                case HDMI_ADD:
//                    boolean ret = updateContents();
//                    KtvLog.d("HDMI_ADD updateContents ret is "  + ret);
//                    //setMinorDisplay();
//                    if(ret)
//                        ControlCenter.sendEmptyMessageDelay( ControlCenterConstants.SONG_REPLAY, 1000 );
                    break;
            }

//            switch(msg.what) {
//            case HDMI_REMOVE:
//            	if(mHandler.hasMessages(HDMI_ADD)) {
//            		mHandler.removeMessages(HDMI_ADD);
//            	}
//            	readyOpenVideo();
//            	final boolean isPlaying = (Boolean)msg.obj;
//            	final int seek = msg.arg1;
//            	ThreadFactory.getNormalPool().execute(new Runnable() {
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//		            	try
//		            	{
//		            		if(mMediaPlayer != null){
//		            			mMediaPlayer.reset();
//		            		} else {
//		            			return;
//		            		}
//
//		                    String path = mUri.getPath();
//		                    if(Secure.isEncryptFile(path)) {
//		                    	path = "aes://file://"+path;
//		                    	//KtvLog.d("path="+path);
//		                    	mMediaPlayer.setDataSource(path);
//		                    } else {
//		                    	mMediaPlayer.setDataSource(mContext, mUri);
//		                    }
//		                    mMediaPlayer.setDisplay(mSurfaceHolder);
//		                    mMediaPlayer.setScreenOnWhilePlaying(true);
//		                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//		                    mMediaPlayer.prepare();
//		                    mMediaPlayer.seekTo(seek);
//		                    if(isPlaying){
//		                    	mMediaPlayer.start();
//		                    }
//		                }
//		                catch(Exception e)
//		                {
//		                	stopPlayback();
//		                    e.printStackTrace();
//		                } finally {
//		                	sendPendingUriMessage(0);
//		                }
//					}
//				});
//                Log.d(TAG,"hanlderMessagemMediaPlayerLocal.startmsg.arg1="+msg.arg1);
//                break;
//            case HDMI_ADD:
//            	isVideoOpening = true;
//            	final boolean isPlaying1 = (Boolean)msg.obj;
//            	final int seek1 = msg.arg1;
//            	ThreadFactory.getNormalPool().execute(new Runnable() {
//
//					@Override
//					public void run() {
//		                try
//		                {
//		            		if(mMediaPlayer != null){
//		            			mMediaPlayer.reset();
//		            		} else {
//		            			return;
//		            		}
//		                    String path = mUri.getPath();
//		                    if(Secure.isEncryptFile(path)) {
//		                    	path = "aes://file://"+path;
//		                    	//KtvLog.d("path="+path);
//		                    	mMediaPlayer.setDataSource(path);
//		                    } else {
//		                    	mMediaPlayer.setDataSource(mContext, mUri);
//		                    }
//		                	Invoke.invokeMethod(mMediaPlayer, "setMinorDisplay", new Object[]{mVideoSurfaceRemote.getHolder()});
//		                    //mMediaPlayer.setMinorDisplay(mVideoSurfaceRemote.getHolder());
//		                    mMediaPlayer.setDisplay(mSurfaceHolder);
//		                    mMediaPlayer.setScreenOnWhilePlaying(true);
//		                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//		                    mMediaPlayer.prepare();
//		                    mMediaPlayer.seekTo(seek1);
//		                    if(isPlaying1){
//		                    	mMediaPlayer.start();
//		                    } else {
//		                    	CmdSend.getInstance().setPlayOrPause();
//		                    }
//		                }
//		                catch(Exception e)
//		                {
//		                	stopPlayback();
//		                    e.printStackTrace();
//		                } finally {
//		                	sendPendingUriMessage(0);
//		                }
//					}
//				});
//                Log.d(TAG,"hanlderMessagem MediaPlayer msg.arg1="+msg.arg1);
//
//                break;
//            case SHOW_MEDIA_CONTROLL:
//            	attachMediaController();
//            	break;
//            case FIX_SIZE:
//            	if(mAwFloatingWindow.videoIsFullStatus()) {
//            		mAwFloatingWindow.setVideoScaleFull();
//            	}else if(mAwFloatingWindow.videoIsHideStatus()){
//            		mAwFloatingWindow.setVideoScaleHide();
//            	}else{
//            		mAwFloatingWindow.setVideoScaleDefault();
//            	}
//            	break;
//        }
        }
    };

        private final DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener()
    {
      /**
       * 监听显示器插入
       * 播放过程中插入视频的处理流程：
       * 1、记录当前播放位置
       * 2、停止视频播放
       * 3、更新辅助显示
       * 4、发送HDMI_ADD重新启动视频
       * */
      public void onDisplayAdded(int displayId)
      {
        KtvLog.d("DisplayDisplayDisplay #" + displayId + " added.");

//        if(mMediaPlayer != null) {
//	        Message m = new Message();
//
//	        //1、记录当前播放位置
//	        m.arg1 = tempPlayPosition == 0?mMediaPlayer.getCurrentPosition():tempPlayPosition;
//	        m.what = HDMI_ADD;
//	        tempPlayPosition = 0;
//
//	        //2、停止视频播放
//	        if(mMediaPlayer.isPlaying()) {
//	        	mMediaPlayer.stop();
//	        	m.obj = true;//记录是否播放中
//	        } else {
//	        	m.obj = false;
//	        }
//	        mMediaPlayer.reset();
//
//	        //3、更新辅助显示
//	        updateContents();
//
//	        //4、发送HDMI_ADD重新启动视频
//	        mHandler.sendMessageDelayed(m,200);
//        } else {
//	        //2、停止视频播放
//	        //mMediaPlayer.stop();
//        	updateContents();
//        }
      }

      public void onDisplayChanged(int displayId)
      {
          KtvLog.d("DisplayDisplayDisplay #" + displayId + " changed.");
      }
      /**
       * 显示器移除监听
       * 移除流程
       * 1、记录当前播放的位置
       * 2、停止正在播放的视频
       * 3、更新辅助显示
       * 4、发送HDMI_REMOVE消息，重启播放。
       * */
      public void onDisplayRemoved(int displayId)
      {
          KtvLog.d("DisplayDisplayDisplay #" + displayId + " remove.");
//    	  if(mHandler.hasMessages(HDMI_ADD)) {
//    		  mHandler.removeMessages(HDMI_ADD);
//    	  }
//        if(mMediaPlayer != null){
//        	Message m = new Message();
//        	//1、记录当前播放的位置
//	        m.arg1 = tempPlayPosition == 0?mMediaPlayer.getCurrentPosition():tempPlayPosition;
//	        m.what = HDMI_REMOVE;
//	        tempPlayPosition = 0;
//
//        	//2、停止正在播放的视频，一定要在更新辅助显示之前停止视频播放，否则会报错。
//        	if(mMediaPlayer.isPlaying()) {
//        		mMediaPlayer.stop();
//        		m.obj = true;
//        	} else {
//        		m.obj = false;
//        	}
//        	mMediaPlayer.reset();
//        	Log.d(TAG, "Display #" + displayId + " removed.");
//
//        	//3、更新辅助显示
//        	updateContents();
//
//        	//4、发送HDMI_REMOVE消息，重启播放。
//        	mHandler.sendMessageDelayed(m, 10);
//        }else{
//        	LogUtil.e("显示器移除监听, mMediaPlayer :"+mMediaPlayer);
//        	updateContents();
//        }
      }
    };
    private AnimationDrawable frameAnimVideoBg;
    private AnimationDrawable frameAnimPresent;

//    public void updateContents() {
//    	Display[] displays = mDisplayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
//
//    	log("There are currently " + displays.length + " displays connected.");
//
//    	if (displays == null || displays.length == 0){
//    		hidePresentation();
//    	} else {
//    		log("displays number=" + displays.length);
//
//    		if(displays.length > 1) {
//    			log("showPresentation display is more than one!\n Only show first display.");
//    		}
//
//    		showPresentation(displays[0]);
//    	}
//
//    	for (Display display : displays){
//    		log("  " + display);
//    	}
//    }

//    private void hidePresentation(){
//    	if(mPresentation != null) {
//    		mPresentation.dismiss();
//    		mPresentation = null;
//    	}
//    }

    /**
     * 显示辅助异显
     */
//	private void showPresentation(Display display) {
//		try {
//			if(mPresentation != null) {
//				mPresentation.dismiss();
//			}
//			//显示辅助显示
//			mPresentation = new DemoPresentation(mContext, display);
//
//			mPresentation.show();
//
//			mVideoSurfaceRemote = mPresentation.getSurfaceView();
//			mVideoSurfaceRemote.setMediaPlayer(mMediaPlayer);
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//	}

    /**显示logo*/
	public void showLogo(){
//		if(mPresentation != null) {
//			mPresentation.showLogo();
//		}
	}

	/**隐藏logo*/
	public void hideLogo(){
//		if(mPresentation != null) {
//			mPresentation.hideLogo();
//		}
	}
    /**显示web手机点歌二维码*/
    public void showWebView(){
//        if(mPresentation != null) {
//            mPresentation.showWebView();
//        }
    }

    /**隐藏web手机点歌二维码*/
    public void hideWebView(){
//        if(mPresentation != null) {
//            mPresentation.hideWebView();
//        }
    }

    /**显示辅显视频view*/
    public void showMySurfaceView(){
//        if(mPresentation != null) {
//            mPresentation.showMySurfaceView();
//        }
    }
//
//	/**隐藏辅显视频view*/
//	public void hindMySurfaceView(){
//		if(mPresentation != null) {
//			mPresentation.hindMySurfaceView();
//		}
//	}
//
	/**辅显文字(气氛编辑文字)*/
	public void showTextView(String str, int color){
//		if(mPresentation != null) {
//			mPresentation.clickText(str, color);
//		}
	}

    public void showScore(String str, int color){
//        if(mPresentation != null) {
//            mPresentation.sendScoreDelay(str);
//        }
    }
//
//	/**
//	 * 主页控制时，异显图标
//	 * @param drawable 图标
//	 * @param isHide 是否自动隐藏
//	 * @param isMute 是否是暂停
//	 * @param isMute 是否是静音
//	 */
//	public void MainControlShowPraiseImage(Drawable drawable, boolean isHide, boolean isPaused, boolean isMute){
//		if(mPresentation != null) {
//			mPresentation.MainClickPraise(drawable, isHide, isPaused, isMute);
//		}
//	}
//
//	/**
//	 * 主页控制时，异显隐藏图片
//	 */
//	public void MainControlHidePraiseImage(boolean isPaused){
//		if(mPresentation != null) {
//			mPresentation.MainHideClickPraise(isPaused);
//		}
//	}
//
//	/**
//	 * 异显图标
//	 * @param drawable 图标
//	 */
//	public void showPraiseImage(Drawable drawable){
//		if(mPresentation != null) {
//			mPresentation.clickPraise(drawable);
//		}
//	}
//
//	/**显示报台*/
//	public void showTableNumberView(String str){
//		if(mPresentation != null) {
//			mPresentation.showTableNumberView(str);
//		}
//	}
//	public void hideTableNumberView(){
//		if(mPresentation != null) {
//			mPresentation.hideTableNumberView();
//		}
//	}
//
//	public void setScrollText(String string){
//		if(mPresentation != null) {
//			mPresentation.setScrollText(string);
//		}
//	}
//
//	public void startScrollText(){
//		if(mPresentation != null) {
//			mPresentation.startScrollText();
//		}
//	}
//
//	public void stopScrollText(){
//		if(mPresentation != null) {
//			mPresentation.stopScrollText();
//		}
//	}
//
    /**设置图片隐藏*/
    public void setPresentationBgHide(){
//        if(mPresentation != null) {
//            mPresentation.setBackground(null);
//        }
    }
    //
//	/**设置音乐图片*/
//	public void setPresentationBg(){
//		if(mPresentation != null) {
//			if(mBgAudio==null){
//				mBgAudio = getResources().getDrawable(R.drawable.bg_audio);
//			}
//			mPresentation.setBackground(mBgAudio);
//		}
//	}
//
//	/**设置轮播图片*/
////	public void setPresentationBgFrame(View videoBg){
////		if(mPresentation != null) {
////			if(frameAnimVideoBg!=null){
////				frameAnimVideoBg.stop();
////				frameAnimVideoBg=null;
////			}
////			if(frameAnimPresent!=null){
////				frameAnimPresent.stop();
////				frameAnimPresent=null;
////			}
////			// 为AnimationDrawable添加动画帧
////	    	frameAnimVideoBg = new AnimationDrawable();
////	    	frameAnimPresent = new AnimationDrawable();
////	    	for (int j = 0; j < BackgrBackgroundResArrayoundResArray.length; j++) {
////	    		Drawable drawable = getResources().getDrawable(BackgroundResArray[j]);
////	    		Drawable drawable2 = getResources().getDrawable(BackgroundResArray[j]);
//////	    		Bitmap bitmap = BitmapFactory.decodeFile(MyApplication.LOGO_IMG_PATH+i+MyApplication.LOGO_IMG_PNG);
//////	    		Drawable drawable = ViewUtil.bitmapToDrawble(bitmap, mContext);
////	    		frameAnimVideoBg.addFrame(drawable, 3000);
////	    		frameAnimPresent.addFrame(drawable2, 3000);
////			}
////	    	frameAnimVideoBg.setOneShot(false);
////			videoBg.setBackground(frameAnimVideoBg);
////			frameAnimVideoBg.start();
////			frameAnimPresent.setOneShot(false);
////			mPresentation.setBackground(frameAnimPresent);
////			frameAnimPresent.start();
////		}
////	}
//
////	public void setPresentationBackground(int res){
////		if(mPresentation != null) {
////			LogUtil.e("playBgMedia()  setBackground:"+res);
////			mPresentation.setBackground(res);
////		}
////	}
//
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //doDraw(canvas);
        KtvLog.d("in videoview onDraw");
    }
    /**
     * 显示辅助异显
     */
    private boolean showPresentation(Display display) {
//        try {
//            if(mPresentation != null) {
//                //mPresentation.dismiss();
////                if(mPresentation.isShowing())
//                    return false;
//            }
//
//
//            mPresentation = new Presentation(mContext, display, this);
//
//            mPresentation.show();
//
//            mVideoSurfaceRemote = mPresentation.getSurfaceView();
//            mVideoSurfaceRemote.setMediaPlayer(mMediaPlayer);
//            KtvLog.d("showPresentation over");
//
//        } catch(Exception e) {
//            e.printStackTrace();
//        }


        return true;
    }
    private void hidePresentation(){
        KtvLog.d("hidePresentation ");
//        if(mPresentation != null) {
//            mPresentation.dismiss();
//            mPresentation = null;
//        }
    }
    public boolean updateContents() {
        Display[] displays = mDisplayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);

        log("There are currently " + displays.length + " displays connected.");

        if (displays == null || displays.length == 0){
            hidePresentation();
        } else {
            log("displays number=" + displays.length);

            if(displays.length > 1) {
                log("showPresentation display is more than one!\n Only show first display.");
            }

            return showPresentation(displays[0]);
        }

        return false;

//        for (Display display : displays){
//            log("  " + display);
//        }

    }

    public void resetMedia(){
        mMediaPlayer.reset();
    }
    private void stopPlaybackInternal(){
        if(mMediaPlayer != null) {
           // mFrameProcess.stopGrabFrame();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mUri = null;
        }

//        if (null != VideoProcess.videoProcessInce) {
//            VideoProcess.videoProcessInce.UiThreadStop();
//        }
//
//        if(null != FrameProcess.inTance) {
//            FrameProcess.inTance.setallowGrabFrame(false);
//        }
//        if (null != MySurfaceView.getInstance()) {
//            MySurfaceView.getInstance().hideWindow(new String("magicwindow"));
//        }
//
//        PcmRecordUtil.getInstance().setStopThread();
    }
    public void stopPlayback(){
        stopPlaybackInternal();
    }
    public void destory(){
        stopPlayback();
        hidePresentation();
        if(mSurfaceHolder != null) {
            mSurfaceHolder.getSurface().release();
        }
//        if(mVideoSurfaceRemote != null && mVideoSurfaceRemote.getHolder() != null) {
//            mVideoSurfaceRemote.getHolder().getSurface().release();
//            mVideoSurfaceRemote = null;
//        }
    }
    //
    private void log(String s) {
        if (VIDEO_DBG)
            Log.d(TAG, s);
    }



//	public void setRoomType(int type) {
//    	Log.v(TAG, "setRoomType type="+type);
//		if(type < 0 || type > 8) {
//			Log.v(TAG, "setRoomType size="+type+"   error!!");
//			return;
//		}
//
//    	if(mToneValue != -1){
//    		Log.v(TAG, "has tone(音调), can not setRoomType!!");
//    		return;
//    	}
//
//    	if(mMediaPlayer != null && mMediaPlayer.isPlaying()) {
//    		Invoke.invokeMethod(mMediaPlayer, "setRoomType", new Object[]{type});
//    		mRoomType = type;
//    	}
//	}

    //	/**刷新二维码*/
//	public void updateWebQRcode(){
//		if(mPresentation!=null){
//			mPresentation.updateWebQRcode();
//		}
//	}
//
    private void readyOpenVideo(){
        isVideoOpening = true;
//		MyApplication.isVideoOpening = true;
    }

    private void openVideoFinish(){
        isVideoOpening = false;
//		MyApplication.isVideoOpening = false;
    }

    public void showOsd(Drawable drawable) {
//        if(null != mPresentation)
//            mPresentation.showOsd(drawable);
    }

    @Override
    public void setmute() {
        VolumeConfig.setMute();
    }

    @Override
    public void setunmute() {
//        VolumeConfig.setVolume(mPlayStatus.holdvolume);
        VolumeConfig.setUnMute();
    }

    @Override
    public void setpause() {
        pause();
    }

    @Override
    public void setcancelpause() {
        start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setaccompany() {
        //changeTrack();
        setTrack(PlaySong.getCurPlaySong().gettracknum(1));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setoriginal() {
        //changeTrack();
        setTrack(PlaySong.getCurPlaySong().gettracknum(0));
    }

    @Override
    public void changevolume(int volume) {

    }

    public void setAuraImage(int type) {
//        if(mPresentation != null){
//            mPresentation.setAuraImage( type );
//        }
    }

    public void setAudioTone(int tone) {
//        Log.v(TAG, "setAudioTone tone="+tone);
//        if(mMediaPlayer != null && !isVideoOpening && mMediaPlayer.isPlaying()) {
//            if(tone < 0 || tone > 11) {
//                Log.v(TAG, "setAudioTone tone="+tone+"   error!!");
//            }
//            float f = tone;
//            f = f - 5.0f;
//            Log.d("peter", "f="+f);
//            Invoke.invokeMethod(mMediaPlayer, "setPitch", new Object[]{f});
////            mMediaPlayer.setPitch(f);
//        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(timer!=null){
            timerTask.cancel();
            timer.cancel();
            timerTask = null;
            timer = null;
        }
    }
}


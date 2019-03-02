package com.shinetvbox.vod.certercontrol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.shinetvbox.vod.MainActivity;
import com.shinetvbox.vod.MyApplication;
import com.shinetvbox.vod.VideoView;
import com.shinetvbox.vod.dao.FreeSong;
import com.shinetvbox.vod.dao.PlaySong;
import com.shinetvbox.vod.dao.SelectSong;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessageSong;
import com.shinetvbox.vod.db.Query;
import com.shinetvbox.vod.db.Song;
import com.shinetvbox.vod.db.SongInfo;
import com.shinetvbox.vod.manager.DatabaseManager;
import com.shinetvbox.vod.manager.EventBusManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.manager.SongPlayManager;
import com.shinetvbox.vod.service.wechat.WechatService;
import com.shinetvbox.vod.status.PlayStatus;
import com.shinetvbox.vod.utils.KtvLog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.shinetvbox.vod.MyApplication.SHINESONGDDIR;
import static com.shinetvbox.vod.MyApplication.beginNum;
import static com.shinetvbox.vod.MyApplication.scanProcess;
import static com.shinetvbox.vod.utils.OnclickUtil.isFastDoubleClick;


public class ControlCenter {

    public static final int SELECT_SONG_SUCCESS = 10001;
    public static final int SELECT_SONG_FAILURE = 10002;
    public static final int RECORD_SONG_START = 10003;
    public static final int RECORD_SONG_STOP = 10004;


    public static boolean isOpenRoom = true;

    public static boolean isIsOpenRoom() {
        return isOpenRoom;
    }

    /**
     * 麦克风  默认音量大小
     */
    public static int micphoneVolume = 50;
    /**
     * 麦克风 递增/减小步长
     */
    public static int micphoneVolumeStepValue = 5;

    public static Handler handlerControlCenter;
    private static ResManager mResManager;

    private static PlayStatus mPlayStatus = null;
    private static VideoView mVideoView = null;
    private static Context mContext = null;
    /* 当前播放歌曲类型 */
    public static FreeSong mFreeSong = null;
    public static SelectSong mSelectSong = null;
    public static int ShinePort;

    //热门歌曲id
    public static List<String> listHotSongId = new ArrayList<>(  );
    //本地歌曲id
    public static List<String> listLocalSongId = new ArrayList<>(  );

    private static List<statusChangeLisener> listListener = new ArrayList<>();
    /**
     * 设置状态改变监听handler
     */
    public static void setStatusChangeLisener(statusChangeLisener lisener) {
        listListener.add( lisener );
    }
    private static void sendMessageStatusChange(String val) {
        for (statusChangeLisener scl : listListener) {
            if(scl!=null){
                scl.statusChange( val );
            }else{
                listListener.remove( scl );
            }
        }
    }
    public interface statusChangeLisener {
        void statusChange(String val);
    }


    private static List<Handler> listSongHandler = new ArrayList<>();
    /**
     * 设置歌曲选择监听handler
     */
    public static void setSongHandler(Handler handler) {
        listSongHandler.add( handler );
    }
    private static void sendMessageSongHandler(int what) {
        for (Handler handler : listSongHandler) {
            if(handler!=null){
                handler.sendEmptyMessage( what );
            }else{
                listListener.remove( handler );
            }
        }
    }

    public static void init(MainActivity mainActivity, int shineport) {
        if (mVideoView != null) return;
        ShinePort = shineport;
        mResManager = ResManager.getInstance();
        mPlayStatus = PlayStatus.getInstance();
        mVideoView = mainActivity.mVideoView;
        mContext = mainActivity;
        mPlayStatus.setVideoView( mVideoView );
        initVieoViewListener();
        InitAllSongTypeObject();
        initHandler();
//        if ( isEnableControlRoom())
//        {
//            ControlCenter.sendEmptyMessage(CMD_CLOSE_ROOM);
//        }
//        else
//        {
//            ControlCenter.sendEmptyMessage(CMD_OPEN_ROOM);
//        }
        getHotSongList();
        getLocalSongList();
    }

    private static void getHotSongList() {
        listHotSongId.clear();
        Query mQuery = new Query();
        mQuery.tablename = "top_song";
        mQuery.limit = 10000;
        List<SongInfo> songList = DatabaseManager.getInstance().getSongInfo( mQuery );
        if(songList==null) return;
        for( int i = 0; i < songList.size(); i++ ){
            listHotSongId.add( songList.get(i).song_id );
        }
    }

    private static void getLocalSongList() {
        listLocalSongId.clear();
        File dir = new File( MyApplication.SHINESONGDDIR );
        if (dir == null || !dir.exists() || !dir.isDirectory() || dir.list() == null)
            return;
        for (File file : dir.listFiles()) {
            if(file.getName().endsWith( ".mpg" )){
                listLocalSongId.add( file.getName().replace( ".mpg","" ) );
            }else if(file.getName().endsWith( ".mpg_tmp" )){
                file.delete();
            }
        }
    }

    public static void setLocalSongList(String songid,boolean isAdd) {
        if(isAdd){
            if(!listLocalSongId.contains( songid )){
                listLocalSongId.add( songid );
            }
        }else{
            if(listLocalSongId.contains( songid )){
                listLocalSongId.remove( songid );
            }
        }
    }

    private static void InitAllSongTypeObject() {

        PlaySong.setOutProc(new PlaySong.OutProc() {
                                 @Override
                                 public void onplay(String filename) {
                                     playvideo( filename );
                                 }

                                 @Override
                                 public void onsetvolume(int volume) {
                                     // VolumeConfig.setVolume( volume );
                                     // mMainActivityView.setProgress( volume );
                                     Class aClass[] = new Class[1];
                                     aClass[0] = android.app.Presentation.class;
                                     PlayStatus.getInstance().changeVolnoClass(volume, aClass);
                                 }

                                 @Override
                                 public void onstop() {
                                     handlerControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_PLAY_NEXT_DEFUALT );
                                 }

                                @Override
                                public void onreset() {
                                    handlerControlCenter.sendEmptyMessage( ControlCenterConstants.PLAY_RESET );
                                }

                                @Override
                                public void onrefreshsonginfo() {
                                    sendRefreshSongInfo();
                                }

//                                 @Override
//                                 public void onplayrecord(TapeInfo tape) {
//                                     playvideoandaudio( tape.song_id, tape.mp3path );
//                                 }
                             }
        );

        mFreeSong = FreeSong.getInstance();
        mSelectSong = SelectSong.getInstance();



        PlaySong.setCurPlaySong( mFreeSong );
        mFreeSong.SetisPlay( false );
    }

//    private static void playvideo(String song_id) {
//
//        String shinepath = null;
//        if (song_id.contains( "usb://" )) {
//            shinepath = song_id.replace( "usb://", "" );
//        }
//        else if(song_id.contains( "file://")){
//            shinepath = song_id.replace( "file://", "" );
//        }
//        else if(song_id.contains( "usersong_id")){
//            shinepath = "/mnt/sata2/"+ song_id + ".mpg";
//        }
//        else {
//            String song = "/mnt/sata2/" + song_id + ".mpg";//sata2
//            shinepath = "http://127.0.0.1:" + ShinePort + "/index.php?songID=5" + song + "&user=12344&passwd=1234567890";
//        }
//        mVideoView.setVideoPath( shinepath );
//    }

    private static void playvideo(String song_id) {

        String shinepath = null;
        if (song_id.contains( "usb://" )) {
            shinepath = song_id.replace( "usb://", "" );
        }
        else if(song_id.contains( "file://")){
            shinepath = song_id.replace( "file://", "" );
        }
        else if(song_id.contains( "usersong_id")){
            shinepath = SHINESONGDDIR + song_id + ".mpg";
        }
        else if(song_id.contains( "tv:")){
            shinepath = "http://" + song_id.replace( "tv:","" );
        }
        else {
            if(scanProcess) {
            String song = new String( SHINESONGDDIR + song_id);
//                String song = new String(SHINESONGDDIR2 + song_id);
                shinepath = "http://127.0.0.1:" + ShinePort + "/5" + song ;// lbhttp
            }
            else {
                String song = new String( SHINESONGDDIR + song_id + ".mpg" );///mnt/sata2/ mnt/media_rw/989EBE129EBDE8C0/
                shinepath = "http://127.0.0.1:" + ShinePort + "/5" + song ;// lbhttp
            }
//
//            shinepath = "http://127.0.0.1:" + ShinePort + "/index.php?songID=5" + song + "&user=12344&passwd=1234567890";//shinehttp

        }
        mVideoView.setVideoPath( shinepath );
    }

//    private static void playvideoandaudio(String song_id, String mp3path) {
//        String shinepath = null;
//        //String song = new String ("/sdcard/"+song_id+".mpg");
//        String song = new String( "/mnt/sata2/" + song_id + ".mpg" );//sata2
//        shinepath = "http://127.0.0.1:" + ShinePort + "/index.php?songID=5" + song + "&user=12344&passwd=1234567890";
//        mVideoView.setVideoPath( shinepath, mp3path );
//    }
    private static void playvideoandaudio(String song_id, String mp3path) {
        String shinepath = null;
        if (song_id.contains( "usb://" )) {
            shinepath = song_id.replace( "usb://", "" );
        }
        else if(song_id.contains( "file://")){
            shinepath = song_id.replace( "file://", "" );
        }
        else if(song_id.contains( "tv:")){
            shinepath = "http://" + song_id.replace( "tv:","" );
        }
        else {
            String song = new String( SHINESONGDDIR + song_id + ".mpg" );///mnt/sata2/ mnt/media_rw/989EBE129EBDE8C0/
//            shinepath = "http://127.0.0.1:" + ShinePort + "/index.php?songID=5" + song + "&user=12344&passwd=1234567890";//shinehttp
            shinepath = "http://127.0.0.1:" + ShinePort + "/5" + song ;// lbhttp
        }
        mVideoView.setVideoPath( shinepath, mp3path );
    }

    static boolean uio;

    private static void initVieoViewListener() {
        mVideoView.setOnCompletionListener( new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                // 通知去播放下一首
                handlerControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_PLAY_NEXT_DEFUALT );
            }
        } );
        mVideoView.setOnErrorListener( new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                //handlerControlCenter.sendEmptyMessageDelayed(ControlCenterConstants.SONG_PLAY_NEXT, 1000);
                return true;
            }
        } );
        mVideoView.setOnPreparedListener( new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer arg0) {
                if (SelectSong.getInstance() == PlaySong.getCurPlaySong()) {

                }
                KtvLog.d("ControlCenter  setOnPreparedListener");
                mVideoView.start();

                handlerControlCenter.sendEmptyMessageDelayed( ControlCenterConstants.SONG_KEEP_SAME_TRACK, 100 );
                isFastDoubleClick();

                if(scanProcess) {
                    File file =new File("/sdcard/verconunt");
                    Writer out = null;
                    try {
                        out = new FileWriter(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    beginNum++;
                    String data= Integer.toString(beginNum);
                    try {
                        out.write(data);
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mScanService.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2 * 1100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            ControlCenter.sendEmptyMessage(ControlCenterConstants.SONG_PLAY_NEXT_DEFUALT);
                        }
                    });
                }

//                new Thread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        if(uio)
//                            return;
//                        uio = true;
//                        while (uio){
//                            try {
//                                Thread.sleep(5 * 1100);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//
////                            if(null != VideoProcess.videoProcessInce)
////                                VideoProcess.videoProcessInce.play();
//                            ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_PLAY_NEXT_DEFUALT );
//                        }
//                    }
//                }).start();

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(uio)
//                            return;
//                        uio = true;
//                        File f1 = new File("/sdcard/save.yuv");
////                                       if (f1.exists()==false){
////                                		                f1.getParentFile().mkdirs();
////                               	            }
//
//                        FileOutputStream fos = null;
//                        try {
//                            fos = new FileOutputStream(f1);
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                        setGetPicEnable(arg0, true);
//                        while (uio){
////                            myFrame.logFrame();
//                            VideoPic videopicyuv = (VideoPic) getVideoPic(arg0);
//                            try {
//                                Thread.sleep(450);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//
////                            if(null != VideoProcess.videoProcessInce)
////                                VideoProcess.videoProcessInce.play();
//                            //ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_PLAY_NEXT_DEFUALT );
////
//                            int size = videopicyuv.width * videopicyuv.height * 3 / 2;
//                            KtvLog.d("width is " + videopicyuv.width + " height is " + videopicyuv.height);
//                            try {
//                                fos.write(videopicyuv.picData,0,size);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }).start();
            }
        } );
    }

    static ExecutorService mScanService = Executors.newSingleThreadExecutor();

    public static void sendMessage(Message msg){
        if(handlerControlCenter == null) return;
        if (handlerControlCenter.hasMessages( msg.what )) {
            handlerControlCenter.removeMessages( msg.what );
        }
        handlerControlCenter.sendMessage( msg );
    }
    public static void sendMessageDelay(Message msg, int delay){
        if(handlerControlCenter == null) return;
        handlerControlCenter.hasMessages( msg.what,msg );
        if (handlerControlCenter.hasMessages( msg.what )) {
            handlerControlCenter.removeMessages( msg.what );
        }
        handlerControlCenter.sendMessageDelayed( msg,delay );
    }
    synchronized public  static void sendEmptyMessage(int what){
        if(handlerControlCenter == null) return;
        if (handlerControlCenter.hasMessages( what )) {
            handlerControlCenter.removeMessages( what );
        }
        handlerControlCenter.sendEmptyMessage( what );
    }
    synchronized public static void sendEmptyMessageDelay(int what,int delay){
        if(handlerControlCenter == null) return;
        if (handlerControlCenter.hasMessages( what )) {
            handlerControlCenter.removeMessages( what );
        }
        handlerControlCenter.sendEmptyMessageDelayed( what,delay );
    }
    @SuppressLint("HandlerLeak")
    synchronized private static void initHandler() {
        handlerControlCenter = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    //切换歌曲时保持原来音轨
                    case ControlCenterConstants.SONG_KEEP_SAME_TRACK:
                        setKeepSameTrack();
                        break;
                    //Activity onPause 停止播放歌曲
                    case ControlCenterConstants.SONG_STOP_PLAYBACK:
                        setStopPlayBack();
                        break;
                    //静音/非静音
                    case ControlCenterConstants.MUSIC_VOLUME_MUTE_UNMUTE:
                        setMuteOrUnmuteForApp( 0 );
                        break;
                    case ControlCenterConstants.MUSIC_VOLUME_MUTE:
                        setMuteOrUnmuteForApp( 1 );
                        break;
                    case ControlCenterConstants.MUSIC_VOLUME_UNMUTE:
                        setMuteOrUnmuteForApp( 2 );
                        break;
                    case ControlCenterConstants.SONG_SET_VOLUME:
                        setMusicVolumeValueForApp( (int)msg.obj );
                        break;
                    //播放
                    case ControlCenterConstants.SONG_PLAY_PAUSE:
                        setPlayOrPauseForApp( 0 );
                        break;
                    case ControlCenterConstants.SONG_PLAY:
                        setPlayOrPauseForApp( 1 );
                        break;
                    case ControlCenterConstants.SONG_PAUSE:
                        setPlayOrPauseForApp( 2 );
                        break;
                    //原/伴唱
                    case ControlCenterConstants.SONG_ORIGINAL_ACCOMPANY:
                        setOriginalOrAccompanyForApp( 0 );
                        break;
                    case ControlCenterConstants.SONG_ORIGINAL:
                        setOriginalOrAccompanyForApp( 1 );
                        break;
                    case ControlCenterConstants.SONG_ACCOMPANY:
                        setOriginalOrAccompanyForApp( 2 );
                        break;
                    //音乐音量
                    case ControlCenterConstants.MUSIC_VOLUME_ADD:
                        setMusicVolumeForApp( true );
                        break;
                    case ControlCenterConstants.MUSIC_VOLUME_SUBTRACT:
                        setMusicVolumeForApp( false );
                        break;
                    //麦克风音量
                    case ControlCenterConstants.MIC_VOLUME_ADD:
                        setMicVolumeForApp( true );
                        break;
                    case ControlCenterConstants.MIC_VOLUME_SUBTRACT:
                        setMicVolumeForApp( false );
                        break;
                    //下一首
                    case ControlCenterConstants.SONG_PLAY_NEXT:
                        setNextSongForApp();
                        break;
                    case ControlCenterConstants.SONG_PLAY_NEXT_DEFUALT:
                        setNextSongForApp2();
                        break;
                    case ControlCenterConstants.PLAY_RESET:
                        mVideoView.resetMedia();
                        break;
                    //选择歌曲
                    case ControlCenterConstants.SONG_SELECT:
                        setSelectSongForApp( (SelectSongParams) msg.obj );
                        break;
                    //重播
                    case ControlCenterConstants.SONG_REPLAY:
                        setReplaySongForApp();
                        break;
                    //录音
                    case ControlCenterConstants.SONG_RECORD:
                        setRecorderAudioForApp();
                        break;
//                    //气氛
//                    case ControlCenterConstants.ATMOSPHERE_APPLAUSE:
//                        setAtmosphereForApp( 0 );
//                        break;
//                    case ControlCenterConstants.ATMOSPHERE_PRAISE:
//                        setAtmosphereForApp( 1 );
//                        break;
//                    case ControlCenterConstants.ATMOSPHERE_FLOWER:
//                        setAtmosphereForApp( 2 );
//                        break;
//                    case ControlCenterConstants.ATMOSPHERE_HUGS:
//                        setAtmosphereForApp( 3 );
//                        break;
//                    case ControlCenterConstants.ATMOSPHERE_EGGS:
//                        setAtmosphereForApp( 4 );
//                        break;
//                    case ControlCenterConstants.ATMOSPHERE_AMAZED:
//                        setAtmosphereForApp( 5 );
//                        break;
//
//                    case ControlCenterConstants.PLAY_GAME:
//                        if( msg.arg1 == 0 ) {
//                        SongInfo sInf = (SongInfo) msg.obj;
//                        //GameSong.getInstance().SetSongId(id);
////                        GameSong.getInstance().SetSongInfo(sInf);
////                        GameSong.getInstance().setGameSongPlay(true);
////                        GameSong.getInstance().onstartproc();
//                        ControlCenter.sendEmptyMessageDelay(ControlCenterConstants.SONG_ACCOMPANY, 4000);
//                        //ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_KEEP_SAME_TRACK );
//                    }
//                        else if( msg.arg1 == 1 ){
////                            if( GameSong.getInstance().isPlay ) {
////                                GameSong.getInstance().setGameSongPlay(false);
////                                GameSong.getInstance().onstopproc();
////                            }
//                        }
//                        break;
                    case ControlCenterConstants.SCORE_OPEN_FUNCTION:
                        setScoreState(true);
                        break;
                    case ControlCenterConstants.SCORE_CLOSE_FUNCTION:
                        setScoreState(false);
                        break;
                    case ControlCenterConstants.RECORD_START:
                        setRecordForApp(true);
                        break;
                    case ControlCenterConstants.RECORD_STOP:
                        setRecordForApp(false);
                        break;
                    case ControlCenterConstants.COMPUTER_SHUTDOWN:
                        //发送关机广播
                        SendShutDownBroadcast();
                        break;

                }
            }
        };
    }

    private static void SendShutDownBroadcast(){
        Intent intent = new Intent( "action.uniwin.shutdown" );
        //intent.putExtra( "autostart",false );
        mContext.sendBroadcast( intent );
    }

    /**
     * 设置话筒音量<br>
     *
     * @param isAdd 加/减
     */
    private static void setMicVolumeForApp(boolean isAdd) {
        if (isAdd) {
            if (micphoneVolume == 100) return;
            micphoneVolume += micphoneVolumeStepValue;
            if (micphoneVolume > 100) {
                micphoneVolume = 100;
            }
        } else {
            if (micphoneVolume == 0) return;
            micphoneVolume -= micphoneVolumeStepValue;
            if (micphoneVolume < 0) {
                micphoneVolume = 0;
            }
        }
    }

    /**
     * 设置界面音乐音量<br>
     *
     * @param isAdd 加/减
     */
    private static void setMusicVolumeForApp(boolean isAdd) {
        if (isAdd) {
            mPlayStatus.changeVol( mPlayStatus.getholdvolume() + 5 );
        } else {
            mPlayStatus.changeVol( mPlayStatus.getholdvolume() - 5 );
        }
    }

    private static void setNextSongForApp2() {
        /* 因该不会有有问题，以后做网络版，把media相关统一放到独立线程️中，只允许此线程操作media，ui线程的media回调也只允许将media操作任务分配到该线程*/
        if (mVideoView.isVideoOpening())
        {
            KtvLog.d("setNextSongForApp2 isVideoOpening return");
            return;
        }

//        if(!CloudMessageProc.getInTance().getSendDownSongAllow()){
//            ToastCenter.getInTance().sendToastEvent("在当前下载完成后才可播放下一曲");
//            return;
//        }
//        mPcmRecordUtil.setStopThread();
        //mVideoView.showOsd( mResManager.getDrawable( "osd_playnext" ) );

        PlaySong.executeupdatepre();
        PlaySong mPlaySong = PlaySong.getCurPlaySongListPre();
        if (mPlaySong == null) {
            mPlaySong = FreeSong.getInstance();
        }
        PlaySong.setCurPlaySong( mPlaySong );
        //MainActivity.handlerLogin.sendEmptyMessageDelayed(MainActivity.PLAY_SONG, 0);
        if (mPlayStatus.getIsPause()) {
            mPlayStatus.setcancelpausenothis( mVideoView );
        }
        mPlaySong.onstartproc();

        sendMessageSongHandler( RECORD_SONG_STOP );

        sendRefreshSongInfo();
    }


    /**
     * 切换歌曲
     */
    private static void setNextSongForApp() {
        if (mVideoView.isVideoOpening())
        {
            KtvLog.d("setNextSongForApp isVideoOpening return");
            return;
        }

//        if(!CloudMessageProc.getInTance().getSendDownSongAllow()){
//
//            ToastCenter.getInTance().sendToastEvent("在当前下载完成后才可播放下一曲");
//
//            return;
//        }
//        mPcmRecordUtil.setStopThread();
//        mVideoView.showOsd( mResManager.getDrawable( "osd_playnext" ) );

        PlaySong.executeupdatepre();
        PlaySong mPlaySong = PlaySong.getCurPlaySongListPre();
        if (mPlaySong == null) {
            mPlaySong = FreeSong.getInstance();
        }
        PlaySong.setCurPlaySong( mPlaySong );

        if (mPlayStatus.getIsPause()) {
            mPlayStatus.setcancelpausenothis( mVideoView );
        }
        mPlaySong.onstartproc();

        sendMessageSongHandler( RECORD_SONG_STOP );

        sendRefreshSongInfo();
    }

    private static void setPlaySongStartProcForApp() {
        PlaySong mPlaySong;
        if ((mPlaySong = PlaySong.getCurPlaySong()) == null) {
            PlaySong.setCurPlaySong( FreeSong.getInstance() );
            mPlaySong = FreeSong.getInstance();
        }

        if (mPlayStatus.getIsPause()) {
            mPlayStatus.setcancelpausenothis( mVideoView );
        }
        mPlaySong.onstartproc();
    }

    /**
     * 选择歌曲歌曲
     *
     * @param params SelectSongParams
     */
    private static void setSelectSongForApp(SelectSongParams params) {
        if(params == null) return;
        if(params.isAdd){
            Query mQuery = new Query();
            if(!TextUtils.isEmpty( params.song_id )){
                mQuery.song_id = params.song_id;
            }
            if(!TextUtils.isEmpty( params.song_name )){
                mQuery.song_name = params.song_name;
            }
            if(!TextUtils.isEmpty( params.singer_name )){
                mQuery.singer_name = params.singer_name;
            }
            mQuery.limit = 1;
            List<SongInfo> aSong = DatabaseManager.getInstance().getSongInfo( mQuery );
            if(aSong!=null && aSong.size()>0){
                if(params.first.equals( "1" )){
                    SongPlayManager.prioritySong(  aSong.get( 0 ) );
                }else{
                    SongPlayManager.addSong(  aSong.get( 0 ) );
                }
            }
        }else {
            SongPlayManager.delSong( params.song_id );
        }
    }

    /**
     * 重唱
     */
    private static void setReplaySongForApp() {
        if (!mVideoView.isPlaying()) {
            KtvLog.d("setReplaySongForApp isPlaying  return");
            return;
        }
//        if(!CloudMessageProc.getInTance().getSendDownSongAllow()){
//            KtvLog.d("\"在当前下载完成后才可重唱\"");
//            ToastCenter.getInTance().sendToastEvent("在当前下载完成后才可重唱");
//
//            return;
//        }
//        mPcmRecordUtil.setStopThread();
//        mVideoView.showOsd( mResManager.getDrawable( "osd_replay" ) );
        if (mPlayStatus.getIsPause()) {
            mPlayStatus.setcancelpausenothis( mVideoView );
        }
        if (null != mVideoView) {
            mVideoView.replayvideo();
        }
    }

    /**
     * 打开关闭录音
     * @param isOpen
     */
    private static void setRecordForApp(boolean isOpen) {
//        if(mVideoView.isPlaying() && (SelectSong.getInstance() == PlaySong.getCurPlaySong())) {
//            if(isOpen){
//                mPcmRecordUtil.setStopThread();
//                mVideoView.showOsd( mResManager.getDrawable( "osd_recording" ) );
//                if (mPlayStatus.getIsPause()) {
//                    mPlayStatus.setcancelpausenothis( mVideoView );
//                }
//                if (null != mVideoView)
//                    mVideoView.replayvideo();
//                //new Thread(mPcmRecordUtil.new Pcm2Mp3Runnable(new TapeInfo(PlaySong.getCurPlaySong().getmSomgInfo().clone()))).start();
//                mPcmRecordUtil.setOpenByKey("mp3", new TapeInfo(PlaySong.getCurPlaySong().getmSomgInfo().clone()));
//                sendMessageSongHandler( RECORD_SONG_START );
//            }else{
//                mPcmRecordUtil.setUploadRecordMp3(false);
//                mVideoView.showOsd( mResManager.getDrawable( "osd_record_close" ) );
//                sendMessageSongHandler( RECORD_SONG_STOP );
////                setNextSongForApp();
//            }
//        }
    }

    /**
     * 原唱、伴唱
     * @param type 0原/伴唱 1原唱 2伴唱
     */
    private static long timeOriginalOrAccompany = 0;
    private static void setOriginalOrAccompanyForApp(int type) {
        if(System.currentTimeMillis() - timeOriginalOrAccompany < 500) return;
        timeOriginalOrAccompany = System.currentTimeMillis();
        if (type == 0) {
            if (mPlayStatus.getIsAccompany()) {
                mPlayStatus.setoriginal();
            } else {
                mPlayStatus.setaccompany();
            }
        } else if (type == 1) {
            mPlayStatus.setoriginal();
        } else if (type == 2) {
            mPlayStatus.setaccompany();
        }
        sendButtonStatusWechat();
    }

    /**
     * 切换歌曲时保持原来音轨
     */
    private static void setKeepSameTrack() {
        int staus = 0;
        /* 伴唱 1， 原唱0*/
        if (mPlayStatus.getIsAccompany()) {
            staus = 1;
        } else {
            staus = 0;
        }
        mVideoView.setTrack( PlaySong.getCurPlaySong().gettracknum( staus ) );
        sendButtonStatusWechat();
    }

    /**
     * Activity onPause 停止播放歌曲
     */
    private static void setStopPlayBack() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
        sendButtonStatusWechat();
    }

    /**
     * 播放、暂停
     *
     * @param type 0播放/暂停 1播放 2暂停
     */
    private static void setPlayOrPauseForApp(int type) {
        if (type == 0) {
            if (mPlayStatus.getIsPause()) {
                mPlayStatus.setcancelpause();
            } else {
                mPlayStatus.setpause();
            }
        } else if (type == 1) {
            mPlayStatus.setcancelpause();
        } else if (type == 2) {
            mPlayStatus.setpause();
        }
        sendButtonStatusWechat();
    }
    /**
     * 设置音乐音量
     * @param value 音量大小
     */
    private static void setMusicVolumeValueForApp(int value) {
        mPlayStatus.changeVol( value );
        mPlayStatus.getholdvolume();
        if (value>mPlayStatus.getholdvolume()) {
            mPlayStatus.changeVol( mPlayStatus.getholdvolume() + 5 );
        } else if (value<mPlayStatus.getholdvolume()) {
            mPlayStatus.changeVol( mPlayStatus.getholdvolume() - 5 );
        }
        sendButtonStatusWechat();
    }
    /**
     * 静音、取消静音
     * @param type 0静音/取消静音 1静音 2取消静音
     */
    private static void setMuteOrUnmuteForApp(int type) {
        if (type == 0) {
            if (mPlayStatus.getIsMute()) {
                mPlayStatus.setunmute();
            } else {
                mPlayStatus.setmute();
            }
        } else if (type == 1) {
            mPlayStatus.setmute();
        } else if (type == 2) {
            mPlayStatus.setunmute();
        }
        sendButtonStatusWechat();
    }

    /**
     * 录音 未处理
     */
    private static void setRecorderAudioForApp() {

    }

//    /**
//     * 气氛
//     * @param type 0 给点掌声 1一百个赞 2鲜花一朵 3么么哒 4疯狂砸蛋 5我嘞个去
//     */
//    private static void setAtmosphereForApp(int type) {
//        mVideoView.setAuraImage( type );
//    }

    /**
     * 打开关闭评分
     * @param isOpen
     */
    private static void setScoreState(boolean isOpen) {
//        DialogEffect.getInstance().setRecordingState( isOpen,true );
    }

    public static boolean getIsVideoOpening() {
        if (mVideoView == null) return true;
        return mVideoView.isVideoOpening();
    }
    public static class SelectSongParams{
        public boolean isAdd = true;
        public String first = "";
        public String song_id = "";
        public String song_name = "";
        public String singer_name = "";
    }


    private static void sendButtonStatusWechat(){
        if(WechatService.getInstance()!=null){
            WechatService.getInstance().sendButtonStatus();
        }
    }
    public static void sendRefreshSongInfo(){
        EventBusMessageSong msg = new EventBusMessageSong();
        msg.what = EventBusConstants.SONG_PLAY_CHANGE;
        EventBusManager.sendMessage( msg );
    }

    public static boolean getMediaPlayerIsOpening(){
        if(mPlayStatus==null) return false;
        return mVideoView.isVideoOpening();
    }
    public static int getIsPalying(){
        int st = 1;
        if(mPlayStatus!=null && mPlayStatus.getIsPause()){
            st = 0;
        }
        return st;
    }

    public static int getIsMute(){
        int st = 0;
        if(mPlayStatus!=null && mPlayStatus.getIsMute()){
            st = 1;
        }
        return st;
    }

    public static int getIsAccompany(){
        int st = 1;
        if(mPlayStatus!=null && mPlayStatus.getIsAccompany()){
            st = 0;
        }
        return st;
    }

    public static int getMusicVolume(){
        if(mPlayStatus!=null){
            return mPlayStatus.getholdvolume();
        }
        return 50;
    }
}

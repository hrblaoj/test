package com.shinetvbox.vod.service.wechat;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.shinetvbox.vod.certercontrol.ControlCenter;
import com.shinetvbox.vod.certercontrol.ControlCenter.SelectSongParams;
import com.shinetvbox.vod.certercontrol.ControlCenterConstants;
import com.shinetvbox.vod.data.constants.Constants;
import com.shinetvbox.vod.data.custom.JsonData;
import com.shinetvbox.vod.manager.MemberManager;
import com.shinetvbox.vod.socket.MySocket;
import com.shinetvbox.vod.utils.QrcodeUtil;
import com.shinetvbox.vod.utils.updateapp.HttpConstant;
import com.shinetvbox.vod.view.fragment.pay.FragmentPagePay;
import com.shinetvbox.vod.websocket.MyWebSocket;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class WechatService  extends Service {

    private static WechatService mInstance;

    private MyWebSocket myWebSocket;
    private Handler handler;
    private String address = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static WechatService getInstance(){
        return mInstance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initHandler();
        return START_NOT_STICKY;
    }

    public void initWebAddress(){
        getServerAddress();
        getWechatQrcode();
    }

    private void initSocket(){
//        String address = "ws://120.92.154.70:2018/M_00d0339ab454?mac=00d0339ab454&sign=E376FC978EAD98C0A2F2978345DB78D5";
        if(myWebSocket==null && !TextUtils.isEmpty( address )){
            myWebSocket = new MyWebSocket( address,10 );
            myWebSocket.setOnChangeHandler( handler );
            myWebSocket.connect();
        }
    }

    private void getServerAddress() {
        if(!TextUtils.isEmpty( address )) return;
        OkHttpUtils.post()
                .url( HttpConstant.urlGetAppWechatWebSocketInfo )
                .tag( HttpConstant.urlGetAppWechatWebSocketInfo )
                .id( MyStringCallback.WECHAT_WEBSOCKET_ADDRESS )
                .params( HttpConstant.getAppWebSocketInfoPostParams() )
                .build()
                .connTimeOut( 10000 )
                .readTimeOut( 10000 )
                .execute( new MyStringCallback() );
    }

    private void getWechatQrcode() {
        if(!TextUtils.isEmpty( Constants.WECHAT_ARCODE_PATH )) return;
        OkHttpUtils.post()
                .url( HttpConstant.urlGetAppWechatQrcodeInfo )
                .tag( HttpConstant.urlGetAppWechatQrcodeInfo )
                .id( MyStringCallback.WECHAT_QRCODE )
                .params( HttpConstant.getAppWechatQrcodePostParams() )
                .build()
                .connTimeOut( 10000 )
                .readTimeOut( 10000 )
                .execute( new MyStringCallback() );
    }

    private class MyStringCallback extends StringCallback {
        public static final int WECHAT_WEBSOCKET_ADDRESS = 0;
        public static final int WECHAT_QRCODE = 1;
        public MyStringCallback() {
        }
        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
        }
        @Override
        public void onResponse(String response, int id) {
            JsonData jsonData = new JsonData( response );
            if(jsonData.getValueString( "code" ).equals( "0" )){
                if(id == WECHAT_WEBSOCKET_ADDRESS){
                    Log.i( "222222222222","111111111===="+response );
                    if(TextUtils.isEmpty( address )){
                        address = jsonData.getValueString( "result" );
                        initSocket();
                    }
                }else if(id == WECHAT_QRCODE){
                    Log.i( "222222222222","3222222222===="+response );
                    if(TextUtils.isEmpty( Constants.WECHAT_ARCODE_PATH )){
                        Constants.WECHAT_ARCODE_PATH = jsonData.getSection( "result" ).getValueString( "url" );
                        QrcodeUtil.startLoadWechatQrcode();
                    }
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private void initHandler() {
        handler = new Handler(  ){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MySocket.EVENT_SOCKET_CONNECT:
                        Log.i( "shinetvbox","微信点歌服务器连接成功!" );
                        sendButtonStatus();
                        break;
                    case MySocket.EVENT_SOCKET_DISCONNECT:
                        break;
                    case MySocket.EVENT_SOCKET_RECEIVE_DATA:
                        if(msg.obj.toString().contains( "@{" )){
                            analysisJson( (String) msg.obj );
                            Log.i( "shinetvbox","-receive："+msg.obj );
                        }
                        break;
                }
            }
        };
    }

    public void sendButtonStatus(){
        if(myWebSocket == null || !MemberManager.isMember || !myWebSocket.isConnected()) return;
        myWebSocket.sendMessage( WechatConstant.getButtonState( ControlCenter.getIsPalying(),
                ControlCenter.getMusicVolume(),ControlCenter.getIsMute(),
                ControlCenter.getIsAccompany() ) );
    }

    public void sendSelectSongList(){
        if(myWebSocket == null || !MemberManager.isMember || !myWebSocket.isConnected()) return;
        myWebSocket.sendMessage( WechatConstant.getSelectSongList() );
    }
    private void analysisJson(String obj) {
        int ind = obj.indexOf( "@" );
        String cmd = obj.substring( 0,ind );
        JsonData jd = new JsonData( obj.substring( ind+1,obj.length() ) );
        Message msg = new Message();
        SelectSongParams params;
        switch(cmd){
            //播放/暂停
            case "play":
                if(jd.getValueString( "play" ).equals( "0" )){
                    ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_PAUSE );
                }else if(jd.getValueString( "play" ).equals( "1" )){
                    ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_PLAY );
                }
                break;
            //切歌
            case "cut":
                ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_PLAY_NEXT );
                break;
            //原唱/伴唱
            case "track":
                if(jd.getValueString( "track" ).equals( "0" )){
                    ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_ACCOMPANY );
                }else if(jd.getValueString( "track" ).equals( "1" )){
                    ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_ORIGINAL );
                }
                break;
            //重唱
            case "replay":
                ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_REPLAY );
                break;
            //静音/非静音
            case "mute":
                if(jd.getValueString( "mute" ).equals( "0" )){
                    ControlCenter.sendEmptyMessage( ControlCenterConstants.MUSIC_VOLUME_UNMUTE );
                }else if(jd.getValueString( "mute" ).equals( "1" )){
                    ControlCenter.sendEmptyMessage( ControlCenterConstants.MUSIC_VOLUME_MUTE );
                }
                break;
            //音量
            case "volume":
                msg.what = ControlCenterConstants.SONG_SET_VOLUME;
                msg.obj = jd.getValueInt( "musicvolume" );
                ControlCenter.sendMessage( msg );
                break;
            //服务
            case "server":
                break;
            //评分
            case "score":
                break;
            //点歌 sid歌曲编号 first是否优先（点播0/优先1）
            case "demand":
                msg.what = ControlCenterConstants.SONG_SELECT;
                params = new SelectSongParams();
                params.song_id = jd.getValueString( "sid" );
                params.first = jd.getValueString( "first" );
                params.isAdd = true;
                msg.obj = params;
                ControlCenter.sendMessage( msg );
                break;
            //点歌 sid歌曲编号 first是否优先（点播0/优先1）
            case "delete":
                msg.what = ControlCenterConstants.SONG_SELECT;
                params = new SelectSongParams();
                params.song_id = jd.getValueString( "sid" );
                params.first = jd.getValueString( "first" );
                params.isAdd = false;
                msg.obj = params;
                ControlCenter.sendMessage( msg );
                break;
            default:
                break;
        }

//        Log.i( "222222222222222",obj.substring( 0,ind )+"======"+obj.substring( ind+1,obj.length() ) );
    }

    private void stopWebSocket(){
        if(myWebSocket!=null){
            myWebSocket.onDestroy();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopWebSocket();
    }
}

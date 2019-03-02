package com.shinetvbox.vod.websocket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.shinetvbox.vod.utils.KtvLog;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyWebSocket {

    /**连接成功*/
    public static final int EVENT_SOCKET_CONNECT = 0;
    /**连接断开、未连接成功*/
    public static final int EVENT_SOCKET_DISCONNECT = 1;
    /**接收到消息*/
    public static final int EVENT_SOCKET_RECEIVE_DATA = 2;

    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private MyThread reConnectThread = new MyThread();

    // 要连接的服务器地址
    private String address = "";
    private URI uri;
    private static final String TAG = "WebSocket";
    private WebSocketClient mWebSocketClient;

    private String loginInfo = null;
    private Handler onChangeHandler = null;
    private int reconnectInterval = 15000;
    public boolean isConnecting = false;

    public MyWebSocket(String address){
        this(address,0);
    }

    /**
     * 构造函数
     * @param address
     * @param outTime 单位秒
     */
    public MyWebSocket(String address,int outTime){
        this.address = address;
        try {
            uri = new URI(address);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (null == mWebSocketClient) {
            mWebSocketClient = new WebSocketClient(uri,new HashMap<String, String>(  ) ) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    log("onOpen: "+serverHandshake);
                    KtvLog.d("MyWebSocket  onOpen bture is " + (Looper.getMainLooper() == Looper.myLooper()));
                    isConnecting = false;
                    if(loginInfo!=null && !loginInfo.equals( "" )){
                        sendMessage( loginInfo );
                    }
                    sendMsgToHandler( EVENT_SOCKET_CONNECT, null );
                }
                @Override
                public void onMessage(String s) {
                    log("onMessage: " + s);
                    KtvLog.d("MyWebSocket  onMessage bture is " + (Looper.getMainLooper() == Looper.myLooper()));
                    sendMsgToHandler( EVENT_SOCKET_RECEIVE_DATA, s );
                }
                @Override
                public void onClose(int i, String s, boolean b) {
                    log("onClose: "+s+" /// code:"+i+" /// b:"+b);
                    isConnecting = false;
                    KtvLog.d("MyWebSocket  onClose bture is " + (Looper.getMainLooper() == Looper.myLooper()));
                    reConnectSocket();
                    sendMsgToHandler( EVENT_SOCKET_DISCONNECT, null );
                }
                @Override
                public void onError(Exception e) {
                    isConnecting = false;
                    log("onError: "+e);
                    KtvLog.d("MyWebSocket  onError bture is " + (Looper.getMainLooper() == Looper.myLooper()));
                    //reConnectSocket();
                    //sendMsgToHandler( EVENT_SOCKET_DISCONNECT, null );
                }
            };
        }
        mWebSocketClient.setConnectionLostTimeout( outTime );
    }

//    public boolean isConnected(){
//        if(mWebSocketClient)
//    }

    public void setLoginInfo(String loginInfo){
        this.loginInfo = loginInfo;
    }

    /**
     * socket开始连接
     */
    public void connect() {
        connect(-1);
    }
    public void connect(int reconnectInterval) {
        isConnecting = true;
        this.reconnectInterval = reconnectInterval;
        if(this.reconnectInterval<=0 && this.reconnectInterval <15000){
            this.reconnectInterval = 15000;
        }
        mWebSocketClient.connect();
    }

    public void reConnectSocket(){
        if(reconnectInterval<=0 || isConnecting || isConnected()) return;
        isConnecting = true;
        if(!reConnectThread.isAlive()){
            reConnectThread.start();
        }
//        new Thread( new Runnable() {
//            @Override
//            public void run() {
//                while (isConnecting){
//                    if(isConnected) break;
//                    try {
//                        Thread.sleep( reconnectInterval );
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    if(isConnected) break;
//                    log(isConnected+"-reConnectSocket-----isClosing="+mWebSocketClient.isClosing()+
//                            "====isClosed="+mWebSocketClient.isClosed()+"====isOpen="+mWebSocketClient.isOpen());
//                    if(mWebSocketClient!=null){
//                        mWebSocketClient.reconnect();
//                    }
//                }
//            }
//        } ).start();
    }

    private class MyThread extends Thread{
        @Override
        public void run() {
            super.run();
            while (true){
                if(isConnected()) break;
                try {
                    Thread.sleep( reconnectInterval );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(isConnected()) {
                    break;
                }else{
                    log(isConnected()+"-reConnectSocket-----====isClosed="+mWebSocketClient.isClosed());
                    if(mWebSocketClient!=null){
                        mWebSocketClient.reconnect();
                    }
                }
            }
        }
    }

    private void closeWebSocket(){
        if(mWebSocketClient!=null){
            mWebSocketClient.close();
            mWebSocketClient = null;
        }
        sendMsgToHandler(EVENT_SOCKET_DISCONNECT,null);
    }

    public boolean isConnected(){
        if(mWebSocketClient!=null && !mWebSocketClient.isClosed()){
            return true;
        }
        return false;
    }
    /**
     * 发送字符串到服务器
     * @param message
     */
    public void sendMessage(final String message){
        if(isConnected()){
            log(isConnected()+"-111111111111--"+mWebSocketClient+"---"+message);
//        if(isConnected && mWebSocketClient!=null){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if(mWebSocketClient.isOpen() && isConnected()){
                        mWebSocketClient.send( message );
                    }
                }
            };
            singleThreadExecutor.execute(runnable);
        }
    }

    public void setOnChangeHandler(Handler listener){
        onChangeHandler = listener;
    }
    private void sendMsgToHandler(int type, String data){
        if(onChangeHandler != null){
            Message msg = new Message();
            msg.what = type;
            msg.obj = data;
            onChangeHandler.sendMessage( msg );
        }
    }

    public void onDestroy(){
        reConnectThread.interrupt();
        closeWebSocket();
    }

    private void log(String msg){
//        Log.i(TAG, msg);
    }
}
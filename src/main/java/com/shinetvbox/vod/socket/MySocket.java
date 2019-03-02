package com.shinetvbox.vod.socket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MySocket {

    /**连接成功*/
    public static final int EVENT_SOCKET_CONNECT = 0;
    /**连接断开、未连接成功*/
    public static final int EVENT_SOCKET_DISCONNECT = 1;
    /**接收到消息*/
    public static final int EVENT_SOCKET_RECEIVE_DATA = 2;

    // 要连接的服务器IP地址
    private String hostIp;
    // 要连接的远程服务器在监听的端口
    private int hostPort;

    private Socket mSocket = null;
    private InputStream inputStream = null;
    private DataInputStream dataInputStream = null;
    private OutputStream outputStream = null;

    private String loginInfo = null;
    private String clientSigns = null;
    private String serverSigns = null;
    private int outTime = 0;
    private Handler onChangeHandler = null;
    private String charsetName = "utf-8";
    private int reconnectInterval = 1000;
    private boolean isConnected = false;

    private boolean isInitiativeHeart = true;//是否主动发送心跳
    private long initiativeHeartTimer = 0;//主动心跳上次发送时间
    private Thread initiativeHeartThread;//主动心跳发送进程
    /**
     * 构造函数
     * @param hostIp
     * @param hostPort
     */
    public MySocket(String hostIp, int hostPort){
        this(hostIp,hostPort,0);
    }
    /**
     * 构造函数
     * @param hostIp
     * @param hostPort
     * @param outTime 超时时间(规定时间内未接收到信息，自动断开连接)
     */
    public MySocket(String hostIp, int hostPort, int outTime){
        this.hostIp = hostIp;
        this.hostPort = hostPort;
        this.outTime = outTime;
    }

    private void initialize() {
        // 打开监听信道并设置为非阻塞模式
        while(true) {
            try {
                mSocket = new Socket( hostIp, hostPort );//mHost为服务器地址，mPort和服务器的端口号一样
                isConnected = true;
                if (outTime != 0) {
                    mSocket.setSoTimeout( outTime );
                }
                inputStream = mSocket.getInputStream();
                outputStream = mSocket.getOutputStream();
                dataInputStream = new DataInputStream( inputStream );
                if (loginInfo != null) {
                    sendMsgToServer( loginInfo );
                }
                byte[] b = new byte[100000];
                sendMsgToHandler( EVENT_SOCKET_CONNECT, null );
                while (true) {
                    int length = dataInputStream.read( b );
                    if (length > 0) {
                        String msg = new String( b, 0, length, charsetName );
                        if (!isInitiativeHeart && serverSigns != null && serverSigns.equals( msg )) {
                            sendMsgToServer( clientSigns );
                        } else {
                            sendMsgToHandler( EVENT_SOCKET_RECEIVE_DATA, msg );
                        }
//                    Log.i("222222222222","收到消息---------------------"+msg);
                    } else {
                        closeSocket();
                        break;
                    }
                }
            } catch (IOException e) {
//            e.printStackTrace();
                closeSocket();
            }
            try {
                Thread.sleep( reconnectInterval );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void setLoginInfo(String loginInfo){
        this.loginInfo = loginInfo;
    }
    /**
     * 设置被动心跳包(服务器主动发送)
     * @param clientSigns 客肇端发送心跳标识
     * @param serverSigns 服务器发送心跳标识
     */
    public void setHeartBeatPassive(String clientSigns, String serverSigns){
        isInitiativeHeart = false;
        this.clientSigns = clientSigns;
        this.serverSigns = serverSigns;
    }
    /**
     * 设置主动心跳包(客户端主动发送)
     * @param clientSigns
     * @param serverSigns
     * @param interval
     */
    public void setHeartBeatInitiative(final String clientSigns, String serverSigns, final int interval){
        initiativeHeartTimer = System.currentTimeMillis();
        isInitiativeHeart = true;
        this.clientSigns = clientSigns;
        this.serverSigns = serverSigns;
        if(initiativeHeartThread!=null) return;
        initiativeHeartThread = new Thread( new Runnable() {
            @Override
            public void run() {
                while (isInitiativeHeart){
                    try {
                        Thread.sleep( 1000 );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(System.currentTimeMillis() - initiativeHeartTimer > interval){
                        if(isConnected){
                            sendMsgToServer( clientSigns );
                        }
                        initiativeHeartTimer = System.currentTimeMillis();
                    }
                }
            }
        } );
        initiativeHeartThread.start();
    }

    public void onDestroy(){
        if(initiativeHeartThread!=null){
            initiativeHeartThread.interrupt();
        }
    }
    /**
     * socket开始连接
     */
    public void connect() {
        connect(1000);
    }

    /**
     * socket开始连接
     * @param reconnectInterval 重连间隔
     */
    public void connect(int reconnectInterval) {
        this.reconnectInterval = reconnectInterval;
        if(this.reconnectInterval <1000){
            this.reconnectInterval = 1000;
        }
        new Thread( new Runnable() {
            @Override
            public void run() {
                initialize();
            }
        } ).start();
    }

    private void closeSocket(){
        isConnected = false;
        try {
            if(inputStream!=null){
                inputStream.close();
            }
            if(outputStream!=null){
                outputStream.close();
            }
            if(dataInputStream!=null){
                dataInputStream.close();
            }
            if(mSocket!=null){
                mSocket.close();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        inputStream = null;
        outputStream = null;
        dataInputStream = null;
        mSocket = null;
        sendMsgToHandler(EVENT_SOCKET_DISCONNECT,null);
    }

    /**
     * 发送字符串到服务器
     * @param message
     */
    public void sendMessage(final String message) {
        if(!isConnected) return;
        new Thread( new Runnable() {
            @Override
            public void run() {
                sendMsgToServer(message);
            }
        } ).start();
    }
    private void sendMsgToServer(String message){
        if(isConnected){
            try {
                outputStream.write( message.getBytes() );
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 设置默认的编码格式
     * 为null表示不自动转换data到string
     */
    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }
    public String getCharsetName() {
        return this.charsetName;
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
}
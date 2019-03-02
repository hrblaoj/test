package com.shinetvbox.vod.service.cloudserver;

import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.manager.EventBusManager;
import com.shinetvbox.vod.socket.MessageType;
import com.shinetvbox.vod.socket.TcpShortConnectionClient;
import com.shinetvbox.vod.socket.TcpShortConnectionServer;
import com.shinetvbox.vod.utils.KtvLog;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import static com.shinetvbox.vod.service.cloudserver.CloudMessageProc.DOWNLOAD_CMD.CLOUD_SERVICE_OK;
import static com.shinetvbox.vod.service.cloudserver.CloudMessageProc.DOWNLOAD_CMD.GET_DOWNSONGTHREAD_IDLE;
import static com.shinetvbox.vod.service.cloudserver.CloudMessageProc.DOWNLOAD_CMD.GET_NULL;
import static com.shinetvbox.vod.service.cloudserver.CloudMessageProc.DOWNLOAD_CMD.GET_SONG;
import static com.shinetvbox.vod.service.cloudserver.CloudMessageProc.DOWNLOAD_CMD.GET_SONG_FAILED;
import static com.shinetvbox.vod.service.cloudserver.CloudMessageProc.DOWNLOAD_CMD.GET_SONG_IDLE;
import static com.shinetvbox.vod.service.cloudserver.CloudMessageProc.DOWNLOAD_CMD.GET_SONG_OK;
import static com.shinetvbox.vod.data.eventbus.EventBusConstants.CLOUD_UI_PROC;
import static com.shinetvbox.vod.socket.MessageType.BYTE_TYPE;
import static com.shinetvbox.vod.socket.MessageType.GBK_CODE;
import static com.shinetvbox.vod.socket.SocketManger.Port4444;
import static com.shinetvbox.vod.socket.SocketManger.Port7777;

/**
 * Created by hrblaoj on 2019/1/11.
 */

public class CloudMessageProc {
    public enum DOWNLOAD_CMD
    {
        GET_NULL,
        //获取数据库版本
        GET_DB_ALL,
        GET_DB_VERSION,
        GET_DB_VERSION_OK,
        GET_DB_VERSION_FAILED,

        //下载数据库
        GET_DATABASE_SINGLE,
        GET_DATABASE,
        GET_DATABASE_OK,
        GET_DATABASE_FAILED,
        GET_DB_FINISH, //下载成功后删除数据库
        GET_DATABASE_DISK_FULL, //磁盘空间已满
        //下载歌曲
        GET_DOWNSONGTHREAD_IDLE,
        GET_SONG_IDLE,
        GET_SONG_NOTIDLE,
        GET_SONG,
        GET_SONG_START,
        GET_SONG_OK,
        GET_SONG_FAILED,

        //设置CPU ID地址
        SET_CPUID,
        SET_DISK,
        GET_DISK,
        GET_DISK_FAILED,

        //获取在线更新包版本
        GET_ONLINE_UPDATE_VERSION,
        GET_ONLINE_UPDATE_VERSION_OK,
        GET_ONLINE_UPDATE_VERSION_FAILED,

        //下载在线更新包
        GET_ONLINE_UPDATE,
        GET_ONLINE_UPDATE_OK,
        GET_ONLINE_UPDATE_FAILED,
        GET_ONLINE_UPDATE_DISK_FULL, //磁盘空间已满

        //设置下载数据库的百分比
        SET_DOWNLOAD_DB_PERCENT,

        //设置下载歌曲进度百分比
        SET_DOWNLOAD_SONG_PERCENT,

        SONG_SORT,

        //查询云加歌过期时间
        CLOUD_OUT_OF_DATE,
        SET_CLOUD_OUT_OF_DATE,

        //By Bati添加
        GET_SONG_EXIT,
        CLOUD_SERVICE_OK,
        DOWNLOAD_LIST_DEL,//下载列表删除
        DOWNLOAD_LIST_SEQUNCE,//下载列表排序
    }

    DOWNLOAD_CMD curDownSongStatus = GET_NULL;

    static public CloudMessageProc inTance;
    static public CloudMessageProc getInTance(){
        return inTance;
    }

    ReentrantLock mLock = new ReentrantLock();
    public Map<DOWNLOAD_CMD, ArrayList<cloudRecvProc>> mapProc = new HashMap<DOWNLOAD_CMD, ArrayList<cloudRecvProc>>();
    public CloudMessageProc(){
        EventBus.getDefault().register( this );
        TcpShortConnectionServer  mConnection = TcpShortConnectionServer.getIntanceByPort(Port7777);
        if(null == mConnection)
            return;

        inTance = this;
        new MessageType(BYTE_TYPE, "CLOUD", "", GBK_CODE, new MessageType.onProcess(){

            @Override
            public void onProcessHead() {

            }

            @Override
            public void onReponse(byte[] sendbuf) {

            }

            @Override
            public void onProcessBody(String str, TcpShortConnectionServer.socketProcessRunnable socketProcessRunnable) {
                KtvLog.d("CloudMessageProc str");
            }

            @Override
            public void onProcessBody(byte[] bytes,TcpShortConnectionServer.socketProcessRunnable socketProcessRunnable) {
                KtvLog.d("CloudMessageProc bytes");


                byte[] tempStr = new byte[bytes.length];
                System.arraycopy(bytes, 4, tempStr, 0, bytes.length - 4);
                String coneten = new String(tempStr);

                for (int i = 0; i < coneten.length(); i++) {
                    String strCmptemp = new String("" + coneten.charAt(i));
                    if (strCmptemp.equals("\0")) {
                        coneten = coneten.substring(0, i);
                        break;
                    }
                }

                EventBusMessage msg = new EventBusMessage();

                DOWNLOAD_CMD cmd = DOWNLOAD_CMD.values()[byte2Integer(bytes, 0)];
                switch (cmd){
                    case GET_SONG_OK:
                    case GET_SONG_FAILED:

                        byte[] send = new byte[8];

                       socketProcessRunnable.sendAndRecv(send);
                        //setDownSongStatus(cmd);
                        break;

                    case CLOUD_SERVICE_OK:
                    case GET_SONG_IDLE:
                            //setDownSongStatus(cmd);
                        break;
                }
                CloudDownloadSongStruce obj = new CloudDownloadSongStruce(cmd, coneten);
                KtvLog.d("DOWNLOAD_CMD.values()[byte2Integer(bytes, 0)] is " + obj.cmd );
                msg.what = CLOUD_UI_PROC;
                msg.obj = obj;

                EventBusManager.sendMessage( msg );

            }
        }, mConnection);

    }

    static public int byte2Integer(byte[] pByte, int presult){
        int tmpVal = 0;

        tmpVal |= pByte[3]<<24 & 0xFF000000;
        tmpVal |= pByte[2]<<16 & 0x00FF0000;
        tmpVal |= pByte[1]<<8 & 0x0000FF00;
        tmpVal |= pByte[0] & 0x000000FF;

        presult = presult | tmpVal;

        return tmpVal;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusMessage msg) {
        switch (msg.what) {
            case EventBusConstants.CLOUD_UI_PROC:

                mLock.lock();
                CloudDownloadSongStruce o = (CloudDownloadSongStruce)(msg.obj);
                KtvLog.d("getEventBus CloudMessageProc o.cmd is " + o.cmd);
                if(GET_SONG_OK == o.cmd || GET_SONG_FAILED == o.cmd || CLOUD_SERVICE_OK == o.cmd || GET_SONG_IDLE == o.cmd)
                    setDownSongStatus(o.cmd);
                ArrayList<cloudRecvProc> p = mapProc.get(o.cmd);
                if(p != null && p.size() > 0){
                    for(cloudRecvProc t:p){
                        t.onProc((CloudDownloadSongStruce)msg.obj);
                    }
                }

                mLock.unlock();
                break;
        }
    }
    public abstract class cloudRecvProc{
        public cloudRecvProc(DOWNLOAD_CMD cmd){
            mLock.lock();
            ArrayList<cloudRecvProc> p = mapProc.get(cmd);
            if(null == p){
                p = new ArrayList<cloudRecvProc>();
                p.add(this);
                mapProc.put(cmd, p);
            }
            else {
                p.add(this);
            }
            mLock.unlock();
        }
        public abstract void onProc(CloudDownloadSongStruce obj);
    }

    public interface onSendProc{
        void onSendFailedProc();
        void onSendSucessProc();
    }

    public interface onRecvProc{
        void onGetSongOKProc();
        void onGetSongFailedProc();
        void onGetSongPerProc();
    }
    public  class cloudSendProc{
        public cloudSendProc(CloudDownloadSongStruce obj, onSendProc proc){
            TcpShortConnectionClient connection = TcpShortConnectionClient.getIntanceByPort(Port4444);
            if(null == connection)
                return;
//            if(!getSendDownSongAllow()) {
//                if(null != proc)
//                proc.onSendFailedProc();
//                return;
//            }

            switch (obj.cmd){
                case GET_SONG:
                    if(!getSendDownSongAllow()){
                        KtvLog.d("cloudSendProccloudSendProc 111111");
                        if(null != proc)
                            proc.onSendFailedProc();
                        return;
                    }
                    KtvLog.d("cloudSendProccloudSendProc 222222");
                    setDownSongStatus(GET_SONG);
                    if(null != proc)
                        proc.onSendSucessProc();
                    break;
            }

            byte[] buf = new byte[obj.getContent().getBytes().length + 4];
            byte[] b = new byte[4];
            int nEmToInt = obj.getCmd().ordinal();
            b[0] = (byte) (nEmToInt & 0xff);
            b[1] = (byte) (nEmToInt >> 8 & 0xff);
            b[2] = (byte) (nEmToInt >> 16 & 0xff);
            b[3] = (byte) (nEmToInt >> 24 & 0xff);

            System.arraycopy(b, 0, buf, 0, 4);
            System.arraycopy(obj.getContent().getBytes(), 0, buf, 4, obj.getContent().getBytes().length);

            if(GET_SONG == obj.cmd) {
                connection.execute(connection.new SendRunnable(buf, "localhost", new TcpShortConnectionClient.onSendProcess() {
                    @Override
                    public void onFailed() {
                        setDownSongStatus(GET_SONG_FAILED);
                    }
                }) {
                });
            }
            else {
                connection.execute(connection.new SendRunnable(buf, "localhost") {
                });
            }

        }


    }

    synchronized public boolean getSendDownSongAllow(){
        if(GET_SONG == curDownSongStatus) {
//            new cloudSendProc(new CloudDownloadSongStruce(GET_DOWNSONGTHREAD_IDLE, ""), new onSendProc() {
//                @Override
//                public void onSendFailedProc() {
//
//                }
//
//                @Override
//                public void onSendSucessProc() {
//
//                }
//            });
            return false;
        }

        return true;
    }

    synchronized public boolean getSendDownSongAllow2(){
        if(GET_NULL == curDownSongStatus)
            return false;

        return true;
    }
    synchronized public void setDownSongStatus(DOWNLOAD_CMD cmd){
//        if(GET_SONG_IDLE == cmd && GET_SONG != curDownSongStatus)
//            return;
        curDownSongStatus = cmd;
    }

    public void cloudSend4Out(CloudDownloadSongStruce st, onSendProc proc){
        new cloudSendProc(st, proc);
    }
}

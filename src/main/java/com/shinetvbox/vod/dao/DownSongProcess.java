package com.shinetvbox.vod.dao;

import com.shinetvbox.vod.R;
import com.shinetvbox.vod.certercontrol.ControlCenter;
import com.shinetvbox.vod.certercontrol.ToastCenter;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessageSongDownload;
import com.shinetvbox.vod.db.SongInfo;
import com.shinetvbox.vod.manager.EventBusManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.manager.SongPlayManager;
import com.shinetvbox.vod.service.cloudserver.CloudDownloadSongStruce;
import com.shinetvbox.vod.service.cloudserver.CloudMessageProc;
import com.shinetvbox.vod.utils.ConversionsUtil;
import com.shinetvbox.vod.utils.KtvLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.shinetvbox.vod.service.cloudserver.CloudMessageProc.DOWNLOAD_CMD.GET_SONG;
import static com.shinetvbox.vod.service.cloudserver.CloudMessageProc.DOWNLOAD_CMD.GET_SONG_FAILED;
import static com.shinetvbox.vod.service.cloudserver.CloudMessageProc.DOWNLOAD_CMD.GET_SONG_OK;
import static com.shinetvbox.vod.service.cloudserver.CloudMessageProc.DOWNLOAD_CMD.SET_DOWNLOAD_SONG_PERCENT;

/**
 * Created by hrblaoj on 2019/2/14.
 */

public class DownSongProcess {
    private long curtime = 0;
    private SongInfo curSongInfo = null;
    private CopyOnWriteArrayList<SongInfo> DownSongCacheList= new CopyOnWriteArrayList<SongInfo>();

    private static DownSongProcess sInstance = null;
    public static DownSongProcess getInstance(){
        if(sInstance == null) {
            sInstance = new DownSongProcess();
        }
        return sInstance;
    }

    public synchronized SongInfo effectiveSongGet(){
        for(SongInfo t:DownSongCacheList){
            if(0 == t.downStatus)
                curSongInfo = t;
                return t;
        }
        curSongInfo = null;
        return null;
    }
    private String getSongId(){
        String id = "";
        if(curSongInfo!=null){
            id = curSongInfo.song_id;
        }
        return id;
    }

    public DownSongProcess(){
        CloudMessageProc.getInTance().new cloudRecvProc(GET_SONG_OK) {
            @Override
            public void onProc(CloudDownloadSongStruce obj) {
                synchronized (this){
                    ControlCenter.setLocalSongList( getSongId(),true );
                    SongPlayManager.addSong( curSongInfo );
                    DownSongCacheList.remove( curSongInfo );
                    sendEventBusMessage(EventBusConstants.SONG_DOWNLOAD_SUCCESS,"100" );
                    SongInfo tmp = effectiveSongGet();
                    if(null == tmp) return;
                    tmp.downStatus = 0;
                            CloudMessageProc.getInTance().cloudSend4Out(new CloudDownloadSongStruce(GET_SONG, tmp.song_id + ".mpg"), new CloudMessageProc.onSendProc() {
                                @Override
                                public void onSendFailedProc() {

                                }

                                @Override
                                public void onSendSucessProc() {

                                }
                            });
//                    for(SongInfo t:DownSongCacheList){
//                        if(-1 != obj.getContent().indexOf(t.song_id)){
////                        if(t.song_id.equals(obj.getContent())){
//                            DownSongCacheList.remove(t);
//                            t.downStatus = 1;
//                              SongPlayManager.addSong( curSongInfo );
//                            SongInfo tmp = effectiveSongGet();
//                            if(null == tmp)
//                                return;
//                            tmp.downStatus = 0;
//                            CloudMessageProc.getInTance().cloudSend4Out(new CloudDownloadSongStruce(GET_SONG, tmp.song_id + ".mpg"), new CloudMessageProc.onSendFailedProc() {
//                                @Override
//                                public void onSendFailedProc() {
//
//                                }
//                            });
//
//                            return;
//                        }
//                    }
                }
            }
        };
        CloudMessageProc.getInTance().new cloudRecvProc(GET_SONG_FAILED) {
            @Override
            public void onProc(CloudDownloadSongStruce obj) {
                synchronized (this) {
                    DownSongCacheList.remove( curSongInfo );
                    sendEventBusMessage(EventBusConstants.SONG_DOWNLOAD_FAILURE,"0" );
                    String code = getErrorCode(obj.content);
                    if(code.equals( "2" )){
                        ToastCenter.getInTance().sendToastEvent( ResManager.getInstance()
                                .getStringById( R.string.system_hint_download_reject ) );
                        return;
                    }else{
                        if(curSongInfo!=null){
                            ToastCenter.getInTance().sendToastEvent("《"+curSongInfo.song_name+"》"+ResManager.
                                    getInstance().getStringById( R.string.system_hint_download_failure ));
                        }
                    }
                    SongInfo tmp = effectiveSongGet();
                    if(null == tmp) return;
                    tmp.downStatus = 0;
                            CloudMessageProc.getInTance().cloudSend4Out(new CloudDownloadSongStruce(GET_SONG, tmp.song_id + ".mpg"), new CloudMessageProc.onSendProc() {
                                @Override
                                public void onSendFailedProc() {

                                }

                                @Override
                                public void onSendSucessProc() {
                                    
                                }
                            });
//                    for (SongInfo t : DownSongCacheList) {
//                        if (-1 != obj.getContent().indexOf(t.song_id)) {
//                            t.downStatus = 2;
//
//                            SongInfo tmp = effectiveSongGet();
//                            if(null == tmp)
//                                return;
//
//                            tmp.downStatus = 0;
//                            CloudMessageProc.getInTance().cloudSend4Out(new CloudDownloadSongStruce(GET_SONG, tmp.song_id + ".mpg"), new CloudMessageProc.onSendFailedProc() {
//                                @Override
//                                public void onSendFailedProc() {
//
//                                }
//                            });
//                            return;
//                        }
//                    }
                }
            }
        };


        CloudMessageProc.getInTance().new cloudRecvProc(SET_DOWNLOAD_SONG_PERCENT) {
            @Override
            public void onProc(CloudDownloadSongStruce obj) {
                if(System.currentTimeMillis() - curtime >500){
                    String content = obj.getContent();
                    int pos = content.indexOf(':');
                    if(-1 == pos) return;
                    String jd = content.substring(pos+1, content.length());
                    sendEventBusMessage(EventBusConstants.SONG_DOWNLOAD_PROGRESS,jd );
                    curtime = System.currentTimeMillis();
                }
            }
        };
    }
    private String getErrorCode(String str){
        String code = "";
        Pattern pattern = Pattern.compile("code=(.+?)");
        Matcher matcher = pattern.matcher(str);
        while(matcher.find()){
            if(!code.equals( "" )) break;
            code = matcher.group(1);
        }
        return code;
    }
    public synchronized boolean addDownSong(SongInfo song,boolean isPriority){
        KtvLog.d("addDownSongaddDownSong ininin song is " + song.song_id);
        if(!CloudMessageProc.getInTance().getSendDownSongAllow2()){
            KtvLog.d("云下载服务未启动");
            ToastCenter.getInTance().sendToastEvent( ResManager.getInstance()
                    .getStringById( R.string.system_hint_cloudserver_error ) );
            return false;
        }

        for(SongInfo t:DownSongCacheList){
            if(t.song_id.equals(song.song_id))
                return false;
        }
        song.downStatus = 0;
        if(isPriority && DownSongCacheList.size()>1){
            DownSongCacheList.add(1,song);
        }else{
            DownSongCacheList.add(song);
        }
        SongInfo tmp = effectiveSongGet();
        if(null == tmp)
            return false;
        sendEventBusMessage(EventBusConstants.SONG_DOWNLOAD_REFRESH,"0" );
        KtvLog.d("addDownSongaddDownSong song is " + song.song_id);
        CloudMessageProc.getInTance().cloudSend4Out(new CloudDownloadSongStruce(GET_SONG, tmp.song_id + ".mpg"), new CloudMessageProc.onSendProc() {
                        @Override
                        public void onSendFailedProc() {
//                            KtvLog.d("procccccccc");
                        }

            @Override
            public void onSendSucessProc() {
                
            }
        });
        return true;
    }

    /**
     * 获取正在下载歌曲ID
     */
    public synchronized String getCurrentSongId() {
        if(curSongInfo!=null) return curSongInfo.song_id;
        return "0";
    }
    public synchronized int GetIndexById(String song_id) {
        int index = 0;
        for(SongInfo t : DownSongCacheList) {
            if(t.song_id.equals(song_id))
                return index;
            index++;
        }
        return -1;
    }
    /**
     * 获取下载歌曲状态
     */
    public synchronized int GetStatusById(String song_id) {
        for(SongInfo t : DownSongCacheList) {
            if(t.song_id.equals(song_id))
                return t.downStatus;
        }
        return 0;
    }
    public synchronized int getDownloadSongConut() {
        return DownSongCacheList.size();
    }
    public synchronized List<SongInfo> getDownlistSongListByPage(int page, int num){
        int size = 0;
        int index = 0;
        if(0 >= (size = DownSongCacheList.size()))
            return null;
        int i = 0;
        List<SongInfo> songInfoListTemp =  new ArrayList<>();
        for(i = 0; i < num; i++){
            index = page*num + i;
            if(index >= size || index < 0) {
                break;
            }
            songInfoListTemp.add(i, DownSongCacheList.get(index).clone());
        }
        return songInfoListTemp;
    }
    public synchronized boolean cleanDownloadList(){
        for(SongInfo si:DownSongCacheList){
            if(!si.song_id.equals( getSongId() )){
                DownSongCacheList.remove( si );
            }
        }
        sendEventBusMessage(EventBusConstants.SONG_DOWNLOAD_REFRESH,"0" );
        return true;
    }
    public synchronized boolean delDownSong(SongInfo song){
        if(song!=null && !song.song_id.equals( getSongId() )){
            for(SongInfo si:DownSongCacheList){
                if(si.song_id.equals( song.song_id )){
                    DownSongCacheList.remove( si );
                }
            }
            if(DownSongCacheList.size()==0) curSongInfo = null;
            sendEventBusMessage(EventBusConstants.SONG_DOWNLOAD_REFRESH,"0" );
            return true;
        }
        return false;
    }

    public synchronized boolean priorityDownSong(SongInfo song){
        if(song == null || DownSongCacheList.size()<3) return false;
        for(SongInfo si:DownSongCacheList){
            if(si.song_id.equals( song.song_id )){
                DownSongCacheList.remove( si );
                DownSongCacheList.add( 1,song );
                break;
            }
        }
        sendEventBusMessage(EventBusConstants.SONG_DOWNLOAD_REFRESH,"0" );
//        if(song == null) return false;
//        if(DownSongCacheList.size() <= 2)
//            return false;
//        if(song.song_id.equals( getSongId() ))
//            return false;
//        boolean inList = false;
//        int pos = 0;
//        int i = 0;
//        int j = 0;
//        for(SongInfo t:DownSongCacheList){
//            if(t.song_id.equals(song.song_id)) {
//                pos = i;
//                inList = true;
//                break;
//            }
//            i++;
//        }
//
//        if(!inList){
//            return false;
//        }
//
//        if(2 <= pos) {
//            DownSongCacheList.add(1, song);
//            DownSongCacheList.remove(pos + 1);
//            sendEventBusMessage(EventBusConstants.SONG_DOWNLOAD_REFRESH,"0" );
//        }else {
//            return false;
//        }

        return true;
    }

    private void sendEventBusMessage(int what,String progress){
        EventBusMessageSongDownload msg = new EventBusMessageSongDownload();
        msg.what = what;
        msg.songid = getSongId();
        msg.progress = ConversionsUtil.stringToInteger( progress );
        EventBusManager.sendMessage( msg );
    }
}

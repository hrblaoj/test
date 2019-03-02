package com.shinetvbox.vod.manager;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.shinetvbox.vod.MyApplication;
import com.shinetvbox.vod.R;
import com.shinetvbox.vod.certercontrol.ControlCenter;
import com.shinetvbox.vod.certercontrol.ToastCenter;
import com.shinetvbox.vod.data.custom.JsonData;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessageSongDownload;
import com.shinetvbox.vod.data.eventbus.EventBusMessageUpdateApp;
import com.shinetvbox.vod.db.SongInfo;
import com.shinetvbox.vod.utils.FileUtil;
import com.shinetvbox.vod.utils.StorageUtil;
import com.shinetvbox.vod.utils.updateapp.HttpConstant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import okhttp3.Call;

public class SongDownloadManager {

private static SongInfo curSongInfo = null;

    private static String tagDownloadSong = "tagDownloadSong";
    private static String tagMpgTemp = ".mpg_tmp";
    private static String tagMpg = ".mpg";
    private static CopyOnWriteArrayList<SongInfo> songList = new CopyOnWriteArrayList<>(  );
    private static int downloadFailureNumber = 0;
    /**
     * 添加到下载列表
     * @param songInfo
     */
    public static synchronized boolean addDownSong(@NonNull SongInfo songInfo){
        return addSongToList(songInfo.clone(),0);
    }
    /**
     * 优先下载
     * @param songInfo
     */
    public static synchronized boolean priorityDownSong(@NonNull SongInfo songInfo){
        return addSongToList(songInfo.clone(),1);
    }

    /**
     * 清空列表
     */
    public static synchronized boolean clearDownloadList(){
        if(curSongInfo !=null){
            stopDownloadSong();
        }
        songList.clear();
        sendEventBusMessage(EventBusConstants.SONG_DOWNLOAD_REFRESH,0 );
        return true;
    }
    /**
     * 删除下载列表中的歌曲
     * @param song
     */
    public static synchronized boolean delDownSong(SongInfo song){
        if(song!=null){
            if(song.song_id.equals( getSongId() )){
                stopDownloadSong();
            }else{
                deleteSonginfoFromList(song.song_id);
            }
            sendEventBusMessage(EventBusConstants.SONG_DOWNLOAD_REFRESH,0 );
            return true;
        }
        return false;
    }
    private static synchronized void stopDownloadSong(){
        deleteSonginfoFromList(getSongId());
        OkHttpUtils.getInstance().cancelTag( tagDownloadSong );
        if(FileUtil.fileIsExists( MyApplication.SHINESONGDDIR+getSongId()+tagMpgTemp )){
            FileUtil.deleteFile( MyApplication.SHINESONGDDIR+getSongId()+tagMpgTemp );
        }
    }

    /**
     * 获取正在下载歌曲ID
     */
    public static synchronized String getCurrentSongId() {
        return getSongId();
    }
    /**
     * 获取歌曲在下载列表中的位置
     * @param song_id
     */
    public static synchronized int GetIndexById(String song_id) {
        int index = 0;
        for(SongInfo t : songList) {
            if(t.song_id.equals(song_id))
                return index;
            index++;
        }
        return -1;
    }

    /**
     * 获取歌曲在下状态
     * @param song_id
     */
    public static synchronized int GetStatusById(String song_id) {
        for(SongInfo t : songList) {
            if(t.song_id.equals(song_id))
                return t.downStatus;
        }
        return 0;
    }

    /**
     * 获取下载歌曲数量
     */
    public static synchronized int getDownloadSongConut() {
        return songList.size();
    }
    /**
     * 获取下载歌曲通过页数
     * @param page 当前页面
     * @param num 获取数量
     */
    public static synchronized List<SongInfo> getDownlistSongListByPage(int page, int num){
        int size = 0;
        int index = 0;
        if(0 >= (size = songList.size()))
            return null;
        int i = 0;
        List<SongInfo> songInfoListTemp =  new ArrayList<>();
        for(i = 0; i < num; i++){
            index = page*num + i;
            if(index >= size || index < 0) {
                break;
            }
            songInfoListTemp.add(i, songList.get(index).clone());
        }
        return songInfoListTemp;
    }

    private static synchronized boolean addSongToList(SongInfo song,int priority){
        if(song == null) return false;
        for(SongInfo si: songList){
            if(si.song_id.equals( song.song_id )){
                if(priority == 1){
                    if(si.downStatus != 2){
                        if(songList.size()<2) return false;
                        songList.remove( si );
                    }else{
                        downloadFailureNumber --;
                        songList.remove( si );
                    }
                    break;
                }else{
                    return false;
                }
            }
        }
        if(priority == 1 && songList.size()>downloadFailureNumber+1){
            songList.add( downloadFailureNumber+1,song );
        }else{
            songList.add( song );
        }
        if(curSongInfo==null){
            startLoad();
        }
        sendEventBusMessage(EventBusConstants.SONG_DOWNLOAD_REFRESH,0 );
        return true;
    }
    private static synchronized void startLoad(){
        curSongInfo = getNewSongInfo();
        if(!checkStorageSpace()){
            clearDownloadList();
            return;
        }
        if(curSongInfo!=null){
            getSongLoadUrl();
        }
    }
    private static synchronized void getSongLoadUrl(){
        if(getSongId().equals( "0" )) return;
        OkHttpUtils.post()
                .tag( tagDownloadSong )
                .params( HttpConstant.getSongDownloadInfoPostParams(getSongId()) )
                .url( HttpConstant.urlGetDownLoadFiles )
                .build()
                .connTimeOut( 10000 )
                .readTimeOut( 10000 )
                .execute( new MyStringCallback() );
    }
    private static class MyStringCallback extends StringCallback {

        public MyStringCallback() {
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            loadFailure();
        }

        @Override
        public void inProgress(float progress, long total, int id) {
        }
        @Override
        public void onResponse(String response, int id) {
            //{"code":"0","type":"success","description":"请求成功",
            // "result":"http://ks3.mov.joyk.com.cn/641922.mpg?AccessKeyId=
            // nXLz3axA9cpFXPsrs0fS&Expires=1550032859&Signature=2mQrJHLqQW5DcDEKVlZRNmFEE8o%3D&"}
            JsonData jd = new JsonData( response );
            String code = jd.getValueString( "code" );
            if(code.equals( "0" )){
                loadSong(jd.getValueString( "result" ));
            }else if(code.equals( "2" )){
                ToastCenter.getInTance().sendToastEvent( ResManager.getInstance()
                        .getStringById( R.string.system_hint_download_reject ) );
            }
        }
    }
    private static synchronized void loadSong(String url){
        if(TextUtils.isEmpty( url ) || curSongInfo==null) {
            loadFailure();
            return;
        }
        OkHttpUtils.get()
                .tag( tagDownloadSong )
                .url( url )
                .build()
                .connTimeOut( 10000 )
                .readTimeOut( 30000 )
                .execute( new MyFileCallback( MyApplication.SHINESONGDDIR, getSongId()+tagMpgTemp ) );
    }

    private static class MyFileCallback extends FileCallBack {

        private EventBusMessageUpdateApp msg = new EventBusMessageUpdateApp();
        private long curTime = 0;

        public MyFileCallback(String destFileDir, String destFileName) {
            super( destFileDir, destFileName );
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            loadFailure();
        }

        @Override
        public void inProgress(float progress, long total, int id) {
            if (System.currentTimeMillis() - curTime > 1000) {
                sendEventBusMessage(EventBusConstants.SONG_DOWNLOAD_PROGRESS,(int) (progress * 100) );
                curTime = System.currentTimeMillis();
                Log.i( "songdownload",getSongId()+"-progress="+progress );
            }
        }

        @Override
        public void onResponse(final File response, final int id) {
            loadSuccess();
        }
    }
    private static synchronized void loadFailure(){
        ToastCenter.getInTance().sendToastEvent("《"+getSongName()+"》"+ResManager.getInstance()
                .getStringById( R.string.system_hint_download_failure ));
//        deleteSonginfoFromList(getSongId());
        if(curSongInfo!=null){
            downloadFailureNumber ++;
            curSongInfo.downStatus = 2;
        }
        sendEventBusMessage(EventBusConstants.SONG_DOWNLOAD_FAILURE,0 );
        startLoad();
    }
    private static synchronized void loadSuccess(){
        if (FileUtil.RenameFile(MyApplication.SHINESONGDDIR+getSongId()+tagMpgTemp,
                MyApplication.SHINESONGDDIR+getSongId()+tagMpg)){
            SongPlayManager.addSong( curSongInfo );
        }
        ControlCenter.setLocalSongList( getSongId(),true );
        deleteSonginfoFromList(getSongId());
        sendEventBusMessage(EventBusConstants.SONG_DOWNLOAD_SUCCESS,100 );
        startLoad();
    }

    private static synchronized String getSongId(){
        if(curSongInfo!=null){
            return curSongInfo.song_id;
        }
        return "0";
    }
    private static synchronized String getSongName(){
        if(curSongInfo!=null){
            return curSongInfo.song_name;
        }
        return "";
    }
    private static synchronized SongInfo getNewSongInfo(){
        if(songList.size()>downloadFailureNumber){
            return songList.get( downloadFailureNumber );
        }
        return null;
    }
    private static synchronized boolean deleteSonginfoFromList(String songid){
        for(SongInfo si: songList){
            if(si.song_id.equals( songid )){
                if(si.downStatus == 2){
                    downloadFailureNumber --;
                }
                songList.remove( si );
                return true;
            }
        }
        return false;
    }
    private static synchronized void sendEventBusMessage(int what,int progress){
        EventBusMessageSongDownload msg = new EventBusMessageSongDownload();
        msg.what = what;
        msg.songid = getSongId();
        msg.progress = progress;
        EventBusManager.sendMessage( msg );
    }

    private static boolean checkStorageSpace(){
        if(StorageUtil.externalMemoryAvailable()){
            if(StorageUtil.getAvailableExternalMemorySize()<350){
                if(ControlCenter.listLocalSongId.size()<0) {
                    ToastCenter.getInTance().sendToastEvent( ResManager.getInstance().
                            getStringById( R.string.system_hint_kongjianbuzu ) );
                    return false;
                }
                String songid = null;
                for(String sid:ControlCenter.listLocalSongId){
                    if(!SongPlayManager.getCurrentPlaySongId().equals( sid ) &&
                            SongPlayManager.getSelectedSongIndex( sid )==-1){
                        songid = sid;
                        break;
                    }
                }
                if (songid == null) {
                    songid = ControlCenter.listLocalSongId.get( 0 );
                    SongPlayManager.delSong( songid );
                }
                Log.i( "222222222222222222222",StorageUtil.getAvailableExternalMemorySize()+"MB---------------22222222-----------"+FileUtil.fileIsExists( MyApplication.SHINESONGDDIR+songid+".mpg" ) );
                FileUtil.deleteFile( MyApplication.SHINESONGDDIR+songid+".mpg" );
                ControlCenter.setLocalSongList( songid,false );
                Log.i( "222222222222222222222",StorageUtil.getAvailableExternalMemorySize()+"MB---------------33333333-----------"+FileUtil.fileIsExists( MyApplication.SHINESONGDDIR+songid+".mpg" ) );
                return true;
            }else{
                return true;
            }
        }else{

        }
        return false;
    }
}

package com.shinetvbox.vod.manager;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.shinetvbox.vod.MainActivity;
import com.shinetvbox.vod.MyApplication;
import com.shinetvbox.vod.db.DatabaseService;
import com.shinetvbox.vod.db.DateBaseUpdate;
import com.shinetvbox.vod.db.IDatabaseService;
import com.shinetvbox.vod.db.Query;
import com.shinetvbox.vod.db.Singer;
import com.shinetvbox.vod.db.SingerInfo;
import com.shinetvbox.vod.db.SingerQuery;
import com.shinetvbox.vod.db.Song;
import com.shinetvbox.vod.db.SongInfo;
import com.shinetvbox.vod.utils.FileUtil;
import com.shinetvbox.vod.utils.KtvLog;
import com.shinetvbox.vod.utils.KtvSystemApi;
import com.shinetvbox.vod.utils.ServiceUtil;
import com.shinetvbox.vod.utils.SharedPreferencesUtil;
import com.shinetvbox.vod.utils.UnzipUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static DatabaseManager mInstance = null;
    public static IDatabaseService mIDatabaseService = null;

    public static DatabaseManager getInstance(){
        if(mInstance==null){
            synchronized(ResManager.class){
                if(mInstance==null){
                    mInstance = new DatabaseManager();
                }
            }
        }
        return mInstance;
    }

    private Application mActivity = null;
    private final String dbName = "ktv10.db";
    private final String zipName = "ktv10.zip";
    private Handler zipHandler;

    public void init(Application activity){
        if(mActivity!=null) return;
        mActivity = activity;
        initHandler();
        initDatabase();
    }
    public void initDatabase(){
        if(SharedPreferencesUtil.isCopyDatabase() && FileUtil.fileIsExists( MyApplication.SHINEDBDIR+dbName )){
            openDatabase();
            return;
        }
        new Thread( new Runnable() {
            @Override
            public void run() {
                InputStream in = null;
                FileOutputStream out = null;
                try {
                    in = mActivity.getAssets().open(zipName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(in == null) {
                    return;
                }

                File dbFile = new File( MyApplication.SHINEDBDIR+zipName );

                try {
                    out = new FileOutputStream(dbFile);
                    int length = -1;
                    byte[] buf = new byte[1024];
                    while ((length = in.read(buf)) != -1) {
                        out.write(buf, 0, length);
                    }
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if(out!=null){
                            out.close();
                        }
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                if(out == null) {
                    return;
                }

                if(!FileUtil.fileIsExists( MyApplication.SHINEDBDIR )){
                    FileUtil.makeRootDirectory( MyApplication.SHINEDBDIR );
                }

                if(FileUtil.fileIsExists( MyApplication.SHINEDBDIR+zipName )){
                    UnzipUtil.Unzip( MyApplication.SHINEDBDIR+zipName,MyApplication.SHINEDBDIR,zipHandler );
                }
            }
        } ).start();
    }

    @SuppressLint("HandlerLeak")
    private void initHandler(){
        zipHandler = new Handler(  ){
            @Override
            public void handleMessage(Message msg) {
                FileUtil.deleteFile( MyApplication.SHINEDBDIR+zipName );
                switch (msg.what){
                    case UnzipUtil.UNZIP_OVER:
                        SharedPreferencesUtil.setCopyDatabase( true );
                        openDatabase();
                        break;
                    case UnzipUtil.UNZIP_ERROR:
                        SharedPreferencesUtil.setCopyDatabase( false );
                        break;
                }
            }
        };
    }
    private void openDatabase(){
        new Thread( new Runnable() {
            @Override
            public void run() {
                DateBaseUpdate.initCallBack.init();
//                mActivity.startService(new Intent().setClass(MyApplication.getInstance(), DatabaseService.class));
                if(!KtvSystemApi.isRunningService(MyApplication.getInstance(), "com.shinetvbox.vod.db.DatabaseService")) {
                    KtvLog.d("no isRunningService");
                    mActivity.startService(new Intent().setClass(MyApplication.getInstance(), DatabaseService.class));
                }
                else{
                    Intent nIntentCld = new Intent();
                    nIntentCld.setClass(mActivity, DatabaseService.class);
                    mActivity.stopService(nIntentCld);
                    mActivity.startService(new Intent().setClass(MyApplication.getInstance(), DatabaseService.class));
                    KtvLog.d("yes isRunningService");
                }
                mActivity.bindService(new Intent().setClass(MyApplication.getInstance(), DatabaseService.class), conn, 0);
            }
        } ).start();
    }

    public IDatabaseService getDbService(){
        if(mIDatabaseService == null) {
            KtvLog.e("getDbService mIDatabaseService is null");
        }
        return mIDatabaseService;
    }
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            KtvLog.d("onServiceConnected");
            mIDatabaseService = IDatabaseService.Stub.asInterface(service);

            String pkg = MainActivity.class.getName();
            String mRunningActivity = ServiceUtil.getRunningActivityName(mActivity);
            Log.i("Service", "ktvMain connect IDatabaseService, RunningActivity="+mRunningActivity);

            //如果当前运行的Activity不是FirstActivity，则认为是运行过程中，dbService奔溃后重启连接。
            //这种情况下，要主动初始化数据库
//            if(!pkg.equals(mRunningActivity) && null != mIDatabaseService){
//                try {
//                    KtvLog.d("onServiceConnected will opendb");
//                    mIDatabaseService.opendbAndSetTemp(APPFILEPATH, APPDBEPATH);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//                KtvLog.d("onServiceConnected if");
//            }
//            else
//            {
//                KtvLog.d("onServiceConnected else");
//            }

            try {
                mIDatabaseService.opendbAndSetTemp(MyApplication.SHINEDBDIR + dbName, MyApplication.APPFILEPATH);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("Service", "ktvMain onServiceDisconnected IDatabaseService!");
            mIDatabaseService = null;
        }
    };

    public List<SongInfo> getSongInfo(Query query) {
        if(getDbService() == null || query == null) return null;
        try {
            List<Song> songList = getDbService().querySong(query);
            if(songList==null) return null;
            List<SongInfo> ksongListTemp =  new ArrayList<>();
            for( int i = 0; i < songList.size(); i++ ){
                ksongListTemp.add(i, SongInfo.buildKsong(songList.get(i)) );
            }
            return ksongListTemp;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<SingerInfo> getSingerInfo(SingerQuery query) {
        if(getDbService() == null || query == null) return null;
        if(DatabaseManager.getInstance().getDbService() != null) {
            List<Singer> singerlist = null;
            try {
                singerlist = getDbService().querySinger(query);
            } catch (RemoteException e) {
                e.printStackTrace();
                return null;
            }
            if(singerlist==null) return null;
            List<SingerInfo> ksongListTemp =  new ArrayList<>();
            for( int i = 0; i < singerlist.size(); i++ ){
                ksongListTemp.add(i, SingerInfo.buildKsinger(singerlist.get(i)) );
            }
            return ksongListTemp;
        }
        return null;
    }

    public SongInfo getSongInfoById(String songid) {
        if(getDbService() == null || songid == null) return null;
        Query mQuery = new Query(  );
        mQuery.limit = 1;
        mQuery.song_id = songid;
        try {
            List<Song> songList = getDbService().querySong(mQuery);
            if(songList!=null && songList.size()>0){
                return SongInfo.buildKsong( songList.get( 0 ) );
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getSongCount(Query query) {
        if(getDbService() == null || query == null) return 0;
        try {
            return getDbService().getSongCount(query);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public int getSingerCount(SingerQuery query) {
        if(getDbService() == null || query == null) return 0;
        try {
            return getDbService().getSingerCount(query);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public List<String> getSongSmartPinyin(Query query) {
        if(getDbService() == null || query == null) return null;
        try {
            return getDbService().querySongSmartPinyin(query);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<String> getSingerSmartPinyin(SingerQuery query) {
        if(getDbService() == null || query == null) return null;
        try {
            return getDbService().querySingerSmartPinyin(query);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }
}

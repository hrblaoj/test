package com.shinetvbox.vod.service.cloudserver;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.shinetvbox.vod.MyApplication.APPFILEPATH;
import static com.shinetvbox.vod.MyApplication.CPU_ID;
import static com.shinetvbox.vod.MyApplication.MAC_ADDRESS;
import static com.shinetvbox.vod.MyApplication.SHINEDBDIR;
import static com.shinetvbox.vod.MyApplication.SHINESONGDDIR;
import static com.shinetvbox.vod.MyApplication.SHINEUPDBDIR;
import static com.shinetvbox.vod.MyApplication.SUBJECT;
import static com.shinetvbox.vod.service.cloudserver.KtvCloudDownNative.SetServerDownloadDBPath;
import static com.shinetvbox.vod.service.cloudserver.KtvCloudDownNative.SetServerDownloadSongPath;
import static com.shinetvbox.vod.service.cloudserver.KtvCloudDownNative.SetServerDownloadUpdateDBPath;
import static com.shinetvbox.vod.service.cloudserver.KtvCloudDownNative.setServerCpuIdAndSubject;
import static com.shinetvbox.vod.service.cloudserver.KtvCloudDownNative.setServerDwonloadUrl;
import static com.shinetvbox.vod.utils.updateapp.HttpConstant.urlGetDownLoadFiles;

/**
 * Created by Administrator on 2018/5/9.
 */

public class CloudDownloadService extends Service {

    private CloudDownloadService.MyBinder mIBinder;
    private LogThread mThrLog = null;

//    public String cpuid;
//    public String mac;
//    public String subject;
//    public String downloadUrl;
//    public String appfilepath;
//    public String shinedbdir;
//    public String shinesongddir;
//    public String shineupdbdir;

    //client 可以通过Binder获取Service实例
    public class MyBinder extends Binder {
        public CloudDownloadService getService() {
            return CloudDownloadService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    @Override
    public void onCreate() {
        //android.os.Debug.waitForDebugger();
//        Log.i("dfdf", "startCloudServer onCreate");
        super.onCreate();
        mIBinder = new CloudDownloadService.MyBinder();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.i("Kathy", "TestTwoService - onStartCommand - startId = " + startId + ", Thread = " + Thread.currentThread().getName());
//        appfilepath = intent.getStringExtra( "appfilepath" );
//        shinedbdir = intent.getStringExtra( "shinedbdir" );
//        shinesongddir = intent.getStringExtra( "shinesongddir" );
//        shineupdbdir = intent.getStringExtra( "shineupdbdir" );
//        cpuid = intent.getStringExtra( "cpuid" );
//        mac = intent.getStringExtra( "mac" );
//        subject = intent.getStringExtra( "subject" );
//        downloadUrl = intent.getStringExtra( "url" );

//        SetServerDownloadSongPath(shinesongddir);
//        SetServerDownloadDBPath(shineupdbdir);
//        SetServerDownloadUpdateDBPath(shinedbdir, appfilepath);
//        setServerCpuIdAndSubject( cpuid,mac,subject );
//        setServerDwonloadUrl(downloadUrl);

        SetServerDownloadSongPath(SHINESONGDDIR);
        SetServerDownloadDBPath(SHINEUPDBDIR);
        SetServerDownloadUpdateDBPath(SHINEDBDIR, APPFILEPATH);
        setServerCpuIdAndSubject( CPU_ID,MAC_ADDRESS,SUBJECT );
        setServerDwonloadUrl(urlGetDownLoadFiles);
        mThrLog = new LogThread();
        mThrLog.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("Kathy", "onDestroy - Thread ID = " + Thread.currentThread().getId());
        super.onDestroy();

        android.os.Process.killProcess( android.os.Process.myPid() );
    }

    //log
    class LogThread extends Thread{
        @Override
        public void run() {

            try {
//                Log.e("dfdf", "onCreate start method startccloudserver");
                KtvCloudDownNative.startccloudserver();
            }
            catch (Exception e){
//                Log.e("dfdf", "onCreate start method startccloudserver failed"+e);
            }
        }
    }

}

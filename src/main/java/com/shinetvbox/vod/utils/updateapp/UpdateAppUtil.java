package com.shinetvbox.vod.utils.updateapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.shinetvbox.vod.BuildConfig;
import com.shinetvbox.vod.MyApplication;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.data.eventbus.EventBusMessageUpdateApp;
import com.shinetvbox.vod.data.params.FragmentParams;
import com.shinetvbox.vod.manager.EventBusManager;
import com.shinetvbox.vod.utils.FileUtil;
import com.shinetvbox.vod.utils.MD5Util;
import com.shinetvbox.vod.utils.UnzipUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;

import okhttp3.Call;
import okhttp3.Request;

public class UpdateAppUtil {
    /**
     * 更新包存储路径
     */
    public static String pathApp = MyApplication.SHINESDCARDDIR+ "/updateapp/";

    private static String tag_update_app = "update_app";

    private static boolean installEnable = false;
    private static boolean isStartLoading = false;
    private static boolean isUnzipOk = false;

    private static Context mContextApp = null;

    /**
     * 初始化
     * @param context
     */
    public static void init(Context context) {
        if (context == null || mContextApp != null) return;
        mContextApp = context;
        if(!FileUtil.fileIsExists( pathApp )){
            FileUtil.makeRootDirectory( pathApp );
        }
        FileUtil.deleteAllFile( new File( pathApp ),false );
        getServerVersion();
    }
    /**
     * 获取服务器应用的版本
     */
    private static void getServerVersion() {
        OkHttpUtils.post()
                .url( HttpConstant.urlGetVersion )
                .params( HttpConstant.getVersionPostParams( "0" ) )
                .build()
                .connTimeOut( 10000 )
                .readTimeOut( 10000 )
                .execute( new MyStringCallback( MyStringCallback.TYPE_VERSION_INFO ) );
    }

    private static void getDwonloadInfo() {
        OkHttpUtils.post()
                .url( HttpConstant.urlGetDownLoadApp )
                .params( HttpConstant.getVersionPostParams( ""+VersionUtil.getSermaxversion() ) )
                .build()
                .connTimeOut( 10000 )
                .readTimeOut( 10000 )
                .execute( new MyStringCallback( MyStringCallback.TYPE_DOWNLOAD_INFO ) );
    }
    private static void checkNewVersion() {
        if(VersionUtil.hasNewVersionByName()) {
            isStartLoading = false;
            isUnzipOk = false;
            EventBusMessage msg = new EventBusMessage();
            FragmentParams param = new FragmentParams();
            msg.what = EventBusConstants.PAGE_GOTO_UPDATEAPP;
            param.pageIndex = msg.what;
            param.isShowBtnSelectedSong = false;
            param.softUpdateInfo.version = VersionUtil.getAppVersionNameServer();
            param.softUpdateInfo.describe = VersionUtil.getDescription();
            msg.obj = param;
            EventBusManager.sendMessage( msg );
        }
    }

    public static void downNewVersion() {
        if(isStartLoading) return;
        if(isUnzipOk){
            checkAppExists();
            return;
        }
        installEnable = true;
        isStartLoading = true;
        Log.i( "shinektv", "开始下载更新包" );
        if(!VersionUtil.hasNewVersionByName()) return;
        OkHttpUtils
                .get()
                .tag( tag_update_app )
                .url( VersionUtil.getDownloadUrl() )
                .build()
                .connTimeOut( 10000 )
                .readTimeOut( 3600000 )
                .execute( new MyFileCallback( pathApp, VersionUtil.getFilename() ) );
    }
    public static void stopDownNewVersion() {
        if(!installEnable) return;
        installEnable = false;
        OkHttpUtils.getInstance().cancelTag( tag_update_app );
    }
    private static void installApp(String path) {
        if(!installEnable) return;
        File appf = new File( path );
        if(!appf.exists()) return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContextApp, BuildConfig.APPLICATION_ID + ".fileProvider", appf);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(appf), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mContextApp.startActivity(intent);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
////            File f = new File( mContextApp.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "xxx.apk" );
////            Uri contentUri = FileProvider.getUriForFile(mContextApp,BuildConfig.APPLICATION_ID+ ".fileprovider", appf);
////            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
////        }else{
//            intent.setDataAndType( Uri.fromFile(appf),
//                    "application/vnd.android.package-archive");
////        }
//        mContextApp.startActivity(intent);
    }

    private static class MyStringCallback extends StringCallback {
        public static final int TYPE_VERSION_INFO = 0;
        public static final int TYPE_DOWNLOAD_INFO = 1;

        private int type = -1;

        public MyStringCallback(int type) {
            this.type = type;
        }

        @Override
        public void onBefore(Request request, int id) {
//            setTitle("loading...");
        }

        @Override
        public void onAfter(int id) {
//            setTitle("Sample-okHttp");
        }

        @Override
        public void onError(Call call, Exception e, int id) {
//            e.printStackTrace();
        }

        @Override
        public void inProgress(float progress, long total, int id) {
        }
        @Override
        public void onResponse(String response, int id) {
            if (type == TYPE_VERSION_INFO) {
                VersionUtil.analysisJsonVersionInfo( response );
                getDwonloadInfo();
            } else if (type == TYPE_DOWNLOAD_INFO) {
                VersionUtil.analysisJsonDownloadInfo( response );
                checkNewVersion();
            }
        }
    }


    private static class MyFileCallback extends FileCallBack {

        private EventBusMessageUpdateApp msg = new EventBusMessageUpdateApp();
        private long curTime = 0;

        public MyFileCallback(String destFileDir, String destFileName) {
            super( destFileDir, destFileName );
        }

        @Override
        public void onError(Call call, Exception e, int id) {
        }

        @Override
        public void inProgress(float progress, long total, int id) {
            if(System.currentTimeMillis()-curTime>1000 && installEnable){
                msg.what = EventBusConstants.APP_UPDATE_PROGRESS;
                msg.progress = (int) (progress*100);
                EventBusManager.sendMessage( msg );
                curTime = System.currentTimeMillis();
            }
        }

        @Override
        public void onResponse(final File response, final int id) {
            msg.what = EventBusConstants.APP_UPDATE_PROGRESS;
            msg.progress = 100;
            EventBusManager.sendMessage( msg );
            isStartLoading = false;
            new Thread( new Runnable() {
                @Override
                public void run() {
                    if (!VersionUtil.getEtag().equals( MD5Util.getFileMD5( response ) )) {
                        response.delete();
                        Log.i( "shinektv", id + "更新包Md5验证未通过" );
                    } else {
                        if (!response.getAbsolutePath().contains( ".zip" )) return;
                        UnzipUtil.Unzip( response.getAbsolutePath(), pathApp, handlerUnzip );
                        Log.i( "shinektv", id + "更新包加载完成,解压中..." );
                    }
                }
            } ).start();
        }
        @SuppressLint("HandlerLeak")
        private Handler handlerUnzip = new Handler(  ){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case UnzipUtil.UNZIP_OVER:
                        Log.i( "shinektv", "更新包解压完成" );
                        isUnzipOk = true;
                        checkAppExists();
                        break;
                    case UnzipUtil.UNZIP_ERROR:
                        Log.i( "shinektv", "更新包解压错误" );
                        break;
                }
            }
        };
    }

    private static void checkAppExists(){
        if(!installEnable) return;
        File forder = new File( pathApp );
        if(forder.exists() && forder.list()!=null && forder.list().length != 0){
            for(File updateFile:forder.listFiles()){
                if(updateFile.getName().contains( ".apk" )){
                    if(updateFile.getName().startsWith( "shinetvbox" )){
                        installApp( updateFile.getPath() );
                        return;
                    }
                }
            }
        }
    }

}
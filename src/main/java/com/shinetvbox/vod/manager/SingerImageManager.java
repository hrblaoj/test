package com.shinetvbox.vod.manager;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.shinetvbox.vod.MyApplication;
import com.shinetvbox.vod.data.constants.Constants;
import com.shinetvbox.vod.utils.FileUtil;
import com.shinetvbox.vod.utils.KtvLog;
import com.shinetvbox.vod.utils.SharedPreferencesUtil;
import com.shinetvbox.vod.utils.UnzipUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SingerImageManager {

    private static SingerImageManager mInstance = null;
    public static SingerImageManager getInstance(){
        if(mInstance==null){
            synchronized(ResManager.class){
                if(mInstance==null){
                    mInstance = new SingerImageManager();
                }
            }
        }
        return mInstance;
    }


    private Application mActivity = null;
    private final String singerName = "singer.zip";
    private Handler zipHandler;

    public void init(Application activity){
        if(mActivity!=null) return;
        mActivity = activity;
        time = System.currentTimeMillis();
        initHandler();
        initSingerImage();
    }
    long time = 0;
    private void initSingerImage(){
        if(SharedPreferencesUtil.isSingerImageUnzip()){
            MyApplication.isInitSingerImage = true;
            return;
        }
        new Thread( new Runnable() {
            @Override
            public void run() {
                InputStream in = null;
                FileOutputStream out = null;
                try {
                    in = mActivity.getAssets().open(singerName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(in == null) {
                    MyApplication.isInitSingerImage = true;
                    return;
                }

                File singerFile = new File( MyApplication.SHINESDCARDDIR+singerName );

                try {
                    out = new FileOutputStream(singerFile);
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
                    MyApplication.isInitSingerImage = true;
                    return;
                }

                if(!FileUtil.fileIsExists( MyApplication.SHINESDCARDDIR + "singer/" )){
                    FileUtil.makeRootDirectory( MyApplication.SHINESDCARDDIR + "singer/" );
                }

                if(FileUtil.fileIsExists( MyApplication.SHINESDCARDDIR+singerName )){
                    UnzipUtil.Unzip( MyApplication.SHINESDCARDDIR+singerName,MyApplication.SHINESDCARDDIR,zipHandler );
                }
            }
        } ).start();
    }
    @SuppressLint("HandlerLeak")
    private void initHandler(){
        zipHandler = new Handler(  ){
            @Override
            public void handleMessage(Message msg) {
                FileUtil.deleteFile( MyApplication.SHINESDCARDDIR+singerName );
                MyApplication.isInitSingerImage = true;
                switch (msg.what){
                    case UnzipUtil.UNZIP_OVER:
                        SharedPreferencesUtil.setSingerImageUnzip( true );
                        break;
                    case UnzipUtil.UNZIP_ERROR:
                        SharedPreferencesUtil.setSingerImageUnzip( false );
                        break;
                }
            }
        };
    }
    public String getSingerImage(String singerId){
        String path = MyApplication.SHINE_SINGER_IMAGE_PATH +singerId+".jpg";
        if(!FileUtil.fileIsExists( path )){
            path = Constants.SINGER_IMAGE_PATH+singerId+".jpg";
        }
        return path;
    }
}

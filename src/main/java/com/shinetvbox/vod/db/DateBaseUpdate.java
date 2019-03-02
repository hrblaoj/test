package com.shinetvbox.vod.db;

import com.shinetvbox.vod.MyApplication;
import com.shinetvbox.vod.service.cloudserver.CloudDownloadSongStruce;
import com.shinetvbox.vod.service.cloudserver.CloudMessageProc;
import com.shinetvbox.vod.utils.FileUtil;
import com.shinetvbox.vod.utils.KtvLog;

import java.io.File;

import static com.shinetvbox.vod.MyApplication.SHINEDBDIR;
import static com.shinetvbox.vod.service.cloudserver.CloudMessageProc.DOWNLOAD_CMD.CLOUD_SERVICE_OK;
import static com.shinetvbox.vod.service.cloudserver.CloudMessageProc.DOWNLOAD_CMD.GET_DATABASE;
import static com.shinetvbox.vod.service.cloudserver.CloudMessageProc.DOWNLOAD_CMD.GET_DB_ALL;
import static com.shinetvbox.vod.service.cloudserver.CloudMessageProc.DOWNLOAD_CMD.GET_DB_VERSION;
import static com.shinetvbox.vod.service.cloudserver.CloudMessageProc.DOWNLOAD_CMD.GET_DB_VERSION_OK;
import static com.shinetvbox.vod.service.cloudserver.CloudMessageProc.DOWNLOAD_CMD.GET_SONG;

/**
 * Created by hrblaoj on 2019/1/21.
 */

public class DateBaseUpdate {
    static String canUpdateName = "db_canUpdate";
    public static   MyApplication.InitCallBack initCallBack = new MyApplication.InitCallBack() {
        @Override
        public void init() {
            File dbDirFile = new File(SHINEDBDIR);
            if(!dbDirFile.isDirectory()){
                return;
            }
            else {

            }
            String[] filelist = dbDirFile.list();
            for (int i = 0; i < filelist.length; i++) {

                if(filelist[i].equals("ktv10.db_bak") || filelist[i].equals(canUpdateName) || filelist[i].equals("ktv10.db")){
                    continue;
                }
                else
                {
                    File readfile = new File(SHINEDBDIR + filelist[i]);
                    readfile.delete();
                }
            }
            File isUpdateFile = new File(SHINEDBDIR + canUpdateName);
            File isDbBak = new File(SHINEDBDIR + "ktv10.db_bak");
            if(isUpdateFile.exists() && isDbBak.exists()){
                if(0 < KtvDbNative.getSongCountByDbName(SHINEDBDIR + "ktv10.db_bak")) {
                    FileUtil.RenameFile(SHINEDBDIR + "ktv10.db_bak", SHINEDBDIR + "ktv10.db");
                    isUpdateFile.delete();
                }
            }

            if(isDbBak.exists()){
                isDbBak.delete();
            }

            FileUtil.copyFile(SHINEDBDIR + "ktv10.db", SHINEDBDIR + "ktv10.db_bak");

//            CloudMessageProc.getInTance().new cloudRecvProc(CLOUD_SERVICE_OK) {
//                @Override
//                public void onProc(CloudDownloadSongStruce obj) {
//                    CloudMessageProc.getInTance().cloudSend4Out(new
// (GET_DB_ALL, "0"), new CloudMessageProc.onSendFailedProc() {
//                        @Override
//                        public void onSendFailedProc() {
//                            KtvLog.d("procccccccc");
//                        }
//                    });
//                }
//            };

//            CloudMessageProc.getInTance().new cloudRecvProc(GET_DB_VERSION_OK) {
//                @Override
//                public void onProc(CloudDownloadSongStruce obj) {
//                    CloudMessageProc.getInTance().cloudSend4Out(new CloudDownloadSongStruce(GET_DATABASE, "0"), new CloudMessageProc.onSendFailedProc() {
//                        @Override
//                        public void onSendFailedProc() {
//                            KtvLog.d("procccccccc");
//                        }
//                    });
//                }
//            };
        }
    };
}

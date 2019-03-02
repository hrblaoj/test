package com.shinetvbox.vod.service.cloudserver;

import com.shinetvbox.vod.MyApplication;

import static com.shinetvbox.vod.service.cloudserver.CloudMessageProc.DOWNLOAD_CMD.CLOUD_SERVICE_OK;


/**
 * Created by hrblaoj on 2019/1/11.
 */

public class CloudManger {
    public static   MyApplication.InitCallBack initCallBack = new MyApplication.InitCallBack() {
        @Override
        public void init() {
            CloudMessageProc p = new CloudMessageProc();


        }
    };
}

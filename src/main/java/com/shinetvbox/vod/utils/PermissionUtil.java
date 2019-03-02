package com.shinetvbox.vod.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.manager.EventBusManager;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtil {

    private static AppCompatActivity mActivity;
    private static EventBusMessage msg;

    private static final int mRequestCode = 10000;
    private static String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private static List<String> mPermissionList = new ArrayList<>();

    public static void init(AppCompatActivity activity){
        mActivity = activity;
        initPermission();
    }
    //权限判断和申请
    private static void initPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            msg = new EventBusMessage();
            msg.what = EventBusConstants.REQUEST_PERMISSION_SUCCESS;
            EventBusManager.sendMessage( msg );
        }else{
            mPermissionList.clear();//清空没有通过的权限
            //逐个判断你要的权限是否已经通过
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(mActivity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);//添加还未授予的权限
                }
            }
            //申请权限
            if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
                ActivityCompat.requestPermissions(mActivity, permissions, mRequestCode);
            }else{
                //说明权限都已经通过，可以做你想做的事情去
                msg = new EventBusMessage();
                msg.what = EventBusConstants.REQUEST_PERMISSION_SUCCESS;
                EventBusManager.sendMessage( msg );
            }
        }
    }
    public static void setResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean isOK = true;
        switch (requestCode) {
            case mRequestCode: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        isOK = false;
                    }
                }
                if (isOK) {
                    msg = new EventBusMessage();
                    msg.what = EventBusConstants.REQUEST_PERMISSION_SUCCESS;
                    EventBusManager.sendMessage( msg );
//                        Log.e("TTT","Permissions --> " + "Permission Granted: " + permissions[i]);
                } else{
                    msg = new EventBusMessage();
                    msg.what = EventBusConstants.REQUEST_PERMISSION_FAILURE;
                    EventBusManager.sendMessage( msg );
//                        Log.e("TTT","Permissions --> " + "Permission Denied: " + permissions[i]);
                }
            }
            break;
        }
    }
}

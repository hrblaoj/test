package com.shinetvbox.vod.utils.updateapp;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.shinetvbox.vod.MyApplication;
import com.shinetvbox.vod.data.custom.JsonData;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.utils.FileUtil;

import java.io.File;
import java.io.IOException;

public class VersionUtil {
    private static String codeversion = "";
    private static String typeversion = "";
    private static String description = "";
    private static int startversion = -1;
    private static int sermaxversion = -1;

    private static String versionAppNameServer = "";

    private static String codedownload = "";
    private static String typedownload = "";
    private static String filename = "";
    private static String downloadUrl = "";
    private static String etag = "";
    private static int size = -1;
    private static String versionNameAppLocal = "";

    public static void analysisJsonVersionInfo(String jsonString) {
        //{"code":"0","type":"success","":"获取成功","result":{"startversion":2,"sermaxversion":2}}
        JsonData jsonData = new JsonData( jsonString );
        codeversion = jsonData.getValueString( "code" );
        typeversion = jsonData.getValueString( "type" );
        startversion = jsonData.getSection( "result" ).getValueInt( "startversion" );
        sermaxversion = jsonData.getSection( "result" ).getValueInt( "sermaxversion" );
    }
    public static void analysisJsonDownloadInfo(String jsonString) {
        //{"code":"0","type":"success","description":"获取成功","result":{"softtype":"1101",
        // "filename":"app1.0.1.apk","downurl":"http://rznetwork.platupdate.cloudsong.kss.ksyun.com/
        // 1218/app1.0.1.apk?AccessKeyId=nXLz3axA9cpFXPsrs0fS&Expires=1528438913&Signature=
        // JUWD7QvYXcnhkWeGMsIH/XJUKcM%3D&","contentlength":7356593,"etag":"b44247cf6577ff675d515af4140bea04",
        // "description":"","versioncode":"v1.0.1"}}
        JsonData jsonData = new JsonData( jsonString );
        codedownload = jsonData.getValueString( "code" );
        typedownload = jsonData.getValueString( "type" );
        filename = jsonData.getSection( "result" ).getValueString( "filename" );
        versionAppNameServer = jsonData.getSection( "result" ).getValueString( "versioncode" );
        downloadUrl = jsonData.getSection( "result" ).getValueString( "downurl" );
        etag = jsonData.getSection( "result" ).getValueString( "etag" );
        description = jsonData.getSection( "result" ).getValueString( "description" );
        size = jsonData.getSection( "result" ).getValueInt( "contentlength" );
    }

    /**
     * 检查更新包是否安装（通过已安装App的versionName和服务器versioncode对比），未安装则继续hasDownloadFile()
     * @return
     */
    public static boolean hasNewVersionByName(){
        if( !versionAppNameServer.equals( "" ) && !versionAppNameServer.equals( getAppVersionNameLocal() ) ){
            return compareToVersion(getAppVersionNameLocal(),versionAppNameServer);
        }
        return false;
    }
    public static boolean compareToVersion(String lv, String sv){
        String svt = sv.replace( "v","" );
        String lvt = lv.replace( "v","" );
        String[] svts = svt.split( "\\." );
        String[] lvts = lvt.split( "\\." );
        if(svts.length!=lvts.length) return true;
        for(int i=0;i<svts.length;i++){
            try {
                if(Integer.parseInt( lvts[i] )>Integer.parseInt( svts[i] )) return false;
                if(Integer.parseInt( lvts[i] )<Integer.parseInt( svts[i] )) return true;
            }catch (Exception e){
            }
        }
        return false;
    }

    public static int getStartversion() {
        return startversion;
    }

    public static int getSermaxversion() {
        return sermaxversion;
    }


    public static String getAppVersionNameServer() {
        return versionAppNameServer;
    }

    public static String getFilename() {
        return filename;
    }

    public static String getDownloadUrl() {
        return downloadUrl;
    }

    public static String getEtag() {
        return etag;
    }

    public static int getSize() {
        return size;
    }

    public static String getDescription() {
        return description;
    }

    public static String getAppVersionNameLocal() {
        if(versionNameAppLocal.equals( "" )){
            getLocalVersion();
        }
        return versionNameAppLocal;
    }

    /**
     * 获取当前应用的版本
     */;
    private static void getLocalVersion(){
        try {
            PackageManager managerApp = MyApplication.getInstance().getPackageManager();
            PackageInfo infoApp = managerApp.getPackageInfo( MyApplication.getInstance().getPackageName(),0);
            versionNameAppLocal = infoApp.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.shinetvbox.vod.service.cloudserver;

/**
 * Created by Administrator on 2018/5/10.
 */

public class KtvCloudDownNative {

    static {
        System.loadLibrary("native-lib");
    }


    public static native boolean startccloudserver();

    public static native boolean SetServerDownloadSongPath( String pPath );
    public static native boolean SetServerDownloadDBPath( String pPath );
    public static native boolean SetServerDownloadUpdateDBPath( String pPath, String para);
    public static native boolean setServerCpuIdAndSubject(String cpuid,String mac,String subject);
    public static native boolean setServerDwonloadUrl(String url);
//    public static native boolean SetKTVDBPath( String pPath );

    //CLOUDSERVER_COMMONPATH;
    //String fileName = CLOUDSERVER_VRSN_TXT_FRM_NET;

//    public static native boolean SetServerDownloadDBNetVersion( String version );
//    public static native boolean SetServerDownloadDBLocalVersion( String version );
}

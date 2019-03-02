package com.shinetvbox.vod.utils.updateapp;
import android.util.Log;

import com.shinetvbox.vod.MyApplication;
import com.shinetvbox.vod.utils.DateUtil;
import com.shinetvbox.vod.utils.MD5Util;

import java.util.HashMap;
import java.util.Map;

public class HttpConstant {
    /**
     * 获取服务器时间
     */
    public static final String urlGetServerTime = "http://ks3.cloud.joyk.com.cn/Cloud/GetSysDateTimestamp";
    /**
     * 获取版本信息链接
     */
    public static final String urlGetVersion = "http://ks3.cloud.joyk.com.cn/App/GetSoftVersion";
    /**
     * 获取下载app链接
     */
    public static final String urlGetDownLoadApp = "http://ks3.cloud.joyk.com.cn/App/DownSoftFile";
    /**
     * 获取下载文件链接
     */
    public static final String urlGetDownLoadFiles = "http://ks3.cloud.joyk.com.cn/App/DownSongFile";
    /**
     * 获取会员链接
     */
    public static final String urlGetAppMemeberInfo = "http://ks3.cloud.joyk.com.cn/App/GetUserDetail";
    /**
     * 获取会员续费列表
     */
    public static final String urlGetAppPayList = "http://ks3.cloud.joyk.com.cn/App/GetLevelPage";
    /**
     * 获取会员续费二维码信息
     */
    public static final String urlGetAppPayQrcodeInfo = "http://ks3.cloud.joyk.com.cn/Pay/App/GetAppPayUrl";
    /**
     * 获取会员续费结果
     */
    public static final String urlGetAppPayResultInfo = "http://ks3.cloud.joyk.com.cn/Pay/App/orderQuery";
    /**
     * 获取微信点歌二维码
     */
    public static final String urlGetAppWechatQrcodeInfo = "http://oaplat.joyk.com.cn/WxQrCode/App";
    /**
     * 获取微信点歌websocket信息
     */
    public static final String urlGetAppWechatWebSocketInfo = "http://wechatkfun.joyk.com.cn:800/api/WxApi/AppOnline";
    /**
     * 计算签名算法时key放到所有拼接参数后面，再计算方式:MD5
     */
    public static final String key = "platZP6SNVHJoaTJOq4bon6tXL2HWc5R";
    /**
     * soft_type 1220 安卓电视盒子
     */
    public static final String soft_type = "1220";

    public static Map<String,String> getVersionPostParams(String localVersion){
        String strRandom = ""+ (int) (Math.random()*10000000);
        String strDate = DateUtil.getServerTimeInSecond();
        String sign = "cpuid="+MyApplication.CPU_ID+"&mac="+MyApplication.MAC_ADDRESS+"&noncestr="+
                strRandom+"&soft_type="+soft_type+"&subject="+MyApplication.SUBJECT+"&timestamp="+
                strDate+"&version="+localVersion+"&key="+key;

        Map<String, String> params = new HashMap<>();
        params.put("cpuid", MyApplication.CPU_ID);
        params.put("mac", MyApplication.MAC_ADDRESS);
        params.put("noncestr", strRandom);
        params.put("sign", MD5Util.string2MD5( sign ).toUpperCase());
        params.put("soft_type", soft_type);
        params.put("subject", MyApplication.SUBJECT);
        params.put("timestamp", strDate);
        params.put("version", localVersion);
//        Log.i( "22222222222222",sign+"-----"+params );
        return params;
    }

    public static Map<String,String> getAppMemberInfoPostParams(){
        String strRandom = ""+ (int) (Math.random()*10000000);
        String strDate = DateUtil.getServerTimeInSecond();
        String sign = "cpuid="+MyApplication.CPU_ID+"&mac="+MyApplication.MAC_ADDRESS+"&noncestr="+
                strRandom+"&subject="+MyApplication.SUBJECT+"&timestamp="+strDate+"&key="+key;

        Map<String, String> params = new HashMap<>();
        params.put("cpuid", MyApplication.CPU_ID);
        params.put("mac", MyApplication.MAC_ADDRESS);
        params.put("noncestr", strRandom);
        params.put("sign", MD5Util.string2MD5( sign ).toUpperCase());
        params.put("subject",MyApplication.SUBJECT);
        params.put("timestamp", strDate);
//        Log.i( "22222222222222",sign+"-----"+params );
        return params;
    }

    public static Map<String,String> getSongDownloadInfoPostParams(String songid){
        String strRandom = ""+ (int) (Math.random()*10000000);
        String strDate = DateUtil.getServerTimeInSecond();
        String sign = "cpuid="+MyApplication.CPU_ID+"&mac="+MyApplication.MAC_ADDRESS+"&noncestr="+
                strRandom+"&songid="+songid+"&subject="+MyApplication.SUBJECT+"&timestamp="+strDate+"&key="+key;

        Map<String, String> params = new HashMap<>();
        params.put("cpuid", MyApplication.CPU_ID);
        params.put("mac", MyApplication.MAC_ADDRESS);
        params.put("noncestr", strRandom);
        params.put("sign", MD5Util.string2MD5( sign ).toUpperCase());
        params.put("songid",songid);
        params.put("subject",MyApplication.SUBJECT);
        params.put("timestamp", strDate);
//        Log.i( "22222222222222",sign+"-----"+params );
        return params;
    }

    public static Map<String,String> getAppPayQrcodePostParams(String level){
        String strRandom = ""+ (int) (Math.random()*10000000);
        String strDate = DateUtil.getServerTimeInSecond();
        String sign = "cpuid="+MyApplication.CPU_ID+"&level="+level+"&mac="+MyApplication.MAC_ADDRESS+"&noncestr="+
                strRandom+"&subject="+MyApplication.SUBJECT+"&timestamp="+strDate+"&key="+key;

        Map<String, String> params = new HashMap<>();
        params.put("cpuid", MyApplication.CPU_ID);
        params.put("level", level);
        params.put("mac", MyApplication.MAC_ADDRESS);
        params.put("noncestr", strRandom);
        params.put("sign", MD5Util.string2MD5( sign ).toUpperCase());
        params.put("subject",MyApplication.SUBJECT);
        params.put("timestamp", strDate);
//        Log.i( "22222222222222",sign+"-----"+params );
        return params;
    }

    public static Map<String,String> getAppPayResultPostParams(String ordercode){
        String strRandom = ""+ (int) (Math.random()*10000000);
        String strDate = DateUtil.getServerTimeInSecond();
        String sign = "cpuid="+MyApplication.CPU_ID+"&mac="+MyApplication.MAC_ADDRESS+"&noncestr="+
                strRandom+"&ordercode="+ordercode+"&subject="+MyApplication.SUBJECT+"&timestamp="+strDate+"&key="+key;

        Map<String, String> params = new HashMap<>();
        params.put("cpuid", MyApplication.CPU_ID);
        params.put("ordercode", ordercode);
        params.put("mac", MyApplication.MAC_ADDRESS);
        params.put("noncestr", strRandom);
        params.put("sign", MD5Util.string2MD5( sign ).toUpperCase());
        params.put("subject",MyApplication.SUBJECT);
        params.put("timestamp", strDate);
//        Log.i( "22222222222222",sign+"-----"+params );
        return params;
    }

    public static Map<String,String> getAppWechatQrcodePostParams(){
        String strRandom = ""+ (int) (Math.random()*10000000);
        String strDate = DateUtil.getServerTimeInSecond();
        String sign = "cpuid="+MyApplication.CPU_ID+"&mac="+MyApplication.MAC_ADDRESS+"&noncestr="+
                strRandom+"&subject="+MyApplication.SUBJECT+"&timestamp="+
                strDate+"&type=QR"+"&key="+key;

        Map<String, String> params = new HashMap<>();
        params.put("cpuid", MyApplication.CPU_ID);
        params.put("mac", MyApplication.MAC_ADDRESS);
        params.put("noncestr", strRandom);
        params.put("sign", MD5Util.string2MD5( sign ).toUpperCase());
        params.put("subject",MyApplication.SUBJECT);
        params.put("timestamp", strDate);
        params.put("type", "QR");
//        Log.i( "22222222222222",sign+"-----"+params );
        return params;
    }

    public static Map<String,String> getAppWebSocketInfoPostParams(){
        String strRandom = ""+ (int) (Math.random()*10000000);
        String strDate = DateUtil.getServerTimeInSecond();
        String sign = "cpuid="+MyApplication.CPU_ID+"&mac="+MyApplication.MAC_ADDRESS+"&noncestr="+
                strRandom+"&subject="+MyApplication.SUBJECT+"&timestamp="+strDate+"&key="+key;

        Map<String, String> params = new HashMap<>();
        params.put("cpuid", MyApplication.CPU_ID);
        params.put("mac", MyApplication.MAC_ADDRESS);
        params.put("noncestr", strRandom);
        params.put("sign", MD5Util.string2MD5( sign ).toUpperCase());
        params.put("subject",MyApplication.SUBJECT);
        params.put("timestamp", strDate);
//        Log.i( "22222222222222",sign+"-----"+params );
        return params;
    }
}

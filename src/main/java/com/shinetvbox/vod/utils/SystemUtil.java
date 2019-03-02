package com.shinetvbox.vod.utils;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.shinetvbox.vod.MyApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SystemUtil {

    public static String packageName = "";

    public static int uiWidth = 1920;
    public static int uiHeight = 1080;
    public static int screenWidth = 1920;
    public static int screenHeight = 1080;

    private static String time = "";
    private static String yearAgoTime = "";

    public static void init(Application activity) {
        // 获取屏幕的高宽
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
//        Log.d("shinetvbok", screenWidth+"=======screen======"+screenHeight);
//        Log.d("shinetvbok", scaleX +"======scale======="+ scaleY );
        packageName = activity.getPackageName();

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //当前系统时间
        long tmptime = 0;
        Date date = new Date();
        time = format.format(date);
        if(date.getTime() != 0) {
            tmptime = date.getTime() - (long) 12 * 30 * 24 * 3600 * 1000;
            date.setTime(tmptime);
            yearAgoTime = format.format(date);
        }else{
            yearAgoTime = "2017-01-01";
        }
    }

    public static String getStaticYearAgoTime(){
        return yearAgoTime;
    }

    /**
     * 获取系统MAC号
     * */
    public static String gethMac(Context context){
        if(MyApplication.MAC_ADDRESS!=null && !MyApplication.MAC_ADDRESS.equals( "" )) return MyApplication.MAC_ADDRESS;
        String mac = "";
        mac = getPerMac(context,"/sys/class/net/eth0/address");
        if(mac.equals( "" )){
            mac = getPerMac(context,"/sys/class/net/wlan0/address");
        }
        if(mac.equals( "" )){
            mac = getMacAddressFromIp(context).replace( ":","" );
        }
        return mac;
    }

    private static String getPerMac(Context context,String path){
        String mac = "";
        File file = new File(path);
        if(!file.exists()) return mac;

        Reader reader = null;
        FileInputStream in = null;
        try {
            // 一次读一个字符
            reader = new InputStreamReader(in = new FileInputStream(file));
            int tempchar;
            while ((tempchar = reader.read()) != -1) {
                // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
                // 但如果这两个字符分开显示时，会换两次行。
                // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
                if (((char) tempchar) != '\r' && ((char) tempchar) != '\n' && ((char) tempchar) != ':') {
                    mac += (char)(tempchar);
                }
            }

        } catch (Exception e) {
//            e.printStackTrace();
        }
        finally {
            if(null != reader)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if(null != in)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            return mac;
        }
    }
    /**
     * 获取系统MAC号
     * */
    public static String getRoomId(String mac){
        if(MyApplication.ROOM_ID !=null && !MyApplication.ROOM_ID.equals( "" )) return MyApplication.ROOM_ID;
        String placeid = "";
        int lenMac = mac.length()/2;
        for (int i=0;i<lenMac;i++){
            placeid += mac.substring( i*2,i*2+1 );
        }
        int lenId = 10-placeid.length();
        if(lenId<0) return placeid.substring( 0,10 );
        String timer = ""+System.currentTimeMillis();
        placeid += timer.substring( timer.length()-lenId,timer.length() );
        return placeid;
    }

//    /**
//     * 获取手机的MAC地址
//     *
//     * @return
//     */
//    public static String getMac() {
//        String str = "";
//        String macSerial = "";
//        try {
//            Process pp = Runtime.getRuntime().exec(
//                    "cat /sys/class/net/wlan0/address ");
//            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
//            LineNumberReader input = new LineNumberReader(ir);
//
//            for (;  != str;) {
//                str = input.readLine();
//                if (str != ) {
//                    macSerial = str.trim();// 去空格
//                    break;
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        if (macSerial ==  || "".equals(macSerial)) {
//            try {
//                return loadFileAsString("/sys/class/net/eth0/address")
//                        .toUpperCase().substring(0, 17);
//            } catch (Exception e) {
//                e.printStackTrace();
//
//            }
//
//        }
//        return macSerial;
//    }

    public static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    public static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }


    public static String getCPUSerial() {
        if(MyApplication.CPU_ID!=null && !MyApplication.CPU_ID.equals( "" )) return MyApplication.CPU_ID;
        String myCpuSerial = android.os.Build.SERIAL;
        if(myCpuSerial!=null && !myCpuSerial.equals( "" )) return myCpuSerial;
        String chipSerial = null;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader("/proc/cpuinfo");
            br = new BufferedReader(fr);
            String readline = null;
            while ((readline = br.readLine()) != null) {
                if (readline.trim().toLowerCase().startsWith("serial")) {
                    chipSerial = readline;
                    break;
                }
            }
        } catch (IOException io) {
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }

        String[] serialSplit = chipSerial != null ? chipSerial.split(":") : null;
        if (serialSplit != null &&  serialSplit.length == 2) {
            myCpuSerial = serialSplit[1].trim();
        }
        return myCpuSerial;
    }
//    /**
//     * 获取CPU序列号
//     * @return CPU序列号(16位) 读取失败为"0000000000000000"
//     */
//    public static String getCPUSerial() {
//        String str = "", strCPU = "", cpuAddress = "0000000000000000";
//        try {
//            // 读取CPU信息
//            Process pp = Runtime. getRuntime().exec("cat/proc/cpuinfo");
//            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
//            LineNumberReader input = new LineNumberReader(ir);
//            // 查找CPU序列号
//            for ( int i = 1; i < 100; i++) {
//                str = input.readLine();
//                if (str != null) {
//                    // 查找到序列号所在行
//                    if (str.indexOf( "Serial") > -1) {
//                        // 提取序列号
//                        strCPU = str.substring(str.indexOf(":" ) + 1, str.length());
//                        // 去空格
//                        cpuAddress = strCPU.trim();
//                        break;
//                    }
//                } else {
//                    // 文件结尾
//                    break;
//                }
//            }
//        } catch (IOException ex) {
//            // 赋予默认值
//            ex.printStackTrace();
//        }
//        return cpuAddress;
//    }


    public static String getMacAddressFromIp(Context context) {
        String mac_s= "";
        StringBuilder buf = new StringBuilder();
        try {
            byte[] mac;
//            WifiManager mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress.getByName(getActiveIpAddress()));
//            NetworkInterface ne=NetworkInterface.getByInetAddress(InetAddress.getByName(mWifiManager.getConnectionInfo().getIpAddress()));
            mac = ne.getHardwareAddress();
            for (byte b : mac) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            mac_s = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mac_s;
    }
    public static final int NET= 0;
    public static final int WIFI = 1;
    public static String getActiveIpAddress(){
        String [] ips = getLocalIpAddress();
        if(ips[NET] != null) {
            return ips[NET];
        } else if(ips[WIFI] != null) {
            return ips[WIFI];
        }
        return "0.0.0.0";
    }
    //获取IP
    public static String [] getLocalIpAddress() {
        String [] ret = new String[2];
        try {
            List<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni: nilist){
                List<InetAddress>  ialist = Collections.list(ni.getInetAddresses());
                for (InetAddress address: ialist){
                    KtvLog.d("address.getHostAddress is " + address.getHostAddress() + " ni.getName() is" + ni.getName());
//                    if (!address.isLoopbackAddress() &&
//                            InetAddressUtils.isIPv4Address(address.getHostAddress())){

                    if (!address.isLoopbackAddress() && ipV4Check(address.getHostAddress())){
                        if(ni.getName().startsWith("wlan")) {
                            ret[WIFI] = address.getHostAddress();
                            KtvLog.d("ret[WIFI] is " + ret[WIFI]);
                        } else {
                            ret[NET] = address.getHostAddress();
                            KtvLog.d("ret[NET] is " + ret[NET]);
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("test", ex.toString());
        }
        return ret;
    }
    public static boolean ipV4Check(String text) {
        if (text != null && !text.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            if (text.matches(regex)) {
                // 返回判断信息
                return true;
            } else {
                // 返回判断信息
                return false;
            }
        }
        return false;
    }
}

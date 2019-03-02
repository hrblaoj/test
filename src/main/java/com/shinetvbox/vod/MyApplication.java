package com.shinetvbox.vod;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.manager.DatabaseManager;
import com.shinetvbox.vod.manager.MemberManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.manager.SingerImageManager;
import com.shinetvbox.vod.service.cloudserver.CloudDownloadService;
import com.shinetvbox.vod.service.cloudserver.CloudManger;
import com.shinetvbox.vod.service.wechat.WechatService;
import com.shinetvbox.vod.socket.SocketManger;
import com.shinetvbox.vod.utils.ConfigUtil;
import com.shinetvbox.vod.utils.FileUtil;
import com.shinetvbox.vod.utils.IniUtil;
import com.shinetvbox.vod.utils.KtvLog;
import com.shinetvbox.vod.utils.KtvSystemApi;
import com.shinetvbox.vod.utils.StorageUtil;
import com.shinetvbox.vod.utils.SystemUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;
import okhttp3.OkHttpClient;

import static com.shinetvbox.vod.utils.StorageUtil.getAvailableExternalMemorySize;


public class MyApplication extends Application {
    private static MyApplication mInstance;
    private OkHttpClient mOkHttpClient = null;

    public  static boolean scanProcess = false;
    public static int  beginNum = 0;
    public static String ProcessId = "";
    public final static String configName = "config.ini";
    public static IniUtil configIni;

    //歌星图片解压是否完成
    public static Boolean isInitSingerImage = false;

    public static String MAC_ADDRESS="";
    public static String ROOM_ID ="";
    public static String PLACE_PASSWD="123456789";
    public static String CPU_ID="";
    public static String SUBJECT = "";

    public static String APPFILEPATH;
    public static String SHINESDCARDDIR = "/sdcard/shinedir/";
    public static String SHINESONGDDIR = "/sdcard/shinedir/shinesong/";
    public static String SHINEDBDIR = "/sdcard/shinedir/shinedb/";
    public static String SHINEUPDBDIR = "/sdcard/shinedir/shineupdb/";
    public static String SHINE_SINGER_IMAGE_PATH = "/sdcard/shinedir/singer/";
    public static String SHINE_GLIDE_CACHE = "/sdcard/shinedir/glideCache/";

    public static MyApplication getInstance(){
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        APPFILEPATH = this.getFilesDir().getPath() + "/";
        if(!StorageUtil.externalMemoryAvailable()){
            SHINESDCARDDIR = "/data/data/com.shinetvbox.vod/shinedir/";
            SHINESONGDDIR = "/data/data/com.shinetvbox.vod/shinedir/shinesong/";
            SHINEDBDIR = "/data/data/com.shinetvbox.vod/shinedir/shinedb/";
            SHINEUPDBDIR = "/data/data/com.shinetvbox.vod/shinedir/shineupdb/";
            SHINE_SINGER_IMAGE_PATH = "/data/data/com.shinetvbox.vod/shinedir/singer/";
            SHINE_GLIDE_CACHE = "/data/data/com.shinetvbox.vod/shinedir/glideCache/";
        }
        MAC_ADDRESS = SystemUtil.gethMac(this);
        ROOM_ID = SystemUtil.getRoomId( MAC_ADDRESS );
        CPU_ID = SystemUtil.getCPUSerial();
        initConfig();
        String processName = getCurProcessName(this);
        if(processName.equals("com.shinetvbox.vod")) {
            mInstance = this;
            ConfigUtil.init();
            EventBus.getDefault().register( this );
            SystemUtil.init(this );
            configUnits();
        }

        ProcessId = String.valueOf(android.os.Process.myPid());
    }

    private void initConfig() {
        InputStream in = null;
        try {
            in = this.getAssets().open(configName);
            configIni = new IniUtil( in );
            SUBJECT = ((String) configIni.get( "CONFIG" ).get( "subject" )).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusMessage msg) {
        switch (msg.what){
            case EventBusConstants.REQUEST_PERMISSION_FAILURE:
                System.exit( 0 );
                break;
            case EventBusConstants.REQUEST_PERMISSION_SUCCESS:
                initApp();
                break;
        }
    }

    private void initApp() {
        MemberManager.init();
        ResManager.getInstance().init();
        initShineDir();
        initOkhttpClient();
        startWechatServer();
        //startCloudServer();
//        DateBaseUpdate.initCallBack.init();
        DatabaseManager.getInstance().init( this );
        SingerImageManager.getInstance().init(this);

        initCallBackList.add(SocketManger.initCallBack);
        initCallBackList.add(CloudManger.initCallBack);
//                initCallBackList.add(DateBaseUpdate.initCallBack);
        for (InitCallBack t : initCallBackList) {
            t.init();
        }
    }

    ArrayList<InitCallBack> initCallBackList = new ArrayList<InitCallBack>();

    public interface InitCallBack {
        void init();
    }
    public void startWechatServer(){
//        if(!KtvSystemApi.isRunningService(this, "com.shinetvbox.vod.service.wechat.WechatService")) {
            this.startService(new Intent().setClass(this, WechatService.class));
//        }
    }

    public void initShineDir(){
        if(!FileUtil.fileIsExists( SHINESDCARDDIR )){
            FileUtil.makeRootDirectory( SHINESDCARDDIR );
        }

        File shineSongDir = new File(SHINESDCARDDIR + "shinesong/");
        if(!shineSongDir.exists()){
            shineSongDir.mkdirs();
        }
        else {
            if(1024 * 1024 * 256 >= getAvailableExternalMemorySize()){
                shineSongDir.delete();
                shineSongDir.mkdirs();
            }
        }

        File shineUpDbDir = new File(SHINESDCARDDIR + "shineupdb/");
        if(!shineUpDbDir.exists()){
            shineUpDbDir.mkdirs();
        }

        File shineCloudCommon = new File(SHINESDCARDDIR + "cloudcommon/");
        if(!shineCloudCommon.exists()){
            shineCloudCommon.mkdirs();
        }

        File shineDbDir = new File(SHINESDCARDDIR + "shinedb/");
        if(!shineDbDir.exists()){
            shineDbDir.mkdirs();
        }

        if(!FileUtil.fileIsExists( SHINE_GLIDE_CACHE )){
            FileUtil.makeRootDirectory( SHINE_GLIDE_CACHE );
        }

    }

    public void InitStartCloudServer() {
        //启动云服务
        Intent nIntentCld = new Intent(this, CloudDownloadService.class);
        this.startService(nIntentCld);
    }

    public void startCloudServer(){
        //在启动云服务之前需要先， 进行数据库更新
        //启动云服务
        if(!KtvSystemApi.isRunningService(this, "com.shinetvbox.vod.service.cloudserver.CloudDownloadService")) {
            KtvLog.d("startCloudServer no isRunningService");
            InitStartCloudServer();
        }
        else{
            KtvLog.d("startCloudServer kill service and restart");
            stopCloudServer();
            InitStartCloudServer();
        }
    }

    public void stopCloudServer(){
        Intent nIntentCld = new Intent();
        nIntentCld.setClass(this, CloudDownloadService.class);
        this.stopService(nIntentCld);
    }

    private String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }
//    public void startDbServer(){
//        new Thread( new Runnable() {
//            @Override
//            public void run() {
//                copyAssertToSd("ktv10db", SHINEDBDIR + "/ktv10db");
//                //KtvCloudDownNative.startccloudserver();
//                if(!KtvSystemApi.isRunningService(mInstance, "com.shinetvbox.vod.db.DatabaseService")) {
//                    KtvLog.d("no isRunningService");
//                    startService(new Intent().setClass(mInstance, DatabaseService.class));
//                }
//                else{
//                    KtvLog.d("yes isRunningService");
//                }
//                bindService(new Intent().setClass(mInstance, DatabaseService.class), conn, 0);
//            }
//        } ).start();
//        Log.i( "22222222222222222","22222222222222222++++ start db over" );
//    }
//    private static IDatabaseService mIDatabaseService = null;
//
//    public IDatabaseService getDbService(){
//        if(MyApplication.mIDatabaseService == null) {
//            KtvLog.e("getDbService mIDatabaseService is null");
//        }
//        return mIDatabaseService;
//    }
//    private ServiceConnection conn = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            KtvLog.d("onServiceConnected");
//            mIDatabaseService = IDatabaseService.Stub.asInterface(service);
//
//            String pkg = MainActivity.class.getName();
//            String mRunningActivity = ServiceUtil.getRunningActivityName(mInstance);
//            Log.i("Service", "ktvMain connect IDatabaseService, RunningActivity="+mRunningActivity);
//
//            //如果当前运行的Activity不是FirstActivity，则认为是运行过程中，dbService奔溃后重启连接。
//            //这种情况下，要主动初始化数据库
////            if(!pkg.equals(mRunningActivity) && null != mIDatabaseService){
////                try {
////                    KtvLog.d("onServiceConnected will opendb");
////                    mIDatabaseService.opendbAndSetTemp(APPFILEPATH, APPDBEPATH);
////                } catch (RemoteException e) {
////                    e.printStackTrace();
////                }
////                KtvLog.d("onServiceConnected if");
////            }
////            else
////            {
////                KtvLog.d("onServiceConnected else");
////            }
//
//            try {
//                mIDatabaseService.opendbAndSetTemp(SHINEDBDIR + "ktv10db", APPFILEPATH);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//
//        }
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            Log.i("Service", "ktvMain onServiceDisconnected IDatabaseService!");
//            mIDatabaseService = null;
//        }
//    };
    public OkHttpClient initOkhttpClient() {
        if (mOkHttpClient == null) {
            //HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            //logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            mOkHttpClient = new OkHttpClient.Builder()
                    //.addNetworkInterceptor(logInterceptor)
                    .addInterceptor(new com.zhy.http.okhttp.log.LoggerInterceptor("JGTAG"))
                    .build();
            mOkHttpClient.dispatcher().setMaxRequestsPerHost(3);

            com.zhy.http.okhttp.OkHttpUtils.initClient(mOkHttpClient);
        }
        return mOkHttpClient;
    }
    /**
     * 注意!!! 布局时的实时预览在开发阶段是一个很重要的环节, 很多情况下 Android Studio 提供的默认预览设备并不能完全展示我们的设计图
     * 所以我们就需要自己创建模拟设备, 以下链接是给大家的福利, 按照链接中的操作可以让预览效果和设计图完全一致!
     * @see <a href="https://github.com/JessYanCoding/AndroidAutoSize/blob/master/README-zh.md#preview">dp、pt、in、mm 这四种单位的模拟设备创建方法</a>
     * <p>
     * v0.9.0 以后, AndroidAutoSize 强势升级, 将这个方案做到极致, 现在支持5种单位 (dp、sp、pt、in、mm)
     * {@link UnitsManager} 可以让使用者随意配置自己想使用的单位类型
     * 其中 dp、sp 这两个是比较常见的单位, 作为 AndroidAutoSize 的主单位, 默认被 AndroidAutoSize 支持
     * pt、in、mm 这三个是比较少见的单位, 只可以选择其中的一个, 作为 AndroidAutoSize 的副单位, 与 dp、sp 一起被 AndroidAutoSize 支持
     * 副单位是用于规避修改 {@link DisplayMetrics#density} 所造成的对于其他使用 dp 布局的系统控件或三方库控件的不良影响
     * 您选择什么单位, 就在 layout 文件中用什么单位布局
     * <p>
     * 两个主单位和一个副单位, 可以随时使用 {@link UnitsManager} 的方法关闭和重新开启对它们的支持
     * 如果您想完全规避修改 {@link DisplayMetrics#density} 所造成的对于其他使用 dp 布局的系统控件或三方库控件的不良影响
     * 那请调用 {@link UnitsManager#setSupportDP}、{@link UnitsManager#setSupportSP} 都设置为 {@code false}
     * 停止对两个主单位的支持 (如果开启 sp, 对其他三方库控件影响不大, 也可以不关闭对 sp 的支持)
     * 并调用 {@link UnitsManager#setSupportSubunits} 从三个冷门单位中选择一个作为副单位
     * 三个单位的效果都是一样的, 按自己的喜好选择, 比如我就喜欢 mm, 翻译为中文是妹妹的意思
     * 然后在 layout 文件中只使用这个副单位进行布局, 这样就可以完全规避修改 {@link DisplayMetrics#density} 所造成的不良影响
     * 因为 dp、sp 这两个单位在其他系统控件或三方库控件中都非常常见, 但三个冷门单位却非常少见
     */
    private void configUnits() {
        //AndroidAutoSize 默认开启对 dp 的支持, 调用 UnitsManager.setSupportDP(false); 可以关闭对 dp 的支持
        //主单位 dp 和 副单位可以同时开启的原因是, 对于旧项目中已经使用了 dp 进行布局的页面的兼容
        //让开发者的旧项目可以渐进式的从 dp 切换到副单位, 即新页面用副单位进行布局, 然后抽时间逐渐的将旧页面的布局单位从 dp 改为副单位
        //最后将 dp 全部改为副单位后, 再使用 UnitsManager.setSupportDP(false); 将 dp 的支持关闭, 彻底隔离修改 density 所造成的不良影响
        //如果项目完全使用副单位, 则可以直接以像素为单位填写 AndroidManifest 中需要填写的设计图尺寸, 不需再把像素转化为 dp
        AutoSizeConfig.getInstance().getUnitsManager()
                .setSupportDP(false)

                //当使用者想将旧项目从主单位过渡到副单位, 或从副单位过渡到主单位时
                //因为在使用主单位时, 建议在 AndroidManifest 中填写设计图的 dp 尺寸, 比如 360 * 640
                //而副单位有一个特性是可以直接在 AndroidManifest 中填写设计图的 px 尺寸, 比如 1080 * 1920
                //但在 AndroidManifest 中却只能填写一套设计图尺寸, 并且已经填写了主单位的设计图尺寸
                //所以当项目中同时存在副单位和主单位, 并且副单位的设计图尺寸与主单位的设计图尺寸不同时, 可以通过 UnitsManager#setDesignSize() 方法配置
                //如果副单位的设计图尺寸与主单位的设计图尺寸相同, 则不需要调用 UnitsManager#setDesignSize(), 框架会自动使用 AndroidManifest 中填写的设计图尺寸
//                .setDesignSize(2160, 3840)

                //AndroidAutoSize 默认开启对 sp 的支持, 调用 UnitsManager.setSupportSP(false); 可以关闭对 sp 的支持
                //如果关闭对 sp 的支持, 在布局时就应该使用副单位填写字体的尺寸
                //如果开启 sp, 对其他三方库控件影响不大, 也可以不关闭对 sp 的支持, 这里我就继续开启 sp, 请自行斟酌自己的项目是否需要关闭对 sp 的支持
//                .setSupportSP(false)

                //AndroidAutoSize 默认不支持副单位, 调用 UnitsManager#setSupportSubunits() 可选择一个自己心仪的副单位, 并开启对副单位的支持
                //只能在 pt、in、mm 这三个冷门单位中选择一个作为副单位, 三个单位的适配效果其实都是一样的, 您觉的哪个单位看起顺眼就用哪个
                //您选择什么单位就在 layout 文件中用什么单位进行布局, 我选择用 mm 为单位进行布局, 因为 mm 翻译为中文是妹妹的意思
                //如果大家生活中没有妹妹, 那我们就让项目中最不缺的就是妹妹!
                .setSupportSubunits(Subunits.MM);
    }



    /**销毁程序*/
    public void onDestroy() {
        stopCloudServer();
        EventBus.getDefault().unregister( this );
    }
}

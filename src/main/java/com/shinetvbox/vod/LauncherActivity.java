package com.shinetvbox.vod;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.shinetvbox.vod.db.DateBaseUpdate;
import com.shinetvbox.vod.manager.DatabaseManager;
import com.shinetvbox.vod.service.cloudserver.CloudDownloadService;
import com.shinetvbox.vod.utils.KtvLog;
import com.shinetvbox.vod.utils.KtvSystemApi;
import com.shinetvbox.vod.utils.PermissionUtil;
import com.shinetvbox.vod.utils.updateapp.HttpConstant;

import static com.shinetvbox.vod.MyApplication.APPFILEPATH;
import static com.shinetvbox.vod.MyApplication.CPU_ID;
import static com.shinetvbox.vod.MyApplication.MAC_ADDRESS;
import static com.shinetvbox.vod.MyApplication.SHINEDBDIR;
import static com.shinetvbox.vod.MyApplication.SHINESONGDDIR;
import static com.shinetvbox.vod.MyApplication.SHINEUPDBDIR;
import static com.shinetvbox.vod.MyApplication.SUBJECT;

public class LauncherActivity extends AppCompatActivity {
    public static AppCompatActivity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        // 避免从桌面启动程序后，会重新实例化入口类的activity
        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                    return;
                }
            }
        }
//        //首次启动 Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT 为 0，再次点击图标启动时就不为零了
//        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
//            finish();
//            return;
//        }

        setContentView( R.layout.activity_launcher );

        checkStarServertOver();
        mActivity=this;
        new Thread( new Runnable() {
            @Override
            public void run() {
                PermissionUtil.init( mActivity );
            }
        } ).start();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        PermissionUtil.setResult( requestCode, permissions, grantResults );
    }

    int countCheck = 0;
    private void checkStarServertOver(){
        new Thread( new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep( 1000 );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(DatabaseManager.mIDatabaseService!=null && MyApplication.isInitSingerImage){
                        try {
                            Thread.sleep(600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        MyApplication.getInstance().startCloudServer();
                        gotoMainActivity();
                        break;
                    }
                    if(countCheck>120){
                        break;
                    }
                    countCheck++;
                }
            }
        } ).start();
    }

    private void gotoMainActivity() {
        Intent intent = new Intent( this, MainActivity.class );
        this.startActivity( intent );
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

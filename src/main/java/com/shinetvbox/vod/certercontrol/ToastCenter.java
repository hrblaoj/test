package com.shinetvbox.vod.certercontrol;

import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shinetvbox.vod.MyApplication;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.manager.EventBusManager;
import com.shinetvbox.vod.utils.KtvLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.shinetvbox.vod.data.eventbus.EventBusConstants.WINDOWN_TOAST;

/**
 * Created by hrblaoj on 2019/1/17.
 */

public class ToastCenter {

    static ToastCenter inTance;
    Toast showToast;
    public ToastCenter(){
        inTance = this;
        EventBus.getDefault().register( this );
        showToast=Toast.makeText(MyApplication.getInstance(), "下载进度", Toast.LENGTH_SHORT);
        showToast.setGravity(Gravity.CENTER, 0, 0);
        //key parameter
        LinearLayout layout = (LinearLayout) showToast.getView();
        TextView tv = (TextView) layout.getChildAt(0);
        tv.setTextSize(25);
    }

    public static ToastCenter getInTance(){
        if(null == inTance){
            inTance = new ToastCenter();
        }

        return inTance;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusMessage msg) {
        switch (msg.what) {
            case WINDOWN_TOAST:

                showToast.setDuration(Toast.LENGTH_SHORT);
                showToast.setGravity(Gravity.CENTER, 0, 0);
                showToast.setText((CharSequence) msg.obj);
                KtvLog.d("showToast setText is " + msg.obj);
                showToast.show();

                break;
        }
    }

    public void sendToastEvent(String content){
        EventBusMessage msg = new EventBusMessage();

        msg.what = WINDOWN_TOAST;
        msg.obj = content;

        EventBusManager.sendMessage( msg );
    }
}

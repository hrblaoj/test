package com.shinetvbox.vod.view.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.shinetvbox.vod.R;
import com.shinetvbox.vod.certercontrol.ControlCenter;
import com.shinetvbox.vod.certercontrol.ControlCenterConstants;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.floatwindow.ShineVideoFloatWindow;
import com.shinetvbox.vod.manager.EventBusManager;
import com.shinetvbox.vod.status.PlayStatus;
import com.shinetvbox.vod.utils.KtvLog;
import com.shinetvbox.vod.utils.volume.VolumeConfig;

import static android.view.MotionEvent.ACTION_DOWN;
import static com.shinetvbox.vod.floatwindow.ShineVideoFloatWindow.SCREEN_DEFAULT;

@SuppressLint("AppCompatCustomView")
public class ButtonFocus extends RelativeLayout {

    public boolean focusIsZoomin = true;

    public ButtonFocus(Context context) {
        this( context,null );
    }

    public ButtonFocus(Context context, AttributeSet attrs) {
        this( context, attrs,0 );
    }

    public ButtonFocus(Context context, AttributeSet attrs, int defStyleAttr) {
        super( context, attrs, defStyleAttr );
        init( context,attrs,defStyleAttr );
    }
    public void init(Context context,AttributeSet attrs,int defStyleAttr){
        if(attrs == null) return;
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs
                , R.styleable.ButtonFocus
                , defStyleAttr, 0);
        focusIsZoomin = array.getBoolean(R.styleable.ButtonFocus_focusIsZoomin,true);
        array.recycle();
    }
}

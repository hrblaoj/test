package com.shinetvbox.vod.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textservice.TextInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.shinetvbox.vod.MyApplication;
import com.shinetvbox.vod.R;
import com.shinetvbox.vod.data.constants.Constants;
import com.shinetvbox.vod.utils.KtvLog;
import com.shinetvbox.vod.utils.SharedPreferencesUtil;
import com.shinetvbox.vod.utils.SystemUtil;
import com.shinetvbox.vod.view.custom.ButtonFocus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ResManager {

    private Context appContext = null;
    private Resources appResources = null;
    private Map<String, Integer> mIdCache = new HashMap<String,Integer>();
    private Map<View,TextInfo> mTextViewList = new HashMap<>(  );

    private static ResManager mInstance = null;
    public static ResManager getInstance(){
        if(mInstance==null){
            synchronized(ResManager.class){
                if(mInstance==null){
                    mInstance = new ResManager();
                }
            }
        }
        return mInstance;
    }
    public void init(){
        if(appResources!=null) return;
        appContext = MyApplication.getInstance().getBaseContext();
        appResources = MyApplication.getInstance().getResources();
        showDefaultLanguage();
    }
    public void register(View view) {
        if(view == null) return;
        setOnFocusChangeListener(view);
        if(view instanceof ViewGroup) {
            resetViewGroup((ViewGroup)view);
        } else {
            resetView(view);
        }
    }
    private void resetViewGroup(ViewGroup vG){
        resetView(vG);
        int count = vG.getChildCount();
        for(int i=0; i<count; i++) {
            View v = vG.getChildAt(i);
            if(v instanceof ViewGroup) {
                resetViewGroup((ViewGroup)v);
            } else {
                resetView(v);
            }
        }
    }
    private void resetView(View v){
//        if(v instanceof ButtonFocus){
//            v.setOnFocusChangeListener( focusChange );
//        }
//        if(v instanceof TextView)
//            根据tag获取资源id存到数组中，切换语言用 下同
//            if(v.getTag()!=null){
//                getStringId(v.getTag().toString());
//            }
//        }else if(v instanceof Button){
//            ((Button) v).setTextSize( getNewTextSize(v) );
//        }else if(v instanceof RadioButton){
//            ((RadioButton) v).setTextSize( getNewTextSize(v) );
//        }
//        Log.i("22222222222","==========="+v.getLayoutParams());

        if(v instanceof TextView || v instanceof Button || v instanceof RadioButton){
            if(v.getTag()!=null){
                int resId = getStringId( v.getTag().toString() );
                int hintId = getStringId( v.getTag().toString()+"_hint" );
                if(resId>0){
                    cacheText( v,resId,null);
                }
            }
        }
    }

    public void setOnFocusChangeListener(View v) {
        if(v == null && !(v instanceof ViewGroup)) return;
        addFocusChangelistener( (ViewGroup) v );
    }
    private void addFocusChangelistener(ViewGroup vG){
        if(vG instanceof ButtonFocus){
            vG.setOnFocusChangeListener( focusChange );
            return;
        }
        int count = vG.getChildCount();
        for(int i=0; i<count; i++) {
            View v = vG.getChildAt(i);
            if(v instanceof ViewGroup) {
                addFocusChangelistener((ViewGroup)v);
                if(v instanceof ButtonFocus){
                    v.setOnFocusChangeListener( focusChange );
                }
            }
        }
    }
    private View.OnFocusChangeListener focusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, final boolean hasFocus) {
            if(!(v instanceof ButtonFocus)) return;
            final ButtonFocus bf = (ButtonFocus) v;
//            KtvLog.d("focusChangefocusChange tag is " + v.getTag());
            if(hasFocus){
                if(bf.focusIsZoomin){

                    bf.animate()
                            .scaleX( Constants.FOCUS_XSCALE )
                            .scaleY( Constants.FOCUS_YSCALE )
                            .setDuration(Constants.FOCUS_DURATION)
                            .start();
                }
                ImageView iv = bf.findViewById( getId("image_focus") );
                if(iv!=null){
                    iv.setVisibility( View.VISIBLE );
                }
                bf.bringToFront();

            }else{
                bf.animate()
                        .scaleX( (float) 1 )
                        .scaleY( (float) 1 )
                        .setDuration(Constants.FOCUS_DURATION)
                        .start();

                ImageView iv = bf.findViewById( getId("image_focus") );
                if(iv!=null){
                    iv.setVisibility( View.GONE );
                }

            }
        }
    };
    public int getId(String name){
        if(this.mIdCache.containsKey(name)) {
            return mIdCache.get(name);
        } else {
            int ret = appResources.getIdentifier(name, "id", SystemUtil.packageName );
            if(ret != 0) {
                mIdCache.put(name, ret);
            }
            return ret;
        }
    }
    public int getStringId(String name){
        int sid = appResources.getIdentifier(name, "string", SystemUtil.packageName);
        return sid;
    }

    public String getStringById(int sid){
        return appResources.getString(sid);
    }

    public int getColorById(int cid){
        return appResources.getColor(cid);
    }

    public View getViewNoCache(int vid){
        if(vid == 0) {
            return null;
        } else {
            return LayoutInflater.from(appContext).inflate(vid, null);
        }
    }

    public void setText(View v, int resId) {
        if(resId<=0) return;
        if(v instanceof TextView || v instanceof Button || v instanceof RadioButton){
            ((TextView) v).setText( resId );
            cacheText( v,resId,null);
        }
    }
    public void setTextReplaceXXX(View v, int resId, String replaceStr) {
        if(resId<=0) return;
        if(v instanceof TextView || v instanceof Button || v instanceof RadioButton){
            ((TextView) v).setText( getStringById(resId).replace( "xxx",replaceStr ) );
            cacheText( v,resId,replaceStr);
        }
    }
    public void setTextReplaceXXX(View v, int resId, int replaceResId) {
        if(resId<=0) return;
        if(v instanceof TextView || v instanceof Button || v instanceof RadioButton){
            ((TextView) v).setText( getStringById(resId).replace( "xxx",getStringById(replaceResId) ) );
            cacheText( v,resId,replaceResId);
        }
    }
    private void cacheText(View v, int resId,String replaceStr){
        if(resId<=0) return;
        if(mTextViewList.containsKey( v )) {
            mTextViewList.remove( v );
        }
        TextInfo tf = new TextInfo();
        tf.resId = resId;
        tf.replaceStr = replaceStr;
        mTextViewList.put( v,tf );
    }
    private void cacheText(View v, int resId,int replaceResId){
        if(resId<=0) return;
        if(mTextViewList.containsKey( v )) {
            mTextViewList.remove( v );
        }
        TextInfo tf = new TextInfo();
        tf.resId = resId;
        tf.replaceResId = replaceResId;
        mTextViewList.put( v,tf );
    }

    public class TextInfo{
        public int resId = 0;
        public int replaceResId = 0;
        public String replaceStr = null;
        public int btnHintId = 0;
    }
    /**
     * 显示用默认语言
     */
    public void showDefaultLanguage(){
        LanguageManager.initAllLocale();
        String localeKey = SharedPreferencesUtil.getLangage_key();
        LanguageManager.currentLang = localeKey;
        languageSwitch(localeKey);
    }

    public void languageSwitch(String localeKey){
        //////////////////////////////////语言切换////////////////////////////////////////////
//                    ResManager.getInstance().languageSwitch( LanguageManager.MY_MM );
        Locale locale = LanguageManager.getLocale(localeKey);
        //更新主程序的语言
        Configuration config = appResources.getConfiguration();
        if(config.locale.equals(locale)) {
            return;
        }
        config.locale = locale;
        appResources.updateConfiguration(config, appResources.getDisplayMetrics());
        mIdCache.clear();
        //保存语言key
        SharedPreferencesUtil.setLangage_key(localeKey);

        Iterator<Map.Entry<View, TextInfo>> iter = mTextViewList.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<View, TextInfo> entry = iter.next();
            View view = entry.getKey();
            TextInfo item = entry.getValue();
//            public int resId = 0;
//            public int replaceResId = 0;
//            public String replaceStr = null;
            if(item.resId>0){
                if(item.replaceResId>0){
                    ((TextView) view).setText( getStringById(item.resId).replace( "xxx",getStringById(item.replaceResId) ) );
                }else if(item.replaceStr!= null && !item.replaceStr.equals( "" )){
                    ((TextView) view).setText( getStringById(item.resId).replace( "xxx",item.replaceStr ) );
                }else{
                    ((TextView) view).setText( item.resId );
                }
            }
        }
    }
}

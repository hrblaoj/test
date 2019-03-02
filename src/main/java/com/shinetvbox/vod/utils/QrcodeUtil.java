package com.shinetvbox.vod.utils;

import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shinetvbox.vod.R;
import com.shinetvbox.vod.data.constants.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class QrcodeUtil {
    private static CopyOnWriteArrayList<ImageView> listWechatQrcode = new CopyOnWriteArrayList<>(  );
    public static void loadWechartQrcode(ImageView iv){
        if(Constants.WECHAT_ARCODE_PATH.equals( "" )){
            listWechatQrcode.add( iv );
        }else{
            loadQrcode(iv,Constants.WECHAT_ARCODE_PATH);
        }
    }
    public synchronized static void startLoadWechatQrcode(){
        for(ImageView iv:listWechatQrcode){
            loadQrcode(iv,Constants.WECHAT_ARCODE_PATH);
            listWechatQrcode.remove( iv );
        }
    }
    private static void loadQrcode(ImageView iv,String path){
        Glide.with(iv.getContext())
                .load(path)
                .apply(new RequestOptions().centerCrop().diskCacheStrategy( DiskCacheStrategy.NONE ).
                        placeholder( R.drawable.app_sunshine_wechat_qrcode ))
                .into(iv);
    }
    public static void createQrcode(ImageView imageView,String qrcodeString,int border){
        if(!qrcodeString.equals( "" )){
            ZXingUtils.setQrcodeToImageView(imageView, qrcodeString,6);
        }
    }
}

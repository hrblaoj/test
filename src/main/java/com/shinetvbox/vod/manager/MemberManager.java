package com.shinetvbox.vod.manager;

import com.shinetvbox.vod.R;
import com.shinetvbox.vod.data.custom.JsonData;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.service.wechat.WechatService;
import com.shinetvbox.vod.utils.DateUtil;
import com.shinetvbox.vod.utils.updateapp.HttpConstant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class MemberManager {
    //获取服务器时间时记录本地时间点 单位毫秒
    public static long localTime = 0;
    //获取服务器时间 单位秒
    public static long serverTime = 0;
    public static boolean isMember = false;
    public static int songPlayNumber = 5;
    public static int songDownloadNumber = 5;
    public static long memberExpirationTime = 0;
    public static String memberStringRemainingDay = "0";
    public static String memberStringExpirationDate = "";

    public static List<ComboBean> listPayCombo = new ArrayList<>(  );

    public static void init(){
        getServerTime();
    }

    public static void refresh() {
        init();
    }

    private static void getServerTime() {
        OkHttpUtils.get()
                .url( HttpConstant.urlGetServerTime )
                .tag( HttpConstant.urlGetServerTime )
                .id(MyStringCallback.TYPE_SERVER_TIMER)
                .build()
                .connTimeOut( 10000 )
                .readTimeOut( 10000 )
                .execute( new MyStringCallback() );
    }
    private static void getMemberInfo() {
        OkHttpUtils.post()
                .url( HttpConstant.urlGetAppMemeberInfo )
                .tag( HttpConstant.urlGetAppMemeberInfo )
                .id(MyStringCallback.TYPE_MEMBER_INFO)
                .params( HttpConstant.getAppMemberInfoPostParams() )
                .build()
                .connTimeOut( 10000 )
                .readTimeOut( 10000 )
                .execute( new MyStringCallback() );
    }

    private static void getPayComboInfo(){
        OkHttpUtils.post()
                .url( HttpConstant.urlGetAppPayList )
                .tag( HttpConstant.urlGetAppPayList )
                .id(MyStringCallback.TYPE_PAY_LIST)
                .params( HttpConstant.getAppMemberInfoPostParams() )
                .build()
                .connTimeOut( 10000 )
                .readTimeOut( 10000 )
                .execute( new MyStringCallback() );
    }

    private static class MyStringCallback extends StringCallback {

        public static final int TYPE_SERVER_TIMER = 0;
        public static final int TYPE_MEMBER_INFO = 1;
        public static final int TYPE_PAY_LIST = 2;

        public MyStringCallback() {
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            //e.printStackTrace();
        }
        @Override
        public void onResponse(String response, int id) {
            if(id == TYPE_SERVER_TIMER){
                analysisJsonServerTime(response);
            }else if (id == TYPE_MEMBER_INFO) {
                analysisJsonMemberInfo(response);
            }else if (id == TYPE_PAY_LIST) {
                analysisJsonPayList(response);
            }
//            Log.i( "2222222222222",id+"====="+response );
        }
    }

    private static void analysisJsonServerTime(String jsonString) {
        JsonData jsonData = new JsonData( jsonString );
        if(jsonData.getValueString( "code" ).equals( "0" )){
            localTime = System.currentTimeMillis();
            serverTime = jsonData.getValueLong( "result" );
            getMemberInfo();
        }
    }
    private static void analysisJsonMemberInfo(String jsonString) {
        JsonData jsonData = new JsonData( jsonString );
        if(jsonData.getValueString( "code" ).equals( "0" )){
            getPayComboInfo();
            memberExpirationTime = jsonData.getSection( "result" ).getValueLong( "expire" );
            songPlayNumber = jsonData.getSection( "result" ).getValueInt( "playcount" );
            songDownloadNumber = jsonData.getSection( "result" ).getValueInt( "downcount" );
            memberStringExpirationDate = DateUtil.getAppointTime( memberExpirationTime*1000 );
            isMember = memberExpirationTime>0;
            if(isMember){
                long remainingTime = memberExpirationTime - serverTime;
                int day;
                if(remainingTime%86400 != 0){
                    day = (int) (remainingTime/86400)+1;
                }else{
                    day = (int) (remainingTime/86400);
                }
                memberStringRemainingDay = day+"";
                if(WechatService.getInstance()!=null){
                    WechatService.getInstance().initWebAddress();
                }
            }
        }
    }
    private static void analysisJsonPayList(String jsonString){
        //{"code":"0","type":"success","description":"请求成功","result":[{"id":1,"levname":"非会员",
        // "unit":0,"recharge":0,"price":0.00,"disprice":0.00,"downcount":2,"playcount":5},
        // {"id":2,"levname":"VIP-青铜","unit":0,"recharge":1,"price":2.00,"disprice":0.01,"downcount":50,"playcount":-1}
        JsonData jsonData = new JsonData( jsonString );
        if(jsonData.getValueString( "code" ).equals( "0" )){
            listPayCombo.clear();
            Object[] objs = jsonData.getValueArray( "result" );
            for(int i=0;i<objs.length;i++){
                if(i==0) continue;
                JsonData jd = new JsonData( objs[i].toString() );
                ComboBean cob = new ComboBean();
                cob.id = jd.getValueString( "id" );
                cob.levname = jd.getValueString( "levname" );

                String unit = jd.getValueString( "unit" );
                //0：天，1：月，2：年
                if(unit.equals( "0" )){
                    cob.titleStrId = R.string.pay_combo_vip0;
                    cob.playcountStrId = R.string.btn_pay_combo_play_number0;
                }else if(unit.equals( "1" )){
                    cob.titleStrId = R.string.pay_combo_vip1;
                    cob.playcountStrId = R.string.btn_pay_combo_play_number1;
                }else if(unit.equals( "2" )){
                    cob.titleStrId = R.string.pay_combo_vip2;
                    cob.playcountStrId = R.string.btn_pay_combo_play_number2;;
                }
                cob.recharge = jd.getValueInt( "recharge" );
                cob.price = jd.getValueFloat( "price" );
                cob.priceStrId = R.string.btn_pay_combo_price;
                cob.disprice = jd.getValueFloat( "disprice" );
                if(cob.price > 0 && cob.disprice > 0){
                    cob.discount = (float) ((int)(cob.disprice/cob.price*100))/100;
                    cob.discountStrId = R.string.btn_pay_combo_discount;
                }

                cob.playcount = jd.getValueInt( "playcount" );
                cob.downcount = jd.getValueInt( "downcount" );
                cob.downcountStrId = R.string.btn_pay_combo_download_number;
                listPayCombo.add(cob);
            }

            EventBusMessage msg = new EventBusMessage();
            msg.what = EventBusConstants.MEMBER_INFO_REFRESH;
            EventBusManager.sendMessage( msg );
        }
    }
    public static class ComboBean{
        //{"id":3,"levname":"VIP-白银","unit":0,"recharge":7,"price":14,"disprice":0.01,"downcount":50,"playcount":-1}
        public String id = "";
        public String levname = "";
        public int titleStrId = 0;
        public int recharge = 0;
        public float price = 0;
        public int priceStrId = 0;
        public float disprice = 0;
        public float discount = 0;
        public int discountStrId = 0;
        public int playcountStrId = 0;
        public int playcount = 0;
        public int downcountStrId = 0;
        public int downcount = 0;
    }
}

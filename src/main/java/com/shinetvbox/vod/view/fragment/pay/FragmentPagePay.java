package com.shinetvbox.vod.view.fragment.pay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shinetvbox.vod.R;
import com.shinetvbox.vod.data.constants.Constants;
import com.shinetvbox.vod.data.custom.JsonData;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.data.params.FragmentParams;
import com.shinetvbox.vod.manager.EventBusManager;
import com.shinetvbox.vod.manager.MemberManager;
import com.shinetvbox.vod.manager.MyViewManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.utils.QrcodeUtil;
import com.shinetvbox.vod.utils.updateapp.HttpConstant;
import com.shinetvbox.vod.view.custom.MyFragment;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class FragmentPagePay extends MyFragment {

    private Context mContext = null;
    private View mView = null;

    private FragmentParams fragmentParams;

    private View btnFristCombo;
    private Button btnDuihuanma;
    private Button btnTuichu;

    private TextView textSelectComboContent;
    private TextView textSelectComboPrice;
    private ImageView imageSelectComboQrcode;
    private TextView textSelectComboQrcodeError;
    private TextView textFreeMemberHint;

    private String orderCode = "";

    private boolean listIsInit = false;

    private List<ComboHolder> listComboList = new ArrayList<>(  );

    public FragmentPagePay() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach( context );
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate( R.layout.fragment_page_pay, container, false );
        initView();
        return mView;
    }

    private void initView() {
        if(mView == null) return;
        ResManager.getInstance().register( mView );
        EventBus.getDefault().register( this );
        for(int i=0;i<6;i++){
            ComboHolder holder = new ComboHolder();
            holder.btn = mView.findViewById( ResManager.getInstance().getId( "item_layout_pay_combo"+i ) );
            holder.btn.setVisibility( View.GONE );
            holder.textTitle = holder.btn.findViewById( R.id.btn_pay_combo_title );
            holder.textDisprice = holder.btn.findViewById( R.id.btn_pay_combo_disprice );
            holder.textPrice = holder.btn.findViewById( R.id.btn_pay_combo_price );
            holder.imageX = holder.btn.findViewById( R.id.image_pay_combo_x );
            holder.textDiscount = holder.btn.findViewById( R.id.btn_pay_combo_zhekou );
            holder.textDownload = holder.btn.findViewById( R.id.btn_pay_combo_download_number );
            holder.textPlay = holder.btn.findViewById( R.id.btn_pay_combo_sung_number );

            holder.btn.setTag( "item_layout_pay_combo"+i );
            if(i==0){
                btnFristCombo = holder.btn;
            }
            holder.btn.setOnClickListener( onClick );
            holder.btn.setOnFocusChangeListener( onFocusChange );
            listComboList.add( holder );
        }
        btnDuihuanma = mView.findViewById( R.id.pay_btn_combo_duihuanma );
        btnDuihuanma.setOnClickListener( onClick );
        btnTuichu = mView.findViewById( R.id.pay_btn_combo_tuichu );
        btnTuichu.setOnClickListener( onClick );

        textSelectComboContent = mView.findViewById( R.id.pay_text_select_combo_content );
        textSelectComboPrice = mView.findViewById( R.id.pay_text_select_combo_price );
        imageSelectComboQrcode = mView.findViewById( R.id.pay_image_select_combo_qrcode );
        textSelectComboQrcodeError = mView.findViewById( R.id.pay_text_select_combo_hint_error );
        textSelectComboQrcodeError.setVisibility( View.GONE );
        textFreeMemberHint = mView.findViewById( R.id.pay_text_hint_freemember );
    }

    private View.OnFocusChangeListener onFocusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                v.animate()
                        .scaleX( Constants.FOCUS_XSCALE )
                        .scaleY( Constants.FOCUS_YSCALE )
                        .setDuration(Constants.FOCUS_DURATION)
                        .start();
                ImageView iv = v.findViewById( ResManager.getInstance().getId("image_focus") );
                if(iv!=null){
                    iv.setVisibility( View.VISIBLE );
                }
                v.bringToFront();
                setSelectComboFocus(v);
            }else{
                v.animate()
                        .scaleX( (float) 1 )
                        .scaleY( (float) 1 )
                        .setDuration(Constants.FOCUS_DURATION)
                        .start();
                ImageView iv = v.findViewById( ResManager.getInstance().getId("image_focus") );
                if(iv!=null){
                    iv.setVisibility( View.GONE );
                }
            }
        }
    };

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getTag() == null) return;
            FragmentParams param = new FragmentParams();
            switch(v.getTag().toString()) {
//                case "item_layout_pay_combo0":
//                    setSelectComboFocus(v);
//                    break;
//                case "item_layout_pay_combo1":
//                    setSelectComboFocus(v);
//                    break;
//                case "item_layout_pay_combo2":
//                    setSelectComboFocus(v);
//                    break;
//                case "item_layout_pay_combo3":
//                    setSelectComboFocus(v);
//                    break;
//                case "item_layout_pay_combo4":
//                    setSelectComboFocus(v);
//                    break;
//                case "item_layout_pay_combo5":
//                    setSelectComboFocus(v);
//                    break;
                case "pay_btn_combo_duihuanma":
                    param.isShowBtnSelectedSong = false;
                    sendEventBusMessage(EventBusConstants.PAGE_GOTO_PAY_CDKEY,param);
                    break;
                case "pay_btn_combo_tuichu":
                    sendEventBusMessage(EventBusConstants.PAGE_BACK,param);
                    break;
            }
        }
    };

    private void setSelectComboFocus(View v){
        if(fragmentParams!=null){
            fragmentParams.viewFocus = v;
        }
        for(ComboHolder holder:listComboList){
            if(holder.btn.equals( v )){
                textSelectComboContent.setText( holder.textTitle.getText().toString() );
                textSelectComboPrice.setText( ResManager.getInstance().
                        getStringById( R.string.pay_text_select_combo_price ).
                        replace( "xxx",holder.textDisprice.getText().toString() ) );
                textSelectComboQrcodeError.setVisibility( View.GONE );
                imageSelectComboQrcode.setVisibility( View.GONE );
                orderCode = "";
                countRequestResult = 0;
                requestQrcodeInfo(holder.id);
            }
        }
    }

    private CountDownTimer requestQrcodeCD;
    private void requestQrcodeInfo(final String level){
        if(requestQrcodeCD!=null){
            requestQrcodeCD.cancel();
            requestQrcodeCD = null;
        }
        requestQrcodeCD = new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {
                getPayQrcode(level);
            }
        }.start();
    }

    private void getPayQrcode(String level) {
        OkHttpUtils.getInstance().cancelTag( HttpConstant.urlGetAppPayQrcodeInfo );
        OkHttpUtils.post()
                .url( HttpConstant.urlGetAppPayQrcodeInfo )
                .tag( HttpConstant.urlGetAppPayQrcodeInfo )
                .id( MyStringCallback.TYPE_QRCODE_INFO )
                .params( HttpConstant.getAppPayQrcodePostParams(level) )
                .build()
                .connTimeOut( 10000 )
                .readTimeOut( 10000 )
                .execute( new MyStringCallback() );
    }

    private CountDownTimer requestResultCD;
    private int countRequestResult = 0;
    private void requestResultInfo(){
        if(requestResultCD!=null){
            requestResultCD.cancel();
            requestResultCD = null;
        }
        requestResultCD = new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {
                getPayResult();
            }
        }.start();
    }
    private void getPayResult() {
        if(orderCode.equals( "" )) return;
        OkHttpUtils.getInstance().cancelTag( HttpConstant.urlGetAppPayResultInfo );
        OkHttpUtils.post()
                .url( HttpConstant.urlGetAppPayResultInfo )
                .tag( HttpConstant.urlGetAppPayResultInfo )
                .id( MyStringCallback.TYPE_PAY_RESULT )
                .params( HttpConstant.getAppPayResultPostParams(orderCode) )
                .build()
                .connTimeOut( 10000 )
                .readTimeOut( 10000 )
                .execute( new MyStringCallback() );
    }

    private class MyStringCallback extends StringCallback {
        public final static int TYPE_QRCODE_INFO = 0;
        public final static int TYPE_PAY_RESULT = 1;

        public MyStringCallback() {
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            //e.printStackTrace();
        }
        @Override
        public void onResponse(String response, int id) {
            if(id == TYPE_QRCODE_INFO){
                analysisJsonQrcodeInfo(response);
            }else if(id == TYPE_PAY_RESULT){
                analysisJsonPayResult(response);
            }
        }
    }
    private void analysisJsonQrcodeInfo(String jsonString) {
        JsonData jsonData = new JsonData( jsonString );
        if(jsonData.getValueString( "code" ).equals( "0" )){
            orderCode = jsonData.getSection( "result" ).getValueString( "ordercode" );
            imageSelectComboQrcode.setVisibility( View.VISIBLE );
            QrcodeUtil.createQrcode( imageSelectComboQrcode,jsonData.getSection( "result" ).getValueString( "payurl" ),10 );
            countRequestResult++;
            if(countRequestResult<60){
                requestResultInfo();
            }
        }else{
            textSelectComboQrcodeError.setVisibility( View.VISIBLE );
        }
    }
    private void analysisJsonPayResult(String jsonString) {
        JsonData jsonData = new JsonData( jsonString );
        String code = jsonData.getValueString( "code" );
//        Log.i("222222222222","========="+jsonString);
        if(code.equals( "0" )){
            MemberManager.refresh();
        }else if(code.equals( "1" )){
            requestResultInfo();
        }
    }

    private void closeRequestQrcode(){
        if(requestQrcodeCD!=null){
            requestQrcodeCD.cancel();
            requestQrcodeCD = null;
        }
        OkHttpUtils.getInstance().cancelTag( HttpConstant.urlGetAppPayQrcodeInfo );
    }
    private void closeReauestPayResult(){
        if(requestResultCD!=null){
            requestResultCD.cancel();
            requestResultCD = null;
        }
        OkHttpUtils.getInstance().cancelTag( HttpConstant.urlGetAppPayResultInfo );
    }
    private void sendEventBusMessage(int what,FragmentParams params){
        EventBusMessage msg = new EventBusMessage();
        msg.what = what;
        params.pageIndex = what;
        msg.obj = params;
        EventBusManager.sendMessage( msg );
    }

    @SuppressLint("SetTextI18n")
    private void refreshPlayList(){
        String strhint = "";
        if(MemberManager.isMember){
            strhint = ResManager.getInstance().getStringById( R.string.pay_text_hint_viptime );
            strhint = strhint.replace( "xxx",MemberManager.memberStringExpirationDate );
            strhint = strhint.replace( "yyy",MemberManager.memberStringRemainingDay );
        }else{
            strhint = ResManager.getInstance().getStringById( R.string.pay_text_hint_freemember );
            strhint = strhint.replace( "xxx",MemberManager.songDownloadNumber+"" );
            strhint = strhint.replace( "yyy",MemberManager.songPlayNumber+"" );
        }

        textFreeMemberHint.setText( strhint );

        int len = MemberManager.listPayCombo.size();
        for(int i=0;i<listComboList.size();i++){
            if(i<len){
                listComboList.get( i ).id = MemberManager.listPayCombo.get( i ).id;
                listComboList.get( i ).btn.setVisibility( View.VISIBLE );
                ResManager.getInstance().setTextReplaceXXX( listComboList.get( i ).textTitle,
                        MemberManager.listPayCombo.get( i ).titleStrId,MemberManager.listPayCombo.get( i ).recharge+"");
                listComboList.get( i ).textDisprice.setText( MemberManager.listPayCombo.get( i ).disprice+"" );
                ResManager.getInstance().setTextReplaceXXX( listComboList.get( i ).textPrice,
                        MemberManager.listPayCombo.get( i ).priceStrId,MemberManager.listPayCombo.get( i ).price+"");
                ResManager.getInstance().setTextReplaceXXX( listComboList.get( i ).textDiscount,
                        MemberManager.listPayCombo.get( i ).discountStrId,MemberManager.listPayCombo.get( i ).discount+"");
                ResManager.getInstance().setTextReplaceXXX( listComboList.get( i ).textDownload,
                        MemberManager.listPayCombo.get( i ).downcountStrId,MemberManager.listPayCombo.get( i ).downcount+"");
                ResManager.getInstance().setTextReplaceXXX( listComboList.get( i ).textPlay,
                        MemberManager.listPayCombo.get( i ).playcountStrId,MemberManager.listPayCombo.get( i ).recharge+"");
                RelativeLayout.LayoutParams tplp = (RelativeLayout.LayoutParams) listComboList.get( i ).textPrice.getLayoutParams();
                Paint paint = new Paint(  );
                paint.setTextSize( listComboList.get( i ).textPrice.getTextSize() );
                int wid = (int) paint.measureText(listComboList.get( i ).textPrice.getText().toString());
                RelativeLayout.LayoutParams imgXlp = (RelativeLayout.LayoutParams) listComboList.get( i ).imageX.getLayoutParams();
                imgXlp.leftMargin = tplp.leftMargin+(tplp.width-wid);
                imgXlp.width = wid;
                listComboList.get( i ).imageX.setLayoutParams( imgXlp );
            }else{
                listComboList.get( i ).btn.setVisibility( View.GONE );
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusMessage msg) {
        switch (msg.what) {
            case EventBusConstants.MEMBER_INFO_REFRESH:
                listIsInit = true;
                refreshPlayList();
                break;
        }
    }

    @Override
    public void setFragmentParams(FragmentParams param) {
        if(mView==null || param == null) return;
        if(param != null){
            fragmentParams = param;
        }else{
            fragmentParams = new FragmentParams();
        }

        if(fragmentParams.viewFocus == null){
            fragmentParams.viewFocus = btnFristCombo;
        }

        if(!listIsInit){
            refreshPlayList();
        }

        if(fragmentParams.viewFocus == null && listComboList.size()>0){
            fragmentParams.viewFocus = listComboList.get( 0 ).btn;
        }
        if(fragmentParams.viewFocus != null){
            MyViewManager.getInstance().requestFocus( fragmentParams.viewFocus );
        }
    }

    @Override
    public FragmentParams getFragmentParams() {
        return fragmentParams;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(hidden){
            closeRequestQrcode();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeRequestQrcode();
        closeReauestPayResult();
        EventBus.getDefault().unregister( this );
    }

    @Override
    public boolean isBaseOnWidth() {
        return true;
    }

    @Override
    public float getSizeInDp() {
        return 0;
    }

    private class ComboHolder{
        public String id = "";
        public View btn;
        public TextView textTitle;
        public TextView textPrice;
        public ImageView imageX;
        public TextView textDisprice;
        public TextView textDiscount;
        public TextView textDownload;
        public TextView textPlay;
    }
}

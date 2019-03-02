package com.shinetvbox.vod.view.fragment.pay;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.shinetvbox.vod.R;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.data.params.FragmentParams;
import com.shinetvbox.vod.manager.EventBusManager;
import com.shinetvbox.vod.manager.MyViewManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.view.custom.ButtonFocus;
import com.shinetvbox.vod.view.custom.MyFragment;

public class FragmentPagePayCdkey extends MyFragment {

    private Context mContext = null;
    private View mView = null;

    private FragmentParams fragmentParams;

    private ButtonFocus btnFristCombo;
    private Button btnDuihuanma;
    private Button btnTuichu;

    private TextView textSelectComboContent;
    private TextView textSelectComboPrice;
    private ImageView imageSelectComboQrcode;
    private TextView textFreeMemberHint;

    public FragmentPagePayCdkey() {
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
        mView = inflater.inflate( R.layout.fragment_page_pay_cdkey, container, false );
        initView();
        return mView;
    }

    private void initView() {
        if(mView == null) return;
        ResManager.getInstance().register( mView );
//        for(int i=0;i<6;i++){
//            ButtonFocus bf = mView.findViewById( ResManager.getInstance().getId( "item_layout_pay_combo"+i ) );
//            bf.setTag( "item_layout_pay_combo"+i );
//            bf.setOnClickListener( onClick );
//            if(i==0){
//                btnFristCombo = bf;
//            }
//        }
//        btnDuihuanma = mView.findViewById( R.id.pay_btn_combo_duihuanma );
//        btnDuihuanma.setOnClickListener( onClick );
//        btnTuichu = mView.findViewById( R.id.pay_btn_combo_tuichu );
//        btnTuichu.setOnClickListener( onClick );
//
//        textSelectComboContent = mView.findViewById( R.id.pay_text_select_combo_content );
//        textSelectComboPrice = mView.findViewById( R.id.pay_text_select_combo_price );
//        imageSelectComboQrcode = mView.findViewById( R.id.pay_image_select_combo_qrcode );
//        textFreeMemberHint = mView.findViewById( R.id.pay_text_hint_freemember );
    }

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
//                case "pay_btn_combo_duihuanma":
//                    break;
//                case "pay_btn_combo_tuichu":
//                    sendEventBusMessage(EventBusConstants.PAGE_BACK,param);
//                    break;
            }
        }
    };

    private void setSelectComboFocus(View v){
        if(fragmentParams!=null){
            fragmentParams.viewFocus = v;
        }
    }
    private void sendEventBusMessage(int what,FragmentParams params){
        EventBusMessage msg = new EventBusMessage();
        msg.what = what;
        params.pageIndex = what;
        msg.obj = params;
        EventBusManager.sendMessage( msg );
    }

    @Override
    public void setFragmentParams(FragmentParams param) {
        if(mView==null || param == null) return;
        if(param != null){
            fragmentParams = param;
        }else{
            fragmentParams = new FragmentParams();
        }

//        if(fragmentParams.viewFocus == null){
//            fragmentParams.viewFocus = btnFristCombo;
//        }
//        MyViewManager.getInstance().requestFocus( fragmentParams.viewFocus );
    }

    @Override
    public FragmentParams getFragmentParams() {
        return fragmentParams;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean isBaseOnWidth() {
        return true;
    }

    @Override
    public float getSizeInDp() {
        return 0;
    }
}

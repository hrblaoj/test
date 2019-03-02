package com.shinetvbox.vod.view.fragment.gexingdiange;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shinetvbox.vod.R;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.data.params.FragmentParams;
import com.shinetvbox.vod.db.SingerQuery;
import com.shinetvbox.vod.manager.EventBusManager;
import com.shinetvbox.vod.manager.LanguageManager;
import com.shinetvbox.vod.manager.MyViewManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.view.custom.MyFragment;

public class FragmentPageGexingdiange extends MyFragment {

    private Context mContext = null;
    private View mView = null;

    private FragmentParams fragmentParams;

    private View quanbugexing;
    private View huanrennanxing;
    private View huarennvxing;
    private View huarenzuhe;
    private View hanrigexing;
    private View omeigexing;
    private View waiguozuhe;

    public FragmentPageGexingdiange() {
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
        mView = inflater.inflate( R.layout.fragment_page_gexingdiange, container, false );
        initView();
        return mView;
    }

    private void initView() {
        if(mView == null) return;
        ResManager.getInstance().register( mView );
        quanbugexing = mView.findViewById( R.id.btn_gexingdiange_quanbugexing );
        quanbugexing.setOnClickListener( onClick );
        huanrennanxing = mView.findViewById( R.id.btn_gexingdiange_huarennanxing );
        huanrennanxing.setOnClickListener( onClick );
        huarennvxing = mView.findViewById( R.id.btn_gexingdiange_huarennvxing );
        huarennvxing.setOnClickListener( onClick );
        huarenzuhe = mView.findViewById( R.id.btn_gexingdiange_huarenzuhe );
        huarenzuhe.setOnClickListener( onClick );
        hanrigexing = mView.findViewById( R.id.btn_gexingdiange_hanrigexing );
        hanrigexing.setOnClickListener( onClick );
        omeigexing = mView.findViewById( R.id.btn_gexingdiange_omeigexing );
        omeigexing.setOnClickListener( onClick );
        waiguozuhe = mView.findViewById( R.id.btn_gexingdiange_waiguozuhe );
        waiguozuhe.setOnClickListener( onClick );
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getTag() == null) return;
            fragmentParams.viewFocus = v;
            FragmentParams param = new FragmentParams();
            param.keyboard.isShow = true;
            param.keyboard.hintId = R.string.gexingdiange_text_keyboard_hint;
            SingerQuery mQuery = new SingerQuery();
            param.singerListInfo.singerQuery = mQuery;
            switch(v.getTag().toString()) {
                case "btn_gexingdiange_quanbugexing":
                    param.keyboard.titleId = R.string.gexingdiange_type_quanbugexing;
                    break;
                case "btn_gexingdiange_huarennanxing":
                    param.keyboard.titleId = R.string.gexingdiange_type_huarennanxing;
                    mQuery.singer_region_new = "1";
                    break;
                case "btn_gexingdiange_huarennvxing":
                    param.keyboard.titleId = R.string.gexingdiange_type_huarennvxing;
                    mQuery.singer_region_new = "2";
                    break;
                case "btn_gexingdiange_huarenzuhe":
                    param.keyboard.titleId = R.string.gexingdiange_type_huarenzuhe;
                    mQuery.singer_region_new = "3";
                    break;
                case "btn_gexingdiange_hanrigexing":
                    param.keyboard.titleId = R.string.gexingdiange_type_hanrigexing;
                    mQuery.singer_region_new = "4";
                    break;
                case "btn_gexingdiange_omeigexing":
                    param.keyboard.titleId = R.string.gexingdiange_type_omeigexing;
                    mQuery.singer_region_new = "5";
                    break;
                case "btn_gexingdiange_waiguozuhe":
                    param.keyboard.titleId = R.string.gexingdiange_type_waiguozuhe;
                    mQuery.singer_region_new = "6";
                    break;
                    //默认返回
                default:
                    return;
            }
            sendEventBusMessage(EventBusConstants.PAGE_GOTO_GEXINGDIANGE_LIST,param);
        }
    };
    private void sendEventBusMessage(int what,FragmentParams params){
        EventBusMessage msg = new EventBusMessage();
        msg.what = what;
        params.pageIndex = what;
        msg.obj = params;
        EventBusManager.sendMessage( msg );
    }

    @Override
    public void setFragmentParams(FragmentParams param) {
        if(mView==null) return;
        if(param!=null){
            fragmentParams = param;
        }else{
            fragmentParams = new FragmentParams();
        }

        if(fragmentParams.viewFocus==null){
            fragmentParams.viewFocus = quanbugexing;
        }
        MyViewManager.getInstance().requestFocus( fragmentParams.viewFocus );
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(hidden){

        }
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

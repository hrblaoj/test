package com.shinetvbox.vod.view.fragment.updateapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shinetvbox.vod.R;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.data.eventbus.EventBusMessageUpdateApp;
import com.shinetvbox.vod.data.params.FragmentParams;
import com.shinetvbox.vod.manager.EventBusManager;
import com.shinetvbox.vod.manager.MyViewManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.utils.updateapp.UpdateAppUtil;
import com.shinetvbox.vod.view.custom.MyFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FragmentPageUpdateApp extends MyFragment {

    private Context mContext = null;
    private View mView = null;

    private FragmentParams fragmentParams = null;

    private TextView textAppVersion = null;
    private TextView textAppDescribe = null;
    private ProgressBar progressBarDownload = null;
    private Button btnSure = null;
    private Button btnCancel = null;


    public FragmentPageUpdateApp() {
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
        mView = inflater.inflate( R.layout.fragment_page_update_app, container, false );
        initView();
        return mView;
    }

    private void initView() {
        if(mView == null) return;
        ResManager.getInstance().register( mView );
        EventBus.getDefault().register( this );

        textAppVersion = mView.findViewById( R.id.updateapp_textview_version );
        textAppDescribe = mView.findViewById( R.id.updateapp_textview_describe );
        progressBarDownload = mView.findViewById( R.id.updateapp_progressbar );
        btnSure = mView.findViewById( R.id.updateapp_btn_sure );
        btnSure.setOnClickListener( onClick );
        btnCancel = mView.findViewById( R.id.updateapp_btn_cancel );
        btnCancel.setOnClickListener( onClick );
    }
    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() == null) return;
            switch (v.getTag().toString()) {
                case "updateapp_btn_sure":
                    UpdateAppUtil.downNewVersion();
                    progressBarDownload.setVisibility( View.VISIBLE );
                    break;
                case "updateapp_btn_cancel":
                    UpdateAppUtil.stopDownNewVersion();
                    EventBusMessage msg = new EventBusMessage();
                    FragmentParams param = new FragmentParams();
                    msg.what = EventBusConstants.PAGE_BACK;
                    param.pageIndex = msg.what;
                    param.isShowBtnSelectedSong = false;
                    msg.obj = param;
                    EventBusManager.sendMessage( msg );
                    break;
            }
        }
    };
    @Override
    public void setFragmentParams(FragmentParams param) {
        if(mView==null) return;
        if(param != null){
            fragmentParams = param;
        }else{
            fragmentParams = new FragmentParams();
        }
        if(fragmentParams.viewFocus == null) MyViewManager.getInstance().requestFocus( btnSure );

        ResManager.getInstance().setTextReplaceXXX( textAppVersion,R.string.updateapp_textview_version,fragmentParams.softUpdateInfo.version );
        textAppDescribe.setText( fragmentParams.softUpdateInfo.describe );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusMessageUpdateApp msg){
        switch (msg.what){
            case EventBusConstants.APP_UPDATE_PROGRESS:
                progressBarDownload.setProgress( msg.progress );
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(hidden){
            UpdateAppUtil.stopDownNewVersion();
        }
    }

    @Override
    public FragmentParams getFragmentParams() {
        return fragmentParams;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UpdateAppUtil.stopDownNewVersion();
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
}

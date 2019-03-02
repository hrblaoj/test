package com.shinetvbox.vod.view.fragment.home;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shinetvbox.vod.R;
import com.shinetvbox.vod.certercontrol.ControlCenter;
import com.shinetvbox.vod.certercontrol.ControlCenterConstants;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.data.params.FragmentParams;
import com.shinetvbox.vod.db.Query;
import com.shinetvbox.vod.floatwindow.ShineVideoFloatWindow;
import com.shinetvbox.vod.manager.EventBusManager;
import com.shinetvbox.vod.manager.MyViewManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.service.cloudserver.CloudDownloadSongStruce;
import com.shinetvbox.vod.service.cloudserver.CloudMessageProc;
import com.shinetvbox.vod.utils.KtvLog;
import com.shinetvbox.vod.utils.KtvSystemApi;
import com.shinetvbox.vod.utils.QrcodeUtil;
import com.shinetvbox.vod.utils.SystemUtil;
import com.shinetvbox.vod.view.custom.ButtonFocus;
import com.shinetvbox.vod.view.custom.MyFragment;

import static com.shinetvbox.vod.floatwindow.ShineVideoFloatWindow.SCREEN_FULL;
import static com.shinetvbox.vod.service.cloudserver.CloudMessageProc.DOWNLOAD_CMD.GET_SONG;
import static com.shinetvbox.vod.service.cloudserver.CloudMessageProc.DOWNLOAD_CMD.GET_SONG_EXIT;

public class FragmentHomeDiangetai extends MyFragment {

    private Context mContext = null;
    private View mView = null;

    private FragmentParams fragmentParams;

    private ButtonFocus btnVideo;
    private View btnYingshijingqu;
    private View btnPaihangbang;
    private View btnPingyidiange;
    private View btnGexingdiange;
    private View btnFenleidiange;
    private View btnXingetuijian;
    private View btnYichanggequ;

    private ImageView imageViewYingshi;

    public FragmentHomeDiangetai() {
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
        mView = inflater.inflate( R.layout.fragment_home_diangetai, container, false );
        initView();
        return mView;
    }

    private void initView() {
        if(mView == null) return;
        ResManager.getInstance().register( mView );

        imageViewYingshi = mView.findViewById( R.id.btn_home_diangetai_video_image);
        imageViewYingshi.addOnLayoutChangeListener( new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                ShineVideoFloatWindow.getIntance().setWindowsPoint(imageViewYingshi);
                imageViewYingshi.removeOnLayoutChangeListener( this );
            }
        } );

        btnVideo = mView.findViewById( R.id.btn_home_diangetai_video );
        btnVideo.setOnClickListener( onClick );
        btnYingshijingqu = mView.findViewById( R.id.btn_home_diangetai_yingshijinqu );
        btnYingshijingqu.setOnClickListener( onClick );
        btnPaihangbang = mView.findViewById( R.id.btn_home_diangetai_paihangbang );
        btnPaihangbang.setOnClickListener( onClick );
        btnPingyidiange = mView.findViewById( R.id.btn_home_diangetai_pinyindiange );
        btnPingyidiange.setOnClickListener( onClick );
        btnGexingdiange = mView.findViewById( R.id.btn_home_diangetai_gexingdiange );
        btnGexingdiange.setOnClickListener( onClick );
        btnFenleidiange = mView.findViewById( R.id.btn_home_diangetai_fenleidiange );
        btnFenleidiange.setOnClickListener( onClick );
        btnXingetuijian = mView.findViewById( R.id.btn_home_diangetai_xingetuijian );
        btnXingetuijian.setOnClickListener( onClick );
        btnYichanggequ = mView.findViewById( R.id.btn_home_diangetai_yichanggequ );
        btnYichanggequ.setOnClickListener( onClick );

        MyViewManager.getInstance().requestFocus( btnVideo );

        ImageView imgWechatQrcode = mView.findViewById( R.id.app_home_phone_qrcode );
        QrcodeUtil.loadWechartQrcode(imgWechatQrcode);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getTag() == null) return;
            if(fragmentParams!=null){
                fragmentParams.viewFocus = v;
            }
            FragmentParams param = new FragmentParams();
            Query mQuery = new Query();
            switch(v.getTag().toString()) {
                case "btn_home_diangetai_video":
                    ShineVideoFloatWindow.getIntance().setVideoScale(SCREEN_FULL,true);
                    break;
                case "btn_home_diangetai_yingshijinqu":
                    param.keyboard.isShow = true;
                    param.keyboard.titleId = R.string.app_home_yingshijinqu;
                    param.keyboard.hintId = R.string.yingshijinqu_text_keyboard_hint;
                    mQuery.song_theme = "8";
                    param.songListInfo.query = mQuery;
                    sendEventBusMessage(EventBusConstants.PAGE_GOTO_YINGSHIJINQU,param);
//                    Message msg = new Message();
//                    msg.what = ControlCenterConstants.MUSIC_VOLUME_ADD;
//                    ControlCenter.sendMessage( msg );
                    break;
                case "btn_home_diangetai_paihangbang":
                    sendEventBusMessage(EventBusConstants.PAGE_GOTO_PAIHANGBANG,param);
//                    Message msg2 = new Message();
//                    msg2.what = ControlCenterConstants.MUSIC_VOLUME_SUBTRACT;
//                    ControlCenter.sendMessage( msg2 );
                    break;
                case "btn_home_diangetai_pinyindiange":
                    param.keyboard.isShow = true;
                    param.keyboard.titleId = R.string.app_home_pinyindiange;
                    param.keyboard.hintId = R.string.pinyindiange_text_keyboard_hint;
                    param.songListInfo.query = mQuery;
                    sendEventBusMessage(EventBusConstants.PAGE_GOTO_PINYINDIANGE,param);
                    break;
                case "btn_home_diangetai_gexingdiange":
                    sendEventBusMessage(EventBusConstants.PAGE_GOTO_GEXINGDIANGE,param);
                    break;
                case "btn_home_diangetai_fenleidiange":
                    sendEventBusMessage(EventBusConstants.PAGE_GOTO_FENLEIDIANGE,param);
                    break;
                case "btn_home_diangetai_xingetuijian":
                    param.songListInfo.titleId = R.string.app_home_xingetuijian;
                    param.songListInfo.hintId = R.string.xingetuijian_text_hint;
                    param.songListInfo.hintContentId = R.string.xingetuijian_text_hint_content;
                    param.songListInfo.thumbnailId = R.drawable.image_xingetuijian_default;
                    mQuery.new_song_date = SystemUtil.getStaticYearAgoTime();
                    param.songListInfo.query = mQuery;
                    sendEventBusMessage(EventBusConstants.PAGE_GOTO_XINGGETUIJIAN,param);
                    break;
                case "btn_home_diangetai_yichanggequ":
                    param.songListInfo.titleId = R.string.app_home_yichanggequ;
                    param.songListInfo.hintId = R.string.yichanggequ_text_hint;
                    param.songListInfo.hintContentId = R.string.yichanggequ_text_hint_content;
                    param.songListInfo.thumbnailId = R.drawable.image_yixuangequ_default;
                    sendEventBusMessage(EventBusConstants.PAGE_GOTO_YICHANGGEQU,param);
                    break;
                default:
                    break;
            }
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
        if(mView==null || param == null) return;
        fragmentParams = param;
        if(fragmentParams.viewFocus == null){
            fragmentParams.viewFocus = btnVideo;
        }
        MyViewManager.getInstance().requestFocus( fragmentParams.viewFocus );
    }

    @Override
    public FragmentParams getFragmentParams() {
        return new FragmentParams();
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

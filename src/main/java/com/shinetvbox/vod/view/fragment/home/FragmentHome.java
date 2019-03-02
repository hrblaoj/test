package com.shinetvbox.vod.view.fragment.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.shinetvbox.vod.MainActivity;
import com.shinetvbox.vod.MyApplication;
import com.shinetvbox.vod.R;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.data.eventbus.EventBusMessageSong;
import com.shinetvbox.vod.data.params.FragmentParams;
import com.shinetvbox.vod.floatwindow.ShineVideoFloatWindow;
import com.shinetvbox.vod.manager.EventBusManager;
import com.shinetvbox.vod.manager.LanguageManager;
import com.shinetvbox.vod.manager.MemberManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.manager.SongPlayManager;
import com.shinetvbox.vod.utils.SharedPreferencesUtil;
import com.shinetvbox.vod.utils.updateapp.VersionUtil;
import com.shinetvbox.vod.view.custom.MyFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.shinetvbox.vod.floatwindow.ShineVideoFloatWindow.SCREEN_DEFAULT;

public class FragmentHome extends MyFragment {

    private Context mContext = null;
    private MainActivity activity = null;
    private View mView = null;

    private FragmentParams fragmentParams = new FragmentParams();

    private MyViewPager viewPager;
    private MyFragmentAdapter fragmentAdapter;
    private List<MyFragment> listFragment;
    private FragmentManager fragmentManager;
    private FragmentHomeDiangetai fragmentHomeDiangetai;

    private TextView text_Playing;
    private TextView text_Next;
    private TextView text_Appinfo;
    private View btn_wifi;
    private View btn_lang;
    private ImageView img_wifi;
    private ImageView img_lang;
    private View btn_vip;
    private ImageView img_vip;

    private List<Button> btnList = new ArrayList<>(  );
    private Button btn_diangetai;
    private Button btn_yingshi;
    private Button btn_yule;
    private Button btn_yingyong;

    public FragmentHome() {
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
        mView = inflater.inflate( R.layout.fragment_home, container, false );

        activity = (MainActivity) getActivity();
        initView();
        return mView;
    }

    private void initView() {
        if(mView == null) return;
        ResManager.getInstance().register( mView );
        EventBus.getDefault().register( this );

        fragmentHomeDiangetai = new FragmentHomeDiangetai();

        fragmentHomeDiangetai.setFragmentParams( fragmentParams );
        listFragment = new ArrayList<>(  );

        listFragment.add( fragmentHomeDiangetai );

        viewPager = mView.findViewById( R.id.home_viewPager_content );
        fragmentManager = activity.getSupportFragmentManager();
        fragmentAdapter = new MyFragmentAdapter( fragmentManager, listFragment );
        viewPager.setAdapter( fragmentAdapter );
        viewPager.addOnPageChangeListener( pageChangeListener );

        text_Playing = mView.findViewById( R.id.app_home_playing_content );
        text_Next = mView.findViewById( R.id.app_home_next_content );
        text_Appinfo = mView.findViewById( R.id.app_home_version_info );

        btn_wifi = mView.findViewById( R.id.home_btn_wifi );
        btn_wifi.setOnClickListener( onClick );
        btn_lang = mView.findViewById( R.id.home_btn_lang );
        btn_lang.setOnClickListener( onClick );
        img_wifi = mView.findViewById( R.id.home_image_wifi );
        img_wifi.setImageLevel( 4 );
        img_lang = mView.findViewById( R.id.home_image_lang );
        btn_vip = mView.findViewById( R.id.app_home_vip );
        btn_vip.setOnClickListener( onClick );
        img_vip = mView.findViewById( R.id.home_image_vip );
        img_vip.setImageLevel( 0 );

        btn_diangetai = mView.findViewById( R.id.app_home_diangetai );
        btn_diangetai.setOnFocusChangeListener( onFocusChange );
        btn_yingshi = mView.findViewById( R.id.app_home_yingshi );
        btn_yingshi.setOnFocusChangeListener( onFocusChange );
        btn_yingshi.setFocusable( false );
        btn_yingshi.setFocusableInTouchMode( false );
        btn_yingshi.setEnabled( false );
        btn_yingshi.setAlpha( (float) 0.4 );
        btn_yule = mView.findViewById( R.id.app_home_yule );
        btn_yule.setOnFocusChangeListener( onFocusChange );
        btn_yule.setFocusable( false );
        btn_yule.setFocusableInTouchMode( false );
        btn_yule.setEnabled( false );
        btn_yule.setAlpha( (float) 0.4 );
        btn_yingyong = mView.findViewById( R.id.app_home_yingyong );
        btn_yingyong.setOnFocusChangeListener( onFocusChange );
        btn_yingyong.setFocusable( false );
        btn_yingyong.setFocusableInTouchMode( false );
        btn_yingyong.setEnabled( false );
        btn_yingyong.setAlpha( (float) 0.4 );

        btnList.add( btn_diangetai );
        btnList.add( btn_yingshi );
        btnList.add( btn_yule );
        btnList.add( btn_yingyong );

        setBtnTitleState(0);
        setLanguage(SharedPreferencesUtil.getLangage_key());
    }
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            setBtnTitleState(position);
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
    private void setBtnTitleState(int ind){
        for(int i=0;i<btnList.size();i++){
            if(ind == i){
                if(!btnList.get( i ).isSelected()){
                    btnList.get( i ).setSelected( true );
                }
            }else{
                if(btnList.get( i ).isSelected()){
                    btnList.get( i ).setSelected( false );
                }
            }
        }
    }
    private View.OnFocusChangeListener onFocusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(v.getTag() == null) return;
            if(hasFocus){
                switch(v.getTag().toString()) {
                    case "app_home_diangetai":
                        if(viewPager.getCurrentItem()!=0){
                            viewPager.setCurrentItem( 0 );
                        }
                        break;
                    case "app_home_yingshi":
                        if(viewPager.getCurrentItem()!=1){
                            viewPager.setCurrentItem( 1 );
                        }
                        break;
                    case "app_home_yule":
                        if(viewPager.getCurrentItem()!=2){
                            viewPager.setCurrentItem( 2 );
                        }
                        break;
                    case "app_home_yingyong":
                        if(viewPager.getCurrentItem()!=3){
                            viewPager.setCurrentItem( 3 );
                        }
                        break;
                }
            }
        }

    };

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getTag() == null) return;
            FragmentParams fg = new FragmentParams();
            switch(v.getTag().toString()) {
                case "home_btn_wifi":
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
//                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
//                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    break;
                case "home_btn_lang":
                    fg.isShowBtnSelectedSong = false;
                    sendEventBusMessage(EventBusConstants.PAGE_GOTO_LANGUAGE,fg);
                    break;
                case "app_home_vip":
                    fg.isShowBtnSelectedSong = false;
                    sendEventBusMessage(EventBusConstants.PAGE_GOTO_PAY,fg);
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
        if(mView==null) return;
        if(param!=null){
            fragmentParams = param;
        }else{
            fragmentParams = new FragmentParams();
        }

        switch (viewPager.getCurrentItem()){
            case 0:
                fragmentHomeDiangetai.setFragmentParams( fragmentParams );
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
        }
    }
    @SuppressLint("SetTextI18n")
    public void refreshinfo(){

        String freename = ResManager.getInstance().getStringById( R.string.app_home_free_song );
        String songname = SongPlayManager.getCurrentPlaySongName();

        if(songname != null && !songname.equals( "" )) {
            text_Playing.setText(songname);
        }
        else {
            text_Playing.setText(freename);
        }
        songname = SongPlayManager.getNextPlaySongName();
        if(songname != null && !songname.equals( "" )) {
            text_Next.setText(songname);
        }
        else {
            text_Next.setText(freename);
        }
        EventBusMessage msg = new EventBusMessage();
        msg.what = EventBusConstants.SONG_SELECT_NUMBER_CHANGE;
        msg.obj = ""+SongPlayManager.getSelectSongCount();
        EventBusManager.sendMessage( msg );

        if(MemberManager.isMember){
            img_vip.setImageLevel( 1 );
        }else{
            img_vip.setImageLevel( 0 );
        }

        text_Appinfo.setText( "MAC:"+getMac() +"     "+VersionUtil.getAppVersionNameLocal() );
    }
    private String mac = null;
    private String getMac(){
        if(mac!=null) return mac;
        mac = "";
        if(MyApplication.MAC_ADDRESS.equals( "" )){
            mac = "00:00:00:00:00:00";
        }else{
            int len = MyApplication.MAC_ADDRESS.length();
            for(int i = 0 ;i < len;i++){
                if(i!=0 && i%2==0){
                    mac+=":";
                }
                mac+= MyApplication.MAC_ADDRESS.substring( i,i+1 ).toUpperCase();
            }
        }
        return mac;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusMessageSong msg){
        switch (msg.what){
            case EventBusConstants.SONG_PLAY_CHANGE:
                refreshinfo();
                break;
        }
    }

    public void setLanguage(String key){
        switch (key){
            case LanguageManager.ZH_CN:
                img_lang.setImageLevel( 0 );
                break;
            case LanguageManager.ZH_CNF:
                img_lang.setImageLevel( 1 );
                break;
            case LanguageManager.EN_US:
                img_lang.setImageLevel( 2 );
                break;
            case LanguageManager.JA_JP:
                img_lang.setImageLevel( 3 );
                break;
            case LanguageManager.KO_KR:
                img_lang.setImageLevel( 4 );
                break;
            case LanguageManager.KM_KH:
                img_lang.setImageLevel( 5 );
                break;
            case LanguageManager.MY_MM:
                img_lang.setImageLevel( 6 );
                break;
            case LanguageManager.IN_ID:
                img_lang.setImageLevel( 7 );
                break;
            case LanguageManager.TH_TH:
                img_lang.setImageLevel( 8 );
                break;
            case LanguageManager.VI_VN:
                img_lang.setImageLevel( 9 );
                break;
        }
    }
    @Override
    public FragmentParams getFragmentParams() {
        return fragmentParams;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister( this );
    }

    @Override
    public boolean isBaseOnWidth() {
        return false;
    }

    @Override
    public float getSizeInDp() {
        return 0;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(hidden){

        }else{
            ShineVideoFloatWindow.getIntance().setVideoScale(SCREEN_DEFAULT, true);
        }
    }
}

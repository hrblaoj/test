package com.shinetvbox.vod.manager;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.shinetvbox.vod.R;
import com.shinetvbox.vod.data.constants.Constants;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.data.params.FragmentParams;
import com.shinetvbox.vod.floatwindow.ShineVideoFloatWindow;
import com.shinetvbox.vod.utils.AnimationUtil;
import com.shinetvbox.vod.view.custom.MyFragment;
import com.shinetvbox.vod.view.fragment.downloadlist.FragmentPageDownloadList;
import com.shinetvbox.vod.view.fragment.fenleidiange.FragmentPageFenleidiange;
import com.shinetvbox.vod.view.fragment.gexingdiange.FragmentPageGexingdiange;
import com.shinetvbox.vod.view.fragment.home.FragmentHome;
import com.shinetvbox.vod.view.fragment.keyboard.FragmentKeyboard;
import com.shinetvbox.vod.view.fragment.language.FragmentPageLanguage;
import com.shinetvbox.vod.view.fragment.paihangbang.FragmentPagePaihangbang;
import com.shinetvbox.vod.view.fragment.pay.FragmentPagePay;
import com.shinetvbox.vod.view.fragment.pay.FragmentPagePayCdkey;
import com.shinetvbox.vod.view.fragment.selectedlist.FragmentPageSelectedList;
import com.shinetvbox.vod.view.fragment.singerlist.FragmentPageSingerList;
import com.shinetvbox.vod.view.fragment.singersonglist.FragmentPageSingerSongList;
import com.shinetvbox.vod.view.fragment.songlist.FragmentPageSongList;
import com.shinetvbox.vod.view.fragment.sunglist.FragmentPageSungList;
import com.shinetvbox.vod.view.fragment.updateapp.FragmentPageUpdateApp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shinetvbox.vod.floatwindow.ShineVideoFloatWindow.SCREEN_FULL;
import static com.shinetvbox.vod.floatwindow.ShineVideoFloatWindow.SCREEN_HIDE;

public class MyViewManager {

    private static MyViewManager mInstance = null;

    private List<Map<MyFragment,FragmentParams>> stackList = new ArrayList<>(  );

    private AppCompatActivity mActivity;
    private FragmentTransaction fragmentTransaction;

    private MyFragment currentFragment = null;
    private int currentPageIndex = -1;

    private View viewgroup_selectedSong;
    private Button btn_selected;
    private TextView text_selected_number;
    private Button btn_download;
    private TextView text_download_number;
    ////fragment
    //小键盘
    FragmentKeyboard keyboard;
    //主页
    FragmentHome fragmentHome;
    //歌曲点歌
    FragmentPageSongList fragmentPageSongList;
    //歌星点歌
    FragmentPageGexingdiange fragmentPageGexingdiange;
    //分类点歌
    FragmentPageFenleidiange fragmentPageFenleidiange;
    //排行榜
    FragmentPagePaihangbang fragmentPagePaihangbang;
    //歌星列表
    FragmentPageSingerList fragmentPageSingerList;
    //歌星歌曲列表
    FragmentPageSingerSongList fragmentPageSingerSongList;
    //已选歌曲列表
    FragmentPageSelectedList fragmentPageSelectedList;
    //已选歌曲列表
    FragmentPageDownloadList fragmentPageDownloadList;
    //已唱歌曲列表
    FragmentPageSungList fragmentPageSungList;
    //应用更新页面
    FragmentPageUpdateApp fragmentPageUpdateApp;
    //语言切换页面
    FragmentPageLanguage fragmentPageLanguage;
    //支付页面
    FragmentPagePay fragmentPagePay;
    //支付页面-兑换码
    FragmentPagePayCdkey fragmentPagePayCdkey;
    ////

    public static MyViewManager getInstance(){
        if(mInstance==null){
            synchronized(MyViewManager.class){
                if(mInstance==null){
                    mInstance = new MyViewManager();
                }
            }
        }
        return mInstance;
    }
    public int getStackListCount(){
        return stackList.size();
    }
    public void init(AppCompatActivity activity){
        if(mActivity != null) return;
        mActivity = activity;
        EventBus.getDefault().register( this );

        viewgroup_selectedSong = mActivity.findViewById( R.id.home_fragment_selectedsong );
        text_selected_number = mActivity.findViewById( R.id.app_home_selected_number );
        text_selected_number.setText( "0" );
        btn_selected = mActivity.findViewById( R.id.app_home_selected );
        btn_selected.setOnClickListener( onClick );
        ResManager.getInstance().setText( btn_selected,R.string.app_home_selected );

        text_download_number = mActivity.findViewById( R.id.app_home_download_number );
        text_download_number.setText( "0" );
        btn_download = mActivity.findViewById( R.id.app_home_download );
        btn_download.setOnClickListener( onClick );
        ResManager.getInstance().setText( btn_download,R.string.app_home_download );

        View home_fragment_content = mActivity.findViewById( R.id.home_fragment_content );
        home_fragment_content.setVisibility( View.VISIBLE );
        keyboard = new FragmentKeyboard();
        fragmentHome = new FragmentHome();
        fragmentPageSongList = new FragmentPageSongList();
        fragmentPagePaihangbang = new FragmentPagePaihangbang();
        fragmentPageGexingdiange = new FragmentPageGexingdiange();
        fragmentPageFenleidiange = new FragmentPageFenleidiange();
        fragmentPageSingerList = new FragmentPageSingerList();
        fragmentPageSingerSongList = new FragmentPageSingerSongList();
        fragmentPageSelectedList = new FragmentPageSelectedList();
        fragmentPageDownloadList = new FragmentPageDownloadList();
        fragmentPageSungList = new FragmentPageSungList();
        fragmentPageUpdateApp = new FragmentPageUpdateApp();
        fragmentPageLanguage = new FragmentPageLanguage();
        fragmentPagePay = new FragmentPagePay();
        fragmentPagePayCdkey = new FragmentPagePayCdkey();

        fragmentTransaction = mActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.home_fragment_content, fragmentHome);
        fragmentTransaction.add(R.id.home_fragment_content, fragmentPageSongList);
        fragmentTransaction.add(R.id.home_fragment_content, fragmentPagePaihangbang);
        fragmentTransaction.add(R.id.home_fragment_content, fragmentPageGexingdiange);
        fragmentTransaction.add(R.id.home_fragment_content, fragmentPageFenleidiange);
        fragmentTransaction.add(R.id.home_fragment_content, fragmentPageSingerList );
        fragmentTransaction.add(R.id.home_fragment_content, fragmentPageSingerSongList );
        fragmentTransaction.add(R.id.home_fragment_content, fragmentPageSelectedList );
        fragmentTransaction.add(R.id.home_fragment_content, fragmentPageDownloadList );
        fragmentTransaction.add(R.id.home_fragment_content, fragmentPageSungList );
        fragmentTransaction.add(R.id.home_fragment_content, fragmentPageUpdateApp );
        fragmentTransaction.add(R.id.home_fragment_content, fragmentPageLanguage );
        fragmentTransaction.add(R.id.home_fragment_content, fragmentPagePay );
        fragmentTransaction.add(R.id.home_fragment_content, fragmentPagePayCdkey );

        fragmentTransaction.add(R.id.home_fragment_content, keyboard);

//        fragmentTransaction.hide( fragmentHome );
        fragmentTransaction.hide( fragmentPageSongList );
        fragmentTransaction.hide( fragmentPagePaihangbang );
        fragmentTransaction.hide( fragmentPageGexingdiange );
        fragmentTransaction.hide( fragmentPageFenleidiange );
        fragmentTransaction.hide( fragmentPageSingerList );
        fragmentTransaction.hide( fragmentPageSingerSongList );
        fragmentTransaction.hide( fragmentPageSelectedList );
        fragmentTransaction.hide( fragmentPageDownloadList );
        fragmentTransaction.hide( fragmentPageSungList );
        fragmentTransaction.hide( fragmentPageUpdateApp );
        fragmentTransaction.hide( fragmentPageLanguage );
        fragmentTransaction.hide( fragmentPagePay );
        fragmentTransaction.hide( fragmentPagePayCdkey );
        fragmentTransaction.hide( keyboard );

        fragmentTransaction.commit();

        currentFragment = fragmentHome;
    }
    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getTag() == null) return;
            FragmentParams param = new FragmentParams();
            switch(v.getTag().toString()) {
                case "app_home_selected":
                    param.songListInfo.titleId = R.string.app_home_selected;
                    param.isShowBtnSelectedSong = false;
                    param.songListInfo.thumbnailId = R.drawable.image_yixuangequ_default;
                    sendEventBusMessage(EventBusConstants.PAGE_GOTO_YIXUANGEQU,param);
                    break;
                case "app_home_download":
                    param.songListInfo.titleId = R.string.app_home_download;
                    param.isShowBtnSelectedSong = false;
                    param.songListInfo.thumbnailId = R.drawable.image_yixuangequ_default;
                    sendEventBusMessage(EventBusConstants.PAGE_GOTO_XIAZAIGEQU,param);
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusMessage msg) {
        switch (msg.what) {
            case EventBusConstants.VIDEO_SHOW:
                ShineVideoFloatWindow.getIntance().setVideoScale(SCREEN_FULL, true);
                break;
            case EventBusConstants.PAGE_BACK:
                backFragment(false);
                break;
            case EventBusConstants.PAGE_GOTO_HOME:
                backFragment(true);
                break;
            case EventBusConstants.PAGE_GOTO_HOME_DIANGETAI:
                break;
            case EventBusConstants.PAGE_GOTO_HOME_YINGSHI:
                break;
            case EventBusConstants.PAGE_GOTO_HOME_YULE:
                break;
            case EventBusConstants.PAGE_GOTO_HOME_YINGYONG:
                break;
            case EventBusConstants.PAGE_GOTO_PAIHANGBANG:
                showFragment(fragmentPagePaihangbang, (FragmentParams) msg.obj );
                break;
            case EventBusConstants.PAGE_GOTO_YINGSHIJINQU:
            case EventBusConstants.PAGE_GOTO_PAIHANGBANG_LIST:
            case EventBusConstants.PAGE_GOTO_PINYINDIANGE:
            case EventBusConstants.PAGE_GOTO_FENLEIDIANGE_LIST:
            case EventBusConstants.PAGE_GOTO_XINGGETUIJIAN:
                showFragment(fragmentPageSongList, (FragmentParams) msg.obj );
                break;
            case EventBusConstants.PAGE_GOTO_GEXINGDIANGE:
                showFragment(fragmentPageGexingdiange, (FragmentParams) msg.obj );
                break;
            case EventBusConstants.PAGE_GOTO_FENLEIDIANGE:
                showFragment(fragmentPageFenleidiange, (FragmentParams) msg.obj );
                break;
            case EventBusConstants.PAGE_GOTO_GEXINGDIANGE_LIST:
                showFragment( fragmentPageSingerList, (FragmentParams) msg.obj );
                break;
            case EventBusConstants.PAGE_GOTO_GEXINGDIANGE_GEQU_LIST:
                showFragment( fragmentPageSingerSongList, (FragmentParams) msg.obj );
                break;
            case EventBusConstants.PAGE_GOTO_YIXUANGEQU:
                showFragment( fragmentPageSelectedList, (FragmentParams) msg.obj );
                break;
            case EventBusConstants.PAGE_GOTO_XIAZAIGEQU:
                showFragment( fragmentPageDownloadList, (FragmentParams) msg.obj );
                break;
            case EventBusConstants.PAGE_GOTO_YICHANGGEQU:
                showFragment( fragmentPageSungList, (FragmentParams) msg.obj );
                break;
            case EventBusConstants.PAGE_GOTO_LANGUAGE:
                showFragment( fragmentPageLanguage, (FragmentParams) msg.obj );
                break;
            case EventBusConstants.PAGE_GOTO_UPDATEAPP:
                showFragment( fragmentPageUpdateApp, (FragmentParams) msg.obj );
                break;
            case EventBusConstants.PAGE_GOTO_PAY:
                showFragment( fragmentPagePay, (FragmentParams) msg.obj );
                break;
            case EventBusConstants.PAGE_GOTO_PAY_CDKEY:
                showFragment( fragmentPagePayCdkey, (FragmentParams) msg.obj );
                break;
            case EventBusConstants.SONG_SELECT_NUMBER_CHANGE:
                text_selected_number.setText( (String) msg.obj );
                break;
            case EventBusConstants.SONG_DOWNLOAD_NUMBER_CHANGE:
                text_download_number.setText( (String) msg.obj );
                break;
            case EventBusConstants.LANGUAGE_CHANGE:
                changeLanguage((String) msg.obj);
                backFragment( false );
                break;
            case EventBusConstants.MEMBER_INFO_REFRESH:
                if(fragmentHome!=null){
                    fragmentHome.refreshinfo();
                }
                break;

        }
    }

    private void showFragment(MyFragment fragment,FragmentParams param){
        if(currentPageIndex == param.pageIndex) return;
        if(!fragment.equals( fragmentHome )){
            ShineVideoFloatWindow.getIntance().setVideoScale(SCREEN_HIDE, true);
        }
        param.isPageBack = false;
        fragment.setFragmentParams( param );
        fragmentTransaction = mActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations( R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_top, R.anim.slide_out_bottom );
        if(currentFragment!=null){
            if(fragment.equals( fragmentHome )){
                stackList.clear();
            }else{
                Map<MyFragment,FragmentParams> map = new HashMap<>(  );
                map.put( currentFragment,currentFragment.getFragmentParams() );
                stackList.add( map );
            }
            fragmentTransaction.hide( currentFragment );
        }
        if(param.keyboard.isShow){
            keyboard.setFragmentParams(param);
            fragmentTransaction.show( keyboard );
        }else{
            fragmentTransaction.hide( keyboard );
        }
        if(param.isShowBtnSelectedSong){
            AnimationUtil.setViewVisible( viewgroup_selectedSong,true,AnimationUtil.TYPE_TOP );
        }else{
            AnimationUtil.setViewVisible( viewgroup_selectedSong,false,AnimationUtil.TYPE_TOP );
        }
        fragmentTransaction.show( fragment );
        fragmentTransaction.commit();
        currentFragment = fragment;
        currentPageIndex = param.pageIndex;
    }

    private void backFragment(boolean isHome){
        if(stackList.size()==0) return;
        if(isHome){
//            ShineVideoFloatWindow.getIntance().setVideoScale(SCREEN_DEFAULT, true);
            stackList.clear();
        }
        int len = stackList.size();
        for (MyFragment fragment : stackList.get( len - 1 ).keySet()) {
            FragmentParams param = stackList.get( len - 1 ).get(fragment);
            param.isPageBack = true;
            fragment.setFragmentParams( param );
            fragmentTransaction = mActivity.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations( R.anim.slide_out_bottom, R.anim.slide_in_top );
            fragmentTransaction.setCustomAnimations( R.anim.slide_in_top, R.anim.slide_out_bottom, R.anim.slide_in_bottom, R.anim.slide_out_top );
            fragmentTransaction.hide( currentFragment );
            if(param.keyboard.isShow){
                keyboard.setFragmentParams(param);
                fragmentTransaction.show( keyboard );
            }else{
                fragmentTransaction.hide( keyboard );
            }
            if(param.isShowBtnSelectedSong){
                AnimationUtil.setViewVisible( viewgroup_selectedSong,true,AnimationUtil.TYPE_TOP );
            }else{
                AnimationUtil.setViewVisible( viewgroup_selectedSong,false,AnimationUtil.TYPE_TOP );
            }
            fragmentTransaction.show( fragment );
            fragmentTransaction.commit();
            if(currentFragment.equals( fragmentPageSelectedList )){
                requestFocus( btn_selected );
            }else if(currentFragment.equals( fragmentPageDownloadList )){
                requestFocus( btn_download );
            }
            currentFragment = fragment;
            currentPageIndex = param.pageIndex;
        }
        stackList.remove( len - 1 );
    }

    private void changeLanguage(String key){
        ResManager.getInstance().languageSwitch( key );
        if(fragmentHome!=null){
            fragmentHome.setLanguage(key);
        }
    }

    public void requestFocus(View v){
        if(v!=null){
            v.requestFocus();
        }
    }
    public void requestFocusCustomButton(View v){
        if(v!=null){
            v.animate()
                    .scaleX( Constants.FOCUS_XSCALE )
                    .scaleY( Constants.FOCUS_YSCALE )
                    .setDuration(Constants.FOCUS_DURATION)
                    .start();
            ImageView iv = v.findViewById( ResManager.getInstance().getId("image_focus") );
            if(iv!=null){
                iv.setVisibility( View.VISIBLE );
            }
            v.requestFocus();
            v.bringToFront();
        }
    }
    public boolean isCurrentFragment(MyFragment mf){
        if(currentFragment == null) return false;
        if(currentFragment.equals( mf )) return true;
        return false;
    }
    public void onDestroy() {
        EventBus.getDefault().unregister( this );
    }

}

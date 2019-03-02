package com.shinetvbox.vod.view.fragment.downloadlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shinetvbox.vod.R;
import com.shinetvbox.vod.certercontrol.ControlCenter;
import com.shinetvbox.vod.data.constants.Constants;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.data.eventbus.EventBusMessageSongDownload;
import com.shinetvbox.vod.data.params.FragmentParams;
import com.shinetvbox.vod.manager.EventBusManager;
import com.shinetvbox.vod.manager.MyViewManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.manager.SongPlayManager;
import com.shinetvbox.vod.utils.QrcodeUtil;
import com.shinetvbox.vod.view.custom.MyFragment;
import com.shinetvbox.vod.view.custom.viewpager.LtViewPager;
import com.shinetvbox.vod.view.custom.viewpager.MyLtViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FragmentPageDownloadList extends MyFragment {

    private Context mContext = null;
    private View mView = null;

    private MyLtViewPager myLtViewPager = null;
    private DownloadListViewPagerAdapter viewPagerAdapter = null;
    private FragmentParams fragmentParams = null;

    private View viewNoResult = null;
    private TextView textSongListInfoTitle = null;
    private Button btnClear = null;
    private ImageView imageSongListInfoThumb = null;
    private TextView textPageNumber = null;
    private Button btnPagePrev = null;
    private Button btnPageNext = null;

    private boolean isSearching = false;

    public FragmentPageDownloadList() {
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
        mView = inflater.inflate( R.layout.fragment_page_downloadlist, container, false );
        initView();
        return mView;
    }

    private void initView() {
        if(mView == null) return;
        EventBus.getDefault().register( this );
        ResManager.getInstance().register( mView );

        viewNoResult = mView.findViewById( R.id.layout_selectlist_noresult );
        textSongListInfoTitle = mView.findViewById( R.id.text_downloadlist_title );
        imageSongListInfoThumb = mView.findViewById( R.id.image_downloadlist_thumbnail );

        btnClear = mView.findViewById( R.id.btn_downloadlist_clear );
        btnClear.setOnClickListener( onClick );

        textPageNumber = mView.findViewById( R.id.page_number_text );
        btnPagePrev = mView.findViewById( R.id.page_number_prev );
        btnPagePrev.setOnClickListener( onClick );
        btnPageNext = mView.findViewById( R.id.page_number_next );
        btnPageNext.setOnClickListener( onClick );
        RelativeLayout listBody = mView.findViewById( R.id.songlist_body );
        myLtViewPager = new MyLtViewPager( mContext,listBody );
        //加入页面内容
        viewPagerAdapter = new DownloadListViewPagerAdapter(mContext, myLtViewPager);
        myLtViewPager.setAdapter( viewPagerAdapter );
        myLtViewPager.setOnPageChangeListener( pageChangeListener );

        ImageView imgWechatQrcode = mView.findViewById( R.id.image_keyboard_qrcode );
        QrcodeUtil.loadWechartQrcode(imgWechatQrcode);
    }
    /**
     * 页面滑动监听
     */
    //LtViewPager.OnPageChangeListener
    LtViewPager.OnPageChangeListener pageChangeListener = new LtViewPager.OnPageChangeListener() {

        @Override
        public void onTotalPageNumberChange(LtViewPager viewPager,
                                            int iCurrentPageNumber, int iTotalPageNumber) {
            if(fragmentParams!=null){
                fragmentParams.songListInfo.curPage = iCurrentPageNumber+1;
                fragmentParams.songListInfo.totalPage = iTotalPageNumber;
                updatePageNumberView();
            }
        }

        @Override
        public void onScrollComplete(LtViewPager viewPager, int iPageNumber) {
        }

        @Override
        public void onCurrentPageNumberChange(LtViewPager viewPager, int pageNumber) {
            if(fragmentParams!=null){
                fragmentParams.songListInfo.curPage = pageNumber+1;
                updatePageNumberView();
            }
        }

        @Override
        public void onPageDirectionChange(boolean mbDragLeft,
                                          boolean mbDragRight, boolean mbDragDown, boolean mbDragUp) {
        }
    };
    /**
     * 设置页数，进度条数
     */
    @SuppressLint("SetTextI18n")
    private void updatePageNumberView() {
        textPageNumber.setText(fragmentParams.songListInfo.curPage + "/" + fragmentParams.songListInfo.totalPage);
        viewPagerAdapter.updateListFocus();
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getTag() == null) return;
            if(fragmentParams!=null){
                fragmentParams.viewFocus = v;
            }
            switch(v.getTag().toString()) {
                // 清空下载
                case "btn_downloadlist_clear":
                    SongPlayManager.clearDownloadSongList();
                    break;
                // 上一页
                case "page_number_prev":
                    if (fragmentParams.songListInfo.curPage > 0) {
                        fragmentParams.songListInfo.curPage --;
                        myLtViewPager.scrollToPrevPage( true );
                    }
                    break;
                // 下一页
                case "page_number_next":
                    if (fragmentParams.songListInfo.curPage < fragmentParams.songListInfo.totalPage) {
                        fragmentParams.songListInfo.curPage++;
                        myLtViewPager.scrollToNextPage( true );
                    }
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

        if(fragmentParams.pageIndex == EventBusConstants.PAGE_GOTO_YINGSHIJINQU){
            viewPagerAdapter.showTagOrNote(false);
        }else{
            viewPagerAdapter.showTagOrNote(true);
        }

        refreshButtonStatus();

        ResManager.getInstance().setText( textSongListInfoTitle,fragmentParams.songListInfo.titleId );
        Glide.with(mContext)
                .load(fragmentParams.songListInfo.thumbnailId)
                .apply(new RequestOptions().centerCrop().circleCrop().override( Constants.SONG_INFO_THUMB_SIZE ))
                .into(imageSongListInfoThumb);
        if(fragmentParams.viewFocus==null) fragmentParams.viewFocus = btnPageNext;
        MyViewManager.getInstance().requestFocus( fragmentParams.viewFocus );

        if(fragmentParams.songListInfo!=null){
            searchSongData(!fragmentParams.isPageBack);
        }
    }
    private void searchSongData(boolean isResetPageNumber){
        isSearching = true;
        fragmentParams.songListInfo.totalSong = SongPlayManager.getDownloadSongCount();
        if(fragmentParams.songListInfo.totalSong > 0){
            fragmentParams.songListInfo.totalPage = fragmentParams.songListInfo.totalSong <
                    Constants.SONG_LIST_LIMIT ? fragmentParams.songListInfo.totalPage = 1 :
                    (fragmentParams.songListInfo.totalSong % Constants.SONG_LIST_LIMIT) == 0 ?
                            (fragmentParams.songListInfo.totalSong / Constants.SONG_LIST_LIMIT) :
                            (fragmentParams.songListInfo.totalSong / Constants.SONG_LIST_LIMIT) + 1;// 算出总共页数
            viewNoResult.setVisibility( View.GONE );
        }else{
            myLtViewPager.setVisibility( View.GONE );
            viewNoResult.setVisibility( View.VISIBLE );
            textPageNumber.setText( "1/1" );
            return;
        }

        viewPagerAdapter.setPageCount( fragmentParams.songListInfo.totalPage );
        myLtViewPager.refreshPage( isResetPageNumber );

        updatePageNumberView();

        if(myLtViewPager.getVisibility() != View.VISIBLE){
            myLtViewPager.setVisibility( View.VISIBLE );
        }

        isSearching = false;
    }
    private void refreshButtonStatus(){
        if(SongPlayManager.getDownloadSongCount()>0){
            btnClear.setAlpha( 1 );
            btnClear.setEnabled( true );
            btnClear.setFocusable( true );
            btnClear.setFocusableInTouchMode( true );
        }else{
            btnClear.setAlpha( (float) 0.6 );
            btnClear.setEnabled( false );
            btnClear.setFocusable( true );
            btnClear.setFocusableInTouchMode( true );

            if(btnClear.hasFocus()){
                MyViewManager.getInstance().requestFocus( btnPageNext );
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusMessageSongDownload msg){
        switch (msg.what){
            case EventBusConstants.SONG_DOWNLOAD_REFRESH:
                sendSongDownloadNumberChange();
                if(MyViewManager.getInstance().isCurrentFragment( this ) && fragmentParams.songListInfo!=null){
                    searchSongData(false);
                    refreshButtonStatus();
                    viewPagerAdapter.refreshListProgress(msg.songid,msg.progress);
                }
                break;
            case EventBusConstants.SONG_DOWNLOAD_SUCCESS:
                sendSongDownloadNumberChange();
                if(MyViewManager.getInstance().isCurrentFragment( this ) && fragmentParams.songListInfo!=null){
                    searchSongData(false);
                    refreshButtonStatus();
                    viewPagerAdapter.refreshListProgress(msg.songid,msg.progress);
                }
                break;
            case EventBusConstants.SONG_DOWNLOAD_FAILURE:
                sendSongDownloadNumberChange();
                if(MyViewManager.getInstance().isCurrentFragment( this ) && fragmentParams.songListInfo!=null){
                    searchSongData(false);
                    viewPagerAdapter.refreshListProgress(msg.songid,msg.progress);
                }
                break;
            case EventBusConstants.SONG_DOWNLOAD_PROGRESS:
                if(MyViewManager.getInstance().isCurrentFragment( this ) && fragmentParams.songListInfo!=null){
                    viewPagerAdapter.refreshListProgress(msg.songid,msg.progress);
                }
                break;
        }
    }

    private void sendSongDownloadNumberChange(){
        EventBusMessage msg = new EventBusMessage();
        msg.what = EventBusConstants.SONG_DOWNLOAD_NUMBER_CHANGE;
        msg.obj = ""+SongPlayManager.getDownloadSongCount();
        EventBusManager.sendMessage( msg );
        ControlCenter.sendRefreshSongInfo();
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
        return true;
    }

    @Override
    public float getSizeInDp() {
        return 0;
    }
}

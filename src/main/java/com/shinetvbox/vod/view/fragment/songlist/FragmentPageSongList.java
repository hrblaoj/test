package com.shinetvbox.vod.view.fragment.songlist;

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
import com.shinetvbox.vod.data.constants.Constants;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessageKeyboard;
import com.shinetvbox.vod.data.eventbus.EventBusMessageSong;
import com.shinetvbox.vod.data.params.FragmentParams;
import com.shinetvbox.vod.db.Query;
import com.shinetvbox.vod.manager.DatabaseManager;
import com.shinetvbox.vod.manager.EventBusManager;
import com.shinetvbox.vod.manager.MyViewManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.utils.QrcodeUtil;
import com.shinetvbox.vod.view.custom.MyFragment;
import com.shinetvbox.vod.view.custom.viewpager.LtViewPager;
import com.shinetvbox.vod.view.custom.viewpager.MyLtViewPager;
import com.shinetvbox.vod.view.fragment.keyboard.FragmentKeyboard;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FragmentPageSongList extends MyFragment {

    private Context mContext = null;
    private View mView = null;

    private MyLtViewPager myLtViewPager = null;
    private SongListViewPagerAdapter viewPagerAdapter = null;
    private FragmentParams fragmentParams = null;

    private View viewSongListInfo = null;
    private View viewNoResult = null;
    private TextView textSongListInfoTitle = null;
    private TextView textSongListInfoHint = null;
    private TextView textSongListInfoHintContent = null;
    private ImageView imageSongListInfoThumb = null;
    private TextView textPageNumber = null;
    private Button btnPagePrev = null;
    private Button btnPageNext = null;

    private boolean isSearching = false;
    protected Query mTempQuery = null;

    public FragmentPageSongList() {
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
        mView = inflater.inflate( R.layout.fragment_page_songlist, container, false );
        initView();
        return mView;
    }

    private void initView() {
        if(mView == null) return;
        ResManager.getInstance().register( mView );
        EventBus.getDefault().register( this );
        viewSongListInfo = mView.findViewById( R.id.layout_songlist_info );
        viewNoResult = mView.findViewById( R.id.layout_songlist_noresult );
        textSongListInfoTitle = mView.findViewById( R.id.text_songlist_title );
        textSongListInfoHint = mView.findViewById( R.id.text_songlist_hint );
        textSongListInfoHintContent = mView.findViewById( R.id.text_songlist_hint_content );
        imageSongListInfoThumb = mView.findViewById( R.id.image_songlist_thumbnail );

        textPageNumber = mView.findViewById( R.id.page_number_text );
        btnPagePrev = mView.findViewById( R.id.page_number_prev );
        btnPagePrev.setOnClickListener( onClick );
        btnPageNext = mView.findViewById( R.id.page_number_next );
        btnPageNext.setOnClickListener( onClick );
        RelativeLayout listBody = mView.findViewById( R.id.songlist_body );
        myLtViewPager = new MyLtViewPager( mContext,listBody );
        //加入页面内容
        viewPagerAdapter = new SongListViewPagerAdapter(mContext, myLtViewPager);
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
            if(v.getTag() == null || fragmentParams == null) return;
            switch(v.getTag().toString()) {
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
    private FragmentKeyboard.OnKeyboardListener keyboardListener = new FragmentKeyboard.OnKeyboardListener() {
        @Override
        public void onClick(String inputText) {
            getKeyboardInfo(inputText);
        }
    };
    private void getKeyboardInfo(String inputText){
        if(isSearching) return;
        if(fragmentParams.keyboard.isShow){
            if(fragmentParams.songListInfo.query!=null){
                fragmentParams.songListInfo.query.spell_first_letter_abbreviation = inputText;
                searchSongData(true);
            }
        }
    }
    @Override
    public void setFragmentParams(FragmentParams param) {
        if(mView==null) return;
        if(param != null){
            fragmentParams = param;
        }else{
            fragmentParams = new FragmentParams();
        }

        textPageNumber.setText("1/1");

        //是否是影视金曲
        if(fragmentParams.pageIndex == EventBusConstants.PAGE_GOTO_YINGSHIJINQU){
            viewPagerAdapter.showTagOrNote(false);
        }else{
            viewPagerAdapter.showTagOrNote(true);
        }
        if(fragmentParams.keyboard.isShow){
            viewSongListInfo.setVisibility( View.GONE );
            EventBusMessageKeyboard msg = new EventBusMessageKeyboard();
            msg.what = EventBusConstants.KEYBOARD_SET_LISTENER;
            msg.onKeyboardListener = keyboardListener;
            EventBusManager.sendMessage( msg );
        }else{
            viewSongListInfo.setVisibility( View.VISIBLE );
            ResManager.getInstance().setText( textSongListInfoTitle,fragmentParams.songListInfo.titleId );
            ResManager.getInstance().setText( textSongListInfoHint,fragmentParams.songListInfo.hintId );
            Glide.with(mContext)
                    .load(fragmentParams.songListInfo.thumbnailId)
                    .apply(new RequestOptions().centerCrop().circleCrop().override( Constants.SONG_INFO_THUMB_SIZE ))
                    .into(imageSongListInfoThumb);
            if(fragmentParams.viewFocus==null) fragmentParams.viewFocus = btnPageNext;
            MyViewManager.getInstance().requestFocus( fragmentParams.viewFocus );
        }

        if(fragmentParams.songListInfo.query!=null){
            searchSongData(!fragmentParams.isPageBack);
        }
    }

    private void searchSongData(boolean isResetPageNumber){
        isSearching = true;
        fragmentParams.songListInfo.totalSong = DatabaseManager.getInstance().getSongCount(fragmentParams.songListInfo.query);
        //新歌推荐最多只保留800首歌曲
        if(fragmentParams.pageIndex == EventBusConstants.PAGE_GOTO_XINGGETUIJIAN){
            if(fragmentParams.songListInfo.totalSong>800){
                fragmentParams.songListInfo.totalSong=800;
            }
        }
        if(fragmentParams.songListInfo.totalSong > 0){
            mTempQuery = fragmentParams.songListInfo.query.clone();
            fragmentParams.songListInfo.totalPage = fragmentParams.songListInfo.totalSong <
                    Constants.SONG_LIST_LIMIT ? fragmentParams.songListInfo.totalPage = 1 :
                    (fragmentParams.songListInfo.totalSong % Constants.SONG_LIST_LIMIT) == 0 ?
                            (fragmentParams.songListInfo.totalSong / Constants.SONG_LIST_LIMIT) :
                            (fragmentParams.songListInfo.totalSong / Constants.SONG_LIST_LIMIT) + 1;// 算出总共页数
            viewNoResult.setVisibility( View.GONE );
        }else{
            if(mTempQuery != null) {
                fragmentParams.songListInfo.query = mTempQuery;
                mTempQuery = null;
            }
            viewNoResult.setVisibility( View.VISIBLE );
            myLtViewPager.setVisibility( View.GONE );
            textSongListInfoHintContent.setText( "" );
            textPageNumber.setText( "1/1" );
            return;
        }
        if(myLtViewPager.getVisibility()!=View.VISIBLE){
            myLtViewPager.setVisibility( View.VISIBLE );
        }
        viewPagerAdapter.setPageCount( fragmentParams.songListInfo.totalPage );
        viewPagerAdapter.setQuery( fragmentParams.songListInfo.query );
        myLtViewPager.refreshPage( isResetPageNumber );

        updatePageNumberView();

        if(fragmentParams.keyboard.isShow){
            EventBusMessageKeyboard msg = new EventBusMessageKeyboard();
            msg.what = EventBusConstants.KEYBOARD_SET_INPUTTEXT;
            msg.inputText = fragmentParams.songListInfo.query.spell_first_letter_abbreviation;
            msg.smartPinyin = DatabaseManager.getInstance().getSongSmartPinyin(fragmentParams.songListInfo.query);
            EventBusManager.sendMessage( msg );
        }else{
            ResManager.getInstance().setTextReplaceXXX( textSongListInfoHintContent,
                    fragmentParams.songListInfo.hintContentId,fragmentParams.songListInfo.totalSong+"" );
        }

        isSearching = false;
    }
    private void sendMessageClearKeyboard(){
        if(fragmentParams == null) return;
        if(fragmentParams.keyboard.isShow){
            if(fragmentParams.songListInfo.query == null) return;
            if(fragmentParams.songListInfo.query.spell_first_letter_abbreviation == null ||
                    fragmentParams.songListInfo.query.spell_first_letter_abbreviation.equals( "" )) return;
            fragmentParams.songListInfo.query.spell_first_letter_abbreviation = "";
            EventBusMessageKeyboard msg = new EventBusMessageKeyboard();
            msg.what = EventBusConstants.KEYBOARD_SET_INPUTTEXT;
            msg.inputText = fragmentParams.songListInfo.query.spell_first_letter_abbreviation;
            EventBusManager.sendMessage( msg );
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusMessageSong msg){
        switch (msg.what){
            case EventBusConstants.SONG_PLAY_CHANGE:
                if(MyViewManager.getInstance().isCurrentFragment( this ) ){
                    viewPagerAdapter.refresh();
                }
                break;
            case EventBusConstants.SONG_CLEAR_QUERY:
                if(MyViewManager.getInstance().isCurrentFragment( this ) ){
                    sendMessageClearKeyboard();
                }
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
        return true;
    }

    @Override
    public float getSizeInDp() {
        return 0;
    }
}

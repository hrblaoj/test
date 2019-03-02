package com.shinetvbox.vod.view.fragment.singerlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shinetvbox.vod.R;
import com.shinetvbox.vod.data.constants.Constants;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessageKeyboard;
import com.shinetvbox.vod.data.params.FragmentParams;
import com.shinetvbox.vod.db.SingerQuery;
import com.shinetvbox.vod.manager.DatabaseManager;
import com.shinetvbox.vod.manager.EventBusManager;
import com.shinetvbox.vod.manager.MyViewManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.view.custom.MyFragment;
import com.shinetvbox.vod.view.custom.viewpager.LtViewPager;
import com.shinetvbox.vod.view.custom.viewpager.MyLtViewPager;
import com.shinetvbox.vod.view.fragment.keyboard.FragmentKeyboard;

public class FragmentPageSingerList extends MyFragment {

    private Context mContext = null;
    private View mView = null;

    public static FragmentParams fragmentParams;

    private MyLtViewPager myLtViewPager = null;
    private SingerListLtViewAdapter viewPagerAdapter = null;
    private View viewNoResult = null;
    private TextView textPageNumber = null;
    private Button btnPagePrev = null;
    private Button btnPageNext = null;
    private int curPage = 1;
    private int totalPage = 1;

    private SingerQuery mTempQuery = null;
    private boolean isSearching = false;

    public FragmentPageSingerList() {
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
        mView = inflater.inflate( R.layout.fragment_page_singerlist, container, false );
        initView();
        return mView;
    }

    private void initView() {
        if(mView == null) return;
        ResManager.getInstance().register( mView );
        viewNoResult = mView.findViewById( R.id.layout_singerlist_noresult );
        textPageNumber = mView.findViewById( R.id.page_number_text );
        btnPagePrev = mView.findViewById( R.id.page_number_prev );
        btnPagePrev.setOnClickListener( onClick );
        btnPageNext = mView.findViewById( R.id.page_number_next );
        btnPageNext.setOnClickListener( onClick );
        RelativeLayout listBody = mView.findViewById( R.id.singerlist_body );
        myLtViewPager = new MyLtViewPager( mContext,listBody );
        //加入页面内容
        viewPagerAdapter = new SingerListLtViewAdapter(mContext, myLtViewPager);
        myLtViewPager.setAdapter( viewPagerAdapter );
        myLtViewPager.setOnPageChangeListener( pageChangeListener );
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
                fragmentParams.singerListInfo.curPage = iCurrentPageNumber+1;
                fragmentParams.singerListInfo.totalPage = iTotalPageNumber;
                updatePageNumberView();
            }
        }

        @Override
        public void onScrollComplete(LtViewPager viewPager, int iPageNumber) {
        }

        @Override
        public void onCurrentPageNumberChange(LtViewPager viewPager, int pageNumber) {
            //更新页码，页码是从
            if(fragmentParams!=null){
                fragmentParams.singerListInfo.curPage = pageNumber+1;
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
        textPageNumber.setText(fragmentParams.singerListInfo.curPage + "/" + fragmentParams.singerListInfo.totalPage);
        viewPagerAdapter.updateListFocus();
    }
    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getTag() == null || fragmentParams == null) return;
            switch(v.getTag().toString()) {
                // 上一页
                case "page_number_prev":
                    if (fragmentParams.singerListInfo.curPage > 0) {
                        fragmentParams.singerListInfo.curPage --;
                        myLtViewPager.scrollToPrevPage( true );
                    }
                    break;
                // 下一页
                case "page_number_next":
                    if (fragmentParams.singerListInfo.curPage < fragmentParams.singerListInfo.totalPage) {
                        fragmentParams.singerListInfo.curPage++;
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
            if(fragmentParams.singerListInfo.singerQuery!=null){
                fragmentParams.singerListInfo.singerQuery.spell_first_letter_abbreviation = inputText;
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
        
        myLtViewPager.setVisibility( View.INVISIBLE );
        textPageNumber.setText("1/1");

        MyViewManager.getInstance().requestFocus( fragmentParams.viewFocus );

        EventBusMessageKeyboard msg = new EventBusMessageKeyboard();
        msg.what = EventBusConstants.KEYBOARD_SET_LISTENER;
        msg.onKeyboardListener = keyboardListener;
        EventBusManager.sendMessage( msg );

        if(fragmentParams.singerListInfo.singerQuery!=null){
            searchSongData(!fragmentParams.isPageBack);
        }
    }

    private void searchSongData(boolean isResetPageNumber){
        isSearching = true;
        fragmentParams.singerListInfo.totalSong = DatabaseManager.getInstance().getSingerCount(fragmentParams.singerListInfo.singerQuery);
        if(fragmentParams.singerListInfo.totalSong > 0){
            mTempQuery = fragmentParams.singerListInfo.singerQuery.clone();
            fragmentParams.singerListInfo.totalPage = fragmentParams.singerListInfo.totalSong <
                    Constants.SINGER_LIST_LIMIT ? fragmentParams.singerListInfo.totalPage = 1 :
                    (fragmentParams.singerListInfo.totalSong % Constants.SINGER_LIST_LIMIT) == 0 ?
                            (fragmentParams.singerListInfo.totalSong / Constants.SINGER_LIST_LIMIT) :
                            (fragmentParams.singerListInfo.totalSong / Constants.SINGER_LIST_LIMIT) + 1;// 算出总共页数
            viewNoResult.setVisibility( View.GONE );
        }else{
            if(mTempQuery != null) {
                fragmentParams.singerListInfo.singerQuery = mTempQuery;
                mTempQuery = null;
            }
            viewNoResult.setVisibility( View.VISIBLE );
            myLtViewPager.setVisibility( View.GONE );
            textPageNumber.setText( "1/1" );
            return;
        }

        viewPagerAdapter.setPageCount( fragmentParams.singerListInfo.totalPage );
        viewPagerAdapter.setQuery( fragmentParams.singerListInfo.singerQuery );
        myLtViewPager.refreshPage( isResetPageNumber );

        updatePageNumberView();

        if(myLtViewPager.getVisibility() != View.VISIBLE){
            myLtViewPager.setVisibility( View.VISIBLE );
        }

        EventBusMessageKeyboard msg = new EventBusMessageKeyboard();
        msg.what = EventBusConstants.KEYBOARD_SET_INPUTTEXT;
        msg.inputText = fragmentParams.singerListInfo.singerQuery.spell_first_letter_abbreviation;
        msg.smartPinyin = DatabaseManager.getInstance().getSingerSmartPinyin(fragmentParams.singerListInfo.singerQuery);
        EventBusManager.sendMessage( msg );

        isSearching = false;
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

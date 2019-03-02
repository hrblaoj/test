package com.shinetvbox.vod.view.fragment.selectedlist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.shinetvbox.vod.R;
import com.shinetvbox.vod.data.constants.Constants;
import com.shinetvbox.vod.db.SongInfo;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.manager.SongPlayManager;
import com.shinetvbox.vod.view.custom.viewpager.LtAbsLayout;
import com.shinetvbox.vod.view.custom.viewpager.LtViewPager;
import com.shinetvbox.vod.view.custom.viewpager.MyLtViewPager;

import java.util.List;

public class SelectedListViewPagerAdapter implements LtViewPager.Adapter {
//    static final int QUERY_TAG_KEY = R.id.tag_query;
    static final int ADAPTER_TAG_KEY =  R.id.tag_adapter;

    private MyLtViewPager myLtViewPager;

    private Context mContext;

    private boolean showTagOrNote = true;

    private int mPageCount;

    public SelectedListViewPagerAdapter(Context context, MyLtViewPager ltViewPager) {
        mContext = context;
        myLtViewPager = ltViewPager;
    }
    @Override
    public LtViewPager.PageView getPage(int page, LtViewPager.PageView paramPageView, ViewGroup paramViewGroup) {

        LtViewPager.PageView localPageView;
        if (paramPageView == null) {
            localPageView = createPage(page);
//            localPageView.setTag(QUERY_TAG_KEY);
        } else {
            localPageView = paramPageView;
            updateData(localPageView,page);
        }
        return localPageView;
    }
    public LtViewPager.PageView createPage(int page){
        LtViewPager.PageView pageview = new LtViewPager.PageView(mContext);

        RelativeLayout.LayoutParams singerParam = (RelativeLayout.LayoutParams) myLtViewPager.getLayoutParams();
        LtAbsLayout.LtLayoutParams param = new LtAbsLayout.LtLayoutParams(singerParam.width, singerParam.height,0,0);
        pageview.setLayoutParams(param);

        View viewpage_item = ResManager.getInstance().getViewNoCache(R.layout.item_layout_selectedlist );
        FragmentPageSelectedListAdapter songMusicBase = new FragmentPageSelectedListAdapter(viewpage_item);

        songMusicBase.showTagOrNote(showTagOrNote);
        //更新数据
        songMusicBase.updateSongData(querySongData(page));
        pageview.setTag(ADAPTER_TAG_KEY, songMusicBase);

        pageview.addView(viewpage_item);
        return pageview;
    }
    public void showTagOrNote(boolean showTagOrNote){
        this.showTagOrNote = showTagOrNote;
        if(myLtViewPager.getCurrentPageView()!=null && myLtViewPager.getCurrentPageView().getTag(SelectedListViewPagerAdapter.ADAPTER_TAG_KEY)!=null){
            FragmentPageSelectedListAdapter pageAdapter = (FragmentPageSelectedListAdapter)
                    myLtViewPager.getCurrentPageView().getTag(SelectedListViewPagerAdapter.ADAPTER_TAG_KEY);
            pageAdapter.showTagOrNote(showTagOrNote);
        }
        if(myLtViewPager.getPrevPageView()!=null && myLtViewPager.getPrevPageView().getTag(SelectedListViewPagerAdapter.ADAPTER_TAG_KEY)!=null){
            FragmentPageSelectedListAdapter pageAdapter = (FragmentPageSelectedListAdapter)
                    myLtViewPager.getPrevPageView().getTag(SelectedListViewPagerAdapter.ADAPTER_TAG_KEY);
            pageAdapter.showTagOrNote(showTagOrNote);
        }
        if(myLtViewPager.getNextPageView()!=null && myLtViewPager.getNextPageView().getTag(SelectedListViewPagerAdapter.ADAPTER_TAG_KEY)!=null){
            FragmentPageSelectedListAdapter pageAdapter = (FragmentPageSelectedListAdapter)
                    myLtViewPager.getNextPageView().getTag(SelectedListViewPagerAdapter.ADAPTER_TAG_KEY);
            pageAdapter.showTagOrNote(showTagOrNote);
        }
    }
    public void updateListFocus(){
        if(myLtViewPager.getCurrentPageView()!=null && myLtViewPager.getCurrentPageView().getTag(SelectedListViewPagerAdapter.ADAPTER_TAG_KEY)!=null){
            FragmentPageSelectedListAdapter pageAdapter = (FragmentPageSelectedListAdapter)
                    myLtViewPager.getCurrentPageView().getTag(SelectedListViewPagerAdapter.ADAPTER_TAG_KEY);
            pageAdapter.setEnableFocus(true);
        }
        if(myLtViewPager.getPrevPageView()!=null && myLtViewPager.getPrevPageView().getTag(SelectedListViewPagerAdapter.ADAPTER_TAG_KEY)!=null){
            FragmentPageSelectedListAdapter pageAdapter = (FragmentPageSelectedListAdapter)
                    myLtViewPager.getPrevPageView().getTag(SelectedListViewPagerAdapter.ADAPTER_TAG_KEY);
            pageAdapter.setEnableFocus(false);
        }
        if(myLtViewPager.getNextPageView()!=null && myLtViewPager.getNextPageView().getTag(SelectedListViewPagerAdapter.ADAPTER_TAG_KEY)!=null){
            FragmentPageSelectedListAdapter pageAdapter = (FragmentPageSelectedListAdapter)
                    myLtViewPager.getNextPageView().getTag(SelectedListViewPagerAdapter.ADAPTER_TAG_KEY);
            pageAdapter.setEnableFocus(false);
        }
    }

    public void updateData(LtViewPager.PageView pageView, int page){
        FragmentPageSelectedListAdapter songMusicBase = (FragmentPageSelectedListAdapter)pageView.getTag(ADAPTER_TAG_KEY);
        songMusicBase.updateSongData(querySongData(page));
    }
    private List<SongInfo> querySongData(int page) {
        return SongPlayManager.getSelectSongData(page, Constants.SONG_LIST_LIMIT);
    }

    public void setPageCount(int pageCount){
        mPageCount = pageCount;
    }

    @Override
    public int getCount() {
        return mPageCount;//返回总页数
    }


    public LtViewPager.PageView GetPageNoData(LtViewPager.PageView convertView){
        return null;
    }
}

package com.shinetvbox.vod.view.fragment.sunglist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.shinetvbox.vod.R;
import com.shinetvbox.vod.data.constants.Constants;
import com.shinetvbox.vod.db.Query;
import com.shinetvbox.vod.db.SongInfo;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.manager.SongPlayManager;
import com.shinetvbox.vod.view.custom.viewpager.LtAbsLayout;
import com.shinetvbox.vod.view.custom.viewpager.LtViewPager;
import com.shinetvbox.vod.view.custom.viewpager.MyLtViewPager;
import com.shinetvbox.vod.view.fragment.songlist.FragmentPageSongListAdapter;

import java.util.List;

public class SungListViewPagerAdapter implements LtViewPager.Adapter {
//    static final int QUERY_TAG_KEY = R.id.tag_query;
    static final int ADAPTER_TAG_KEY =  R.id.tag_adapter;

    private MyLtViewPager myLtViewPager;

    private Context mContext;

    private boolean showTagOrNote = true;
    private boolean setSingerPegging = true;

    private Query mQuery;

    private int mPageCount;

    public SungListViewPagerAdapter(Context context, MyLtViewPager ltViewPager) {
        mContext = context;
        myLtViewPager = ltViewPager;
    }
    public void setQuery(Query query){
        mQuery = query;
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

        View viewpage_item = ResManager.getInstance().getViewNoCache(R.layout.item_layout_songlist);
        FragmentPageSongListAdapter songMusicBase = new FragmentPageSongListAdapter(viewpage_item);

        songMusicBase.showTagOrNote(showTagOrNote);
        songMusicBase.setSingerPegging(setSingerPegging);
        //更新数据
        songMusicBase.updateSongData(querySongData(page));
        pageview.setTag(ADAPTER_TAG_KEY, songMusicBase);

        pageview.addView(viewpage_item);
        return pageview;
    }
    public void showTagOrNote(boolean showTagOrNote){
        this.showTagOrNote = showTagOrNote;
        if(myLtViewPager.getCurrentPageView()!=null && myLtViewPager.getCurrentPageView().getTag(SungListViewPagerAdapter.ADAPTER_TAG_KEY)!=null){
            FragmentPageSongListAdapter pageAdapter = (FragmentPageSongListAdapter)
                    myLtViewPager.getCurrentPageView().getTag(SungListViewPagerAdapter.ADAPTER_TAG_KEY);
            pageAdapter.showTagOrNote(showTagOrNote);
        }
        if(myLtViewPager.getPrevPageView()!=null && myLtViewPager.getPrevPageView().getTag(SungListViewPagerAdapter.ADAPTER_TAG_KEY)!=null){
            FragmentPageSongListAdapter pageAdapter = (FragmentPageSongListAdapter)
                    myLtViewPager.getPrevPageView().getTag(SungListViewPagerAdapter.ADAPTER_TAG_KEY);
            pageAdapter.showTagOrNote(showTagOrNote);
        }
        if(myLtViewPager.getNextPageView()!=null && myLtViewPager.getNextPageView().getTag(SungListViewPagerAdapter.ADAPTER_TAG_KEY)!=null){
            FragmentPageSongListAdapter pageAdapter = (FragmentPageSongListAdapter)
                    myLtViewPager.getNextPageView().getTag(SungListViewPagerAdapter.ADAPTER_TAG_KEY);
            pageAdapter.showTagOrNote(showTagOrNote);
        }
    }

    public void setSingerPegging(boolean setSingerPegging){
        this.setSingerPegging = setSingerPegging;
        if(myLtViewPager.getCurrentPageView()!=null && myLtViewPager.getCurrentPageView().getTag(SungListViewPagerAdapter.ADAPTER_TAG_KEY)!=null){
            FragmentPageSongListAdapter pageAdapter = (FragmentPageSongListAdapter)
                    myLtViewPager.getCurrentPageView().getTag(SungListViewPagerAdapter.ADAPTER_TAG_KEY);
            pageAdapter.setSingerPegging(setSingerPegging);
        }
        if(myLtViewPager.getPrevPageView()!=null && myLtViewPager.getPrevPageView().getTag(SungListViewPagerAdapter.ADAPTER_TAG_KEY)!=null){
            FragmentPageSongListAdapter pageAdapter = (FragmentPageSongListAdapter)
                    myLtViewPager.getPrevPageView().getTag(SungListViewPagerAdapter.ADAPTER_TAG_KEY);
            pageAdapter.setSingerPegging(setSingerPegging);
        }
        if(myLtViewPager.getNextPageView()!=null && myLtViewPager.getNextPageView().getTag(SungListViewPagerAdapter.ADAPTER_TAG_KEY)!=null){
            FragmentPageSongListAdapter pageAdapter = (FragmentPageSongListAdapter)
                    myLtViewPager.getNextPageView().getTag(SungListViewPagerAdapter.ADAPTER_TAG_KEY);
            pageAdapter.setSingerPegging(setSingerPegging);
        }
    }

    public void updateListFocus(){
        if(myLtViewPager.getCurrentPageView()!=null && myLtViewPager.getCurrentPageView().getTag(SungListViewPagerAdapter.ADAPTER_TAG_KEY)!=null){
            FragmentPageSongListAdapter pageAdapter = (FragmentPageSongListAdapter)
                    myLtViewPager.getCurrentPageView().getTag(SungListViewPagerAdapter.ADAPTER_TAG_KEY);
            pageAdapter.setEnableFocus(true);
        }
        if(myLtViewPager.getPrevPageView()!=null && myLtViewPager.getPrevPageView().getTag(SungListViewPagerAdapter.ADAPTER_TAG_KEY)!=null){
            FragmentPageSongListAdapter pageAdapter = (FragmentPageSongListAdapter)
                    myLtViewPager.getPrevPageView().getTag(SungListViewPagerAdapter.ADAPTER_TAG_KEY);
            pageAdapter.setEnableFocus(false);
        }
        if(myLtViewPager.getNextPageView()!=null && myLtViewPager.getNextPageView().getTag(SungListViewPagerAdapter.ADAPTER_TAG_KEY)!=null){
            FragmentPageSongListAdapter pageAdapter = (FragmentPageSongListAdapter)
                    myLtViewPager.getNextPageView().getTag(SungListViewPagerAdapter.ADAPTER_TAG_KEY);
            pageAdapter.setEnableFocus(false);
        }
    }

    public void updateData(LtViewPager.PageView pageView, int page){
        FragmentPageSongListAdapter songMusicBase = (FragmentPageSongListAdapter)pageView.getTag(ADAPTER_TAG_KEY);
        songMusicBase.updateSongData(querySongData(page));
    }
    private List<SongInfo> querySongData(int page) {
        return SongPlayManager.getSungSongData(page, Constants.SONG_LIST_LIMIT);
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

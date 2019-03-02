package com.shinetvbox.vod.view.fragment.songlist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.shinetvbox.vod.R;
import com.shinetvbox.vod.data.constants.Constants;
import com.shinetvbox.vod.db.Query;
import com.shinetvbox.vod.db.SongInfo;
import com.shinetvbox.vod.manager.DatabaseManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.view.custom.viewpager.LtAbsLayout;
import com.shinetvbox.vod.view.custom.viewpager.LtViewPager;
import com.shinetvbox.vod.view.custom.viewpager.MyLtViewPager;

import java.util.ArrayList;
import java.util.List;

public class SongListViewPagerAdapter implements LtViewPager.Adapter {
//    static final int QUERY_TAG_KEY = R.id.tag_query;
    static final int ADAPTER_TAG_KEY =  R.id.tag_adapter;

    private MyLtViewPager myLtViewPager;

    private Context mContext;

    private boolean showTagOrNote = true;
    private boolean setSingerPegging = true;

    private Query mQuery;

    private int mPageCount;

    private List<FragmentPageSongListAdapter> listAdapter = new ArrayList<>(  );

    public SongListViewPagerAdapter(Context context, MyLtViewPager ltViewPager) {
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
        listAdapter.add( songMusicBase );
        pageview.setTag(ADAPTER_TAG_KEY, songMusicBase);
        pageview.addView(viewpage_item);
        return pageview;
    }
    public void showTagOrNote(boolean showTagOrNote){
        this.showTagOrNote = showTagOrNote;
        for(FragmentPageSongListAdapter pageAdapter:listAdapter){
            if(pageAdapter!=null){
                pageAdapter.showTagOrNote(showTagOrNote);
            }
        }
    }

    public void setSingerPegging(boolean setSingerPegging){
        this.setSingerPegging = setSingerPegging;
        for(FragmentPageSongListAdapter pageAdapter:listAdapter){
            if(pageAdapter!=null){
                pageAdapter.setSingerPegging(setSingerPegging);
            }
        }
    }

    public void updateListFocus(){
        if(myLtViewPager.getCurrentPageView()!=null && myLtViewPager.getCurrentPageView().getTag(SongListViewPagerAdapter.ADAPTER_TAG_KEY)!=null){
            FragmentPageSongListAdapter pageAdapter = (FragmentPageSongListAdapter)
                    myLtViewPager.getCurrentPageView().getTag(SongListViewPagerAdapter.ADAPTER_TAG_KEY);
            pageAdapter.setEnableFocus(true);
        }
        if(myLtViewPager.getPrevPageView()!=null && myLtViewPager.getPrevPageView().getTag(SongListViewPagerAdapter.ADAPTER_TAG_KEY)!=null){
            FragmentPageSongListAdapter pageAdapter = (FragmentPageSongListAdapter)
                    myLtViewPager.getPrevPageView().getTag(SongListViewPagerAdapter.ADAPTER_TAG_KEY);
            pageAdapter.setEnableFocus(false);
        }
        if(myLtViewPager.getNextPageView()!=null && myLtViewPager.getNextPageView().getTag(SongListViewPagerAdapter.ADAPTER_TAG_KEY)!=null){
            FragmentPageSongListAdapter pageAdapter = (FragmentPageSongListAdapter)
                    myLtViewPager.getNextPageView().getTag(SongListViewPagerAdapter.ADAPTER_TAG_KEY);
            pageAdapter.setEnableFocus(false);
        }
    }

    public void refresh(){
        for(FragmentPageSongListAdapter pageAdapter:listAdapter){
            if(pageAdapter!=null){
                pageAdapter.refresh();
            }
        }
    }

    public void updateData(LtViewPager.PageView pageView, int page){
        FragmentPageSongListAdapter songMusicBase = (FragmentPageSongListAdapter)pageView.getTag(ADAPTER_TAG_KEY);
        songMusicBase.updateSongData(querySongData(page));
    }
    private List<SongInfo> querySongData(int page) {
        if(mQuery == null) return null;

        mQuery.limit = Constants.SONG_LIST_LIMIT;
        mQuery.offset = Constants.SONG_LIST_LIMIT * page;

        return DatabaseManager.getInstance().getSongInfo( mQuery );
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

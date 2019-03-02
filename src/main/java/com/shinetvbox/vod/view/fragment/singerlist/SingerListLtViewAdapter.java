package com.shinetvbox.vod.view.fragment.singerlist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.shinetvbox.vod.R;
import com.shinetvbox.vod.data.constants.Constants;
import com.shinetvbox.vod.db.SingerInfo;
import com.shinetvbox.vod.db.SingerQuery;
import com.shinetvbox.vod.manager.DatabaseManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.view.custom.viewpager.LtAbsLayout;
import com.shinetvbox.vod.view.custom.viewpager.LtViewPager;
import com.shinetvbox.vod.view.custom.viewpager.MyLtViewPager;

import java.util.List;

public class SingerListLtViewAdapter implements LtViewPager.Adapter {
//    static final int QUERY_TAG_KEY = R.id.tag_query;
    private static final int ADAPTER_TAG_KEY =  R.id.tag_adapter;

    private MyLtViewPager myLtViewPager;

    private Context mContext;

    private int mPageCount;

    private SingerQuery singerQuery;

    public SingerListLtViewAdapter(Context context, MyLtViewPager ltViewPager) {
        mContext = context;
        myLtViewPager = ltViewPager;
    }
    public void setQuery(SingerQuery query){
        singerQuery = query;
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

        View viewpage_item = ResManager.getInstance().getViewNoCache(R.layout.item_layout_singer_list );
        FragmentPageSingerListAdapter songMusicBase = new FragmentPageSingerListAdapter(viewpage_item);

        //更新数据
        songMusicBase.updateSongData( querySingerData(page));
        pageview.setTag(ADAPTER_TAG_KEY, songMusicBase);

        pageview.addView(viewpage_item);
        return pageview;
    }

    public void updateData(LtViewPager.PageView pageView, int page){
        FragmentPageSingerListAdapter songMusicBase = (FragmentPageSingerListAdapter)pageView.getTag(ADAPTER_TAG_KEY);
        songMusicBase.updateSongData( querySingerData(page));
    }

    public void updateListFocus(){
        if(myLtViewPager.getCurrentPageView()!=null && myLtViewPager.getCurrentPageView().getTag(SingerListLtViewAdapter.ADAPTER_TAG_KEY)!=null){
            FragmentPageSingerListAdapter pageAdapter = (FragmentPageSingerListAdapter)
                    myLtViewPager.getCurrentPageView().getTag(SingerListLtViewAdapter.ADAPTER_TAG_KEY);
            pageAdapter.setEnableFocus(true);
        }
        if(myLtViewPager.getPrevPageView()!=null && myLtViewPager.getPrevPageView().getTag(SingerListLtViewAdapter.ADAPTER_TAG_KEY)!=null){
            FragmentPageSingerListAdapter pageAdapter = (FragmentPageSingerListAdapter)
                    myLtViewPager.getPrevPageView().getTag(SingerListLtViewAdapter.ADAPTER_TAG_KEY);
            pageAdapter.setEnableFocus(false);
        }
        if(myLtViewPager.getNextPageView()!=null && myLtViewPager.getNextPageView().getTag(SingerListLtViewAdapter.ADAPTER_TAG_KEY)!=null){
            FragmentPageSingerListAdapter pageAdapter = (FragmentPageSingerListAdapter)
                    myLtViewPager.getNextPageView().getTag(SingerListLtViewAdapter.ADAPTER_TAG_KEY);
            pageAdapter.setEnableFocus(false);
        }
    }

    private List<SingerInfo> querySingerData(int page) {
        if(singerQuery==null) return null;

        singerQuery.limit = Constants.SINGER_LIST_LIMIT;
        singerQuery.offset = Constants.SINGER_LIST_LIMIT * page;

        return DatabaseManager.getInstance().getSingerInfo(singerQuery);
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

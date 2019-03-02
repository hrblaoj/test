package com.shinetvbox.vod.view.fragment.home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.shinetvbox.vod.view.custom.MyFragment;

import java.util.List;

public class MyFragmentAdapter extends FragmentStatePagerAdapter {
    //存放fragment的集合
    private List<MyFragment> mFragments;

    public MyFragmentAdapter(FragmentManager fm , List<MyFragment> fragmentList) {
        super( fm );
        mFragments = fragmentList;
    }
    @Override
    public Fragment getItem(int position) {
        return mFragments.get( position );
    }

    @Override
    public void startUpdate(ViewGroup container) {
        super.startUpdate( container );
    }
    @Override
    public int getCount() {
        return mFragments.size();
    }

}

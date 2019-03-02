package com.shinetvbox.vod.view.custom;

import android.support.v4.app.Fragment;

import com.shinetvbox.vod.data.params.FragmentParams;
import com.shinetvbox.vod.view.fragment.keyboard.FragmentKeyboard;

import me.jessyan.autosize.internal.CustomAdapt;

public abstract class MyFragment extends Fragment implements CustomAdapt {
    public abstract void setFragmentParams(FragmentParams param);
    public abstract FragmentParams getFragmentParams();
}

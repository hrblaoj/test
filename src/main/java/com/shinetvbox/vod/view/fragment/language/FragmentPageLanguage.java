package com.shinetvbox.vod.view.fragment.language;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shinetvbox.vod.MainActivity;
import com.shinetvbox.vod.R;
import com.shinetvbox.vod.data.params.FragmentParams;
import com.shinetvbox.vod.manager.LanguageManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.view.custom.MyFragment;
import com.shinetvbox.vod.view.custom.MyRecyclerView;
import com.shinetvbox.vod.view.fragment.language.FragmentPageLanguageAdapter.DataLanguage;

import java.util.ArrayList;
import java.util.List;

public class FragmentPageLanguage extends MyFragment {

    private Context mContext = null;
    private MainActivity activity = null;
    private View mView = null;

    private MyRecyclerView recyclerView;
    private FragmentPageLanguageAdapter myAdapter;
    private List<DataLanguage> listData = new ArrayList<>(  );

    public static FragmentParams fragmentParams;

    public FragmentPageLanguage() {
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
        mView = inflater.inflate( R.layout.fragment_page_language, container, false );

        activity = (MainActivity) getActivity();
        initView();
        return mView;
    }

    private void initView() {
        if(mView == null) return;
        ResManager.getInstance().register( mView );
        getData();
        final int spanCount = 5;
        recyclerView = mView.findViewById( R.id.recyclerview_type_language );
        final GridLayoutManager gridLayoutManager = new GridLayoutManager( mContext,spanCount );
        gridLayoutManager.setOrientation( GridLayoutManager.VERTICAL );
        recyclerView.setLayoutManager(gridLayoutManager);
        myAdapter = new FragmentPageLanguageAdapter( mContext );
        myAdapter.init( listData,true );
        recyclerView.setAdapter( myAdapter );
        recyclerView.addItemDecoration( new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets( outRect, view, parent, state );
                int position = parent.getChildAdapterPosition(view);
                outRect.left = -16*(position%spanCount);
                if(position > (spanCount - 1)){
                    outRect.top = -84;
                }
            }
        } );
    }

    private void getData(){
        if(listData.size()>0) return;

        DataLanguage data = null;
        if(LanguageManager.isSupportLanaguage(LanguageManager.ZH_CN)){
            data = new DataLanguage();
            data.tag ="0";
            data.language = LanguageManager.ZH_CN;
            data.stringId = R.string.language_switch_zhongwenjianti;
            data.thumbId = R.drawable.image_language_switch_zhongwenjianti;
            listData.add( data );
        }
        if(LanguageManager.isSupportLanaguage(LanguageManager.ZH_CNF)){
            data = new DataLanguage();
            data.tag ="1";
            data.language = LanguageManager.ZH_CNF;
            data.stringId = R.string.language_switch_zhongwenfanti;
            data.thumbId = R.drawable.image_language_switch_zhongwenfanti;
            listData.add( data );
        }
        if(LanguageManager.isSupportLanaguage(LanguageManager.EN_US)){
            data = new DataLanguage();
            data.tag ="2";
            data.language = LanguageManager.EN_US;
            data.stringId = R.string.language_switch_yingyu;
            data.thumbId = R.drawable.image_language_switch_yingyu;
            listData.add( data );
        }
        if(LanguageManager.isSupportLanaguage(LanguageManager.JA_JP)){
            data = new DataLanguage();
            data.tag ="3";
            data.language = LanguageManager.JA_JP;
            data.stringId = R.string.language_switch_riyu;
            data.thumbId = R.drawable.image_language_switch_riyu;
            listData.add( data );
        }
        if(LanguageManager.isSupportLanaguage(LanguageManager.KO_KR)){
            data = new DataLanguage();
            data.tag ="4";
            data.language = LanguageManager.KO_KR;
            data.stringId = R.string.language_switch_hanyu;
            data.thumbId = R.drawable.image_language_switch_hanyu;
            listData.add( data );
        }
        if(LanguageManager.isSupportLanaguage(LanguageManager.KM_KH)){
            data = new DataLanguage();
            data.tag ="5";
            data.language = LanguageManager.KM_KH;
            data.stringId = R.string.language_switch_jianpuzhai;
            data.thumbId = R.drawable.image_language_switch_jianpuzhai;
            listData.add( data );
        }
        if(LanguageManager.isSupportLanaguage(LanguageManager.MY_MM)){
            data = new DataLanguage();
            data.tag ="6";
            data.language = LanguageManager.MY_MM;
            data.stringId = R.string.language_switch_miandian;
            data.thumbId = R.drawable.image_language_switch_miandian;
            listData.add( data );
        }
        if(LanguageManager.isSupportLanaguage(LanguageManager.IN_ID)){
            data = new DataLanguage();
            data.tag ="7";
            data.language = LanguageManager.IN_ID;
            data.stringId = R.string.language_switch_yinniyu;
            data.thumbId = R.drawable.image_language_switch_yinniyu;
            listData.add( data );
        }
        if(LanguageManager.isSupportLanaguage(LanguageManager.TH_TH)){
            data = new DataLanguage();
            data.tag ="8";
            data.language = LanguageManager.TH_TH;
            data.stringId = R.string.language_switch_taiyu;
            data.thumbId = R.drawable.image_language_switch_taiyu;
            listData.add( data );
        }
        if(LanguageManager.isSupportLanaguage(LanguageManager.VI_VN)){
            data = new DataLanguage();
            data.tag ="9";
            data.language = LanguageManager.VI_VN;
            data.stringId = R.string.language_switch_yuenanyu;
            data.thumbId = R.drawable.image_language_switch_yuenanyu;
            listData.add( data );
        }
    }

    public void restFocus(){
        if(mView==null) return;
        myAdapter.restFocus();
    }
    @Override
    public void setFragmentParams(FragmentParams param) {
        if(mView==null) return;
        if(param != null){
            fragmentParams = param;
        }else{
            fragmentParams = new FragmentParams();
        }
        restFocus();
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
        return false;
    }

    @Override
    public float getSizeInDp() {
        return 0;
    }
}

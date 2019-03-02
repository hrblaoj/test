package com.shinetvbox.vod.view.fragment.paihangbang;

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
import com.shinetvbox.vod.manager.MyViewManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.view.custom.MyFragment;
import com.shinetvbox.vod.view.custom.MyRecyclerView;
import com.shinetvbox.vod.view.fragment.paihangbang.FragmentPagePaihangbangAdapter.Datapaihangbang;

import java.util.ArrayList;
import java.util.List;

public class FragmentPagePaihangbang extends MyFragment {

    private Context mContext = null;
    private MainActivity activity = null;
    private View mView = null;

    public static FragmentParams fragmentParams;

    private MyRecyclerView recyclerView;
    private FragmentPagePaihangbangAdapter myAdapter;
    private List<Datapaihangbang> listData = new ArrayList<>(  );

    public FragmentPagePaihangbang() {
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
        mView = inflater.inflate( R.layout.fragment_page_paihangbang, container, false );

        activity = (MainActivity) getActivity();
        initView();
        return mView;
    }

    private void initView() {
        if(mView == null) return;
        ResManager.getInstance().register( mView );
        getData();
        final int spanCount = 1;
        recyclerView = mView.findViewById( R.id.recyclerview_paihangbang );
        final GridLayoutManager gridLayoutManager = new GridLayoutManager( mContext,spanCount );
        gridLayoutManager.setOrientation( GridLayoutManager.HORIZONTAL );
        recyclerView.setLayoutManager(gridLayoutManager);
        myAdapter = new FragmentPagePaihangbangAdapter( mContext );
        myAdapter.init( listData,true );
        recyclerView.setAdapter( myAdapter );
        recyclerView.addItemDecoration( new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets( outRect, view, parent, state );
                int position = parent.getChildAdapterPosition(view);
                if(position!=0){
                    outRect.left = -90;
                }
                outRect.top = 50;
            }
        } );
    }

    private void getData(){
        if(listData.size()>0) return;
        Datapaihangbang data = new Datapaihangbang();
        data.tag ="0";
        data.stringId = R.string.paihangbang_diangezongbang;
        data.thumbId = R.drawable.image_paihangbang_diangezongbang;
        listData.add( data );
        data = new Datapaihangbang();
        data.tag ="1";
        data.language = "1";
        data.stringId = R.string.paihangbang_guoyubang;
        data.thumbId = R.drawable.image_paihangbang_guoyubang;
        listData.add( data );
        data = new Datapaihangbang();
        data.tag ="2";
        data.language = "2";
        data.stringId = R.string.paihangbang_yueyubang;
        data.thumbId = R.drawable.image_paihangbang_yueyubang;
        listData.add( data );
        data = new Datapaihangbang();
        data.tag ="3";
        data.language = "3";
        data.stringId = R.string.paihangbang_minnanyubang;
        data.thumbId = R.drawable.image_paihangbang_minnanyubang;
        listData.add( data );
        data = new Datapaihangbang();
        data.tag ="4";
        data.language = "4";
        data.stringId = R.string.paihangbang_yingyubang;
        data.thumbId = R.drawable.image_paihangbang_yingyubang;
        listData.add( data );
    }

    @Override
    public void setFragmentParams(FragmentParams param) {
        if(mView==null) return;
        if(param != null){
            fragmentParams = param;
        }else{
            fragmentParams = new FragmentParams();
        }
        myAdapter.setPaihangbangParams( fragmentParams );

        MyViewManager.getInstance().requestFocus( fragmentParams.viewFocus );
    }

    @Override
    public FragmentParams getFragmentParams() {
        return fragmentParams;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

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

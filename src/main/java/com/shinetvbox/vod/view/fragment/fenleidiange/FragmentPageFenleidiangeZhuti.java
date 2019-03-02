package com.shinetvbox.vod.view.fragment.fenleidiange;

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
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.view.custom.MyFragment;
import com.shinetvbox.vod.view.custom.MyRecyclerView;
import com.shinetvbox.vod.view.fragment.fenleidiange.FragmentPageFenleidiangeAdapter.DataFenleidiange;

import java.util.ArrayList;
import java.util.List;

public class FragmentPageFenleidiangeZhuti extends MyFragment {

    private Context mContext = null;
    private MainActivity activity = null;
    private View mView = null;

    private MyRecyclerView recyclerView;
    private FragmentPageFenleidiangeAdapter myAdapter;
    private List<DataFenleidiange> listData = new ArrayList<>(  );

    public FragmentPageFenleidiangeZhuti() {
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
        mView = inflater.inflate( R.layout.fragment_page_fenleidiange_zhuti, container, false );

        activity = (MainActivity) getActivity();
        initView();
        return mView;
    }

    private void initView() {
        if(mView == null) return;
        ResManager.getInstance().register( mView );
        getData();
        final int spanCount = 4;
        recyclerView = mView.findViewById( R.id.recyclerview_type_zhuti );
        final GridLayoutManager gridLayoutManager = new GridLayoutManager( mContext,spanCount );
        gridLayoutManager.setOrientation( GridLayoutManager.VERTICAL );
        recyclerView.setLayoutManager(gridLayoutManager);
        myAdapter = new FragmentPageFenleidiangeAdapter( mContext );
        myAdapter.init( listData,true );
        recyclerView.setAdapter( myAdapter );
        recyclerView.addItemDecoration( new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets( outRect, view, parent, state );
                int position = parent.getChildAdapterPosition(view);
                outRect.left = -24*(position%spanCount);
                if(position > (spanCount - 1)){
                    outRect.top = -84;
                }
            }
        } );
    }

    private void getData(){
        if(listData.size()>0) return;
        DataFenleidiange data = new DataFenleidiange();
        data.tag ="0";
        data.song_theme ="4";
        data.stringId = R.string.fenleidiange_type_zhuti_shengrizhufu;
        data.thumbId = R.drawable.image_fenleidiange_zhuti_shengrizhufu;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="1";
        data.song_theme ="5";
        data.stringId = R.string.fenleidiange_type_zhuti_tianmiduichang;
        data.thumbId = R.drawable.image_fenleidiange_zhuti_tianmiduichang;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="2";
        data.song_theme ="14";
        data.stringId = R.string.fenleidiange_type_zhuti_jindianhongge;
        data.thumbId = R.drawable.image_fenleidiange_zhuti_jindianhongge;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="3";
        data.song_theme ="7";
        data.stringId = R.string.fenleidiange_type_zhuti_kuailetongnian;
        data.thumbId = R.drawable.image_fenleidiange_zhuti_kuailetongnian;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="4";
        data.song_theme ="3";
        data.stringId = R.string.fenleidiange_type_zhuti_liaoliangjunge;
        data.thumbId = R.drawable.image_fenleidiange_zhuti_liangliangjunge;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="5";
        data.song_theme ="1";
        data.stringId = R.string.fenleidiange_type_zhuti_jingdiandiqu;
        data.thumbId = R.drawable.image_fenleidiange_zhuti_jingdiandiqu;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="6";
        data.song_theme ="2";
        data.stringId = R.string.fenleidiange_type_zhuti_nongqingwuqu;
        data.thumbId = R.drawable.image_fenleidiange_zhuti_nongqingwuqu;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="7";
        data.song_theme ="17";
        data.stringId = R.string.fenleidiange_type_zhuti_difangminge;
        data.thumbId = R.drawable.image_fenleidiange_zhuti_difangminge;
        listData.add( data );
//        data = new DataFenleidiange();
//        data.tag ="8";
//        data.song_theme ="0";
//        data.stringId = R.string.fenleidiange_type_zhuti_douyinrege;
//        data.thumbId = R.drawable.image_fenleidiange_zhuti_douyinrege;
//        listData.add( data );
    }

    public void restFocus(){
        if(mView==null) return;
        myAdapter.restFocus();
    }

    @Override
    public void setFragmentParams(FragmentParams param) {
        if(mView==null) return;
    }

    @Override
    public FragmentParams getFragmentParams() {
        return null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(hidden){

        }else{
//            final Button btn = mView.findViewById( R.id.fenleidiange_btn_type_xiqu );
//            btn.requestFocus();
        }
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

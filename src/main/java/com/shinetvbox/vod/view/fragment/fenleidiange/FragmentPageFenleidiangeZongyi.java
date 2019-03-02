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

public class FragmentPageFenleidiangeZongyi extends MyFragment {

    private Context mContext = null;
    private MainActivity activity = null;
    private View mView = null;

    private MyRecyclerView recyclerView;
    private FragmentPageFenleidiangeAdapter myAdapter;
    private List<DataFenleidiange> listData = new ArrayList<>(  );

    public FragmentPageFenleidiangeZongyi() {
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
        mView = inflater.inflate( R.layout.fragment_page_fenleidiange_zongyi, container, false );

        activity = (MainActivity) getActivity();
        initView();
        return mView;
    }

    private void initView() {
        if(mView == null) return;
        ResManager.getInstance().register( mView );
        getData();
        final int spanCount = 4;
        recyclerView = mView.findViewById( R.id.recyclerview_type_zongyi );
        final GridLayoutManager gridLayoutManager = new GridLayoutManager( mContext,spanCount );
        gridLayoutManager.setOrientation( GridLayoutManager.VERTICAL );
        recyclerView.setLayoutManager(gridLayoutManager);
        myAdapter = new FragmentPageFenleidiangeAdapter( mContext );
        myAdapter.init( listData,false );
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
        data.new_song_theme ="20";
        data.stringId = R.string.fenleidiange_type_zongyi_woshigeshou;
        data.thumbId = R.drawable.image_fenleidiange_zongyi_woshigeshou;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="1";
        data.new_song_theme ="39";
        data.stringId = R.string.fenleidiange_type_zongyi_chunwan;
        data.thumbId = R.drawable.image_fenleidiange_zongyi_chunwan;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="2";
        data.new_song_theme ="52";
        data.stringId = R.string.fenleidiange_type_zongyi_zhongguoxingesheng;
        data.thumbId = R.drawable.image_fenleidiange_zongyi_zhongguoxingeshen;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="3";
        data.new_song_theme ="40";
        data.stringId = R.string.fenleidiange_type_zongyi_zhongguozhixing;
        data.thumbId = R.drawable.image_fenleidiange_zongyi_zhongguozhixing;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="4";
        data.new_song_theme ="35";
        data.stringId = R.string.fenleidiange_type_zongyi_mengmiangewang;
        data.thumbId = R.drawable.image_fenleidiange_zongyi_mengmiangewang;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="5";
        data.new_song_theme ="50";
        data.stringId = R.string.fenleidiange_type_zongyi_kuajiegewang;
        data.thumbId = R.drawable.image_fenleidiange_zongyi_kuajiegewang;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="6";
        data.new_song_theme ="19";
        data.stringId = R.string.fenleidiange_type_zongyi_zhongguomengzhisheng;
        data.thumbId = R.drawable.image_fenleidiange_zongyi_zhongguomengzhisheng;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="7";
        data.new_song_theme ="36";
        data.stringId = R.string.fenleidiange_type_zongyi_zhongguoxinshengdai;
        data.thumbId = R.drawable.image_fenleidiange_zongyi_zhongguoxinshengdai;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="8";
        data.new_song_theme ="21";
        data.stringId = R.string.fenleidiange_type_zongyi_zuimeihesheng;
        data.thumbId = R.drawable.image_fenleidiange_zongyi_zuimeihesheng;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="9";
        data.new_song_theme ="26";
        data.stringId = R.string.fenleidiange_type_zongyi_zhongguohaogequ;
        data.thumbId = R.drawable.image_fenleidiange_zongyi_zhongguohaogequ;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="10";
        data.new_song_theme ="54";
        data.stringId = R.string.fenleidiange_type_zongyi_tianlaizhizhan;
        data.thumbId = R.drawable.image_fenleidiange_zongyi_tianlaizhizhan;
        listData.add( data );
//        data = new DataFenleidiange();
//        data.tag ="11";
//        data.new_song_theme ="";
//        data.stringId = R.string.fenleidiange_type_zongyi_chuangzao101;
//        data.thumbId = R.drawable.image_fenleidiange_zongyi_chuangzao101;
//        listData.add( data );
    }

    @Override
    public void setFragmentParams(FragmentParams param) {
        if(mView==null) return;
    }

    public void restFocus(){
        if(mView==null) return;
        myAdapter.restFocus();
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

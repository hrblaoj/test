package com.shinetvbox.vod.view.fragment.fenleidiange;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.shinetvbox.vod.MainActivity;
import com.shinetvbox.vod.R;
import com.shinetvbox.vod.data.params.FragmentParams;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.view.custom.MyFragment;
import com.shinetvbox.vod.view.custom.MyRecyclerView;
import com.shinetvbox.vod.view.fragment.fenleidiange.FragmentPageFenleidiangeAdapter.DataFenleidiange;

import java.util.ArrayList;
import java.util.List;

public class FragmentPageFenleidiangeYuzhong extends MyFragment {

    private Context mContext = null;
    private MainActivity activity = null;
    private View mView = null;

    private MyRecyclerView recyclerView;
    private FragmentPageFenleidiangeAdapter myAdapter;
    private List<DataFenleidiange> listData = new ArrayList<>(  );

    public FragmentPageFenleidiangeYuzhong() {
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
        mView = inflater.inflate( R.layout.fragment_page_fenleidiange_yuzhong, container, false );

        activity = (MainActivity) getActivity();
        initView();
        return mView;
    }

    private void initView() {
        if(mView == null) return;
        ResManager.getInstance().register( mView );
        getData();
        final int spanCount = 4;
        recyclerView = mView.findViewById( R.id.recyclerview_type_yuzhong );
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
        data.language = "";
        data.stringId = R.string.fenleidiange_type_yuzhong_quanbuyuzhong;
        data.thumbId = R.drawable.image_fenleidiange_yuzhong_quanbuyuzhong;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="1";
        data.language = "1";
        data.stringId = R.string.fenleidiange_type_yuzhong_guoyu;
        data.thumbId = R.drawable.image_fenleidiange_yuzhong_guyu;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="2";
        data.language = "2";
        data.stringId = R.string.fenleidiange_type_yuzhong_yueyu;
        data.thumbId = R.drawable.image_fenleidiange_yuzhong_yueyu;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="3";
        data.language = "3";
        data.stringId = R.string.fenleidiange_type_yuzhong_minnanyu;
        data.thumbId = R.drawable.image_fenleidiange_yuzhong_minnanyu;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="4";
        data.language = "4";
        data.stringId = R.string.fenleidiange_type_yuzhong_yingyu;
        data.thumbId = R.drawable.image_fenleidiange_yuzhong_yingyu;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="5";
        data.language = "5";
        data.stringId = R.string.fenleidiange_type_yuzhong_hanyu;
        data.thumbId = R.drawable.image_fenleidiange_yuzhong_hanyu;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="6";
        data.language = "6";
        data.stringId = R.string.fenleidiange_type_yuzhong_riyu;
        data.thumbId = R.drawable.image_fenleidiange_yuzhong_riyu;
        listData.add( data );
        data = new DataFenleidiange();
        data.tag ="7";
        data.language = "39";
        data.stringId = R.string.fenleidiange_type_yuzhong_qitaiyuzhong;
        data.thumbId = R.drawable.image_fenleidiange_yuzhong_qitayuzhong;
        listData.add( data );
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

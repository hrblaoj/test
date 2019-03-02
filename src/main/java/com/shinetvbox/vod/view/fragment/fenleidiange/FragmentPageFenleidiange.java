package com.shinetvbox.vod.view.fragment.fenleidiange;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.shinetvbox.vod.MainActivity;
import com.shinetvbox.vod.R;
import com.shinetvbox.vod.data.params.FragmentParams;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.view.custom.MyFragment;
import com.shinetvbox.vod.view.fragment.home.MyFragmentAdapter;
import com.shinetvbox.vod.view.fragment.home.MyViewPager;

import java.util.ArrayList;
import java.util.List;

public class FragmentPageFenleidiange extends MyFragment {

    private Context mContext = null;
    private MainActivity activity = null;
    private View mView = null;

    public static FragmentParams fragmentParams;

    private MyViewPager viewPager;
    private MyFragmentAdapter fragmentAdapter;
    private List<MyFragment> listFragment;
    private FragmentManager fragmentManager;

    private FragmentPageFenleidiangeYuzhong fragmentYuzhong;
    private FragmentPageFenleidiangeZhuti fragmentZhuti;
    private FragmentPageFenleidiangeZongyi fragmentZongyi;
    private FragmentPageFenleidiangeXiqu fragmentXiqu;

    private List<Button> btnList = new ArrayList<>(  );
    private Button btn_yuzhong;
    private Button btn_zhuti;
    private Button btn_zongyi;
    private Button btn_xiqu;

    public FragmentPageFenleidiange() {
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
        mView = inflater.inflate( R.layout.fragment_page_fenleidiange, container, false );

        activity = (MainActivity) getActivity();
        initView();
        return mView;
    }

    private void initView() {
        if(mView == null) return;
        ResManager.getInstance().register( mView );

        fragmentYuzhong = new FragmentPageFenleidiangeYuzhong();
        fragmentZhuti = new FragmentPageFenleidiangeZhuti();
        fragmentZongyi = new FragmentPageFenleidiangeZongyi();
        fragmentXiqu = new FragmentPageFenleidiangeXiqu();
        listFragment = new ArrayList<>(  );

        listFragment.add( fragmentYuzhong );
        listFragment.add( fragmentZhuti );
        listFragment.add( fragmentZongyi );
        listFragment.add( fragmentXiqu );

        viewPager = mView.findViewById( R.id.fenleidiange_viewPager_content );
        fragmentManager = activity.getSupportFragmentManager();
        fragmentAdapter = new MyFragmentAdapter( fragmentManager, listFragment );
        viewPager.setAdapter( fragmentAdapter );
        viewPager.addOnPageChangeListener( pageChangeListener );

        btn_yuzhong = mView.findViewById( R.id.fenleidiange_btn_type_yuzhong );
        btn_yuzhong.setOnFocusChangeListener( onFocusChange );
        btn_zhuti = mView.findViewById( R.id.fenleidiange_btn_type_zhuti );
        btn_zhuti.setOnFocusChangeListener( onFocusChange );
        btn_zongyi = mView.findViewById( R.id.fenleidiange_btn_type_zongyi );
        btn_zongyi.setOnFocusChangeListener( onFocusChange );
        btn_xiqu = mView.findViewById( R.id.fenleidiange_btn_type_xiqu );
        btn_xiqu.setOnFocusChangeListener( onFocusChange );

        btnList.add( btn_yuzhong );
        btnList.add( btn_zhuti );
        btnList.add( btn_zongyi );
        btnList.add( btn_xiqu );

        setBtnTitleState(0);

    }
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            setBtnTitleState(position);
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
    private void setBtnTitleState(int ind){
        for(int i=0;i<btnList.size();i++){
            if(ind == i){
                if(!btnList.get( i ).isSelected()){
                    btnList.get( i ).setSelected( true );
                }
            }else{
                if(btnList.get( i ).isSelected()){
                    btnList.get( i ).setSelected( false );
                }
            }
        }
        restFocus();
    }
    private View.OnFocusChangeListener onFocusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(v.getTag() == null) return;
            if(hasFocus){
                switch(v.getTag().toString()) {
                    case "fenleidiange_btn_type_yuzhong":
                        if(viewPager.getCurrentItem()!=0){
                            viewPager.setCurrentItem( 0 );
                        }
                        break;
                    case "fenleidiange_btn_type_zhuti":
                        if(viewPager.getCurrentItem()!=1){
                            viewPager.setCurrentItem( 1 );
                        }
                        break;
                    case "fenleidiange_btn_type_zongyi":
                        if(viewPager.getCurrentItem()!=2){
                            viewPager.setCurrentItem( 2 );
                        }
                        break;
                    case "fenleidiange_btn_type_xiqu":
                        if(viewPager.getCurrentItem()!=3){
                            viewPager.setCurrentItem( 3 );
                        }
                        break;
                }
            }
        }
    };

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
    private void restFocus(){
        switch (viewPager.getCurrentItem()){
            case 0:
                fragmentYuzhong.restFocus();
                break;
            case 1:
                fragmentZhuti.restFocus();
                break;
            case 2:
                fragmentZongyi.restFocus();
                break;
            case 3:
                fragmentXiqu.restFocus();
                break;
        }
    }
    @Override
    public FragmentParams getFragmentParams() {
        return fragmentParams;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(hidden){

        }else{

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

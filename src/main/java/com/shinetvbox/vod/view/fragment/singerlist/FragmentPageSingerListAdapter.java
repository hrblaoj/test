package com.shinetvbox.vod.view.fragment.singerlist;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shinetvbox.vod.R;
import com.shinetvbox.vod.data.constants.Constants;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.data.params.FragmentParams;
import com.shinetvbox.vod.db.Query;
import com.shinetvbox.vod.db.SingerInfo;
import com.shinetvbox.vod.manager.EventBusManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.manager.SingerImageManager;
import com.shinetvbox.vod.utils.ConversionsUtil;

import java.util.ArrayList;
import java.util.List;

public class FragmentPageSingerListAdapter {

    class ViewHold {
        public SingerInfo singerInfo = null;

        public RelativeLayout page_list_item = null;
        public TextView textSingerName = null;
        public ImageView imageSinger = null;
        public ImageView imageBorder = null;
    }

    protected List<ViewHold> mPageListItems = new ArrayList<>(  );

    public FragmentPageSingerListAdapter(View viewpage_item) {
        ResManager.getInstance().register( viewpage_item );
        for (int i = 0; i < Constants.SINGER_LIST_LIMIT; i++)
        {
            //初始各个状态
            ViewHold hold = new ViewHold();

            RelativeLayout page_list_item = viewpage_item.findViewById( ResManager.getInstance().getId("singerlist_item"+i) );
            page_list_item.setTag( ""+i );
            hold.page_list_item = page_list_item;
            hold.textSingerName = page_list_item.findViewById( ResManager.getInstance().getId("item_layout_text_singername") );
            hold.imageSinger = page_list_item.findViewById( ResManager.getInstance().getId("item_layout_image_singer") );
            hold.imageBorder = page_list_item.findViewById( ResManager.getInstance().getId("image_focus") );

            hold.page_list_item.setFocusable( false );
            hold.page_list_item.setFocusableInTouchMode( false );

            hold.page_list_item.setOnFocusChangeListener( focusChange );
            hold.page_list_item.setOnClickListener(onClick);

            mPageListItems.add( hold );
        }
    }
    private View.OnFocusChangeListener focusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v.getTag() == null) return;
            setViewFocus( v, hasFocus );
        }
    };
    private void setViewFocus(View v,boolean hasFocus){
        for(int i=0;i<mPageListItems.size();i++){
            if (hasFocus){
                if(mPageListItems.get( i ).page_list_item.equals( v )){
                    mPageListItems.get( i ).page_list_item.animate()
                            .scaleX( Constants.FOCUS_XSCALE )
                            .scaleY( Constants.FOCUS_YSCALE )
                            .setDuration(Constants.FOCUS_DURATION)
                            .start();
                    mPageListItems.get( i ).imageBorder.setVisibility( View.VISIBLE );
                    mPageListItems.get( i ).page_list_item.bringToFront();
                    mPageListItems.get( i ).textSingerName.setTextColor( ResManager.getInstance().getColorById(R.color.colorHomePrimary) );
                }
            }else{
                if(mPageListItems.get( i ).page_list_item.equals( v )){
                    mPageListItems.get( i ).page_list_item.animate()
                            .scaleX( (float) 1 )
                            .scaleY( (float) 1 )
                            .setDuration(Constants.FOCUS_DURATION)
                            .start();;
                    mPageListItems.get( i ).imageBorder.setVisibility( View.GONE );
                    mPageListItems.get( i ).textSingerName.setTextColor( ResManager.getInstance().getColorById(R.color.colorSongListSingerName) );
                }
            }
        }
    }

    public void setEnableFocus(boolean enableFocus){
        for (int i = 0; i < Constants.SINGER_LIST_LIMIT; i++)
        {
            mPageListItems.get( i ).page_list_item.setFocusable( enableFocus );
            mPageListItems.get( i ).page_list_item.setFocusableInTouchMode( enableFocus );
        }
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getTag() == null) return;
            int index = ConversionsUtil.stringToInteger( v.getTag().toString() );
            if(index == -1 && index < Constants.SINGER_LIST_LIMIT) return;
            FragmentPageSingerList.fragmentParams.viewFocus = v;
            FragmentParams param = new FragmentParams();
            param.singerListInfo.singerName = mPageListItems.get( index ).singerInfo.singer_name;
            param.songListInfo.hintContentId = R.string.gexingdiange_gequ_text_keyboard_hint;
            Query mQuery = new Query();
            mQuery.singer_id1 = mPageListItems.get( index ).singerInfo.singer_id;
            param.songListInfo.query = mQuery;
            EventBusMessage msg = new EventBusMessage();
            msg.what = EventBusConstants.PAGE_GOTO_GEXINGDIANGE_GEQU_LIST;
            param.pageIndex = EventBusConstants.PAGE_GOTO_GEXINGDIANGE_GEQU_LIST;
            msg.obj = param;
            EventBusManager.sendMessage( msg );
        }
    };

    /**更新歌曲*/
    public void updateSongData(List<SingerInfo> songList) {
        if(null == songList ){
            for(int i = 0; i < Constants.SINGER_LIST_LIMIT; i++) {
                mPageListItems.get( i ).singerInfo = null;
                mPageListItems.get( i ).page_list_item.setVisibility( View.GONE );
            }
            return;
        }

        int len = songList.size();
        for(int i = 0; i < Constants.SINGER_LIST_LIMIT; i++) {
            if(i<len){
                mPageListItems.get( i ).singerInfo = songList.get( i );
                mPageListItems.get( i ).page_list_item.setVisibility( View.VISIBLE );
            }else{
                mPageListItems.get( i ).singerInfo = null;
                mPageListItems.get( i ).page_list_item.setVisibility( View.GONE );
            }
        }
        Refresh();
    }

    public void Refresh(){
        for(int i = 0; i < Constants.SINGER_LIST_LIMIT; i++){
            if(mPageListItems.get( i ).singerInfo!=null){
                SingerInfo singerInfo = mPageListItems.get( i ).singerInfo;
                mPageListItems.get( i ).textSingerName.setText( singerInfo.singer_name );
                loadSingerPic(mPageListItems.get( i ).imageSinger,SingerImageManager.getInstance()
                        .getSingerImage( mPageListItems.get( i ).singerInfo.singer_id ));
            }
        }
    }
    private void loadSingerPic(ImageView iv,String path){
        Glide.with( iv.getContext() )
                .load( path )
                .apply( new RequestOptions().centerCrop().circleCrop().placeholder( R.drawable.image_gexingdiange_singer_default ) )
                .into( iv );
    }
}

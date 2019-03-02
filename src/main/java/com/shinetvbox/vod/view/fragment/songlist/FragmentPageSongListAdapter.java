package com.shinetvbox.vod.view.fragment.songlist;

import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shinetvbox.vod.R;
import com.shinetvbox.vod.certercontrol.ControlCenter;
import com.shinetvbox.vod.data.constants.Constants;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.data.eventbus.EventBusMessageSong;
import com.shinetvbox.vod.data.params.FragmentParams;
import com.shinetvbox.vod.db.Query;
import com.shinetvbox.vod.db.SongInfo;
import com.shinetvbox.vod.manager.EventBusManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.manager.SongPlayManager;
import com.shinetvbox.vod.utils.ConversionsUtil;

import java.util.ArrayList;
import java.util.List;

public class FragmentPageSongListAdapter {

    private boolean isCanSingerPegging = true;

    class ViewHold {
        SongInfo songInfo = null;

        //点歌区域， 基本一个条都能点击
        public RelativeLayout page_list_item = null;

        public View tagGroup = null;
        public Button tagYuzhong = null;
        public Button tagLeixing = null;
        public Button tagBendi = null;
        public Button tagRemen = null;
        public Button tagPingfen = null;
        public Button tagYuanchang = null;
        public Button tagGaoqing = null;

        public Button btnTagStatus = null;
        public Button btnTianjia = null;
        public Button btnYanchang = null;
        public TextView textSongName = null;
        public Button btnSingerName = null;
        public TextView textSongNote = null;
        public ImageView imageBorder = null;
    }

    private boolean isShowTag = true;
    private String colorSongNote;

    private List<ViewHold> mPageListItems = new ArrayList<>(  );

    public FragmentPageSongListAdapter(View viewpage_item) {
        ResManager.getInstance().register( viewpage_item );
        colorSongNote = "《<font color='#"+String.format("%2x", ResManager.getInstance().getColorById( R.color.colorSongListSongNote )).substring( 2 )+"'>";
        for (int i = 0; i < Constants.SONG_LIST_LIMIT; i++)
        {
            //初始各个状态
            ViewHold hold = new ViewHold();
            RelativeLayout ipage_list_item = viewpage_item.findViewById( ResManager.getInstance().getId("songlist_item"+i) );
            hold.page_list_item = ipage_list_item;

            hold.tagGroup = ipage_list_item.findViewById( ResManager.getInstance().getId("item_layout_tag") );
            hold.tagYuzhong = ipage_list_item.findViewById( ResManager.getInstance().getId("item_layout_btn_yuzhong") );
            hold.tagLeixing = ipage_list_item.findViewById( ResManager.getInstance().getId("item_layout_btn_leixing") );
            hold.tagBendi = ipage_list_item.findViewById( ResManager.getInstance().getId("item_layout_btn_bendi") );
            hold.tagRemen = ipage_list_item.findViewById( ResManager.getInstance().getId("item_layout_btn_remen") );
            hold.tagPingfen = ipage_list_item.findViewById( ResManager.getInstance().getId("item_layout_btn_pingfen") );
            hold.tagYuanchang = ipage_list_item.findViewById( ResManager.getInstance().getId("item_layout_btn_yuanchuang") );
            hold.tagGaoqing = ipage_list_item.findViewById( ResManager.getInstance().getId("item_layout_btn_gaoqing") );
            hold.btnTagStatus = ipage_list_item.findViewById( ResManager.getInstance().getId("item_layout_btn_status") );
            hold.btnTianjia = ipage_list_item.findViewById( ResManager.getInstance().getId("item_layout_btn_tianjia") );
            hold.btnYanchang = ipage_list_item.findViewById( ResManager.getInstance().getId("item_layout_btn_yanchang") );
            hold.btnSingerName = ipage_list_item.findViewById( ResManager.getInstance().getId("item_layout_btn_singername") );
            hold.textSongName = ipage_list_item.findViewById( ResManager.getInstance().getId("item_layout_text_songname") );
            hold.textSongNote = ipage_list_item.findViewById( ResManager.getInstance().getId("item_layout_text_songnote") );
            hold.imageBorder = ipage_list_item.findViewById( ResManager.getInstance().getId("image_focus") );

            hold.btnSingerName.setFocusable( false );
            hold.btnSingerName.setFocusableInTouchMode( false );
            hold.btnYanchang.setFocusable( false );
            hold.btnYanchang.setFocusableInTouchMode( false );
            hold.btnTianjia.setFocusable( false );
            hold.btnTianjia.setFocusableInTouchMode( false );

            hold.btnTagStatus.setVisibility( View.GONE );
//            hold.btnSingerName.setOnClickListener(onClick);
//            hold.btnSingerName.setOnFocusChangeListener( focusChange );
//            hold.btnSingerName.setTag( ""+i );
            hold.btnTianjia.setOnClickListener(onClick);
            hold.btnTianjia.setOnFocusChangeListener( focusChange );
            hold.btnTianjia.setTag( ""+i );
            hold.btnYanchang.setOnClickListener(onClick);
            hold.btnYanchang.setOnFocusChangeListener( focusChange );
            hold.btnYanchang.setTag( ""+i );

            mPageListItems.add( hold );
        }
    }
    private View.OnFocusChangeListener focusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v.getTag() == null) return;
            setViewFocus( v );
        }
    };
    private void setViewFocus(View v){
        int clen = 0;
        for(int i=0;i<mPageListItems.size();i++){
            boolean isFocus = false;
            clen = mPageListItems.get( i ).page_list_item.getChildCount();
            for(int j=0;j<clen;j++){
                if(mPageListItems.get( i ).page_list_item.getChildAt( j ).hasFocus()){
                    isFocus = true;
                }
            }
            if(isFocus){
                mPageListItems.get( i ).imageBorder.setVisibility( View.VISIBLE );
            }else{
                mPageListItems.get( i ).imageBorder.setVisibility( View.GONE );
            }
        }
    }
    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getTag() == null) return;
            int index = ConversionsUtil.stringToInteger( v.getTag().toString() );
            if(index == -1 || index >= Constants.SONG_LIST_LIMIT) return;
            if(v.equals( mPageListItems.get( index ).btnSingerName )){
                FragmentParams param = new FragmentParams();
                param.singerListInfo.singerName = mPageListItems.get( index ).songInfo.singer_name;
                param.songListInfo.hintContentId = R.string.gexingdiange_gequ_text_keyboard_hint;
                Query mQuery = new Query();
                if(mPageListItems.get( index ).songInfo.singer_id1.equals( "" )){
                    mQuery.song_id = mPageListItems.get( index ).songInfo.song_id;
                }else{
                    mQuery.singer_id1 = mPageListItems.get( index ).songInfo.singer_id1;
                }
                param.songListInfo.query = mQuery;
                EventBusMessage msg = new EventBusMessage();
                msg.what = EventBusConstants.PAGE_GOTO_GEXINGDIANGE_GEQU_LIST;
                param.pageIndex = EventBusConstants.PAGE_GOTO_GEXINGDIANGE_GEQU_LIST;
                msg.obj = param;
                EventBusManager.sendMessage( msg );
            }else if(v.equals( mPageListItems.get( index ).btnTianjia )){
                sendMessageClearQuery();
                SongPlayManager.addSong( mPageListItems.get( index ).songInfo );
            }else if(v.equals( mPageListItems.get( index ).btnYanchang )){
                sendMessageClearQuery();
                SongPlayManager.playSong( mPageListItems.get( index ).songInfo );
            }
        }
    };

    private void sendMessageClearQuery(){
        EventBusMessageSong msg = new EventBusMessageSong();
        msg.what = EventBusConstants.SONG_CLEAR_QUERY;
        EventBusManager.sendMessage( msg );
    }
    /**
     * 设置是否允许歌星反查
     * @param singerPegging
     */
    public void setSingerPegging(boolean singerPegging){
        isCanSingerPegging = singerPegging;
    }

    /**
     * 显示标签或备注文本
     * @param showTag
     */
    public void showTagOrNote(boolean showTag){
        isShowTag = showTag;
        for(int i = 0; i < Constants.SONG_LIST_LIMIT; i++) {
            if(showTag){
                mPageListItems.get( i ).tagGroup.setVisibility( View.VISIBLE );
                mPageListItems.get( i ).textSongNote.setVisibility( View.GONE );
            }else{
                mPageListItems.get( i ).tagGroup.setVisibility( View.GONE );
                mPageListItems.get( i ).textSongNote.setVisibility( View.VISIBLE );
            }
        }
    }
    /**
     * 设置焦点是否可用
     * @param enableFocus
     */
    public void setEnableFocus(boolean enableFocus){
        for (int i = 0; i < Constants.SONG_LIST_LIMIT; i++)
        {
//            if(isCanSingerPegging){
//                mPageListItems.get( i ).btnSingerName.setFocusable( enableFocus );
//                mPageListItems.get( i ).btnSingerName.setFocusableInTouchMode( enableFocus );
//            }else{
//                mPageListItems.get( i ).btnSingerName.setFocusable( false );
//                mPageListItems.get( i ).btnSingerName.setFocusableInTouchMode( false );
//            }
            mPageListItems.get( i ).btnTianjia.setFocusable( enableFocus );
            mPageListItems.get( i ).btnTianjia.setFocusableInTouchMode( enableFocus );
            mPageListItems.get( i ).btnYanchang.setFocusable( enableFocus );
            mPageListItems.get( i ).btnYanchang.setFocusableInTouchMode( enableFocus );
        }
    }

    /**更新歌曲*/
    public void updateSongData(List<SongInfo> songList) {
        if(null == songList ){
            for(int i = 0; i < Constants.SONG_LIST_LIMIT; i++) {
                mPageListItems.get( i ).songInfo = null;
                mPageListItems.get( i ).page_list_item.setVisibility( View.GONE );
            }
            return;
        }
        int len = songList.size();
        for(int i = 0; i < Constants.SONG_LIST_LIMIT; i++) {
            if(i<len){
                mPageListItems.get( i ).songInfo = songList.get( i );
                mPageListItems.get( i ).page_list_item.setVisibility( View.VISIBLE );
            }else{
                mPageListItems.get( i ).songInfo = null;
                mPageListItems.get( i ).page_list_item.setVisibility( View.GONE );
            }
        }

        refresh();
    }

    public void refresh(){

        for(int i = 0; i < Constants.SONG_LIST_LIMIT; i++){
            if(mPageListItems.get( i ).songInfo!=null){
                SongInfo songInfo = mPageListItems.get( i ).songInfo;
                mPageListItems.get( i ).textSongName.setText( songInfo.song_name );
                mPageListItems.get( i ).btnSingerName.setText( songInfo.singer_name );
                mPageListItems.get( i ).textSongNote.setText( songInfo.show_movie_name );

                resetSongState(i);

                if(isShowTag){

                    ResManager.getInstance().setText(mPageListItems.get( i ).tagYuzhong,getSongLangId(songInfo.language));
                    ResManager.getInstance().setText(mPageListItems.get( i ).tagLeixing,getSongLeixingId(songInfo.song_version));

                    if(ControlCenter.listHotSongId.contains( songInfo.song_id )){
                        mPageListItems.get( i ).tagRemen.setVisibility( View.VISIBLE );
                    }else{
                        mPageListItems.get( i ).tagRemen.setVisibility( View.GONE );
                    }
                    if(songInfo.pingfen.equals( "1" )){
                        mPageListItems.get( i ).tagPingfen.setVisibility( View.VISIBLE );
                    }else{
                        mPageListItems.get( i ).tagPingfen.setVisibility( View.GONE );
                    }
                    if(ControlCenter.listLocalSongId.contains( songInfo.song_id )){
                        mPageListItems.get( i ).tagBendi.setVisibility( View.VISIBLE );
                    }else{
                        mPageListItems.get( i ).tagBendi.setVisibility( View.GONE );
                    }
                    if(songInfo.yuanchang.equals( "1" )){
                        mPageListItems.get( i ).tagYuanchang.setVisibility( View.VISIBLE );
                    }else{
                        mPageListItems.get( i ).tagYuanchang.setVisibility( View.GONE );
                    }
                    if(songInfo.gaoqing.equals( "1" )){
                        mPageListItems.get( i ).tagGaoqing.setVisibility( View.VISIBLE );
                    }else{
                        mPageListItems.get( i ).tagGaoqing.setVisibility( View.GONE );
                    }

                }else{
                    if(!songInfo.show_movie_name.equals( "" )){
                        mPageListItems.get( i ).textSongNote.setText( Html.fromHtml( songInfo
                                .show_movie_name.replace( "《",colorSongNote ).replace( "》","</font>》" ) ) );
                    }
                }
            }
        }
    }

    private int getSongLangId(String str){
        int lid = 39;
        if (str.length() > 0) {
            lid = ConversionsUtil.stringToInteger( str );
            if(lid==1 || lid==2 || lid==3 || lid==4 || lid==5 || lid==6 || lid==21 || lid==29){

            }else{
                lid = 39;
            }
        }
        return ResManager.getInstance().getStringId( "song_list_db_lang"+lid );
    }
    private int getSongLeixingId(String str){
        int lid = 9;
        if (str.length() > 0) {
            lid = ConversionsUtil.stringToInteger( str );
            if(lid <1 || lid>11) lid = 9;
        }
        return ResManager.getInstance().getStringId( "song_list_db_version"+lid );
    }

    /**
     * 重置歌曲名文本
     */
    private void resetSongState( int index ){
        mPageListItems.get( index ).btnTagStatus.setSelected( false );
        if(SongPlayManager.getCurrentPlaySongId().equals( mPageListItems.get( index ).songInfo.song_id )){
            mPageListItems.get( index ).btnTagStatus.setVisibility( View.VISIBLE );
            mPageListItems.get( index ).btnTagStatus.setSelected( true );
            mPageListItems.get( index ).btnTagStatus.setText( R.string.item_layout_btn_playing);
        }else if(SongPlayManager.getSelectedSongIndex( mPageListItems.get( index ).songInfo.song_id )!=-1){
            mPageListItems.get( index ).btnTagStatus.setVisibility( View.VISIBLE );
            mPageListItems.get( index ).btnTagStatus.setText( R.string.item_layout_btn_yidian);
        }else if(SongPlayManager.getDownloadSongIndex( mPageListItems.get( index ).songInfo.song_id )!=-1){
            mPageListItems.get( index ).btnTagStatus.setVisibility( View.VISIBLE );
            int statusDown = SongPlayManager.getDownloadSongStatus( mPageListItems.get( index ).songInfo.song_id );
            if(statusDown == 2){
                mPageListItems.get( index ).btnTagStatus.setText( R.string.item_layout_btn_xiazai_failure);
            }else{
                mPageListItems.get( index ).btnTagStatus.setText( R.string.item_layout_btn_xiazai);
            }
        }else{
            mPageListItems.get( index ).btnTagStatus.setVisibility( View.GONE );
        }
    }
}

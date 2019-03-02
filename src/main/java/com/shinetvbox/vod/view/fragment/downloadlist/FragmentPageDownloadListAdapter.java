package com.shinetvbox.vod.view.fragment.downloadlist;

import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shinetvbox.vod.R;
import com.shinetvbox.vod.certercontrol.ControlCenter;
import com.shinetvbox.vod.data.constants.Constants;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.data.params.FragmentParams;
import com.shinetvbox.vod.db.Query;
import com.shinetvbox.vod.db.SongInfo;
import com.shinetvbox.vod.manager.EventBusManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.manager.SongPlayManager;
import com.shinetvbox.vod.utils.ConversionsUtil;

import java.util.ArrayList;
import java.util.List;

public class FragmentPageDownloadListAdapter {

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

        public Button btnTagXiazaiFailure = null;
        public Button btnZhiding = null;
        public Button btnShanchu = null;
        public Button btnSingerName = null;
        public TextView textSongName = null;
        public TextView textSongNote = null;
        public ImageView imageBorder = null;
        public ProgressBar progressBar = null;
    }

    private boolean isShowTag = true;
    private String colorSongNote;

    private List<ViewHold> mPageListItems = new ArrayList<>(  );

    public FragmentPageDownloadListAdapter(View viewpage_item) {
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
            hold.btnZhiding = ipage_list_item.findViewById( ResManager.getInstance().getId("item_layout_btn_zhiding") );
            hold.btnShanchu = ipage_list_item.findViewById( ResManager.getInstance().getId("item_layout_btn_shanchu") );
            hold.textSongName = ipage_list_item.findViewById( ResManager.getInstance().getId("item_layout_text_songname") );
            hold.btnSingerName = ipage_list_item.findViewById( ResManager.getInstance().getId("item_layout_btn_singername") );
            hold.textSongNote = ipage_list_item.findViewById( ResManager.getInstance().getId("item_layout_text_songnote") );
            hold.imageBorder = ipage_list_item.findViewById( ResManager.getInstance().getId("image_focus") );
            hold.btnTagXiazaiFailure = ipage_list_item.findViewById( ResManager.getInstance().getId("item_layout_btn_xiazai_failure") );
            hold.progressBar = ipage_list_item.findViewById( ResManager.getInstance().getId("progressbar_download_progress") );

            hold.btnSingerName.setFocusable( false );
            hold.btnSingerName.setFocusableInTouchMode( false );
            hold.btnZhiding.setFocusable( false );
            hold.btnZhiding.setFocusableInTouchMode( false );
            hold.btnShanchu.setFocusable( false );
            hold.btnShanchu.setFocusableInTouchMode( false );

//            hold.btnSingerName.setOnFocusChangeListener( focusChange );
//            hold.btnSingerName.setTag( ""+i );
            hold.btnZhiding.setOnFocusChangeListener( focusChange );
            hold.btnZhiding.setTag( ""+i );
            hold.btnShanchu.setOnFocusChangeListener( focusChange );
            hold.btnShanchu.setTag( ""+i );
//            hold.btnSingerName.setOnClickListener(onClick);
            hold.btnZhiding.setOnClickListener(onClick);
            hold.btnShanchu.setOnClickListener(onClick);

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

    public void setEnableFocus(boolean enableFocus){
        for (int i = 0; i < Constants.SONG_LIST_LIMIT; i++)
        {
//            mPageListItems.get( i ).btnSingerName.setFocusable( enableFocus );
//            mPageListItems.get( i ).btnSingerName.setFocusableInTouchMode( enableFocus );
            mPageListItems.get( i ).btnZhiding.setFocusable( enableFocus );
            mPageListItems.get( i ).btnZhiding.setFocusableInTouchMode( enableFocus );
            mPageListItems.get( i ).btnShanchu.setFocusable( enableFocus );
            mPageListItems.get( i ).btnShanchu.setFocusableInTouchMode( enableFocus );
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
            }else if(v.equals( mPageListItems.get( index ).btnZhiding )){
                SongPlayManager.priorityDownloadSong( mPageListItems.get( index ).songInfo );
            }else if(v.equals( mPageListItems.get( index ).btnShanchu )){
                SongPlayManager.delDownloadSong( mPageListItems.get( index ).songInfo );
            }
        }
    };

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

                if(!SongPlayManager.getCurrentDownloadSongId().equals( songInfo.song_id )){
                    mPageListItems.get( i ).progressBar.setVisibility( View.GONE );
                    mPageListItems.get( i ).progressBar.setProgress( 0 );
                    int statusDown = SongPlayManager.getDownloadSongStatus( songInfo.song_id );
                    if(statusDown == 2){
                        mPageListItems.get( i ).progressBar.setVisibility( View.GONE );
                        mPageListItems.get( i ).progressBar.setProgress( 0 );
                        mPageListItems.get( i ).btnTagXiazaiFailure.setVisibility( View.VISIBLE );
                    }else{
                        mPageListItems.get( i ).btnTagXiazaiFailure.setVisibility( View.GONE );
                    }
                }

                if(isShowTag){
                    ResManager.getInstance().setText(mPageListItems.get( i ).tagYuzhong,getSongLangId(songInfo.language));
                    ResManager.getInstance().setText(mPageListItems.get( i ).tagLeixing,getSongLeixingId(songInfo.song_version));

                    mPageListItems.get( i ).tagBendi.setVisibility( View.GONE );
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

    public void refreshListProgress(String songid, int progress) {
        for(int i = 0; i < Constants.SONG_LIST_LIMIT; i++){
            if(mPageListItems.get( i ).songInfo!=null) {
                SongInfo songInfo = mPageListItems.get( i ).songInfo;
                if (songInfo.song_id.equals( songid )) {
                    if(progress<100 && progress>0){
                        mPageListItems.get( i ).progressBar.setVisibility( View.VISIBLE );
                        mPageListItems.get( i ).progressBar.setProgress( progress );
                    }else{
                        mPageListItems.get( i ).progressBar.setVisibility( View.GONE );
                        mPageListItems.get( i ).progressBar.setProgress( 0 );
                    }
                    if(SongPlayManager.getDownloadSongStatus( songInfo.song_id ) == 2){
                        mPageListItems.get( i ).btnTagXiazaiFailure.setVisibility( View.VISIBLE );
                    }else{
                        mPageListItems.get( i ).btnTagXiazaiFailure.setVisibility( View.GONE );
                    }
                }
            }
        }
    }
    private int getSongLangId(String str){
        int lid = 39;
        if (str.length() > 0) {
            lid = ConversionsUtil.stringToInteger( str );
            if(lid==1 || lid==2 || lid==4 || lid==5 || lid==6 || lid==21 || lid==29){

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
}

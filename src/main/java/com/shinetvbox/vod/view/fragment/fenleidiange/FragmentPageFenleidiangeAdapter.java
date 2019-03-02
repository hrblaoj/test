package com.shinetvbox.vod.view.fragment.fenleidiange;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shinetvbox.vod.R;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.data.params.FragmentParams;
import com.shinetvbox.vod.db.Query;
import com.shinetvbox.vod.manager.EventBusManager;
import com.shinetvbox.vod.manager.MyViewManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.utils.ConversionsUtil;

import java.util.ArrayList;
import java.util.List;

public class FragmentPageFenleidiangeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mlayoutInflater;
    private List<DataFenleidiange> list;
    public List<MyHolder> listItemView;
    private boolean isShowText = true;

    public FragmentPageFenleidiangeAdapter(Context context) {
        mlayoutInflater = LayoutInflater.from( context );
    }
    public void init(List<DataFenleidiange> list,boolean showText) {
        isShowText = showText;
        this.list = list;
        listItemView = new ArrayList<>(  );
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyHolder myHolder = new MyHolder( mlayoutInflater.inflate( R.layout.item_layout_fenleidiange,parent,false ) );
        listItemView.add( myHolder );
        return myHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MyHolder)holder).bindHolder( list.get( position ) );
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemSelectState( v );
            }
        });

        ResManager.getInstance().setOnFocusChangeListener(((MyHolder)holder).itemView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void setItemSelectState(View vi){
        if(vi.getTag()!=null){
            int index = ConversionsUtil.stringToInteger(vi.getTag().toString());
            if(index<0 || index >= list.size()) return;
            FragmentPageFenleidiange.fragmentParams.viewFocus = vi;
            FragmentParams param = new FragmentParams();
            param.keyboard.isShow = true;
            param.keyboard.titleId = list.get( index ).stringId;
            param.keyboard.hintId = R.string.pinyindiange_text_keyboard_hint;

            Query mQuery = new Query(  );
            mQuery.language = list.get( index ).language;
            mQuery.song_type = list.get( index ).song_type;
            mQuery.song_theme = list.get( index ).song_theme;
            mQuery.new_song_theme = list.get( index ).new_song_theme;
            param.songListInfo.query = mQuery;

            EventBusMessage msg = new EventBusMessage();
            msg.what = EventBusConstants.PAGE_GOTO_FENLEIDIANGE_LIST;
            param.pageIndex = EventBusConstants.PAGE_GOTO_FENLEIDIANGE_LIST;
            msg.obj = param;
            EventBusManager.sendMessage( msg );
        }
    }

    public void restFocus() {
        if(FragmentPageFenleidiange.fragmentParams.viewFocus != null){
            MyViewManager.getInstance().requestFocusCustomButton( FragmentPageFenleidiange.fragmentParams.viewFocus );
        }
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        private View thisView;
        private ImageView imageView;
        private TextView textView;

        public MyHolder(View itemView ) {
            super( itemView );
            thisView = itemView;
        }
        public void bindHolder(DataFenleidiange data){
            if(thisView == null) return;
            thisView.setTag( data.tag );
            imageView = thisView.findViewById( R.id.image_item_fenleidiange );
            imageView.setBackgroundResource( data.thumbId );
            textView = thisView.findViewById( R.id.text_item_fenleidiange );
            textView.setText( data.stringId );
            if(isShowText){
                textView.setVisibility( View.VISIBLE );
            }else{
                textView.setVisibility( View.GONE );
            }
        }
    }

    public static class DataFenleidiange {
        public String tag = "";
        public int thumbId = 0;
        public int stringId = 0;
        public String language = "";
        public String song_type = "";
        public String song_theme = "";
        public String new_song_theme = "";

    }
}

package com.shinetvbox.vod.view.fragment.paihangbang;

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

public class FragmentPagePaihangbangAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mlayoutInflater;
    private List<Datapaihangbang> list;
    public List<MyHolder> listItemView;
    private boolean isShowText = true;

    public FragmentPagePaihangbangAdapter(Context context) {
        mlayoutInflater = LayoutInflater.from( context );
    }
    public void init(List<Datapaihangbang> list, boolean showText) {
        isShowText = showText;
        this.list = list;
        listItemView = new ArrayList<>(  );
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyHolder myHolder = new MyHolder( mlayoutInflater.inflate( R.layout.item_layout_paihangbang,parent,false ) );
        listItemView.add( myHolder );
        return myHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(paihangbangParams.viewFocus == null && position == 0){
            btnFirstFocus = holder.itemView;
            paihangbangParams.viewFocus = holder.itemView;
            MyViewManager.getInstance().requestFocusCustomButton( holder.itemView );
        }
        ((MyHolder)holder).bindHolder( list.get( position ) );
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemSelectState( v );
            }
        });

        ResManager.getInstance().setOnFocusChangeListener(((MyHolder)holder).itemView);
    }


    private View btnFirstFocus = null;
    private FragmentParams paihangbangParams = null;
    public void setPaihangbangParams(FragmentParams fragmentParams){
        paihangbangParams = fragmentParams;
        if(paihangbangParams.viewFocus == null){
            paihangbangParams.viewFocus = btnFirstFocus;
        }
        if(paihangbangParams.viewFocus != null){
            MyViewManager.getInstance().requestFocusCustomButton( paihangbangParams.viewFocus );
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    private void setItemSelectState(View vi){
        if(vi.getTag()!=null){
            int index = ConversionsUtil.stringToInteger(vi.getTag().toString());
            if(index<0 || index >= list.size()) return;
            if(paihangbangParams!=null){
                paihangbangParams.viewFocus = vi;
            }
            FragmentParams param = new FragmentParams();
            param.songListInfo.titleId = list.get( index ).stringId;
            param.songListInfo.hintId = R.string.paihangbang_text_hint;
            param.songListInfo.hintContentId = R.string.paihangbang_text_hint_content;
            param.songListInfo.thumbnailId = list.get( index ).thumbId;
            Query mQuery = new Query();
            mQuery.tablename = "top_song";
            mQuery.language = list.get( index ).language;
            param.songListInfo.query = mQuery;
            EventBusMessage msg = new EventBusMessage();
            msg.what = EventBusConstants.PAGE_GOTO_PAIHANGBANG_LIST;
            param.pageIndex = EventBusConstants.PAGE_GOTO_PAIHANGBANG_LIST;
            msg.obj = param;
            EventBusManager.sendMessage( msg );
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
        public void bindHolder(Datapaihangbang data){
            if(thisView == null) return;
            thisView.setTag( data.tag );
            imageView = thisView.findViewById( R.id.image_item_fenleidiange );
            imageView.setBackgroundResource( data.thumbId );
            textView = thisView.findViewById( R.id.text_item_fenleidiange );
            if(isShowText){
                ResManager.getInstance().setText( textView,data.stringId );
                textView.setVisibility( View.VISIBLE );
            }else{
                textView.setVisibility( View.GONE );
            }
        }
    }

    public static class Datapaihangbang {
        public String tag = "";
        public int thumbId = 0;
        public int stringId = 0;
        public String language = "";
    }
}

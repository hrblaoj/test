package com.shinetvbox.vod.view.fragment.language;

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
import com.shinetvbox.vod.manager.EventBusManager;
import com.shinetvbox.vod.manager.MyViewManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.utils.ConversionsUtil;
import com.shinetvbox.vod.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class FragmentPageLanguageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mlayoutInflater;
    private List<DataLanguage> list;
    public List<MyHolder> listItemView;
    private boolean isShowText = true;

    public FragmentPageLanguageAdapter(Context context) {
        mlayoutInflater = LayoutInflater.from( context );
    }
    public void init(List<DataLanguage> list,boolean showText) {
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
        if(list.get( position ).language.equals( SharedPreferencesUtil.getLangage_key())){
            btnSelectLanguageFocus = holder.itemView;
            FragmentPageLanguage.fragmentParams.viewFocus = holder.itemView;
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void setItemSelectState(View vi){
        if(vi.getTag()!=null){
            int index = ConversionsUtil.stringToInteger(vi.getTag().toString());
            if(index<0 || index >= list.size()) return;
            FragmentPageLanguage.fragmentParams.viewFocus = btnSelectLanguageFocus = vi;
            EventBusMessage msg = new EventBusMessage();
            msg.what = EventBusConstants.LANGUAGE_CHANGE;
            msg.obj = list.get( index ).language;
            EventBusManager.sendMessage( msg );
        }
    }

    private View btnSelectLanguageFocus = null;
    public void restFocus() {
        if(FragmentPageLanguage.fragmentParams.viewFocus == null){
            FragmentPageLanguage.fragmentParams.viewFocus = btnSelectLanguageFocus;
        }
        if(FragmentPageLanguage.fragmentParams.viewFocus != null){
            MyViewManager.getInstance().requestFocusCustomButton( FragmentPageLanguage.fragmentParams.viewFocus );
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
        public void bindHolder(DataLanguage data){
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

    public static class DataLanguage {
        public String tag = "";
        public int thumbId = 0;
        public int stringId = 0;
        public String language = "";
    }
}

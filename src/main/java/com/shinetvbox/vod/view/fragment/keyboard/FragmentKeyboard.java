package com.shinetvbox.vod.view.fragment.keyboard;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shinetvbox.vod.R;
import com.shinetvbox.vod.data.constants.Constants;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessageKeyboard;
import com.shinetvbox.vod.data.params.FragmentParams;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.manager.MyViewManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.utils.AnimationUtil;
import com.shinetvbox.vod.utils.QrcodeUtil;
import com.shinetvbox.vod.view.custom.MyFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentKeyboard extends MyFragment {

    private Context mContext = null;
    private View mView = null;

    FragmentParams fragmentParams;

    private TextView textTitle;
    private TextView textHint;
    private Button textInput;
    private Button btnHuishan;
    private Button btnShanchu;

    private List<Button> listBtnAlphabet = new ArrayList<>(  );
    private String[] alphabets = {"A","B","C","D","E","F","G","H","I","J","K","L","M",
            "N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    public FragmentKeyboard() {
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
        mView = inflater.inflate( R.layout.fragment_keyboard, container, false );
        initView();
        return mView;
    }

    private void initView() {
        if(mView == null) return;
        EventBus.getDefault().register( this );
        ResManager.getInstance().register( mView );
        textTitle = mView.findViewById( R.id.text_keboard_title );
        textHint = mView.findViewById( R.id.text_keboard_hint );
        textInput = mView.findViewById( R.id.btn_search_text );

        btnHuishan = mView.findViewById( R.id.btn_keyboard_huishan );
        btnHuishan.setOnClickListener( onClick );
        btnShanchu = mView.findViewById( R.id.btn_keyboard_shanchu );
        btnShanchu.setOnClickListener( onClick );

        listBtnAlphabet.clear();
        for(int i=0;i<alphabets.length;i++){
            Button btn = mView.findViewById( ResManager.getInstance().getId( "btn_keyboard_"+alphabets[i] ) );
            listBtnAlphabet.add(btn);
            btn.setOnClickListener( onClick );
        }

        ImageView imgWechatQrcode = mView.findViewById( R.id.image_keyboard_qrcode );
        QrcodeUtil.loadWechartQrcode(imgWechatQrcode);
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getTag() == null) return;
            fragmentParams.viewFocus = v;
            switch(v.getTag().toString()) {
                case "btn_keyboard_huishan":
                    if(!fragmentParams.keyboard.inputText.equals( "" )){
                        fragmentParams.keyboard.inputText = fragmentParams.keyboard.inputText.substring(
                                0,fragmentParams.keyboard.inputText.length()-1
                        );
                        sendKeyClickEvent();
                    }
                    break;
                case "btn_keyboard_shanchu":
                    fragmentParams.keyboard.inputText = "";
                    sendKeyClickEvent();
                    break;
                default:
                    fragmentParams.keyboard.inputText += ((TextView)v).getText().toString();
                    sendKeyClickEvent();
                    break;
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusMessageKeyboard msg) {
        switch (msg.what) {
            case EventBusConstants.KEYBOARD_SET_LISTENER:
                onKeyboardListener = msg.onKeyboardListener;
                break;
            case EventBusConstants.KEYBOARD_SET_INPUTTEXT:
                fragmentParams.keyboard.inputText = msg.inputText;
                fragmentParams.keyboard.smartPinyin = msg.smartPinyin;
                setKeyboardByParam();
                break;
        }
    }

    private OnKeyboardListener onKeyboardListener;
    public interface OnKeyboardListener{
        public void onClick(String inputText);
    }
    private void sendKeyClickEvent(){
        if(onKeyboardListener!=null){
            onKeyboardListener.onClick( fragmentParams.keyboard.inputText );
        }
    }

    private void setSmartPinying(List<String> list){
//        if(strs == null) return;
        Button btnFocus = null;
        boolean isChangeFocus = false;
        for(int i=0;i<alphabets.length;i++){
            Button btn = mView.findViewById( ResManager.getInstance().getId( "btn_keyboard_"+alphabets[i] ) );
            if(list != null){
                if(list.contains( alphabets[i] )){
                    btn.setEnabled( true );
                    btn.setFocusable( true );
                    btn.setFocusableInTouchMode( true );
                    btn.setAlpha( 1 );
                    if(btnFocus==null){
                        btnFocus = btn;
                    }
                }else{
                    if(btn.hasFocus()){
                        isChangeFocus = true;
                    }
                    btn.setEnabled( false );
                    btn.setFocusable( false );
                    btn.setFocusableInTouchMode( false );
                    btn.setAlpha( (float) 0.3 );
                }
            }else{
                btn.setEnabled( true );
                btn.setFocusable( true );
                btn.setFocusableInTouchMode( true );
                btn.setAlpha( 1 );
            }
        }
        if(isChangeFocus){
            if(btnFocus!=null){
                fragmentParams.viewFocus = btnFocus;
            }else{
                fragmentParams.viewFocus = btnHuishan;
            }
            MyViewManager.getInstance().requestFocus( fragmentParams.viewFocus );
        }
    }
    @Override
    public void setFragmentParams(FragmentParams param) {
        if(mView==null) return;
        if(param !=null ){
            fragmentParams = param;
        }else{
            fragmentParams = new FragmentParams();
        }
        onKeyboardListener = null;
        if(!fragmentParams.keyboard.isShow) return;

        if(fragmentParams.viewFocus == null ) fragmentParams.viewFocus = mView.findViewById( R.id.btn_keyboard_A );

        setKeyboardByParam();
    }
    private void setKeyboardByParam(){
        ResManager.getInstance().setText( textTitle,fragmentParams.keyboard.titleId );
        ResManager.getInstance().setText( textHint,fragmentParams.keyboard.hintId );
        textInput.setText( fragmentParams.keyboard.inputText );
        setSmartPinying(fragmentParams.keyboard.smartPinyin);

        MyViewManager.getInstance().requestFocus( fragmentParams.viewFocus );
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(hidden){
            onKeyboardListener = null;
        }
    }

    @Override
    public FragmentParams getFragmentParams() {
        return fragmentParams;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean isBaseOnWidth() {
        return true;
    }

    @Override
    public float getSizeInDp() {
        return 0;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister( this );
    }
}

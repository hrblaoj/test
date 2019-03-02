package com.shinetvbox.vod.data.eventbus;

import com.shinetvbox.vod.view.fragment.keyboard.FragmentKeyboard;

import java.util.List;

public class EventBusMessageKeyboard {
    public int what = -1;
    public FragmentKeyboard.OnKeyboardListener onKeyboardListener = null;
    public String inputText = "";
    public List<String> smartPinyin = null;
}

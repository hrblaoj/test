package com.shinetvbox.vod.manager;

import android.support.annotation.NonNull;

import com.shinetvbox.vod.data.eventbus.EventBusMessage;
import com.shinetvbox.vod.data.eventbus.EventBusMessageKeyboard;
import com.shinetvbox.vod.data.eventbus.EventBusMessageSong;
import com.shinetvbox.vod.data.eventbus.EventBusMessageSongDownload;
import com.shinetvbox.vod.data.eventbus.EventBusMessageUpdateApp;

import org.greenrobot.eventbus.EventBus;

public class EventBusManager {
    public static void sendMessage(@NonNull EventBusMessage msg){
        EventBus.getDefault().post( msg );
    }
    public static void sendMessage(@NonNull EventBusMessageSong msg){
        EventBus.getDefault().post( msg );
    }
    public static void sendMessage(@NonNull EventBusMessageKeyboard msg){
        EventBus.getDefault().post( msg );
    }
    public static void sendMessage(@NonNull EventBusMessageUpdateApp msg){
        EventBus.getDefault().post( msg );
    }
    public static void sendMessage(@NonNull EventBusMessageSongDownload msg){
        EventBus.getDefault().post( msg );
    }
}

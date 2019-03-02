package com.shinetvbox.vod.manager;

import android.util.Log;
import android.view.KeyEvent;

import com.shinetvbox.vod.certercontrol.ControlCenter;
import com.shinetvbox.vod.certercontrol.ControlCenterConstants;
import com.shinetvbox.vod.data.eventbus.EventBusConstants;
import com.shinetvbox.vod.data.eventbus.EventBusMessage;

public class KeyDownManager {
    /**音量加*/
    public static int volume_up=-10000;
    /**音量减*/
    public static int volume_down=-10000;
    /**静音、取消静音*/
    public static int volume_mute_unmute=-10000;
    /**静音*/
    public static int volume_mute=-10000;
    /**取消静音*/
    public static int volume_unmute=-10000;
    /**切歌*/
    public static int next_song=-10000;
    /**播放、暂停*/
    public static int song_play_pause=-10000;
    /**播放*/
    public static int song_play=-10000;
    /**暂停*/
    public static int song_pause=-10000;
    /**原、伴唱*/
    public static int song_original_accompany=-10000;
    /**原唱*/
    public static int song_original=-10000;
    /**#伴唱*/
    public static int song_accompany=-10000;
    /**重唱*/
    public static int song_replay=-10000;
    /**显示视频画面*/
    public static int show_video=-10000;

    private static long curtime = 0;
    public static void customKeyDown(int keyCode, KeyEvent event){
        Log.i("2222222222222222222",keyCode+"+============================="+(System.currentTimeMillis()-curtime));
        if(keyCode<-9999) return;
        if(System.currentTimeMillis()-curtime<500) return;
        curtime = System.currentTimeMillis();
        if(keyCode==volume_up){
            ControlCenter.sendEmptyMessage( ControlCenterConstants.MUSIC_VOLUME_ADD);
        }else if(keyCode==volume_down){
            ControlCenter.sendEmptyMessage( ControlCenterConstants.MUSIC_VOLUME_SUBTRACT );
        }else if(keyCode==volume_mute_unmute){
            ControlCenter.sendEmptyMessage( ControlCenterConstants.MUSIC_VOLUME_MUTE_UNMUTE );
        }else if(keyCode==volume_mute){
            ControlCenter.sendEmptyMessage( ControlCenterConstants.MUSIC_VOLUME_MUTE );
        }else if(keyCode==volume_unmute){
            ControlCenter.sendEmptyMessage( ControlCenterConstants.MUSIC_VOLUME_UNMUTE );
        }else if(keyCode==next_song){
            ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_PLAY_NEXT );
        }else if(keyCode==song_play_pause){
            ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_PLAY_PAUSE );
        }else if(keyCode==song_play){
            ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_PLAY );
        }else if(keyCode==song_pause){
            ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_PAUSE );
        }else if(keyCode==song_original_accompany){
            ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_ORIGINAL_ACCOMPANY );
        }else if(keyCode==song_original){
            ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_ORIGINAL );
        }else if(keyCode==song_accompany){
            ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_ACCOMPANY );
        }else if(keyCode==song_replay){
            ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_REPLAY );
        }else if(keyCode==show_video){
            EventBusMessage msg = new EventBusMessage();
            msg.what = EventBusConstants.VIDEO_SHOW;
            EventBusManager.sendMessage( msg );
        }
//        switch (keyCode) {
//            //音量加 value 24
//            case volume_up:
//                ControlCenter.sendEmptyMessage( ControlCenterConstants.MUSIC_VOLUME_ADD);
//                break;
//            //音量减 value 25
//            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                ControlCenter.sendEmptyMessage( ControlCenterConstants.MUSIC_VOLUME_SUBTRACT );
//                break;
//            //视频屏切 value 131
//            case KeyEvent.KEYCODE_F1:
//                EventBusMessage msg = new EventBusMessage();
//                msg.what = EventBusConstants.VIDEO_SHOW;
//                EventBusManager.sendMessage( msg );
//                break;
//            //切歌 value 132
//            case KeyEvent.KEYCODE_F2:
//                ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_PLAY_NEXT );
//                break;
//            //静音、取消静音 value 133
//            case KeyEvent.KEYCODE_F3:
//                ControlCenter.sendEmptyMessage( ControlCenterConstants.MUSIC_VOLUME_MUTE_UNMUTE );
//                break;
//            //静音 value 134
//            case KeyEvent.KEYCODE_F4:
//                ControlCenter.sendEmptyMessage( ControlCenterConstants.MUSIC_VOLUME_MUTE );
//                break;
//            //取消静音 value 135
//            case KeyEvent.KEYCODE_F5:
//                ControlCenter.sendEmptyMessage( ControlCenterConstants.MUSIC_VOLUME_UNMUTE );
//                break;
//            //播放、暂停 value 136
//            case KeyEvent.KEYCODE_F6:
//                ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_PLAY_PAUSE );
//                break;
//            //播放 value 137
//            case KeyEvent.KEYCODE_F7:
//                ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_PLAY );
//                break;
//            //暂停 value 138
//            case KeyEvent.KEYCODE_F8:
//                ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_PAUSE );
//                break;
//            //原、伴唱 value 139
//            case KeyEvent.KEYCODE_F9:
//                ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_ORIGINAL_ACCOMPANY );
//                break;
//            //原唱 value 140
//            case KeyEvent.KEYCODE_F10:
//                ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_ORIGINAL );
//                break;
//            //伴唱 value 141
//            case KeyEvent.KEYCODE_F11:
//                ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_ACCOMPANY );
//                break;
//            //重唱 value 142
//            case KeyEvent.KEYCODE_F12:
//                ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_REPLAY );
//                break;
//        }
    }
}

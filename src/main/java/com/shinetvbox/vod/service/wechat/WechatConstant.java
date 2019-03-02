package com.shinetvbox.vod.service.wechat;

import android.util.Log;

import com.shinetvbox.vod.MyApplication;
import com.shinetvbox.vod.db.SongInfo;
import com.shinetvbox.vod.manager.SongPlayManager;
import com.shinetvbox.vod.utils.ConversionsUtil;

import java.util.List;

public class WechatConstant {

    /**
     * play	播控状态，播放值：0，暂停值：1
     * musicvolume音量值，范围0-100
     * mute 静音0 非静音1
     * track 原伴唱，原唱值：0，伴唱值：1
     * //windows	魔窗，1-4(魔窗对应码值)int类型
     * //light	灯光，灯光值（0-8）0是手机端关闭灯光页面显示 1-8对应灯光码值int类型
     * @return
     */
    public static String getButtonState(int play,int musicvolume,int mute,int track){
        String str = "buttonstate@{\"roomid\":\""+MyApplication.MAC_ADDRESS+"\",\"musicvolume\":"+musicvolume
                +",\"mute\":"+mute+",\"track\":"+track+",\"play\":"+play+"}";
        return str;
    }
    public static String getSelectSongList(){
        //selectsong@{"roomid":"00d0339ab454","sid":"100001,100003,100011,100023,100035,100036,100038,100041,100044,100045"}
        String str = "selectsong@{\"roomid\":\""+MyApplication.MAC_ADDRESS+"\",\"sid\":\"";
        if(SongPlayManager.getSelectSongCount()>0){
            List<SongInfo> list = SongPlayManager.getSelectSongData( 0,SongPlayManager.getSelectSongCount() );
            for(SongInfo sf:list){
                int sid = ConversionsUtil.stringToInteger( sf.song_id );
                if(sid>0){
                    str += sf.song_id+",";
                }
            }
        }
        str += "\"}";
//        Log.i("2222222222222222",str+"======");
        return str;
    }
}

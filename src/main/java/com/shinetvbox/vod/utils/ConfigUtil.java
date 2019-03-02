package com.shinetvbox.vod.utils;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.shinetvbox.vod.MyApplication;
import com.shinetvbox.vod.dao.FreeSong;
import com.shinetvbox.vod.manager.DatabaseManager;
import com.shinetvbox.vod.manager.KeyDownManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static com.shinetvbox.vod.MyApplication.configIni;

public class ConfigUtil {

    private static boolean singerPegging = false;

    private static boolean xiaomei_open = true;
    private static int xiaomei_type = 0;
    private static int xiaomei_try_cnt = 3;
    private static int xiaomei_jiange = 60;

    private static List<String> freeSongIdList = new ArrayList<>(  );

    public static void init() {
        String str = (String) configIni.get("PUBLICSONG", "SONGCODE", "");
        freeSongIdList.clear();
        if(str!=null && !str.equals( "" )){
            freeSongIdList.addAll( Arrays.asList( str.split( "," ) ) );
        }
        str = (String) configIni.get("CONFIG", "singersong_pegging", "0");
        if(str.equals( "1" )){
            singerPegging = true;
        }else {
            singerPegging = false;
        }
        str = (String) configIni.get("CONFIG", "xiaomei_open", "1");
        if(str.equals( "1" )){
            xiaomei_open = true;
        }else {
            xiaomei_open = false;
        }

        xiaomei_type = stringToInt( (String) configIni.get("CONFIG", "XIAOMEI_SOUND_TYPE", "0"),0 );
        xiaomei_try_cnt = stringToInt( (String) configIni.get("CONFIG", "XIAOMEI_TRY_CNT", "3"),3 );
        xiaomei_jiange = stringToInt( (String) configIni.get("CONFIG", "XIAOMEI_JIANGE", "60"),60 );

        KeyDownManager.volume_up=stringToInt( (String) configIni.get("KEYCODE", "volume_up", "-10000"),-10000 );
        KeyDownManager.volume_down=stringToInt( (String) configIni.get("KEYCODE", "volume_down", "-10000"),-10000 );
        KeyDownManager.volume_mute_unmute=stringToInt( (String) configIni.get("KEYCODE", "volume_mute_unmute", "-10000"),-10000 );
        KeyDownManager.volume_mute=stringToInt( (String) configIni.get("KEYCODE", "volume_mute", "-10000"),-10000 );
        KeyDownManager.volume_unmute=stringToInt( (String) configIni.get("KEYCODE", "volume_unmute", "-10000"),-10000 );
        KeyDownManager.next_song=stringToInt( (String) configIni.get("KEYCODE", "next_song", "-10000"),-10000 );
        KeyDownManager.song_play_pause=stringToInt( (String) configIni.get("KEYCODE", "song_play_pause", "-10000"),-10000 );
        KeyDownManager.song_play=stringToInt( (String) configIni.get("KEYCODE", "song_play", "-10000"),-10000 );
        KeyDownManager.song_pause=stringToInt( (String) configIni.get("KEYCODE", "song_pause", "-10000"),-10000 );
        KeyDownManager.song_original_accompany=stringToInt( (String) configIni.get("KEYCODE", "song_original_accompany", "0"),-10000 );
        KeyDownManager.song_original=stringToInt( (String) configIni.get("KEYCODE", "song_original", "-10000"),-10000 );
        KeyDownManager.song_accompany=stringToInt( (String) configIni.get("KEYCODE", "song_accompany", "-10000"),-10000 );
        KeyDownManager.song_replay=stringToInt( (String) configIni.get("KEYCODE", "song_replay", "-10000"),-10000 );
        KeyDownManager.show_video=stringToInt( (String) configIni.get("KEYCODE", "show_video", "-10000"),-10000 );
    }

    /**
     * 是否允许歌星反查
     * @return
     */
    public static boolean isSingerPegging() {
        return singerPegging;
    }
    /**
     * 是否允许歌星反查
     * @return
     */
    public static void setSingerPegging(boolean isPegging) {
        singerPegging = isPegging;
        if(singerPegging){
            configIni.set("CONFIG", "singersong_pegging", "1");
        }else{
            configIni.set("CONFIG", "singersong_pegging", "0");
        }
        configIni.save();
    }

    /**
     * 获取空闲歌曲ID列表
     * @return
     */
    public static List<String> getFreeSongIdList() {
        return freeSongIdList;
    }

    /**
     * 设置空闲歌曲
     * @param songId
     * @param isAdd
     */
    public static void setFreeSongIdList(String songId, boolean isAdd) {
        if(isAdd){
            if(!freeSongIdList.contains( songId )){
                freeSongIdList.add( songId );
            }
        }else{
            if(freeSongIdList.contains( songId )){
                freeSongIdList.remove( songId );
            }
        }
        String idlist = "";
        for(String id: freeSongIdList){
            idlist += id + ",";
        }
        configIni.set("PUBLICSONG", "SONGCODE", idlist);
        configIni.save();
        FreeSong.getInstance().resetFreeSong();
    }

    /**
     * 小美是否打开
     * @return
     */
    public static boolean isXiaomeiOpen() {
        return xiaomei_open;
    }

    /**
     * 设置小美打开、关闭
     * @param isOpen
     */
    public static void setXiaomeiOpen(boolean isOpen) {
        xiaomei_open = isOpen;
        if(isOpen){
            configIni.set("CONFIG", "xiaomei_open", "1");
        }else {
            configIni.set("CONFIG", "xiaomei_open", "0");
        }
        configIni.save();
    }
    /**
     * 获取小美声音类型 0-3
     * @return
     */
    public static int getXiaomeiSoundType() {
        if(xiaomei_type>3) xiaomei_type = 0;
        return xiaomei_type;
    }
    /**
     * 设置小美声音类型 0-3
     */
    public static void setXiaomeiSoundType(int type) {
        xiaomei_type = type;
        if(xiaomei_type>3) xiaomei_type = 0;
        configIni.set("CONFIG", "XIAOMEI_SOUND_TYPE", ""+xiaomei_type);
        configIni.save();
    }

    /**
     * 获取小美识别错误次数
     * @return
     */
    public static int getXiaomeiTryCnt() {
        if(xiaomei_try_cnt>3) xiaomei_try_cnt = 3;
        return xiaomei_try_cnt;
    }
    /**
     * 设置小美识别错误次数
     * 最大值3
     */
    public static void setXiaomeiTryCnt(int time) {
        xiaomei_try_cnt = time;
        if(xiaomei_try_cnt>3) xiaomei_try_cnt = 3;
        configIni.set("CONFIG", "XIAOMEI_TRY_CNT", ""+xiaomei_try_cnt);
        configIni.save();
    }

    /**
     * 获取小美提示间隔时间
     * @return
     */
    public static int getXiaomeiHintJiange() {
        if(xiaomei_jiange<20) xiaomei_jiange = 20;
        if(xiaomei_jiange>9999) xiaomei_jiange = 9999;
        return xiaomei_jiange;
    }
    /**
     * 设置小美提示间隔时间
     * 单位秒
     */
    public static void setXiaomeiHintJiange(int time) {
        xiaomei_jiange = time;
        if(xiaomei_jiange<20) xiaomei_jiange = 20;
        if(xiaomei_jiange>9999) xiaomei_jiange = 9999;
        configIni.set("CONFIG", "XIAOMEI_JIANGE", ""+xiaomei_jiange);
        configIni.save();
    }

    /**
     * 进程间同步config信息
     * @param section 节点
     * @param key 属性名
     * @param value 属性值
     */
    public static void processSynchronizationConfig(String section, String key, String value) {
        try {
            DatabaseManager.getInstance().getDbService().setConfigIni(section, key, value);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private static int stringToInt(String str, int defaultVal){
        int i = defaultVal;
        try{
            i = Integer.parseInt( str );
        }catch (Exception e){

        }
        return i;
    }
}

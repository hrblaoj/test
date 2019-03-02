package com.shinetvbox.vod.dao;

import com.shinetvbox.vod.db.SongInfo;

import java.util.ArrayList;

/**
 * Created by hrblaoj on 2018/4/2.
 */

public abstract class PlaySong {

    /* 原唱 */
    static int Original = 0;
    /* 伴唱 */
    static int Accompany = 1;
    static public PlaySong curPlaySong;
    static public ArrayList<PlaySong> curSongTypeList = new ArrayList<PlaySong>();

    public interface OutProc{
        void onplay(String filename);
        void onsetvolume(int volume);
        void onstop();
        void onrefreshsonginfo();
        void onreset();
//        void onplayrecord(TapeInfo tape);
    }
    static public OutProc iOutProc;

    public PlaySong(int index)
    {
        this.isPlay = false;
        curSongTypeList.add(index, this);
    }

    public PlaySong()
    {
        this.isPlay = false;
        curSongTypeList.add(this);
    }

    public boolean isPlay = false;

    public void SetisPlay(boolean play){
        isPlay = play;
    }

    public boolean GetisPlay(){
        return isPlay;
    }

    public void onstartproc() {

    }

    public SongInfo getmSomgInfo() {

        return null;
    }
    public String getsongname() {
        return new String("");
    }

    public String getsongid() {
        return new String("");
    }

    public void updatepre() {

    }

//    public void setVolume(int volume){
//        VolumeConfig.setVolume(volume);
//    }

    public int gettracknum(int type) {
        return 0;
    }

    static public void setOutProc(OutProc l)
    {
        iOutProc = l;
    }

    /* 不一定非要执行，该接口只确定下一首歌曲的类型不要做其它处理，否则会有逻辑错误，以后有时间最好把此接口修改为必须执行且可以加入其它逻辑的接口，否则太别扭 */
    public boolean onstopproc() {
        return false;
    }
    static public void executeupdatepre(){
        for(PlaySong t:curSongTypeList){
            t.updatepre();
        }
    }

    static public void setCurPlaySong(PlaySong mPlaySong){
        //if(null != curPlaySong)
          //  curPlaySong.onstopproc();

        curPlaySong = mPlaySong;
    }

    static public PlaySong getCurPlaySong(){


        return curPlaySong;
    }

    static public PlaySong getCurPlaySongListPre(){
        PlaySong curPlaySong = null;
        for(PlaySong t:curSongTypeList){
            if(t.isPlay)
            {
                curPlaySong = t;
                break;
            }
        }

        return curPlaySong;
    }
}

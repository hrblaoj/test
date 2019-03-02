package com.shinetvbox.vod.dao;


import com.shinetvbox.vod.certercontrol.ControlCenter;
import com.shinetvbox.vod.certercontrol.ControlCenterConstants;
import com.shinetvbox.vod.db.SongInfo;
import com.shinetvbox.vod.status.PlayStatus;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by hrblaoj on 2018/4/2.
 */

public class FreeSong extends PlaySong{
    private static CopyOnWriteArrayList<SongInfo> mFreeSongCacheList= new CopyOnWriteArrayList<>();
    private static CopyOnWriteArrayList<SongInfo> mFreeKsongList= new CopyOnWriteArrayList<>();
    int index = 0;

    SongInfo mSongInfo;
    private int defaultvolume = 10;

    private static FreeSong sInstance = null;
    public static FreeSong getInstance(){
        if(sInstance == null) {
            sInstance = new FreeSong();
        }
        return sInstance;
    }

    public void setDefaultvolume(int volume){
        defaultvolume = volume;
    }

    public FreeSong()
    {
        mSongInfo = new SongInfo();
//        mSongInfo.setsongid("file:///storage/sata2/669966.mpg");
            mSongInfo.setsongid("526091");//545454 - 265  520039 - 4track 198404 - mov 125638 - mpeg
//        mSongInfo.setsongid("file:///sdcard/ttt/101108.mpg");//386471  862587 654321
//        mSongInfo.setsongid("file:///sdcard/141603movnnn.mpg");//386471  862587 654321
            //mSongInfo.set_songname("abc");
            mSongInfo.set_accompany_track("2");
            mSongInfo.set_karaoke_track("1");
            mSongInfo.set_songname("Sexy Love");
            mSongInfo.set_singer_name("T-ara");
            mFreeSongCacheList.add(mSongInfo);

////        //mContext = context;
////        SongInfo mSongInfo = new SongInfo();
////        mSongInfo.setsongid("879322");//386471  862587
////        //mSongInfo.set_songname("abc");
////        mSongInfo.set_accompany_track("3");
////        mSongInfo.set_karaoke_track("2");
////        mSongInfo.set_songname("你是我的眼");
////        mSongInfo.set_singer_name("林宥嘉");
////        mFreeSongCacheList.add(mSongInfo);
////
////        mSongInfo = new SongInfo();
////        mSongInfo.setsongid("101108");//386471  862587
////        //mSongInfo.set_songname("abc");
////        mSongInfo.set_accompany_track("2");
////        mSongInfo.set_karaoke_track("1");
////        mSongInfo.set_songname("测试");
////        mSongInfo.set_singer_name("abc");
////        mFreeSongCacheList.add(mSongInfo);
////
////        mSongInfo = new SongInfo();
////        mSongInfo.setsongid("521007");//386471  862587
////        //mSongInfo.set_songname("abc");
////        mSongInfo.set_accompany_track("2");
////        mSongInfo.set_karaoke_track("1");
////        mSongInfo.set_songname("倒带");
////        mSongInfo.set_singer_name("蔡依林");
////        mFreeSongCacheList.add(mSongInfo);
////
////        mSongInfo = new SongInfo();
//////        mSongInfo.setsongid("file:///storage/sata2/669966.mpg");
////        mSongInfo.setsongid("819527");//545454 - 265  520039 - 4track 198404 - mov 125638 - mpeg
//////        mSongInfo.setsongid("file:///sdcard/ttt/101108.mpg");//386471  862587 654321
//////        mSongInfo.setsongid("file:///sdcard/141603movnnn.mpg");//386471  862587 654321
////        //mSongInfo.set_songname("abc");
////        mSongInfo.set_accompany_track("3");
////        mSongInfo.set_karaoke_track("2");
////        mSongInfo.set_songname("没那么简单");
////        mSongInfo.set_singer_name("黄小琥");
////        mFreeSongCacheList.add(mSongInfo);
////
//        if(scanProcess) {
////            mSongInfo = new SongInfo();
//////        mSongInfo.setsongid("file:///storage/sata2/669966.mpg");
////            mSongInfo.setsongid("781119");//545454 - 265  520039 - 4track 198404 - mov 125638 - mpeg
//////        mSongInfo.setsongid("file:///sdcard/ttt/101108.mpg");//386471  862587 654321
//////        mSongInfo.setsongid("file:///sdcard/141603movnnn.mpg");//386471  862587 654321
////            //mSongInfo.set_songname("abc");
////            mSongInfo.set_accompany_track("3");
////            mSongInfo.set_karaoke_track("2");
////            mSongInfo.set_songname("没那么简单");
////            mSongInfo.set_singer_name("黄小琥");
////            mFreeSongCacheList.add(mSongInfo);
//
////            mSongInfo = new SongInfo();
//////        mSongInfo.setsongid("file:///storage/sata2/669966.mpg");
////            mSongInfo.setsongid("781120");//545454 - 265  520039 - 4track 198404 - mov 125638 - mpeg
//////        mSongInfo.setsongid("file:///sdcard/ttt/101108.mpg");//386471  862587 654321
//////        mSongInfo.setsongid("file:///sdcard/141603movnnn.mpg");//386471  862587 654321
////            //mSongInfo.set_songname("abc");
////            mSongInfo.set_accompany_track("3");
////            mSongInfo.set_karaoke_track("2");
////            mSongInfo.set_songname("没那么简单");
////            mSongInfo.set_singer_name("黄小琥");
////            mFreeSongCacheList.add(mSongInfo);
////
////            mSongInfo = new SongInfo();
//////        mSongInfo.setsongid("file:///storage/sata2/669966.mpg");
////            mSongInfo.setsongid("678366");//545454 - 265  520039 - 4track 198404 - mov 125638 - mpeg
//////        mSongInfo.setsongid("file:///sdcard/ttt/101108.mpg");//386471  862587 654321
//////        mSongInfo.setsongid("file:///sdcard/141603movnnn.mpg");//386471  862587 654321
////            //mSongInfo.set_songname("abc");
////            mSongInfo.set_accompany_track("3");
////            mSongInfo.set_karaoke_track("2");
////            mSongInfo.set_songname("没那么简单");
////            mSongInfo.set_singer_name("黄小琥");
////            mFreeSongCacheList.add(mSongInfo);
////
////            mSongInfo = new SongInfo();
//////        mSongInfo.setsongid("file:///storage/sata2/669966.mpg");
////            mSongInfo.setsongid("676588");//545454 - 265  520039 - 4track 198404 - mov 125638 - mpeg
//////        mSongInfo.setsongid("file:///sdcard/ttt/101108.mpg");//386471  862587 654321
//////        mSongInfo.setsongid("file:///sdcard/141603movnnn.mpg");//386471  862587 654321
////            //mSongInfo.set_songname("abc");
////            mSongInfo.set_accompany_track("3");
////            mSongInfo.set_karaoke_track("2");
////            mSongInfo.set_songname("没那么简单");
////            mSongInfo.set_singer_name("黄小琥");
////            mFreeSongCacheList.add(mSongInfo);
////
////            mSongInfo = new SongInfo();
//////        mSongInfo.setsongid("file:///storage/sata2/669966.mpg");
////            mSongInfo.setsongid("679215");//545454 - 265  520039 - 4track 198404 - mov 125638 - mpeg
//////        mSongInfo.setsongid("file:///sdcard/ttt/101108.mpg");//386471  862587 654321
//////        mSongInfo.setsongid("file:///sdcard/141603movnnn.mpg");//386471  862587 654321
////            //mSongInfo.set_songname("abc");
////            mSongInfo.set_accompany_track("3");
////            mSongInfo.set_karaoke_track("2");
////            mSongInfo.set_songname("没那么简单");
////            mSongInfo.set_singer_name("黄小琥");
////            mFreeSongCacheList.add(mSongInfo);
////
////            mSongInfo = new SongInfo();
//////        mSongInfo.setsongid("file:///storage/sata2/669966.mpg");
////            mSongInfo.setsongid("819527");//545454 - 265  520039 - 4track 198404 - mov 125638 - mpeg
//////        mSongInfo.setsongid("file:///sdcard/ttt/101108.mpg");//386471  862587 654321
//////        mSongInfo.setsongid("file:///sdcard/141603movnnn.mpg");//386471  862587 654321
////            //mSongInfo.set_songname("abc");
////            mSongInfo.set_accompany_track("3");
////            mSongInfo.set_karaoke_track("2");
////            mSongInfo.set_songname("没那么简单");
////            mSongInfo.set_singer_name("黄小琥");
////            mFreeSongCacheList.add(mSongInfo);
////
////            mSongInfo = new SongInfo();
//////        mSongInfo.setsongid("file:///storage/sata2/669966.mpg");
////            mSongInfo.setsongid("121453");//545454 - 265  520039 - 4track 198404 - mov 125638 - mpeg
//////        mSongInfo.setsongid("file:///sdcard/ttt/101108.mpg");//386471  862587 654321
//////        mSongInfo.setsongid("file:///sdcard/141603movnnn.mpg");//386471  862587 654321
////            //mSongInfo.set_songname("abc");
////            mSongInfo.set_accompany_track("3");
////            mSongInfo.set_karaoke_track("2");
////            mSongInfo.set_songname("没那么简单");
////            mSongInfo.set_singer_name("黄小琥");
////            mFreeSongCacheList.add(mSongInfo);
//
////            mSongInfo = new SongInfo();
//////        mSongInfo.setsongid("file:///storage/sata2/669966.mpg");
////            mSongInfo.setsongid("121581");//545454 - 265  520039 - 4track 198404 - mov 125638 - mpeg
//////        mSongInfo.setsongid("file:///sdcard/ttt/101108.mpg");//386471  862587 654321
//////        mSongInfo.setsongid("file:///sdcard/141603movnnn.mpg");//386471  862587 654321
////            //mSongInfo.set_songname("abc");
////            mSongInfo.set_accompany_track("3");
////            mSongInfo.set_karaoke_track("2");
////            mSongInfo.set_songname("没那么简单");
////            mSongInfo.set_singer_name("黄小琥");
////            mFreeSongCacheList.add(mSongInfo);
////
////            mSongInfo = new SongInfo();
//////        mSongInfo.setsongid("file:///storage/sata2/669966.mpg");
////            mSongInfo.setsongid("819527");//545454 - 265  520039 - 4track 198404 - mov 125638 - mpeg
//////        mSongInfo.setsongid("file:///sdcard/ttt/101108.mpg");//386471  862587 654321
//////        mSongInfo.setsongid("file:///sdcard/141603movnnn.mpg");//386471  862587 654321
////            //mSongInfo.set_songname("abc");
////            mSongInfo.set_accompany_track("3");
////            mSongInfo.set_karaoke_track("2");
////            mSongInfo.set_songname("没那么简单");
////            mSongInfo.set_singer_name("黄小琥");
////            mFreeSongCacheList.add(mSongInfo);
//
//
//
//
//            mFreeSongCacheList = InitActivity.scanFiles;
//            KtvLog.d("mFreeSongCacheList size  is " + mFreeSongCacheList.size() );
//        }
//        else {
////            mSongInfo = new SongInfo();
//////        mSongInfo.setsongid("file:///storage/sata2/669966.mpg");
////            mSongInfo.setsongid("545454");//545454 - 265  520039 - 4track 198404 - mov 125638 - mpeg
//////        mSongInfo.setsongid("file:///sdcard/ttt/101108.mpg");//386471  862587 654321
//////        mSongInfo.setsongid("file:///sdcard/141603movnnn.mpg");//386471  862587 654321
////            //mSongInfo.set_songname("abc");
////            mSongInfo.set_accompany_track("3");
////            mSongInfo.set_karaoke_track("2");
////            mSongInfo.set_songname("没那么简单");
////            mSongInfo.set_singer_name("黄小琥");
////            mFreeSongCacheList.add(mSongInfo);
//            resetFreeSong();
//        }

    }

    public void resetFreeSong(){
//        Log.i("2222222222222","----------------freesong-------------------"+(Thread.currentThread() == Looper.getMainLooper().getThread()));
//        mFreeSongCacheList.clear();
//        mFreeKsongList.clear();
//        for(String id: ConfigUtil.getFreeSongIdList()){
//            Ksong ksong = KsongDao.getInstance().getKsongBySongID( id );
//            if(ksong!=null && ksong.local_path.equals( "0" )){
//                SongInfo mSongInfo = new SongInfo();
//                mSongInfo.setsongid(id);
//                mSongInfo.set_accompany_track(ksong.accompany_track);
//                mSongInfo.set_karaoke_track(ksong.karaoke_track);
//                mSongInfo.set_songname(ksong.song_name);
//                mSongInfo.set_singer_name(ksong.singer_name);
//                mSongInfo.set_language( ksong.language );
//                mSongInfo.set_song_version( ksong.song_version );
//                mFreeSongCacheList.add(mSongInfo);
//                mFreeKsongList.add( ksong );
//            }
//        }
//
//        String volume = (String) configIni.get("SOUND", "PUBSONGVALUE", "20");
//        defaultvolume = Integer.parseInt(volume);
    }
    public static CopyOnWriteArrayList<SongInfo> getFreeSongList(){
        return mFreeKsongList;
    }
    public static int getFreeSongListCount(){
        return mFreeKsongList.size();
    }
    /**
     * 查询
     *
     * @param num     查询的条数
     * @param index   查询的位置
     * @return
     */
    public static ArrayList<SongInfo> query(long num, final long index) {
        if (mFreeSongCacheList.size() < 1) {
            return new ArrayList<SongInfo>();
        }
        ArrayList<SongInfo> ret = new ArrayList<>();
        int count = 0;
        for (SongInfo song : mFreeKsongList) {
            if (count >= index || num == 0) {
                ret.add( song );
            }
            if (num != 0 && ret.size() == num) {
                break;
            }
            count++;
        }
        return ret;
    }

    @Override
    public synchronized SongInfo getmSomgInfo() {

        if(null != mSongInfo)
            return mSongInfo.clone();
        else
            return null;
    }

    @Override
    public synchronized String getsongname() {
        if(null != mSongInfo)
            return mSongInfo.song_name;
        else
            return null;
    }
    public synchronized String getsongid() {
        if(null != mSongInfo)
            return mSongInfo.song_id;
        else
            return null;
    }
    @Override
    public synchronized void onstartproc()
    {
        if(0 != mFreeSongCacheList.size()) {
//            SongInfo mSongInfo = mFreeSongCacheList.get(index).clone();
            if(index > mFreeSongCacheList.size() - 1) {
                index = 0;
            }
            mSongInfo = mFreeSongCacheList.get(index);
            index++;


            if(null != iOutProc) {
                iOutProc.onplay(mSongInfo.song_id);

//                if(!PlayStatus.getInstance().getIsMute())
//                    iOutProc.onsetvolume(defaultvolume);

            }
        }
    }

    @Override
    public int gettracknum(int type) {

        //byte data[] = {(byte)0xE5, (byte)0xB7, (byte)0xA6,(byte)0x00};
        String left = new String("左");
        String right = new String("右");
        String index1 = "1";
        String index2 = "2";
        String index3 = "3";
        String index4 = "4";

        if(null == mSongInfo)
            return 1;

        //一直保持原唱
        //type=Original;

        if((mSongInfo.accompany_track.equals(index1))&&(mSongInfo.karaoke_track.equals(index2)))
        {
            if(type==Original)
                return 2;
            else
                return 1;
        }
        else if((mSongInfo.accompany_track.equals(index3))&&(mSongInfo.karaoke_track.equals(index2)))
        {
            if(type==Original)
                return 1;
            else
                return 2;
        }
        else if((mSongInfo.accompany_track.equals(index2))&&(mSongInfo.karaoke_track.equals(index1)))
        {
            if(type==Original)
                return 1;
            else
                return 2;
        }
        else if((mSongInfo.accompany_track.equals(index2))&&(mSongInfo.karaoke_track.equals(index3)))
        {
            if(type==Original)
                return 2;
            else
                return 1;
        }
        else if(mSongInfo.accompany_track.equals(left))
        {
            if(type==Original)
                return 2;
            else
                return 1;
        }
        else if(mSongInfo.accompany_track.equals(right))
        {
            if(type==Original)
                return 1;
            else
                return 2;
        }
        else if((mSongInfo.accompany_track.equals(index1))&&(mSongInfo.karaoke_track.equals(index1)))
        {
            return 2;
        }
        else
        {
            if(type==Original)
                return 1;
            else
                return 2;
        }
    }

    @Override
    public synchronized boolean onstopproc()
    {
//        if(super.onstopproc(ioutdo))
//        {
//            if(null != iOutProc)
//                iOutProc.onstop();
//            return true;
//        }


        if(null != iOutProc)
            iOutProc.onstop();

        return true;
    }
}

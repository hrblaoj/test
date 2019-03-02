package com.shinetvbox.vod.dao;

import android.os.RemoteException;
import android.util.Log;

import com.shinetvbox.vod.R;
import com.shinetvbox.vod.certercontrol.ToastCenter;
import com.shinetvbox.vod.db.SongInfo;
import com.shinetvbox.vod.manager.DatabaseManager;
import com.shinetvbox.vod.manager.MemberManager;
import com.shinetvbox.vod.manager.ResManager;
import com.shinetvbox.vod.manager.SongPlayManager;
import com.shinetvbox.vod.service.wechat.WechatService;
import com.shinetvbox.vod.utils.KtvLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import static com.shinetvbox.vod.MyApplication.SHINESONGDDIR;
import static com.shinetvbox.vod.MyApplication.configIni;
import static com.shinetvbox.vod.utils.OnclickUtil.isFastDoubleClick;


/**
 * Created by hrblaoj on 2018/4/2.
 */

public class SelectSong extends PlaySong  {
    private CopyOnWriteArrayList<SongInfo> SelectSongCacheList= new CopyOnWriteArrayList<SongInfo>();
    private CopyOnWriteArrayList<SongInfo> SungSongCacheList= new CopyOnWriteArrayList<SongInfo>();

    SongInfo mSongInfo;
    private int defaultvolume = 50;
    private boolean volumefollow = true;
    private boolean isAllowvolumefollow = true;

    private ReentrantLock mListLock = new ReentrantLock();
    private static  int SELECT_LIMIT = 200;

    private static SelectSong sInstance = null;

    public static SelectSong getInstance(){
        if(sInstance == null) {
            sInstance = new SelectSong();
        }
        return sInstance;

    }

    public void setVolumefollow(boolean ret){
        volumefollow = ret;
    }

    public void setdefaultvolume(int volume){
        defaultvolume = volume;
    }
    public SelectSong()
    {
//        CloudMessageProc.getInTance().new cloudRecvProc(GET_SONG_OK) {
//            @Override
//            public void onProc(CloudDownloadSongStruce obj) {
//                if(null != iOutProc) {
//                    ToastCenter.getInTance().sendToastEvent("下载成功");
//                    updateDateBase(obj.content.toLowerCase().replace( ".mpg","" ));
//                    iOutProc.onplay(mSongInfo.song_id);
//                }
//            }
//        };
//        CloudMessageProc.getInTance().new cloudRecvProc(GET_SONG_FAILED) {
//            @Override
//            public void onProc(CloudDownloadSongStruce obj) {
//                String code = getErrorCode(obj.content);
//                if(null != iOutProc) {
//                    if(code.equals( "2" )){
//                        ToastCenter.getInTance().sendToastEvent("今天下载次数已用完，下载失败!");
//                    }else{
//                        ToastCenter.getInTance().sendToastEvent("下载失败!");
//                    }
//                    iOutProc.onstop();
////                    ControlCenter.sendEmptyMessage( ControlCenterConstants.SONG_PLAY_NEXT );
//                }
//            }
//        };
//
//
//        CloudMessageProc.getInTance().new cloudRecvProc(SET_DOWNLOAD_SONG_PERCENT) {
//            @Override
//            public void onProc(CloudDownloadSongStruce obj) {
//
//                String content = obj.getContent();
//                int pos = content.indexOf(':');
//                if(-1 == pos)
//                    return;
//
//                String jd = content.substring(pos, content.length());
//
//                ToastCenter.getInTance().sendToastEvent("下载进度"+jd+"%");
//
//            }
//        };
        String ret;
        ret = (String) configIni.get("SOUND", "VALUEADJUST", "1");
        if(ret.equals(new String("1"))){
            volumefollow = true;
        }
        else {
            volumefollow = false;
        }

        String volume = (String) configIni.get("SOUND", "MUSICVALUEDEFAULT", "50");
        defaultvolume = Integer.parseInt(volume);
       // mContext = context;
//        SongInfo mSongInfo = new SongInfo();
//        mSongInfo.setsongid("661233");
//        SelectSongCacheList.add(mSongInfo);
//
//        mSongInfo.setsongid("790066");
//        SelectSongCacheList.add(mSongInfo);

//        mSongInfo.setsongid("661233");
//        mPublicSongCacheList.add(mSongInfo);
    }

//    private String getErrorCode(String str){
//        String code = "";
//        Pattern pattern = Pattern.compile("code=(.+?)");
//        Matcher matcher = pattern.matcher(str);
//        while(matcher.find()){
//            if(!code.equals( "" )) break;
//            code = matcher.group(1);
//        }
//        return code;
//    }
    private boolean updateDateBase(String id){
        if(DatabaseManager.getInstance().getDbService() == null) return false;
        try {
            String sql = String.format("update song set local_path='0' where song_id='%s'", id);
            DatabaseManager.getInstance().getDbService().executeSqlStatement(sql);

            String newsong_sql = String.format("update new_song set local_path='0' where song_id='%s'", id);
            DatabaseManager.getInstance().getDbService().executeSqlStatement(newsong_sql);

            String topsong_sql = String.format("update top_song set local_path='0' where song_id='%s'", id);
            DatabaseManager.getInstance().getDbService().executeSqlStatement(topsong_sql);;
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override
    public synchronized SongInfo getmSomgInfo() {

        if(null != mSongInfo)
            return mSongInfo.clone();
        else
            return null;
    }

    public synchronized String getsongname() {

        if(null != mSongInfo)
            return mSongInfo.song_name;
        else
            return null;
    }

    public synchronized int getSelectSongConut() {

        return SelectSongCacheList.size();
    }

    public synchronized int getSungSongConut() {

        return SungSongCacheList.size();
    }

    public synchronized String getsongid() {

        if(null != mSongInfo)
            return mSongInfo.song_id;
        else
            return null;
    }

    public synchronized int  GetIndexById(String song_id) {
        int index = 0;
        for(SongInfo t : SelectSongCacheList) {
            if(t.song_id.equals(song_id))
                return index;
            index++;
        }
        return -1;
    }
    public synchronized boolean delSelected(String song_id){
        for(SongInfo t:SelectSongCacheList)
        {
            if(t.song_id.equals(song_id)){
                SelectSongCacheList.remove(t);
            }
        }

        iOutProc.onrefreshsonginfo();

        return true;
    }

    public synchronized boolean cleanSelected(){
        SelectSongCacheList.clear();
        iOutProc.onrefreshsonginfo();
        return true;
    }

    public synchronized boolean upsetSelected(){
        Collections.shuffle(SelectSongCacheList);
        iOutProc.onrefreshsonginfo();
        return true;
    }
    public synchronized boolean cleanSungSong(){
        SungSongCacheList.clear();
        iOutProc.onrefreshsonginfo();
        return true;
    }
    /**
     * 添加已点歌曲
     */
    public synchronized boolean addSelected(SongInfo selected) {

        if(!MemberManager.isMember && MemberManager.songPlayNumber<1) {
            ToastCenter.getInTance().sendToastEvent( ResManager.getInstance()
                    .getStringById( R.string.system_hint_sung_failure ) );
            return false;
        }

        File songFile = null;
        String songFileName = selected.song_id + ".mpg";
        songFile = new File(SHINESONGDDIR + songFileName);
        if(!songFile.exists()){
            SongPlayManager.addDownSong(selected);
            return false;
        }

        KtvLog.d("SelectSongSelectSong addSelected");
//        if(!isIsOpenRoom() && ConfigUtil.isEnableControlRoom()) {
//            Message sendmsg = new Message();
//            sendmsg.what = Constants.SHOW_DIALOG;
//            DialogParams param = new DialogParams( DialogManagerUtil.DIALOG_TYPE_GENERAL,ResManager.getInstance().getAppString( "open_room_title" ) , ResManager.getInstance().getAppString( "open_room_content" ),true,false,3000 );
//            sendmsg.obj = param;
//            MainActivity.handler.sendMessage( sendmsg );
//            return false;
//        }
        int count = SelectSongCacheList.size();
        if(isFastDoubleClick() &&  ((FreeSong.getInstance() == getCurPlaySong() && 0 == count)))
            return false;
        if (selected == null) {
            Log.d("peter", "addSelected path=null, fail!!!");
            return false;
        }

        if(count >= SELECT_LIMIT){
                return false;
        }

        SELECT_LIMIT--;

        for(SongInfo t:SelectSongCacheList){
            if(t.song_id.equals(selected.song_id))
                return false;
        }

        SelectSongCacheList.add(selected);

        this.isPlay = true;

        iOutProc.onrefreshsonginfo();

        if(FreeSong.getInstance() == getCurPlaySong() && 0 == count) {
            KtvLog.d("SelectSongSelectSong addSelected onstop");
            iOutProc.onstop();
        }
        return true;
    }

    /**
     * 添加已点歌曲
     */
    public synchronized boolean prioritySong(SongInfo selected) {

        if(!MemberManager.isMember && MemberManager.songPlayNumber<1) {
            ToastCenter.getInTance().sendToastEvent( ResManager.getInstance()
                    .getStringById( R.string.system_hint_sung_failure ) );
            return false;
        }

        File songFile = null;
        String songFileName = selected.song_id + ".mpg";
        songFile = new File(SHINESONGDDIR + songFileName);
        if(!songFile.exists()){
            SongPlayManager.addDownSongPriority(selected);
            return false;
        }

        int count = SelectSongCacheList.size();

        if(isFastDoubleClick() &&  ((FreeSong.getInstance() == getCurPlaySong() && 0 == count)))
            return false;

        if (selected == null) {
            Log.d("peter", "addSelected path=null, fail!!!");
            return false;
        }

        if(count >= SELECT_LIMIT){
            return false;
        }

        boolean inList = false;
        int pos = 0;
        int i = 0;
        for(SongInfo t:SelectSongCacheList){
            if(t.song_id.equals(selected.song_id)) {
                pos = i;
                inList = true;
                break;
            }
            i++;
        }

        if(0 != pos) {
            SelectSongCacheList.add(0, selected);
            SelectSongCacheList.remove(pos + 1);
        }
        else if(!inList)
        {
            SelectSongCacheList.add(0, selected);
        }

        this.isPlay = true;

        iOutProc.onrefreshsonginfo();

        if(FreeSong.getInstance() == getCurPlaySong() && 0 == count)
            iOutProc.onstop();

        return true;
    }


    public synchronized boolean playSong(SongInfo selected) {

        if(!MemberManager.isMember && MemberManager.songPlayNumber<1) {
            ToastCenter.getInTance().sendToastEvent( ResManager.getInstance()
                    .getStringById( R.string.system_hint_sung_failure ) );
            return false;
        }

        File songFile = null;
        String songFileName = selected.song_id + ".mpg";
        songFile = new File(SHINESONGDDIR + songFileName);
        if(!songFile.exists()){
            SongPlayManager.addDownSongPriority(selected);
            return false;
        }

        KtvLog.d("SelectSongSelectSong playSong");
        int count = SelectSongCacheList.size();

        if(isFastDoubleClick() &&  ((FreeSong.getInstance() == getCurPlaySong() && 0 == count)))
            return false;

        if (selected == null) {
            Log.d("peter", "addSelected path=null, fail!!!");
            return false;
        }

        if(count >= SELECT_LIMIT){
            return false;
        }

        boolean inList = false;
        int pos = 0;
        int i = 0;
        for(SongInfo t:SelectSongCacheList){
            if(t.song_id.equals(selected.song_id)) {
                pos = i;
                inList = true;
                break;
            }
            i++;
        }

        if(0 != pos) {
            SelectSongCacheList.add(0, selected);
            SelectSongCacheList.remove(pos + 1);
        }
        else if(!inList)
        {
            SelectSongCacheList.add(0, selected);
        }

        this.isPlay = true;

        iOutProc.onrefreshsonginfo();
        iOutProc.onstop();

        return true;
    }

    public synchronized String getnextsongname() {
        String songname = null;
        int count = SelectSongCacheList.size();
        if(0 >= count)
        {
            return null;
        }
        songname = new String(SelectSongCacheList.get(0).get_songname());
        return songname;
    }
    @Override
    public int gettracknum(int type) {
//        unsigned char data[4] = {0xE5, 0xB7, 0xA6, 0x00};
        String left = new String("左");
        String right = new String("右");
        String index1 = "1";
        String index2 = "2";
        String index3 = "3";
        String index4 = "4";

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

    public synchronized List<SongInfo> getSelectSongListByPage(int page, int num){
        int size = 0;
        int index = 0;
        if(0 >= (size = SelectSongCacheList.size()))
            return null;
        int i = 0;
        List<SongInfo> songInfoListTemp =  new ArrayList<>();
        for(i = 0; i < num; i++){
            index = page*num + i;
            if(index >= size || index < 0) {
                break;
            }
            songInfoListTemp.add(i, SelectSongCacheList.get(index).clone());
        }
        return songInfoListTemp;
    }

    public synchronized List<SongInfo> getSungSongListByPage(int page, int num){
        int size = 0;
        int index = 0;
        if(0 == (size = SungSongCacheList.size()))
            return null;
        int i = 0;
        List<SongInfo> songInfoListTemp =  new ArrayList<>();
        for(i = 0; i < num; i++){
            if((index = page*num + i) >= size)
                break;
            songInfoListTemp.add(i, SungSongCacheList.get(index).clone());
        }

        return songInfoListTemp;
    }
    @Override
    public synchronized void onstartproc()
    {
        int selectconut = 0;
        if(0 != (selectconut = SelectSongCacheList.size())) {
            mSongInfo = SelectSongCacheList.get(0);
            SungSongCacheList.add(mSongInfo.clone());
            SelectSongCacheList.remove(0);

            File songFile = null;
            String songFileName = mSongInfo.song_id + ".mpg";
            songFile = new File(SHINESONGDDIR + songFileName);
//            if(!songFile.exists()){
//
//                if(!CloudMessageProc.getInTance().getSendDownSongAllow2()){
//                    ToastCenter.getInTance().sendToastEvent("下载服务启动中，请稍后重试");
//                    iOutProc.onstop();
//                    return;
//                }
//                MemberManager.songPlayNumber--;
//                    CloudMessageProc.getInTance().cloudSend4Out(new CloudDownloadSongStruce(GET_SONG, songFileName), new CloudMessageProc.onSendFailedProc() {
//                        @Override
//                        public void onSendFailedProc() {
////                            KtvLog.d("procccccccc");
//                        }
//                    });
//                if(null != iOutProc) {
//                    iOutProc.onreset();
//                    ToastCenter.getInTance().sendToastEvent("开始下载");
//
////                    if(isAllowvolumefollow)
////                    {
////
////                    }
////                    else
////                    {
////                        if(!PlayStatus.getInstance().getIsMute())
////                            iOutProc.onsetvolume(defaultvolume);
////                    }
//
//
//                    if(WechatService.getInstance()!=null){
//                        WechatService.getInstance().sendSelectSongList();
//                    }
//                }
//                return;
//            }

//            final String nSongID = new String(mSongInfo.song_id);
//            final String nSingerID = new String(mSongInfo.singer_id1);

//            ThreadFactory.getNormalPool().execute(new Runnable(){
//                @Override
//                public void run() {
//
//                    //即将播放修改点击率
//                    Query nQ = new Query();
//                    nQ.song_id = nSongID;//mSongInfo.song_id;
//                    nQ.limit = 1;
//                    nQ.offset = 0;
//                    nQ.singer_id1 = nSingerID;
//
//                    try {
//                        List<Song> songList = MyApplication.getInstance().getDbService().querySong(nQ);
//                        int cnt = 0;
//                        if( !songList.get(0).word_head_code.equals("") ){
//                            cnt =  Integer.parseInt(songList.get(0).word_head_code);
//                        }
//                        String sql = new String();
//                        sql = String.format(  "update song set word_head_code=%d where song_id='%s'", cnt+1, nQ.song_id );
//                        MyApplication.getInstance().getDbService().executeSqlStatement(sql);
//
//                        //数据库静默更新， 操作记录
//                        NoteStepSingleton.GetInstance().WriteStepToNote(NoteStepSingleton.NoteStep_PlaySong_CountNum, sql);
//
//                        String top_sql = new String();
//                        top_sql = String.format(  "update top_song set word_head_code=%d where song_id='%s'", cnt+1, nQ.song_id );
//                        MyApplication.getInstance().getDbService().executeSqlStatement(top_sql);
//
//                        //数据库静默更新， 操作记录
//                        NoteStepSingleton.GetInstance().WriteStepToNote(NoteStepSingleton.NoteStep_PlaySong_CountNum, top_sql);
//
//                        String new_sql = new String();
//                        new_sql = String.format(  "update new_song set word_head_code=%d where song_id='%s'", cnt+1, nQ.song_id );
//                        MyApplication.getInstance().getDbService().executeSqlStatement(new_sql);
//
//                        //数据库静默更新， 操作记录
//                        NoteStepSingleton.GetInstance().WriteStepToNote(NoteStepSingleton.NoteStep_PlaySong_CountNum, new_sql);
//
//
//                        //改变歌星 local_click_rank 本地排行
//                        List<Singer> nSingerList = null;
//                        SingerQuery nQuery = new SingerQuery(); //数据库序列
//                        nQuery.singer_id=nSingerID;
//                        nQuery.limit = 1;
//                        try {
//                            if( !nQuery.singer_id.equals("") ) {
//                                nSingerList = MyApplication.getInstance().getDbService().querySinger(nQuery);
//                            }
//                        } catch (RemoteException e) {
//                            e.printStackTrace();
//                        }
//                        if( nSingerList != null && nSingerList.size() > 0 ) {
//                            int nClkNum =   Integer.parseInt(nSingerList.get(0).local_click_rank);
//                            String sqlSingerLocalClk = new String();
//                            sqlSingerLocalClk = String.format("update singer set local_click_rank=%d where singer_id='%s'", nClkNum  + 1, nQ.singer_id1);
//                            MyApplication.getInstance().getDbService().executeSqlStatement(sqlSingerLocalClk);
//
//                            //数据库静默更新， 操作记录
//                            NoteStepSingleton.GetInstance().WriteStepToNote(NoteStepSingleton.NoteStep_PlaySong_CountNum, sqlSingerLocalClk);
//                        }
//
//                    }
//                    catch ( Exception e ){
//
//                    }
//
//                }
//            });

//            for(SongInfo t2 : SelectSongCacheList)
//            {
//
//                KtvLog.d("t2.song_id " + t2.song_id);
//
//            }

            KtvLog.d("t3.song_id " + mSongInfo.song_id + " selectconut is " + selectconut);
            //mContext.setPlaySongSelect();
            //mContext.playvideo(mSongInfo.song_id);


            if(null != iOutProc) {
                MemberManager.songPlayNumber--;
                iOutProc.onplay(mSongInfo.song_id);
                if(WechatService.getInstance()!=null){
                    WechatService.getInstance().sendSelectSongList();
                }

//                if(isAllowvolumefollow)
//                {
//
//                }
//                else
//                {
//                    if(!PlayStatus.getInstance().getIsMute())
//                        iOutProc.onsetvolume(defaultvolume);
//                }
            }
        }
    }

    @Override
    public void updatepre() {
        int count = SelectSongCacheList.size();
        if(0 == count)
        {
            this.isPlay = false;
        }

        if(this == getCurPlaySong() && volumefollow)
            isAllowvolumefollow = true;
        else
            isAllowvolumefollow = false;
    }

    @Override
    public synchronized boolean onstopproc()
    {
        if(0 == SelectSongCacheList.size())
        {
            this.isPlay = false;
        }

        if(null != iOutProc)
            iOutProc.onstop();

        return true;
    }


    final public synchronized SongInfo  GetSelectSongCacheList(int index){
        return SelectSongCacheList.get(index);
    }

    public String GetSelectSongCacheListFormatString(){
        String nStrMsg = "";

        ArrayList<SongInfo> nList = new ArrayList<SongInfo>();

        synchronized(this) {
            long time = System.nanoTime();
            for (int i = 0; i < SelectSongCacheList.size(); i++) {
                nList.add(SelectSongCacheList.get(i).clone());
            }
            KtvLog.d(" GetSelectSongCacheListFormatString timeprocess is " +  (System.nanoTime() - time));
        }

        for(int i=0; i<nList.size(); i++)
        {
            SongInfo nSng = nList.get(i);
            if(i != 0) //在每个{}节点后添加 逗号
            {
                nStrMsg +=",";
            }
            String nTmpSnd = String.format("{\"id\":\"%s\",\"name\":\"%s\",\"language\":\"%s\",\"version\":\"%s\",\"singer\":\"%s\",\"flag\":\"%s\"}",
                    nSng .song_id,nSng.song_name,nSng .language,
                    nSng .song_version,nSng .singer_name,"s");
            nStrMsg += nTmpSnd;
        }
        return nStrMsg;

    }
}
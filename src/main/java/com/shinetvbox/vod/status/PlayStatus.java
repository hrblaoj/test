package com.shinetvbox.vod.status;




import com.shinetvbox.vod.VideoView;
import com.shinetvbox.vod.utils.KtvLog;
import com.shinetvbox.vod.utils.volume.VolumeConfig;

import java.util.ArrayList;

/**
 * Created by hrblaoj on 2018/4/10.
 */

public class PlayStatus {
    private ArrayList<OnStatuChange> mOnStatuList = new ArrayList<OnStatuChange>();
    private boolean isMute = false;
    private VideoView mVideoView = null;
    private boolean isPause = false;
    private boolean isAccompany = false;
    public int holdvolume = 50;


    private static PlayStatus mInstance = null;
    public static PlayStatus getInstance(){
        if(mInstance == null) {
            mInstance = new PlayStatus();
        }
        return mInstance;
    }
    public interface OnStatuChange{
        public void setmute();
        public void setunmute();
        public void setpause();
        public void setcancelpause();
        public void setaccompany();
        public void setoriginal();
        public void changevolume(int vol);
    }

    public void registerOnStatuList(PlayStatus.OnStatuChange l){
        if(!mOnStatuList.contains(l)) {
            mOnStatuList.add(l);
        }
    }

    public void unRegisterOnStatuList(PlayStatus.OnStatuChange l){
        if(!mOnStatuList.contains(l)) {
            mOnStatuList.remove(l);
        }
    }

    public boolean getIsMute(){
        return isMute;
    }
    public boolean getIsPause(){
        return isPause;
    }
    public boolean getIsAccompany(){
        return isAccompany;
    }
    public void setIsMute(boolean mute){
        isMute = mute;
    }

    public void setholdvolume(int volume){
        holdvolume = volume;
    }
    public int getholdvolume(){
        return holdvolume;
    }
    public void changeVolnoClass(int vol, Class c[])
    {
        if (vol >100)
            vol = 100;

        if(vol < 0)
            vol = 0;
        VolumeConfig.setVolume(vol);

        boolean exist = false;

        for(OnStatuChange l : mOnStatuList){
            exist = false;

            for(Class t:c) {
                /* 两个if都可以 isInstance 可以替代instanceof */
                if(t.isInstance(l))
               //if(l.getClass() == t)
               {
                   exist = true;
                   break;
               }
            }

            if(!exist)
                l.changevolume(vol);
        }
        holdvolume = vol;
        if(0 != vol)
        {
            if (getIsMute()) {
                setunmute();
            }
        }
        else
        {
            if (!getIsMute()) {
                setmute();
            }
        }
    }

    public void changeVol(int vol)
    {
        if (vol >100)
            vol = 100;

        if(vol < 0)
            vol = 0;
        VolumeConfig.setVolume(vol);

        for(OnStatuChange l : mOnStatuList){
            l.changevolume(vol);
        }
        holdvolume = vol;
        if(0 != vol)
        {
            if (getIsMute()) {
                setunmute();
            }
        }
        else
        {
            if (!getIsMute()) {
                setmute();
            }
        }

//        String nSnd = WechatServiceSinglton.GetInstance().GetButtonStateUDP();
//        WechatServiceSinglton.GetInstance().NewUDPThreadSend(nSnd);

    }
    public void setmutenothis(OnStatuChange ll){
        for(OnStatuChange l : mOnStatuList){
            if(l != ll)
                l.setmute();
        }
    }

    public void setunmutenothis(OnStatuChange ll){
        for(OnStatuChange l : mOnStatuList){
            if(l != ll)
                l.setunmute();
        }
    }

    public void setmute(){
        isMute = true;
        //holdvolume = VolumeConfig.getVolume();
        for(OnStatuChange l : mOnStatuList){
            l.changevolume(0);
            l.setmute();
        }
    }

    public void setunmute(){
        isMute = false;
        for(OnStatuChange l : mOnStatuList){
            l.changevolume(holdvolume);
            l.setunmute();
        }
    }

    public void setpause(){
        if(isPause)
            return;
//        if(mVideoView.isVideoOpening()) {
//            KtvLog.d("setpause is isVideoOpening");
//            return;
//        }
        if(!mVideoView.isPlaying()) {
            KtvLog.d("setpause is isPlaying");
            return;
        }
        isPause = true;
        for(OnStatuChange l : mOnStatuList){
            l.setpause();
        }
    }

    public void setcancelpausenothis(OnStatuChange ll){
        if(!isPause)
            return;
        isPause = false;
        for(OnStatuChange l : mOnStatuList){
            if(l != ll)
                l.setcancelpause();
        }
    }

    public void setcancelpause(){
        if(!isPause)
            return;
//        if(mVideoView.isVideoOpening()){
//            KtvLog.d("setcancelpause is isVideoOpening");
//            return;
//        }
        if(!mVideoView.isPlaying()) {
            KtvLog.d("setcancelpause is isPlaying");
            return;
        }

        isPause = false;
        for(OnStatuChange l : mOnStatuList){
            l.setcancelpause();
        }
    }

    public void setVideoView(VideoView videoView){
        mVideoView = videoView;
    }

    public void setaccompany(){
        if(isPause)
            return;
        if(isAccompany)
            return;
        if(!mVideoView.isPlaying()) {
            KtvLog.d("setcancelpause is isPlaying");
            return;
        }
        isAccompany = true;
        for(OnStatuChange l : mOnStatuList){
            l.setaccompany();
        }
    }

    public void setoriginal(){
        if(isPause)
            return;
        if(!isAccompany)
            return;
        if(!mVideoView.isPlaying()) {
            KtvLog.d("setcancelpause is isPlaying");
            return;
        }
        isAccompany = false;
        for(OnStatuChange l : mOnStatuList){
            l.setoriginal();
        }
    }
}

package com.shinetvbox.vod.utils.volume;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;


import com.shinetvbox.vod.MyApplication;
import com.shinetvbox.vod.utils.KtvLog;

import java.util.ArrayList;
import java.util.List;


import static com.shinetvbox.vod.MyApplication.configIni;

public class VolumeConfig {
	private static boolean DBG = false;
	private static int mMaxVolume = 0;
	private static int mVolume = 0;
	private static int userMaxVolume = 0;
	private static boolean isMute = false;
	public static List<VolumeChangeLisener> listListener = new ArrayList<>(  );
	public static void setVolumeChangeLisener(VolumeChangeLisener lisener) {
		listListener.add( lisener );
	}

	public static void destory(){
		listListener.clear();
	}
	private static void sendMessageVolume(int vol){
		for(VolumeChangeLisener vl:listListener){
				vl.volumeChange( vol );
		}
	}
	static{
		String sMaxVolume = (String) configIni.get("CONFIG", "MUSICMAXVALUE", "100");
		userMaxVolume = Integer.parseInt(sMaxVolume);
	}
	public static void setUserMaxVolume(int volume){
		userMaxVolume = volume;
	}

	public static int getStoreVolume() {
		return mVolume;
	}

	public static void setStoreVolume(int volume) {
		VolumeConfig.mVolume = volume;
	}

	public static int getVolume(){
//		if(MainActivity.isMute) {
//			return mVolume;
//		}

		Context context = MyApplication.getInstance().getApplicationContext();
		if(context != null) {
			AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
			int vol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
			float v = mapVolumeSystem2App(vol);;
			//if(DBG) KtvLog.d("vol ="+vol+" v="+v);
			return (int)v;
		}
		return 50;
	}

	public static void setVolume(int vol){
//		if(MainActivity.isMute) {
//			setMute();
////			return;
//		}
		Context context = MyApplication.getInstance().getApplicationContext();
		if(context != null) {
			AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
			float v = mapVolumeApp2System(vol);
//			KtvLog.d("get am.getStreamVolume() " + am.getStreamVolume(AudioManager.STREAM_MUSIC));
			am.setStreamVolume(AudioManager.STREAM_MUSIC, (int)v, 0);
//			if(DBG)
			//sendMessageVolume(vol);
			//KtvLog.d("vol ="+vol+"  real vol="+am.getStreamVolume(AudioManager.STREAM_MUSIC)+"  max="+mMaxVolume+" v="+v);
		}
	}



	public static void setStreamMuteOrUnMute(){
		Context context = MyApplication.getInstance().getApplicationContext();
		if(context != null) {
			AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
//			if(!am.isStreamMute(AudioManager.STREAM_MUSIC)) {
//				KtvLog.d("setStreamMute true");
//				am.adjustStreamVolume(AudioManager.STREAM_MUSIC, ADJUST_UNMUTE, 0);
//			}
//			else {
//				KtvLog.d("setStreamMute false");
//				am.adjustStreamVolume(AudioManager.STREAM_MUSIC, ADJUST_MUTE, 0);
//			}
//			if(!isMute) {
//				am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
//				storeVolume(getVolume());
//			}
//			else
//			{
//				am.setStreamVolume(AudioManager.STREAM_MUSIC, mVolume, 0);
//			}
		}
	}

	public static int holdvolume = 0;
	public static int setHoldVolume(){
		Context context = MyApplication.getInstance().getApplicationContext();
		if(context != null) {
			AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
			holdvolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		}

		return holdvolume;
	}
	public static void setMute(){
		Context context = MyApplication.getInstance().getApplicationContext();
		if(context != null) {
			AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
			holdvolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
			am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
		}
	}

	public static void setUnMute(){
		Context context = MyApplication.getInstance().getApplicationContext();
		if(context != null) {
			AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
			am.setStreamVolume(AudioManager.STREAM_MUSIC, holdvolume, holdvolume);
		}
	}

	public static int mapVolumeApp2System(int appVolume){
		int max = getMaxVolume();
		int ret = (int) Math.ceil((float)appVolume/100*max*((float)userMaxVolume/100f));

		//int ret = (appVolume* max + appVolume* max %100)/100;
//		KtvLog.d("vvvvvvv111 is " + ret);
		return ret;
	}

	public static int mapVolumeSystem2App(int systemVolume){
		int max = getMaxVolume();
		int ret = (int) Math.ceil((float)systemVolume/max*100);

		//int ret = (systemVolume * 100 + 100 % 75)/ max;
//		if(0 != systemVolume * 100 % max)
//			ret++;
//		KtvLog.d("vvvvvvv2222 is " + ret);
		return ret;
	}

//	public static int mapVolumeApp2System(int appVolume){
//		int max = getMaxVolume();
//		int area = (int)((float)max / 5 * 5);
//		int gap = max - area;
//		int ret;
//		if(appVolume >= 10) {
//			ret = (int) Math.rint((float)appVolume/100*area) + gap;
//		} else {
//			ret = 0;
//		}
//		KtvLog.d("ret="+ret+" appVolume="+appVolume);
//		return ret;
//	}
//	public static int mapVolumeSystem2App(int systemVolume){
//		int max = getMaxVolume();
//		int area = (int)((float)max / 5 * 5);
//		int gap = max - area;
//		int ret;
//		if(systemVolume == 0) {
//			ret = 0;
//		}else if(systemVolume <= gap) {
//			ret = 10;
//		} else {
//			ret = (int) Math.rint((float)(systemVolume - gap) /area * 100);
//		}
//
//		if(DBG)KtvLog.d("ret="+ret+" systemVolume="+systemVolume);
//		return ret;
//	}
	
	private static int getMaxVolume(){
		if(mMaxVolume == 0){
			Context context = MyApplication.getInstance().getApplicationContext();
			if(context != null) {
				AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
				mMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			}
		}
//		KtvLog.d("getMaxVolume= "+mMaxVolume);
		return mMaxVolume;
	}


	public interface VolumeChangeLisener{
		void volumeChange(int vol);
	}
}

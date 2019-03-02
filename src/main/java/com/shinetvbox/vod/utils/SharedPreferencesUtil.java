package com.shinetvbox.vod.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.shinetvbox.vod.MyApplication;
import com.shinetvbox.vod.manager.LanguageManager;

/**
 * SharedPreferences工具类<br>
 * SharedPreferences文件只有卸载后才清除<br>
 * 存放的路径 /data/data/com.shinetvbox.vod/shared_prefs/shinetvbox_data.xml
 */
public class SharedPreferencesUtil {
	/**APP SharedPreferences文件名*/
	private static final String SP_DATA_NAME = "shinetvbox_data";

	/**是否是第一次启动*/
	private static String FIRST_BOOT = "first_boot";
	/**语言key值*/
	public static final String LANGAGE_KEY = "language_key";
	/**歌星图片是否解压成功*/
	private static String SINGER_IMAGE_UNZIP_SUCCESS = "singer_image_unzip_success";
	/**歌星图片是否解压成功*/
	private static String DATABASE_IS_COPY = "database_is_copy";

	/**
	 * 设置是否是第一次启动
	 * @param firstBoot 值
	 */
	public static void setFirstBoot(boolean firstBoot){
		if(MyApplication.getInstance().getApplicationContext() == null) return;
		SharedPreferences sp = MyApplication.getInstance().getApplicationContext().getSharedPreferences(SP_DATA_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(FIRST_BOOT, firstBoot);
		editor.commit();
	}
	/**
	 * 获取是否是第一次启动
	 * @return
	 */
	public static boolean isFirstBoot(){
		if(MyApplication.getInstance().getApplicationContext() == null) return true;
		SharedPreferences sp = MyApplication.getInstance().getApplicationContext().getSharedPreferences(SP_DATA_NAME, Context.MODE_PRIVATE);
		return sp.getBoolean(FIRST_BOOT, true);
	}

	/**
	 * 设置语言的key
	 * @param value 0中文，1英文
	 */
	public static void setLangage_key(String value){
		if(MyApplication.getInstance().getApplicationContext() == null) return;
		SharedPreferences sp = MyApplication.getInstance().getApplicationContext().getSharedPreferences(SP_DATA_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(LANGAGE_KEY, value);
		editor.commit();
	}
	/**
	 * 获取语言的key
	 * @return 0中文，1英文
	 */
	public static String getLangage_key(){
		if(MyApplication.getInstance().getApplicationContext() == null) return null;
		SharedPreferences sp = MyApplication.getInstance().getApplicationContext().getSharedPreferences(SP_DATA_NAME, Context.MODE_PRIVATE);
		return sp.getString(LANGAGE_KEY, LanguageManager.ZH_CN);
	}


	/**
	 * 设置歌星图片是否解压
	 * @param isCopy 值
	 */
	public static void setCopyDatabase(boolean isCopy){
		if(MyApplication.getInstance().getApplicationContext() == null) return;
		SharedPreferences sp = MyApplication.getInstance().getApplicationContext().getSharedPreferences(SP_DATA_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(DATABASE_IS_COPY, isCopy);
		editor.commit();
	}
	/**
	 * 获取歌星图片是否解压
	 * @return
	 */
	public static boolean isCopyDatabase(){
		if(MyApplication.getInstance().getApplicationContext() == null) return true;
		SharedPreferences sp = MyApplication.getInstance().getApplicationContext().getSharedPreferences(SP_DATA_NAME, Context.MODE_PRIVATE);
		return sp.getBoolean(DATABASE_IS_COPY, false);
	}

	/**
	 * 设置歌星图片是否解压
	 * @param isUnzip 值
	 */
	public static void setSingerImageUnzip(boolean isUnzip){
		if(MyApplication.getInstance().getApplicationContext() == null) return;
		SharedPreferences sp = MyApplication.getInstance().getApplicationContext().getSharedPreferences(SP_DATA_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(SINGER_IMAGE_UNZIP_SUCCESS, isUnzip);
		editor.commit();
	}
	/**
	 * 获取歌星图片是否解压
	 * @return
	 */
	public static boolean isSingerImageUnzip(){
		if(MyApplication.getInstance().getApplicationContext() == null) return true;
		SharedPreferences sp = MyApplication.getInstance().getApplicationContext().getSharedPreferences(SP_DATA_NAME, Context.MODE_PRIVATE);
		return sp.getBoolean(SINGER_IMAGE_UNZIP_SUCCESS, false);
	}

}
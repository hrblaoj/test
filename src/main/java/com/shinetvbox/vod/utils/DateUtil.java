package com.shinetvbox.vod.utils;

import android.util.Log;

import com.shinetvbox.vod.manager.MemberManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	private static DateUtil sInstance = null;

	/**
	 * 计算每秒执行了多少次
	 */
	public class frame{

		long startTime = System.nanoTime();
		int frames = 0;
		String tag = null;
		public frame(String tag){
			this.tag = tag;
		}
		public void logFrame() {
			frames++;
			if(System.nanoTime() - startTime >= 1000000000) {
				Log.d(tag + " FPSCounter", "fps: " + frames);
				frames = 0;
				startTime = System.nanoTime();
			}
		}

	}


	public static DateUtil getInstance(){
		if(sInstance == null){
			sInstance = new DateUtil();
			sInstance.init();
		}
		return sInstance;
	}
	
	private String time = "";
	private String yearAgoTime = "";
	public void init(){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		//当前系统时间
		long tmptime = 0;
		Date date = new Date();
		time = format.format(date);

		if(date.getTime() != 0) {
			tmptime = date.getTime() - (long) 12 * 30 * 24 * 3600 * 1000;
			date.setTime(tmptime);
			yearAgoTime = format.format(date);
		}
		else{
			yearAgoTime = "2017-01-01";
		}


		KtvLog.d("Cur time is " + time + " yearAgoTime is " + yearAgoTime);
	}
	
	/**
	 * 获取静态时间
	 * @return
	 */
	public String getStaticTime(){
		return time;
	}

	public String getStaticYearAgoTime(){
		return yearAgoTime;
	}
	
	/**
	 * 获取当前时间
	 * @return
	 */
	public static String getCurrentTime(){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		//当前系统时间
		Date date = new Date();
		String time = format.format(date);
		return time;
	}

	/**
	 * 获取指定时间
	 * 单位毫秒
	 * @return
	 */
	public static String getAppointTime(long time){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		//当前系统时间
		Date date = new Date(time);
		String str = format.format(date);
		return str;
	}

	/**
	 * 获取当前时间以秒为单位
	 * @return
	 */
	public static String getTimeInSecond(){
		//当前系统时间
		Date date = new Date();
		return "" + (int)(date.getTime()/1000);
	}

	/**
	 * 获取当前服务器时间以秒为单位
	 * @return
	 */
	public static String getServerTimeInSecond(){
		//服务器时间
		long a = (System.currentTimeMillis() - MemberManager.localTime)/1000;
		return "" + (MemberManager.serverTime+a);
	}

	/**
	 * 是否过期
	 * 当前时间小于云端设置时间，未过期
	 * @return
	 */
	public static boolean isDateExpires(){
		String str = "2018/04/14";
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		try {
			//当前系统时间
			Date date = new Date();
			String time = format.format(date);
			//云端获取时间
			Date cloudDate = format.parse(str);
			String cloudtime = format.format(cloudDate);

			if (date.getTime() <= cloudDate.getTime()) {
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	/**
	 * 比较时间 
	 * @param dt1 Date
	 * @param dt2 Date
	 * @return
	 */
	public static int compareDate(Date dt1, Date dt2){
        if (dt1.getTime() > dt2.getTime()) {
            System.out.println("dt1 在dt2前");
            return 1;
        } else if (dt1.getTime() < dt2.getTime()) {
            System.out.println("dt1在dt2后");
            return -1;
        } else {//相等
            return 0;
        }
	}
}

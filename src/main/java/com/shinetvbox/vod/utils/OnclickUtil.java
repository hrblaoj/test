package com.shinetvbox.vod.utils;

/**
 * 两次点击时间相隔小于1000ms,便不会触发事件
 * 
 * @author lxh
 * @date 2015-6-16
 */
public class OnclickUtil {
	private static long lastClickTime;
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 400) {
			KtvLog.d("----------点击过于频繁!!!");
			return true;
		}
		lastClickTime = time;
		return false;
	}
	
	private static long lastClickTimeDelay;
	/**
	 * 防止快速点击
	 * @param delay 间隔时间
	 * @return
	 */
	public static boolean isFastDoubleClickDelay(long delay) {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTimeDelay;
		if (0 < timeD && timeD < delay) {
			KtvLog.d("间隔时间:"+delay+"---------点击过于频繁!!!");
			return true;
		}
		lastClickTimeDelay = time;
		return false;
	}
}

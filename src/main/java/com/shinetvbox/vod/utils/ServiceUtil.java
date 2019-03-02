package com.shinetvbox.vod.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public class ServiceUtil {
	/**
	 * 判断某个服务是否正在运行的方法
	 * 
	 * @param mContext
	 * @param serviceName
	 *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
	 * @return true代表正在运行，false代表服务没有正在运行
	 */
	public static boolean isServiceWork(Context mContext, String serviceName) {
		boolean isWork = false;
		ActivityManager myAM = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> myList = myAM.getRunningServices(40);
		if (myList.size() <= 0) {
			return false;
		}
		for (int i = 0; i < myList.size(); i++) {
			String mName = myList.get(i).process;
			if (mName.equals(serviceName)) {
				isWork = true;
				break;
			}
		}
		return isWork;
	}

	public static String getRunningActivityPkgName(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity
				.getPackageName();
		return runningActivity;
	}
	
	public static String getRunningActivityName(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		String activityName = activityManager.getRunningTasks(1).get(0).topActivity
				.getClassName();
		return activityName;
	}
	
	public static void startActivity(Context context, Intent intent){
		int count = 0;
		//如果视频正在打开，就进去其他app，就会出现内存泄漏的情况
		//解决方式：视屏正在打开，则在主线程最多等待2秒，就进去其他app
		//hile(MyApplication.isVideoOpening || MyApplication.isPreviewVideoOpening){
//		while(true){
//			if(count > 9){
//				break;
//			}
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				break;
//			}
//
//			count++;
//		};
		context.startActivity(intent);
	}
}

package com.shinetvbox.vod.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.hardware.input.InputManager;
import android.os.PowerManager;
import android.view.InputEvent;
import android.view.WindowManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

public class KtvSystemApi {
	public static final int INJECT_INPUT_EVENT_MODE_ASYNC = 0;
	public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT = 1;
	public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH = 2;
	/**
	 * 重启系统
	 * */
//	public static void reboot(Context context){
//		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//		Invoke.invokeMethod(pm, "reboot", new Object[]{}, new Class[]{});
//	}
//
//	/**
//	 * 关机
//	 * */
//	public static void shutdown(Context context){
//		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//		Invoke.invokeMethod(pm, "shutdown", new Object[]{}, new Class[]{});
//	}
//
//	/**
//	 * 发送按键(KeyEvent)、触摸(MotionEvent)事件
//	 *
//	 * @param event 要发送的事件
//	 * @param mode 发送模式，三种模式：
//	 * <br>KtvSystemApi.INJECT_INPUT_EVENT_MODE_ASYNC, 异步模式，马上返回结果
//	 * <br>KtvSystemApi.INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT， 等待发送完
//	 * <br>KtvSystemApi.INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH，等待消息处理完
//	 * <br>一般用第一种异步模式
//	 * */
//	public static boolean injectInputEvent2(Context context, InputEvent event, int mode) {
//		InputManager im = (InputManager) context.getSystemService(Context.INPUT_SERVICE);
//		Object obj = Invoke.invokeMethod(im, "injectInputEvent2", new Object[]{event, mode}, new Class[]{InputEvent.class, int.class});
//		if(obj == null) {
//			return false;
//		} else {
//			return (Boolean)obj;
//		}
//	}
//
//	/**
//	 * 获取系统SN号
//	 * */
//	public static String getSn(Context context){
//		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//		Object obj = Invoke.invokeMethod(wm, "getSn", new Object[]{}, new Class[]{});
//		if(obj == null) {
//			return "";
//		} else {
//			return (String)obj;
//		}
//	}
	
	/**
	 * 获取系统MAC号
	 * */
	public static String getEthMac(Context context){

		String mac = "";
		File file = new File("/sys/class/net/eth0/address");
		Reader reader = null;
		FileInputStream in = null;
		try {
			// 一次读一个字符
			reader = new InputStreamReader(in = new FileInputStream(file));
			int tempchar;
			while ((tempchar = reader.read()) != -1) {
				// 对于windows下，\r\n这两个字符在一起时，表示一个换行。
				// 但如果这两个字符分开显示时，会换两次行。
				// 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
				if (((char) tempchar) != '\r' && ((char) tempchar) != '\n' && ((char) tempchar) != ':') {
					mac += (char)(tempchar);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(null != reader)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(null != in)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			return mac;
		}


//		return "08d40c48245d"; //测试mac
//		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//		Object obj = Invoke.invokeMethod(wm, "getEthMac", new Object[]{}, new Class[]{});
//		if(obj == null) {
//			return "";
//		} else {
//			return (String)obj;
//		}
	}
	
	/**
	 * 设置系统属性
	 * 
	 * @param key
	 * @param value
	 * */
//	public static void setProp(Context context, String key, String value) {
//		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//		Invoke.invokeMethod(wm, "setProp", new Object[]{key, value}, new Class[]{String.class, String.class});
//	}
//
//	/**
//	 * 获取系统属性值
//	 *
//	 * @param key
//	 * */
//	public static String getProp(String key) {
//		Object obj = Invoke.getStaticMethod("android.os.SystemProperties",  "get", new Object[]{key}, new Class[]{String.class});
//		if(obj == null) {
//			return "";
//		} else {
//			return (String)obj;
//		}
//	}
//
//
//	/**
//	 * 获取当前运行Activity的包名
//	 * <br>
//	 * Tip：android7.0原生是没有权限获取其他当前运行的Activity任何信息
//	 * */
//	public static String getRunningActivity(Context context) {
//		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//		Object obj = Invoke.invokeMethod(wm, "getRunningActivity", new Object[]{}, new Class[]{});
//		if(obj == null) {
//			return "";
//		} else {
//			return (String)obj;
//		}
//	}
	
	/**
	 * 根据服务名，判断服务是否运行中
	 * <br>
	 * Tip：android7.0原生是没有权限获取其他其他服务运行的信息
	 * */
	public static boolean isRunningService(Context context, String serviceName) {
//		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//		Object obj = Invoke.invokeMethod(wm, "isRunningService", new Object[]{serviceName}, new Class[]{String.class});
//		if(obj == null) {
//			return false;
//		} else {
//			return (Boolean)obj;
//		}
		if (("").equals(serviceName) || serviceName == null)
			return false;
		ActivityManager myManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
				.getRunningServices(30);
		for (int i = 0; i < runningService.size(); i++) {
			KtvLog.d("runningService.get(i).service.getClassName().toString() name is " + runningService.get(i).service.getClassName().toString());
			if (runningService.get(i).service.getClassName().toString()
					.equals(serviceName)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 以System权限运行shell命令
	 * */
//	public static void execShellCmd(Context context, String cmd){
//
//		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//		Invoke.invokeMethod(wm, "execShellCmd", new Object[]{cmd}, new Class[]{String.class});
//	}
//
//	/**
//	 * 获取声音透传是否打开的状态
//	 * */
//	public static boolean getVolumeThrough(Context context){
//		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//		Object obj = Invoke.invokeMethod(wm, "getVolumeThrough", new Object[]{}, new Class[]{});
//		if(obj == null) {
//			return false;
//		} else {
//			return (Boolean)obj;
//		}
//	}
//
//	/**
//	 * 设置透传
//	 * */
//	public static void setVolumeThrough(Context context, boolean enable){
//		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//		Invoke.invokeMethod(wm, "getVolumeThrough", new Object[]{enable}, new Class[]{boolean.class});
//	}
//
//	/**
//	 * 设置系统静音
//	 * */
//	public static void setSystemMute(Context context, boolean enable){
//		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//		Invoke.invokeMethod(wm, "setSystemMute", new Object[]{enable}, new Class[]{boolean.class});
//	}
//
//	/**停止开机动画*/
//	public static void stopAnimation(Context context){
//		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//		Invoke.invokeMethod(wm, "stopAnimation", new Object[]{}, new Class[]{});
//	}
//
//	/**设置开机动画，下次启动有效
//	 *
//	 * @param path, 开机动画的绝对路径，视频分辨率不要超过1080P，建议视频时间比开机时间长10到20秒
//	 * 调用这个接口后，大概两分钟内不要关机，否则可能会设置失败
//	 * */
//	public static void setBootVideo(Context context, String path){
//		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//		Invoke.invokeMethod(wm, "setBootVideo", new Object[]{path}, new Class[]{String.class});
//	}
//
//	/**设置开机第一张Logo，下次启动有效
//	 *
//	 * @param path, Logo的绝对路径。Logo的图片格式有严格要求,一定要宽高是1280*720，位宽是32位的bmp格式的图片
//	 *
//	 * 调用这个接口后，大概一分钟内不要关机，否则可能会设置失败
//	 * */
//	public static void setLogo(Context context, String path){
//		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//		Invoke.invokeMethod(wm, "setLogo", new Object[]{path}, new Class[]{String.class});
//	}
}

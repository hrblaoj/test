package com.shinetvbox.vod.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Log工具，类似android.util.Log。 tag自动产生，格式:
 * customTagPrefix:className.methodName(Line:lineNumber),
 * customTagPrefix为空时只输出：className.methodName(Line:lineNumber)。
 * http://blog.csdn.net/finddreams
 */
public class KtvLog {
	public static String mCustomTagPrefix = null; // 自定义Tag的前缀，可以是作者名
	public static boolean isSaveLog = false; // 是否把保存日志到SD卡中
	private static final String ROOT = Environment.getExternalStorageDirectory()
	        .getPath() + File.separator; // SD卡中的根目录
	public static String PATH_LOG_INFO = ROOT + "info" + File.separator;
	public static String TAG = "ktv";
	public static int MessageTitleLen = 45;

	private KtvLog() {
	}

	// 容许打印日志的类型，默认是true，设置为false则不打印
	public static boolean mAllowD = true;// 打印debug信息
	public static boolean mAllowE = true;// 打印Error信息
	public static boolean mAllowI = true;// 打印Info信息
	public static boolean mAllowV = true;// Verbose
	public static boolean mAllowW = true;// Warm
	public static boolean mAllowWtf = true;
	
	/**
	 * 生成Log tag
	 * */
	private static String generateTag(StackTraceElement caller) {
		if(TAG!=null)return TAG;
		String tag = "%s.%s(Line:%d)"; // 占位符
		String callerClazzName = caller.getClassName(); // 获取到类名
		callerClazzName = callerClazzName.substring(callerClazzName
		        .lastIndexOf(".") + 1);
		tag = String.format(tag, callerClazzName, caller.getMethodName(),
		        caller.getLineNumber()); // 替换
		tag = TextUtils.isEmpty(mCustomTagPrefix) ? tag : mCustomTagPrefix + tag;
		return callerClazzName;
	}
	
	/**
	 * 生成Log 内容，增加函数名和行数信息。
	 * */
	private static String generateContent(String content, StackTraceElement caller) {
		String format = "%s(Line:%d)";
		String msg = String.format(format, caller.getMethodName(), caller.getLineNumber());
		String callerClazzName = caller.getClassName(); // 获取到类名
		callerClazzName = callerClazzName.substring(callerClazzName
		        .lastIndexOf(".") + 1);
		
		String ret = callerClazzName+"."+msg;
		int i=ret.length();
		for(; i<=MessageTitleLen; i++) {
			ret = ret+"=";
		}
		
		ret = ret+"== msg: "+content;
		return ret;
	}

	/**
	 * 自定义的logger
	 */
	public static CustomLogger customLogger;

	public interface CustomLogger {
		void d(String tag, String content);

		void d(String tag, String content, Throwable tr);

		void e(String tag, String content);

		void e(String tag, String content, Throwable tr);

		void i(String tag, String content);

		void i(String tag, String content, Throwable tr);

		void v(String tag, String content);

		void v(String tag, String content, Throwable tr);

		void w(String tag, String content);

		void w(String tag, String content, Throwable tr);

		void w(String tag, Throwable tr);

		void wtf(String tag, String content);

		void wtf(String tag, String content, Throwable tr);

		void wtf(String tag, Throwable tr);
	}

	public static void d(String content) {
		if (!mAllowD)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		content = generateContent(content, caller);

		if (customLogger != null) {
			customLogger.d(tag, content);
		} else {
			Log.d(tag, content);
		}
	}

	public static void d(String content, Throwable tr) {
		if (!mAllowD)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		content = generateContent(content, caller);
		
		if (customLogger != null) {
			customLogger.d(tag, content, tr);
		} else {
			Log.d(tag, content, tr);
		}
	}

	public static void e(String content) {
		if (!mAllowE)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		content = generateContent(content, caller);
		
		if (customLogger != null) {
			customLogger.e(tag, content);
		} else {
			Log.e(tag, content);
		}
		if (isSaveLog) {
			point(PATH_LOG_INFO, tag, content);
		}
	}

	public static void e(String content, Throwable tr) {
		if (!mAllowE)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		content = generateContent(content, caller);
		
		if (customLogger != null) {
			customLogger.e(tag, content, tr);
		} else {
			Log.e(tag, content, tr);
		}
		if (isSaveLog) {
			point(PATH_LOG_INFO, tag, tr.getMessage());
		}
	}

	public static void i(String content) {
		if (!mAllowI)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		content = generateContent(content, caller);
		
		if (customLogger != null) {
			customLogger.i(tag, content);
		} else {
			Log.i(tag, content);
		}

	}

	public static void i(String content, Throwable tr) {
		if (!mAllowI)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		content = generateContent(content, caller);
		
		if (customLogger != null) {
			customLogger.i(tag, content, tr);
		} else {
			Log.i(tag, content, tr);
		}

	}

	public static void v(String content) {
		if (!mAllowV)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		content = generateContent(content, caller);
		
		if (customLogger != null) {
			customLogger.v(tag, content);
		} else {
			Log.v(tag, content);
		}
	}

	public static void v(String content, Throwable tr) {
		if (!mAllowV)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		content = generateContent(content, caller);
		
		if (customLogger != null) {
			customLogger.v(tag, content, tr);
		} else {
			Log.v(tag, content, tr);
		}
	}

	public static void w(String content) {
		if (!mAllowW)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		content = generateContent(content, caller);
		
		if (customLogger != null) {
			customLogger.w(tag, content);
		} else {
			Log.w(tag, content);
		}
	}

	public static void w(String content, Throwable tr) {
		if (!mAllowW)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		content = generateContent(content, caller);
		
		if (customLogger != null) {
			customLogger.w(tag, content, tr);
		} else {
			Log.w(tag, content, tr);
		}
	}

	public static void w(Throwable tr) {
		if (!mAllowW)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		
		if (customLogger != null) {
			customLogger.w(tag, tr);
		} else {
			Log.w(tag, tr);
		}
	}

	public static void wtf(String content) {
		if (!mAllowWtf)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		content = generateContent(content, caller);
		
		if (customLogger != null) {
			customLogger.wtf(tag, content);
		} else {
			Log.wtf(tag, content);
		}
	}

	public static void wtf(String content, Throwable tr) {
		if (!mAllowWtf)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);
		content = generateContent(content, caller);
		
		if (customLogger != null) {
			customLogger.wtf(tag, content, tr);
		} else {
			Log.wtf(tag, content, tr);
		}
	}

	public static void wtf(Throwable tr) {
		if (!mAllowWtf)
			return;
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);

		if (customLogger != null) {
			customLogger.wtf(tag, tr);
		} else {
			Log.wtf(tag, tr);
		}
	}

	private static StackTraceElement getCallerStackTraceElement() {
		return Thread.currentThread().getStackTrace()[4];
	}
	
	private static StringBuffer mStringBuffer = new StringBuffer();
	private static String mPath = null;
	/**
	 * 保存Log信息
	 * */
	private static void point(String path, String tag, String msg) {
		
	        if (isSDAva()) {
	        	
	        	if(mPath == null) {
	        		mPath = generateLogPath();
		            File file = new File(path);
		            if (!file.exists()) {
		                createDipPath(path);
		            }
		        	
	        		if(mStringBuffer.length() > 0) {//清除buffer
	        			synchronized(mStringBuffer) {
	        				mStringBuffer.delete(0, mStringBuffer.length()-1);
	        			}
	        		}
	        	}
	        	
	        	String logMsg = generateLogMsg(tag, msg);
	        	
	        	synchronized(mStringBuffer) {
	        		mStringBuffer.append(logMsg);
	        	}
	        	//如果buffer超过1024个字节，则要保存到磁盘中。
	        	if(mStringBuffer.length() > 1024) {
	        		saveLog();
	        	}
	        }
	    }
	
	/**
	 * 生成Log信息，添加时间信息
	 * */
	private static String generateLogMsg(String tag, String msg){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
        dateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(date);
        return "["+time+"]" + "  "  + tag +"   "+ msg + "\n";
	}
	
	/**
	 * 生成Log文件路径
	 * */
	private static String generateLogPath(){
		Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
        dateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(date);
        time = time.replace(" ", "_").replace(":", "-");
        String path = PATH_LOG_INFO+time+".log";
        return path;
	}
	
    /**
     * 創建文件路徑
     * 
     * */
    private static void createDipPath(String file) {
        String parentFile = file.substring(0, file.lastIndexOf(File.separator));
        File file1 = new File(file);
        File parent = new File(parentFile);
        if (!file1.exists()) {
            parent.mkdirs();
            try {
                file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 保存Log
     * 
     * */
    private static void saveLog() {
    	synchronized(mStringBuffer) {
    		if(mPath == null) { 
    			mPath = generateLogPath();
    		}
    		Log.d("peter", "mPath="+mPath+"  mStringBuffer.length="+mStringBuffer.length());
    		
        	File file = new File(mPath);
            if (!file.exists())
                createDipPath(mPath);
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(file, true)));
                out.write(mStringBuffer.toString());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            	if(mStringBuffer.length()>0)
            	mStringBuffer.delete(0, mStringBuffer.length()-1);
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    	}
    }
    
    /**
     * 应用程序介绍时，mStringBuffer可能还有数据，要保存剩余的日志。
     * 应该在Application终止的时候调用。
     * */
    public static void LogFlush(){
    	if(isSaveLog && isSDAva()) {
    		saveLog();
    	}
    }
    
    /**
     * 判断SD卡是否存在
     * 
     * */
	private static boolean isSDAva() {
		if (Environment.getExternalStorageState().equals(
		        Environment.MEDIA_MOUNTED)
		        || Environment.getExternalStorageDirectory().exists()) {
			return true;
		} else {
			return false;
		}
	}

}

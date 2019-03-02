package com.shinetvbox.vod.utils;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * 存储空间
 *
 * @author lxh
 */
public class StorageUtil {
    private static final int ERROR = -1;

    /**
     * SDCARD是否存
     */
    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED );
    }

    /**
     * 获取手机内部剩余存储空间
     *
     * @return
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
//            if(!NativeTools.fileExist(path.getAbsolutePath())) {
//            	return 0;
//            }
        StatFs stat = new StatFs( path.getPath() );
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获取手机内部总的存储空间
     *
     * @return
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
//            if(!NativeTools.fileExist(path.getAbsolutePath())) {
//            	return 0;
//            }
        StatFs stat = new StatFs( path.getPath() );
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 获取SDCARD剩余存储空间
     * @return
     */
    public static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
//	            if(!NativeTools.fileExist(path.getAbsolutePath())) {
//	            	return 0;
//	            }
            StatFs stat = new StatFs( path.getPath() );
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            //return availableBlocks * blockSize;  //单位Byte
            //return (availableBlocks * blockSize)/1024;   //单位KB
            return availableBlocks * blockSize/1024/1024;//单位MB
        } else {
            return ERROR;
        }
    }

    /**
     * 获取SDCARD总的存储空间
     *
     * @return
     */
    public static long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
//	            if(!NativeTools.fileExist(path.getAbsolutePath())) {
//	            	return 0;
//	            }
            StatFs stat = new StatFs( path.getPath() );
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            //return totalBlocks * blockSize; //单位Byte
            //return (totalBlocks * blockSize)/1024; //单位KB
            return (totalBlocks * blockSize)/1024/1024; //单位MB
        } else {
            return ERROR;
        }
    }

    /**
     * 获取USB外部剩余存储空间
     *
     * @return
     */
    public static long getAvailableUSBMemorySize(String str) {
        if (externalMemoryAvailable()) {
            File path = new File( str );
            if (!path.exists())
                return 0;
            StatFs stat = new StatFs( path.getPath() );
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();

            return (availableBlocks * blockSize);
//		        return (availableBlocks * blockSize)/1024;     //KIB 单位
//	            return (availableBlocks * blockSize)/1024 /1024;//MIB单位
//	            return (availableBlocks * blockSize)/1024 /1024 /1024;//GB单位
//	            return availableBlocks * blockSize;
        } else {
            return ERROR;
        }
    }

    /**
     * 获取USB外部总的存储空间
     *
     * @return
     */
    public static long getTotalUSBMemorySize(String str) {
        if (externalMemoryAvailable()) {
            File path = new File( str );
            if (!path.exists())
                return 0;


            StatFs stat = new StatFs( path.getPath() );
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();

            return (totalBlocks * blockSize);
//		        return (totalBlocks * blockSize)/1024;     //KIB 单位
//	            return (totalBlocks * blockSize)/1024 /1024;//MIB单位
//	           return (totalBlocks * blockSize)/1024 /1024 /1024;//GB单位
        } else {
            return ERROR;
        }
    }
}

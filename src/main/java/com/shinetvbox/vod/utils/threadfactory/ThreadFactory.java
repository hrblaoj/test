package com.shinetvbox.vod.utils.threadfactory;

public class ThreadFactory {
	private static ThreadPoolProxy	mNormalPool;	// 只需初始化一次就行了
	//private static ThreadPoolProxy	mDownLoadPool;	// 只需初始化一次就行了
	/**创建了一个普通的线程池*/
	public static ThreadPoolProxy getNormalPool() {
		if (mNormalPool == null) {
			synchronized (ThreadFactory.class) {
				if (mNormalPool == null) {
					mNormalPool = new ThreadPoolProxy(8, 8, 3000);
				}
			}
		}
		return mNormalPool;
	}

	/**创建了一个下载的线程池*/
/*	public static ThreadPoolProxy getDownLoadPool() {
		if (mDownLoadPool == null) {
			synchronized (ThreadFactory.class) {
				if (mDownLoadPool == null) {
					mDownLoadPool = new ThreadPoolProxy(3, 3, 3000);
				}
			}
		}
		return mDownLoadPool;
	}*/
}


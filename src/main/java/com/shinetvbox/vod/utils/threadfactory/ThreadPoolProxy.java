package com.shinetvbox.vod.utils.threadfactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolProxy {

	ThreadPoolExecutor mExecutor;			// 只需要一个对象就行了
	private int			mCorePoolSize;
	private int			mMaximumPoolSize;
	private long		mKeepAliveTime;

	/*
	 * 通过构造方法传入对应的corePoolSize,maximumPoolSize,keepAliveTime
	 */
	public ThreadPoolProxy(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
		super();
		mCorePoolSize = corePoolSize;
		mMaximumPoolSize = maximumPoolSize;
		mKeepAliveTime = keepAliveTime;
	}

	private void initThreadPoolExecutor() {// 双重检查加锁
		if (mExecutor == null) {
			synchronized (ThreadPoolProxy.class) {
				if (mExecutor == null) {
					TimeUnit unit = TimeUnit.MILLISECONDS;
					BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
					ThreadFactory threadFactory = Executors.defaultThreadFactory();
					RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();
					mExecutor = new ThreadPoolExecutor(//
							mCorePoolSize,// 核心线程数
							mMaximumPoolSize,// 最大线程数
							mKeepAliveTime, // 保持时间
							unit, // 保持时间的单位
							workQueue,// 工作队列
							threadFactory,// 线程工厂
							handler// 异常捕获器
							);
				}
			}
		}
	}

	/**执行任务*/
	public void execute(Runnable task) {
		initThreadPoolExecutor();
		mExecutor.execute(task);
	}

	/**提交任务*/
	public Future<?> submit(Runnable task) {
		initThreadPoolExecutor();
		return mExecutor.submit(task);
	}

	/**移除任务*/
	public void remove(Runnable task) {
		initThreadPoolExecutor();
		mExecutor.remove(task);
	}

}


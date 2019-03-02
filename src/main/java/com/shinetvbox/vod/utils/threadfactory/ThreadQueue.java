package com.shinetvbox.vod.utils.threadfactory;





import com.shinetvbox.vod.utils.KtvLog;

import java.util.ArrayList;
import java.util.List;

public class ThreadQueue {
	private static boolean isRunning = false;
	
	public interface Task{
		void run();
	}
	private static List<Task> mTaskList = new ArrayList<Task>();
	
	public static void addTaskAndStart(Task task) {
		if(task == null) {
			return;
		}
		KtvLog.d("isRunning="+isRunning+"  mTaskList.size="+mTaskList.size());
		if(!isRunning) {
			exceuteTask(task);
		} else {
			mTaskList.add(task);
		}
	}
	
	private static Task getNextTask(){
		if(mTaskList.size() > 0) {
			Task task = mTaskList.get(0);
			mTaskList.remove(0);
			return task;
		}
		return null;
	}
	
	private static void exceuteTask(final Task task) {
		isRunning = true;
		ThreadFactory.getNormalPool().execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				task.run();
				Task t = getNextTask();
				if(t != null) {
					exceuteTask(t);
				} else {
					isRunning = false;
				}
			}
		});
	}
}

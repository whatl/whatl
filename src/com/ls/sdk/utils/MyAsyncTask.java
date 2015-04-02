package com.ls.sdk.utils;

import android.os.Handler;

/**
 * 通过线程池来实现一个自己的AsynExcuter
 * 
 * @author ls
 * 
 */
public abstract class MyAsyncTask {
	private ThreadPoolManager threadPoolManager = null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			onPostExecute();
		};
	};

	private MyAsyncTask() {
	}

	public MyAsyncTask(ThreadPoolManager threadPoolManager) {
		this.threadPoolManager = threadPoolManager;
	}

	/**
	 * 当前程开始的时候调用的方法，运行在主线程
	 */
	public abstract void onPreExecute();

	/**
	 * 运行在子线程的方法
	 */
	public abstract void doInBackground();

	/**
	 * 运行在主线程的方法，当子线程结束的时候调用
	 */
	public abstract void onPostExecute();

	/**
	 * 调用此方法执行异步线程池
	 */
	public void execute() {
		onPreExecute();
		if (threadPoolManager != null) {
			threadPoolManager.addTask(new MyAsyncRunnable());
		} else {
			new Thread(new MyAsyncRunnable()).start();
		}
	}

	class MyAsyncRunnable implements Runnable {

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public void run() {
			doInBackground();
			handler.sendEmptyMessage(0);
		}

	}
}

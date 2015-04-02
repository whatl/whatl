package com.ls.sdk.utils;

import android.os.Handler;

/**
 * ͨ���̳߳���ʵ��һ���Լ���AsynExcuter
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
	 * ��ǰ�̿�ʼ��ʱ����õķ��������������߳�
	 */
	public abstract void onPreExecute();

	/**
	 * ���������̵߳ķ���
	 */
	public abstract void doInBackground();

	/**
	 * ���������̵߳ķ����������߳̽�����ʱ�����
	 */
	public abstract void onPostExecute();

	/**
	 * ���ô˷���ִ���첽�̳߳�
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

package com.ls.sdk.utils;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池的管理类
 * 
 * @author ls
 * 
 */
public class ThreadPoolManager {
	private ThreadPoolManager() {
		num = Runtime.getRuntime().availableProcessors();
		Comparator<? super Runnable> comparator = new Comparator<Runnable>() {
			@Override
			public int compare(Runnable lhs, Runnable rhs) {
				return lhs.hashCode() > rhs.hashCode() ? 1 : -1;
			}
		};
		workQueue = new PriorityBlockingQueue<Runnable>(num * 10, comparator);
		executor = new ThreadPoolExecutor(num * 2, num * 2, 8,
				TimeUnit.SECONDS, workQueue,
				new ThreadPoolExecutor.CallerRunsPolicy());
	}

	private static final ThreadPoolManager manager = new ThreadPoolManager();
	public int num;
	private ThreadPoolExecutor executor;
	private PriorityBlockingQueue<Runnable> workQueue;

	public ExecutorService getService() {
		return executor;
	}

	/**
	 * 获取到线程池的实例
	 */
	public static ThreadPoolManager getInstance() {
		return manager;
	}

	/**
	 * 停止所有的线程
	 */
	public void stopReceiveTask() {
		if (!executor.isShutdown()) {
			executor.shutdown();
		}
	}

	/**
	 * 停止所有的线程
	 */
	public void stopAllTask() {
		if (!executor.isShutdown()) {
			executor.shutdownNow();
		}
	}

	/**
	 * 添加一个线程的队列
	 * 
	 * @param runnable
	 */
	public void addTask(Runnable runnable) {
		if (executor.isShutdown()) {
			executor = new ThreadPoolExecutor(num * 2, num * 2, 8,
					TimeUnit.SECONDS, workQueue,
					new ThreadPoolExecutor.CallerRunsPolicy());
		}
		executor.execute(runnable);
	}
}

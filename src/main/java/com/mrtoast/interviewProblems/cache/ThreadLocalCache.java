package com.mrtoast.interviewProblems.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadLocalCache<K, V> {
	
	private final ThreadPoolExecutor executorService = new ThreadPoolExecutorImpl();
	
	private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor( 
			new ThreadFactory() {
				@Override
				public Thread newThread(Runnable r) {
					Thread daemonThread = new Thread(r, ("ScheduledDaemonWorker"));
					daemonThread.setDaemon(true);
					return daemonThread;
				}
			});
	
	private final Map<Thread, ExpiringCache<K, V>> threadGroups = new ConcurrentHashMap<>();
	
	public ThreadLocalCache() {
		//default constructor
	}
	
	/**
	 * 
	 * @param task
	 * @return
	 */
	public Future<?> executeTask(Runnable task) {
		return executorService.submit(task);
	}
	
	public ScheduledFuture<?> executeScheduledCleanup(long initialDelay, long period, TimeUnit unit) {
		return executeScheduledTask(
				new Runnable() {
					@Override
					public void run() {
						cleanup();
					}
				}, initialDelay, period, unit);
	}
	
	private ScheduledFuture<?> executeScheduledTask(Runnable task, long initialDelay, long period, TimeUnit unit) {
		return scheduledExecutorService.scheduleAtFixedRate(task, initialDelay, period, unit);
	}
	
	private void cleanup() {
		for (ExpiringCache<K, V> cache: threadGroups.values()) {
			if (cache != null) {
				cache.cleanup();
			}
		}
	}
	
	public void shutdown() {
		executorService.shutdown();
		scheduledExecutorService.shutdown();
	}
}

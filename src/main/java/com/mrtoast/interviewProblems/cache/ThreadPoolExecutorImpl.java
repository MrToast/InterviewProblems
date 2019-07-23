package com.mrtoast.interviewProblems.cache;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolExecutorImpl extends ThreadPoolExecutor {
	/** 0 - the default number of threads to always keep in the pool */
	private static final int DEFAULT_CORE_POOL_SIZE = 0;
	
	/** 10 - the default maximum number of threads to allow in the pool */
	private static final int DEFAULT_MAX_POOL_SIZE = 10;
	
	/** 10 - the default maximum capacity of the queue */
	private static final int DEFAULT_MAX_QUEUE_SIZE = 10;
	
	/** 3000 ms - the default maximum time (in milliseconds) an idle thread waits for new tasks before terminating */
	private static final long DEFAULT_KEEP_ALIVE_TIME = 3000;
	
	/** milliseconds - the default time unit for the keepAliveTime parameter */
	private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;
	
	/** the default thread factory creates daemon threads with a generated name */
	private static final ThreadFactory DEFAULT_THREAD_FACTORY = new ThreadFactoryImpl("DaemonWorker", true);
	
	public ThreadPoolExecutorImpl() {
		super(DEFAULT_CORE_POOL_SIZE, 
				DEFAULT_MAX_POOL_SIZE, 
				DEFAULT_KEEP_ALIVE_TIME, 
				DEFAULT_TIME_UNIT, 
				newWorkQueue(DEFAULT_MAX_QUEUE_SIZE), 
				DEFAULT_THREAD_FACTORY, 
				new RejectedExecutionHandlerImpl());
	}
	
	public ThreadPoolExecutorImpl(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, 
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler)
	{
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
	}
	
	private static BlockingQueue<Runnable> newWorkQueue(int maxQueueSize) throws IllegalArgumentException {
		if (maxQueueSize == 0) {
			return new LinkedBlockingQueue<>();
		}
		else if (maxQueueSize > 0) {
			return new ArrayBlockingQueue<Runnable>(maxQueueSize);
		}
		throw new IllegalArgumentException("Max queue size must be greater than 0");
	}
	
	private static class ThreadFactoryImpl implements ThreadFactory {
		private final String name;
		private final boolean isDaemon;
		
		private final AtomicInteger uid = new AtomicInteger();
		
		private ThreadFactoryImpl(String name, boolean isDaemon) {
			this.name = name;
			this.isDaemon = isDaemon;
		}
		
		@Override
		public Thread newThread(Runnable r) {
			Thread daemonThread = new Thread(r, (name + uid.getAndIncrement()));
			daemonThread.setDaemon(isDaemon);
			return daemonThread;
		}
	}
	
	private static class RejectedExecutionHandlerImpl implements RejectedExecutionHandler {
		@Override
		public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
			System.out.println(runnable.toString() + " has been rejected.");
		}
	}
}

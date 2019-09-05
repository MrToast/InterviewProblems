package com.mrtoast.interviewProblems.cache;

import java.util.Map;
import java.util.concurrent.DelayQueue;

public class ExpiringCache<K, V> {
	
	private final long delay;
	private final Map<K, V> cache;
	private final DelayQueue<ExpiringKey<K>> delayQueue = new DelayQueue<>();
	
	public ExpiringCache(long delay, int maxSize) {
		this.delay = delay;
		this.cache = Cache.synchornizedCache();
	}
	
	public void put(K key, V value) {
		offerQueue(key);
		cache.put(key, value);
	}
	
	private void offerQueue(K key) {
		ExpiringKey<K> expiringKey = new ExpiringKey<>(key, delay);
		delayQueue.offer(expiringKey);
	}
	
	public V get(K key) {
		return cache.get(key);
	}
	
	public V remove(K key) {
		return cache.remove(key);
	}
	
	public void clear() {
		cache.clear();
	}
	
	/**
	 * Removes expired keys from the cache.
	 */
	public void cleanup() {
		ExpiringKey<K> expiringKey = delayQueue.poll();
		while (expiringKey != null) {
			cache.remove(expiringKey.getKey());
			expiringKey = delayQueue.poll();
		}
	}
}

package com.mrtoast.interviewProblems.cache;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


public class Cache<K, V> extends LinkedHashMap<K, V> {
	
	private static final long serialVersionUID = 1L;

	static final int DEFAULT_MAX_CAPACITY = 1024;
	static final int DEFAULT_INITIAL_CAPACITY = 16;
	static final float DEFAULT_LOAD_FACTOR = 0.75F;
	/** This value must be set TRUE to create an LRU Cache */
	static final boolean DEFAULT_ACCESS_ORDER = true;

	private int maxCapacity;
	
	public Cache() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_ACCESS_ORDER, DEFAULT_MAX_CAPACITY);
	}

	/**
	 * Constructs an empty <tt>Cache</tt> instance with the
     * specified initial and maximum capacity, load factor and ordering mode.
     *
     * @param  initialCapacity the initial capacity
     * @param  loadFactor      the load factor
     * @param  accessOrder     the ordering mode - <tt>true</tt> for
     *         access-order, <tt>false</tt> for insertion-order
     * @param  maxCapacity the maximum cache capacity
     * @throws IllegalArgumentException if the initial capacity is negative
     *         or the load factor is non-positive or maxCapcity is less than initialCapacity.
	 */
	public Cache(int initialCapacity, float loadFactor, boolean accessOrder, int maxCapacity) {
		super(initialCapacity, loadFactor, accessOrder);
		validateMaxCapacity(initialCapacity, maxCapacity);
	}

	private final void validateMaxCapacity(int initialCapacity, int maxCapacity) {
		if (maxCapacity < initialCapacity) {
			throw new IllegalArgumentException("Specified maximum capacity is less than its initial capacity");
		}
		this.maxCapacity = maxCapacity;
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return (size() > maxCapacity);
	}
	
	/**
	 * @return a synchronized implementation of the <tt>Cache</tt>.
	 */
	public static <K, V> Map<K, V> synchornizedCache() {
		return Collections.synchronizedMap(new Cache<K, V>(
				DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_ACCESS_ORDER, DEFAULT_MAX_CAPACITY));
	}
	
	/**
	 * Returns a synchronized implementation of the <tt>Cache</tt> with the
     * specified initial and maximum capacity, load factor and ordering mode.
     *
     * @param  initialCapacity the initial capacity
     * @param  loadFactor      the load factor
     * @param  accessOrder     the ordering mode - <tt>true</tt> for access-order, 
     * 											   <tt>false</tt> for insertion-order
     * @param  maxCapacity the maximum cache capacity
     * @throws IllegalArgumentException if the initial capacity is negative
     *         or the load factor is non-positive or maxCapcity is less than initialCapacity.
	 */
	public static <K, V> Map<K, V> synchornizedCache(int initialCapacity, float loadFactor, 
			boolean isAccessOrder, int maxCapacity) {
		return Collections.synchronizedMap(new Cache<K, V>(initialCapacity, loadFactor, isAccessOrder, maxCapacity));
	}
}

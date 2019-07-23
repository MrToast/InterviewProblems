package com.mrtoast.interviewProblems.cache;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class ExpiringKey<K> implements Delayed {
    
	private final K key;
    private final long delay;
    private long expiringTime;

    /**
     * @param key K
     * @param delay desired delay time in milliseconds
     */
    public ExpiringKey(K key, long delay) {
        this(key, delay, TimeUnit.MILLISECONDS);
    }
    
    /**
     * @param key K
     * @param delay desired delay time in the {@link TimeUnit} provided
     * @param unit TimeUnit of the delay
     */
    public ExpiringKey(K key, long delay, TimeUnit unit) {
    	this.key = key;
        this.delay = TimeUnit.MILLISECONDS.convert(delay, unit);
    	this.expiringTime = System.currentTimeMillis() + this.delay;
    }
    
    public K getKey()
    {
    	return this.key;
    }
    
    @Override
    public long getDelay(TimeUnit unit) {
    	return unit.convert(getRemainingDelay(), TimeUnit.MILLISECONDS);
    }
    
    /**
	 * @return the remaining delay in milliseconds. A zero or negative value indicates the delay has expired.
	 */
    private long getRemainingDelay() {
    	return expiringTime - System.currentTimeMillis();
    }
    
    /**
     * Expires this key.
     */
    protected void expire() {
    	this.expiringTime = 0;
    }
    
    /**
     * Renews this key for an extended delay time.
     */
    protected void renew() {
    	this.expiringTime = System.currentTimeMillis() + this.delay;
    }
    
    @Override
    public int compareTo(Delayed o) {
    	TimeUnit unit = TimeUnit.MILLISECONDS;
    	return Long.compare(this.getDelay(unit), o.getDelay(unit));
    }
}
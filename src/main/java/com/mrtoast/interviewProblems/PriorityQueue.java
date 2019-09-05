package com.mrtoast.interviewProblems;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PriorityQueue<T> {
	
	private final int maxSize;
	private T[] queue;
	private int lastPosition;
	
	public PriorityQueue(int maxSize, Function<Integer, T[]> arrayInitializer) {
		this.maxSize = maxSize;
		this.queue = arrayInitializer.apply(maxSize);
	}
	
	public void add(T value) {
		if (lastPosition == maxSize) {
			throw new RuntimeException("queue is full");
		}
		queue[lastPosition] = value;
		lastPosition++;
	}
	
	public PriorityQueue<T> addAll(PriorityQueue<T> other) {
		Arrays.stream(other.queue).forEach(t -> add(t));
		return this;
	}
	
	public T peek() {
		if (lastPosition < 1) {
			throw new RuntimeException("nothing in queue");
		}
		return queue[0];
	}
	
	public T take() {
		if (lastPosition < 1) {
			throw new RuntimeException("nothing in queue");
		}
		
		T value = queue[0];
		
		for (int i=0; i<lastPosition; i++) {
			queue[i] = queue[i+1];
		}
		lastPosition--;
		
		return value;
	}
	
	@Override
	public String toString() {
		if (lastPosition < 1) {
			return "queue is empty";
		}
		return Arrays.stream(queue)
				.limit(lastPosition)
	//			.filter(x -> x != null)
				.map(x -> x.toString())
				.collect(Collectors.joining(", "));
	}
}

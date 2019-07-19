package com.mrtoast.interviewProblems.mainline;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;

import com.mrtoast.interviewProblems.PrimeNumberGenerator;
import com.mrtoast.interviewProblems.PriorityQueue;
import com.mrtoast.interviewProblems.StringReverser;

public class Main
{
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		List<Integer> list = Arrays.asList(1,2,3,4,5,6,45);
		list.stream().map(PrimeNumberGenerator::getPrime).forEach(System.out::println);
		
		
		StringReverser.printReverse("a,b,c,d");
		
		
		PriorityQueue<Integer> queue = list.stream().collect(getColletor(10));
		System.out.println(queue.toString());
		while (true) {
			queue.take();
			System.out.println(queue.toString());
		}
		
	}
	
	private static Integer[] arrayInitializer(int maxSize) {
		return new Integer[maxSize];
	}

	private static Collector<Integer, PriorityQueue<Integer>, PriorityQueue<Integer>> getColletor(int maxSize) {
		return Collector.of(
				() -> new PriorityQueue<>(maxSize, Main::arrayInitializer), 
				PriorityQueue::add, 
				(x,y) -> x.addAll(y));
	}
}

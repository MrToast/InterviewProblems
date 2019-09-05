package com.mrtoast.interviewProblems;

import java.util.function.Consumer;

public class FibonacciNumberGenerator {
	
	public static void useFibonacci(int value, Consumer<Integer> consumer) {
		consumer.accept(getFibonacci(value));
	}
	
	public static void printFibonacci(int value) {
		System.out.println(getFibonacci(value));
	}
	
	public static int getFibonacci(int value) {
		int valueMinus2 = 1;
		int valueMinus1 = 1;
		
		if (value < 1) {
			return 0;
		}
		else if (value < 3) {
			return 1;
		}
		int currentValue = 2;
		for (int i=2; i<value; i++) {
			valueMinus2 = valueMinus1;
			valueMinus1 = currentValue;
			currentValue = valueMinus2 + valueMinus1;
		}
		if (currentValue < 0) {
			throw new RuntimeException("Out of range");
		}
		return currentValue;
	}
}
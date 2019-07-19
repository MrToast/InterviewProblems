/* Copyright 2019 Jack Henry Software */

package com.mrtoast.interviewProblems;

import java.util.function.Consumer;

public class PrimeNumberGenerator {
	
	public static void usePrime(int value, Consumer<Integer> consumer) {
		consumer.accept(getPrime(value));
	}
	
	public static void printPrime(int value) {
		System.out.println(getPrime(value));
	}
	
	public static int getPrime(int value) {
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
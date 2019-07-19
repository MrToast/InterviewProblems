/* Copyright 2019 Jack Henry Software */

package com.mrtoast.interviewProblems;

public class StringReverser
{
	public static void printReverse(String value) {
		System.out.println(reverse(value));
	}
	
	private static String reverse(String value) {
		char[] chars = value.toCharArray();
		char[] reverse = reverse(chars);
		return String.valueOf(reverse);
	}
	
	private static char[] reverse(char[] value) {
		int lastPosition = value.length - 1;
		
		char c;
		for (int i=0; i<value.length/2; i++) {
			c = value[lastPosition-i];
			value[lastPosition-i] = value[i];
			value[i] = c;
		}
		return value;
	}
}

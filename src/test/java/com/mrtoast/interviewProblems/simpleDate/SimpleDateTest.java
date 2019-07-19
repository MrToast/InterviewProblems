/* Copyright 2019 Jack Henry Software */

package com.mrtoast.interviewProblems.simpleDate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SimpleDateTest
{
	@DataProvider
	public Object[][] nullData()
	{
		return new Object[][] {
				{new SimpleDate()},
				{new SimpleDate(0,0,0)},
		};
	}
	
	@Test(dataProvider="nullData")
	public void nullDateTest(SimpleDate date) throws Exception
	{
		Assert.assertEquals(date.isNullDate(), true);
		//null dates have all zero values
		Assert.assertEquals(date.getYear(), 0);
		Assert.assertEquals(date.getMonth(), 0);
		Assert.assertEquals(date.getDay(), 0);
		Assert.assertEquals(date.toString(), "0000-00-00");
	}
	
	@DataProvider
	public Object[][] testData()
	{
		return new Object[][] {
				{2015, 1, 1}, {2015, 1, 7}, {2015, 1, 31}, // Jan
				{2015, 2, 1}, {2015, 2, 7}, {2015, 2, 28}, {2012, 2, 29}, // Feb
				{2015, 3, 1}, {2015, 3, 7}, {2015, 3, 31}, // Mar
				{2015, 4, 1}, {2015, 4, 7}, {2015, 4, 30}, // Apr
				{2015, 5, 1}, {2015, 5, 7}, {2015, 5, 31}, // May
				{2015, 6, 1}, {2015, 6, 7}, {2015, 6, 30}, // Jun
				{2015, 7, 1}, {2015, 7, 7}, {2015, 7, 31}, // Jul
				{2015, 8, 1}, {2015, 8, 7}, {2015, 8, 31}, // Aug
				{2015, 9, 1}, {2015, 9, 7}, {2015, 9, 30}, // Sep
				{2015, 10, 1}, {2015, 10, 7}, {2015, 10, 31}, // Oct
				{2015, 11, 1}, {2015, 11, 7}, {2015, 11, 30}, // Nov
				{2015, 12, 1}, {2015, 12, 7}, {2015, 12, 31} // Dec
		};
	}
	
	@DataProvider
	public Object[][] specialCaseData()
	{
		return new Object[][] {
				{1,1,1}, // minimum
				{(int)Short.MAX_VALUE, 12, 31}, // maximum
				
				{2000, 2, 29}, // leap year is divisible by 4, 100, 400
				{1900, 2, 28}, // non-leap year is divisible by 4, 100
				{1584, 2, 29}, // first leap year after 1582
				{1200, 2, 29}, // pre-Gregorian leap year divisible by 4, 100, 400
				{1300, 2, 29}, // pre-Gregorian leap year divisible by 4, 100
				{1580, 2, 29} // pre-Gregorian leap year divisible by 4
		};
	}
	
	@DataProvider
	public Object[][] nonNullData()
	{
		List<Object[]> list = new ArrayList<>();
		list.addAll(Arrays.asList(testData()));
		list.addAll(Arrays.asList(specialCaseData()));
		return list.toArray(new Object[list.size()][]);
	}
	
	@Test(dataProvider="nonNullData")
	public void simpleDateTest(int year, int month, int day) throws Exception
	{
		SimpleDate date = new SimpleDate(year, month, day);
		
		Assert.assertEquals(date.isNullDate(), false);
		
		Assert.assertEquals(date.getYear(), year);
		Assert.assertEquals(date.getMonth(), month);
		Assert.assertEquals(date.getDay(), day);
		
		String expectedToString = String.format("%04d-%02d-%02d", year, month, day);
		Assert.assertEquals(date.toString(), expectedToString);
	}
	
	@DataProvider
	public Object[][] invalidData()
	{
		return new Object[][] {
				{-1,0,0}, // year < 1
				{-1,1,1},
				{0,1,1},
				{0,-1,0}, // month < 1
				{1,-1,1},
				{1,0,1},
				{0,0,-1}, // day < 1
				{1,1,-1},
				{0,1,1},
				
				{(Short.MAX_VALUE + 1), 1, 1}, // year > MAX
				{2015, 13, 1}, // month > max
				{2015, 1, 32},  // day > Jan max (31)
				{2015, 2, 29},  // day > Feb max (28)
				{2012, 2, 30},  // day > Feb max (29)*
				{2015, 3, 32},  // day > Mar max (31)
				{2015, 4, 31},  // day > Apr max (30)
				{2015, 5, 32},  // day > May max (31)
				{2015, 6, 31},  // day > Jun max (30)
				{2015, 7, 32},  // day > Jul max (31)
				{2015, 8, 32},  // day > Aug max (31)
				{2015, 9, 31},  // day > Sep max (30)
				{2015, 10, 32}, // day > Oct max (31)
				{2015, 11, 31}, // day > Nov max (30)
				{2015, 12, 32} // day > Dec max (31)
		};
	}
	
	@Test(dataProvider="invalidData")
	public void simpleDateThrowsException(int year, int month, int day) throws Exception
	{
		try
		{
			@SuppressWarnings("unused")
			SimpleDate date = new SimpleDate(year, month, day);
		}
		catch (Exception e)
		{
			Assert.assertEquals(e.getClass(), IllegalArgumentException.class);
		}
	}
	
	@DataProvider
	public Object[][] isNullData()
	{
		return new Object[][] {
				{0,0,0, true},
				{0,0,1, false},
				{0,1,0, false},
				{1,0,0, false},
				{0,1,1, false},
				{1,0,1, false},
				{1,1,0, false},
				{1,1,1, false}
		};
	}
	
	@Test(dataProvider="isNullData")
	public void isNullTest(int year, int month, int day, boolean isNull) throws Exception
	{
		SimpleDate date = new SimpleDate();

		Method method = date.getClass().getDeclaredMethod("isNullDate", Integer.TYPE, Integer.TYPE, Integer.TYPE);
		method.setAccessible(true);
		
		Assert.assertEquals(method.invoke(date, year, month, day), isNull);
	}
	
	@DataProvider
	public Object[][] epochData()
	{
		return new Object[][] {
			{1600, 2, 29, -11670998400000L},
			{1700, 1, 1, -8520336000000L},
			{1800, 1, 1, -5364662400000L},

			{1900, 1, 1, -2208988800000L},
			{1900, 2, 1, -2206310400000L},
			{1900, 3, 1, -2203891200000L},
			{1900, 4, 1, -2201212800000L},
			{1900, 12, 31, -2177539200000L},

			{1970, 1, 1, 0L},
			{1980, 1, 1, 315532800000L},
			{1990, 1, 1, 631152000000L},
			{2000, 1, 1, 946684800000L},
			{2010, 1, 1, 1262304000000L},
			{2020, 1, 1, 1577836800000L},
			{2030, 1, 1, 1893456000000L},
			{2040, 1, 1, 2208988800000L},

			{1970, 3, 15, 6307200000L},
			{1980, 3, 15, 321926400000L},
			{1990, 3, 15, 637459200000L},
			{2000, 3, 15, 953078400000L},
			{2010, 3, 15, 1268611200000L},
			{2020, 3, 15, 1584230400000L},
			{2030, 3, 15, 1899763200000L},
			{2040, 3, 15, 2215382400000L},

			{1970, 7, 4, 15897600000L},
			{1980, 7, 4, 331516800000L},
			{1990, 7, 4, 647049600000L},
			{2000, 7, 4, 962668800000L},
			{2010, 7, 4, 1278201600000L},
			{2020, 7, 4, 1593820800000L},
			{2030, 7, 4, 1909353600000L},
			{2040, 7, 4, 2224972800000L},

			{1970, 11, 15, 27475200000L},
			{1980, 11, 15, 343094400000L},
			{1990, 11, 15, 658627200000L},
			{2000, 11, 15, 974246400000L},
			{2010, 11, 15, 1289779200000L},
			{2020, 11, 15, 1605398400000L},
			{2030, 11, 15, 1920931200000L},
			{2040, 11, 15, 2236550400000L},

			{1970, 12, 31, 31449600000L},
			{1980, 12, 31, 347068800000L},
			{1990, 12, 31, 662601600000L},
			{2000, 12, 31, 978220800000L},
			{2010, 12, 31, 1293753600000L},
			{2020, 12, 31, 1609372800000L},
			{2030, 12, 31, 1924905600000L},
			{2040, 12, 31, 2240524800000L},

			{32767, 12, 31, 971890876800000L},
			{2000, 2, 29, 951782400000L},
			{1900, 2, 28, -2203977600000L},
			{1584, 2, 29, -12175920000000L},

			{1580, 2, 29, -12301286400000L},
			{1200, 2, 29, -24293174400000L},
			{400, 2, 29, -49539254400000L},
			{1, 1, 1, -62135769600000L},

			{1582, 1, 1, -12243225600000L},
			{1582, 2, 1, -12240547200000L},
			{1582, 3, 1, -12238128000000L},
			{1582, 9, 1, -12222230400000L},
			{1582, 10, 1, -12219638400000L},
			{1582, 10, 2, -12219552000000L},
			{1582, 10, 3, -12219465600000L},
			{1582, 10, 4, -12219379200000L},
			//NOTE: October 5 - October 14, 1582 DO NOT EXIST in java calendar
			{1582, 10, 15, -12219292800000L},
			{1582, 10, 16, -12219206400000L},
			{1582, 10, 17, -12219120000000L},
			{1582, 10, 18, -12219033600000L},
			{1582, 10, 19, -12218947200000L},
			{1582, 10, 20, -12218860800000L},
			{1582, 10, 21, -12218774400000L},
			{1582, 10, 22, -12218688000000L},
			{1582, 10, 23, -12218601600000L},
			{1582, 10, 24, -12218515200000L},
			{1582, 10, 25, -12218428800000L},
			{1582, 10, 26, -12218342400000L},
			{1582, 11, 1, -12217824000000L},
			{1582, 12, 1, -12215232000000L},
			{1582, 12, 31, -12212640000000L},
		};
	}

	@Test(dataProvider="epochData")
	public void epochConstructorTest(int year, int month, int day, long time) throws Exception
	{
		SimpleDate date = new SimpleDate(year, month, day);
		
		Assert.assertEquals(date.isNullDate(), false);
		
		Assert.assertEquals(date.getYear(), year);
		Assert.assertEquals(date.getMonth(), month);
		Assert.assertEquals(date.getDay(), day);
		
		Assert.assertEquals(date.getTime(), time);
		
		String expectedToString = String.format("%04d-%02d-%02d", year, month, day);
		Assert.assertEquals(date.toString(), expectedToString);
	}
	
	@Test(dataProvider="epochData")
	public void epochConstructorTest2(int year, int month, int day, long time) throws Exception
	{
		SimpleDate date = new SimpleDate(time);
		
		Assert.assertEquals(date.isNullDate(), false);
		
		Assert.assertEquals(date.getYear(), year);
		Assert.assertEquals(date.getMonth(), month);
		Assert.assertEquals(date.getDay(), day);
		Assert.assertEquals(date.getTime(), time);
		
		String expectedToString = String.format("%04d-%02d-%02d", year, month, day);
		Assert.assertEquals(date.toString(), expectedToString);
	}
	
	@Test(dataProvider="epochData")
	public void getDateTest1(int year, int month, int day, long time) throws Exception
	{
		SimpleDate date = new SimpleDate(time);
		
		Date expectedDate = new Date(time);
		Assert.assertEquals(date.getDate(), expectedDate);
	}
	
	@SuppressWarnings("unused")
	@Test(dataProvider="epochData")
	public void getDateTest2(int year, int month, int day, long time) throws Exception
	{
		SimpleDate date = new SimpleDate(time);
		
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setLenient(false);
		cal.clear();
		cal.setTimeZone(SimpleDate.UTC);
		cal.set(year, month-1, day);
		int y = cal.get(java.util.Calendar.YEAR);
		int m = cal.get(java.util.Calendar.MONTH) + 1;
		int d = cal.get(java.util.Calendar.DAY_OF_MONTH);
		
		Date expectedDate = new Date(cal.getTimeInMillis());
		Assert.assertEquals(date.getDate(), expectedDate);
	}
	
	@SuppressWarnings("unused")
	@Test(dataProvider="epochData")
	public void getDateTest3(int year, int month, int day, long time) throws Exception
	{
		SimpleDate date = new SimpleDate(time);
		
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setLenient(false);
		cal.clear();
		cal.setTimeZone(SimpleDate.UTC);
		cal.set(year, month-1, day);
		int y = cal.get(java.util.Calendar.YEAR);
		int m = cal.get(java.util.Calendar.MONTH) + 1;
		int d = cal.get(java.util.Calendar.DAY_OF_MONTH);
		
		Date expectedDate = cal.getTime();
		Assert.assertEquals(date.getDate(), expectedDate);
	}
	
	@DataProvider
	public Object[][] equalData()
	{
		return new Object[][] {
				{new SimpleDate(), new SimpleDate(), 0}, // nulls are equal
				{new SimpleDate(), new SimpleDate(0, 0, 0), 0}, 
				{new SimpleDate(0, 0, 0), new SimpleDate(), 0},
				{new SimpleDate(2015, 1, 1), new SimpleDate(2015, 1, 1), 0} // non-null equal
		};
	}
	
	@DataProvider
	public Object[][] notEqualData()
	{
		return new Object[][] {
				{new SimpleDate(), new SimpleDate(2015, 1, 1), -1}, //null date is less
				{new SimpleDate(0, 0, 0), new SimpleDate(2015, 1, 1), -1},
				{new SimpleDate(2015, 1, 1), new SimpleDate(2015, 1, 2), -1}, // less than day
				{new SimpleDate(2015, 1, 1), new SimpleDate(2015, 2, 1), -1}, // less than month
				{new SimpleDate(2012, 1, 1), new SimpleDate(2015, 1, 1), -1}, // less than year
				
				{new SimpleDate(2015, 1, 1), new SimpleDate(), 1}, //greater than null date
				{new SimpleDate(2015, 1, 1), new SimpleDate(0, 0, 0), 1},
				{new SimpleDate(2015, 1, 2), new SimpleDate(2015, 1, 1), 1}, // greater than day
				{new SimpleDate(2015, 2, 1), new SimpleDate(2015, 1, 1), 1}, // greater than month
				{new SimpleDate(2015, 1, 1), new SimpleDate(2012, 1, 1), 1}, // greater than year
		};
	}
	
	@DataProvider
	public Object[][] compareToData()
	{
		List<Object[]> list = new ArrayList<>();
		list.addAll(Arrays.asList(equalData()));
		list.addAll(Arrays.asList(notEqualData()));
		return list.toArray(new Object[list.size()][]);
	}
	
	@Test(dataProvider="compareToData")
	public void compareToTest(SimpleDate date, SimpleDate other, int expectedResult) throws Exception
	{
		Assert.assertEquals(date.compareTo(other), expectedResult);
	}

	//NOTE: convenience method for calculating epoch time for a specific date to be used in testing.
//	@DataProvider
//	public Object[][] dates()
//	{
//		return new Object[][] {
//			{1970, 1, 1}, {1970, 3, 1},
//			{1980, 1, 1}, {1970, 3, 1},
//			{1990, 1, 1}, {1970, 3, 1},
//			{2000, 1, 1}, {1970, 3, 1},
//			{2010, 1, 1}, {1970, 3, 1},
//			{2020, 1, 1}, {1970, 3, 1},
//			{2030, 1, 1}, {1970, 3, 1},
//			{2040, 1, 1}, {1970, 3, 1},
//		};
//	}
//	
//	@Test(dataProvider="epochData")
//	public void fuJavaCalendar(int year, int month, int day, long ignore)
//	{
//		java.util.Calendar cal = java.util.Calendar.getInstance();
//		cal.setLenient(false);
//		cal.clear();
//		cal.setTimeZone(SimpleDate.UTC);
//		cal.set(year, month-1, day);
//		int y = cal.get(java.util.Calendar.YEAR);
//		int m = cal.get(java.util.Calendar.MONTH) + 1;
//		int d = cal.get(java.util.Calendar.DAY_OF_MONTH);
//		long t = cal.getTimeInMillis();
//		System.out.println(String.format("{%d, %d, %d, %dL},", y,m,d,t));
//	}
}


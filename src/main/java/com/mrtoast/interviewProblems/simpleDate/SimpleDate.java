package com.mrtoast.interviewProblems.simpleDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Simple date class representing a year, month, and day value.
 * <pre>
 * A SimpleDate is considered <code>null</code> if
 * 
 * year == 0 &&
 * month == 0 &&
 * day == 0
 * 
 * Valid field values are as follows:
 * 
 *            Null   Greatest     Least
 * Field   Minimum    Minimum   Maximum   Maximum
 * -----   -------   --------   -------   -------
 * YEAR          0          1     32767     32767
 * MONTH         0          1        12        12
 * DAY           0          1        28*       31
 * <pre>
 */
public class SimpleDate implements Comparable<SimpleDate>
{
    /** 
     * The universal time code (UTC).
     * <br><b>Note:</b> Some classes, like {@link SimpleDateFormat}, set a default time zone,
     * which could affect the formatted output of a SimpleDate, which is UTC. 
     */
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    
    /**
     * <p>
     * The point at which the Gregorian calendar rules are used, measured in
     * milliseconds from the standard epoch.  Default is 1582-10-15T00:00:00 UTC
     * or -12219292800000L.
     * </p>
     * <p>
     * 10 days are added to the epoch time between 
     * 1582-10-15T00:00:00.000 and 1582-10-14T23:59:59.999.
     * Therefore, the range of dates from 1582-10-05 - 1582-10-25 has 
     * duplicate epoch time values.
     * So the last valid date for which SimpleDate can provide epoch time is 1582-10-26
     * or -12218342400000L.
     * </p>
     */
    private static final long DEFAULT_GREGORIAN_CUTOVER = -12218342400000L;
	
	/** The Gregorian calendar officially started in year 1582. */
	private static final int GREGORIAN_CUTOVER_YEAR = 1582;
	private static final int GREGORIAN_CUTOVER_MONTH = 10;
	private static final int GREGORIAN_CUTOVER_DAY = 25;
	
	/** The number of milliseconds in one day. */
	private static final int MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
	
	/** The number of days in a 400 year cycle. */
    private static final int DAYS_PER_400_CYCLE = 146097;
    
    /** The number of days in a 100 year cycle. */
    private static final int DAYS_PER_100_CYCLE = 36524;
    
    /** The number of days in a 4 year cycle. */
    private static final int DAYS_PER_4_CYCLE = 1461;
    
    /** The number of days in a year. */
    private static final int DAYS_PER_YEAR = 365;
    
    /** The number of days from year 0 to 1970. */
    private static final long DAYS_0_TO_1970 = 719528;
	
	private short year;
	private byte month;
	private byte day;
	private long epochTime;
	
	/**
	 * returns a SimpleDate initialized with zeroed values.
	 */
	public SimpleDate()
	{
		this.year = 0;
		this.month = 0;
		this.day = 0;
		this.epochTime = 0;
	}
	
	public SimpleDate(int year, int month, int day)
	{
		validate(year, month, day);
		this.year = (short) year;
		this.month = (byte) month;
		this.day = (byte) day;
		
		if (isNullDate(year, month, day))
		{
			this.epochTime = 0;
		}
		else
		{
			setEpochTime();
		}
	}
	
	/** <b>NOTE:</b>
	 * cannot be used to create a Null Date.
	 */
	public SimpleDate(long epochTime)
	{
		this.epochTime = epochTime;
		
		setDate();
		validate(year, month, day);
	}
	
	public SimpleDate(Calendar calendar)
	{
		setDateFromCalendar(calendar);
		validate(year, month, day);
	}
	
	/**
	 * @return the year
	 */
	public int getYear()
	{
		return year;
	}

	/**
	 * @return the month
	 */
	public int getMonth()
	{
		return month;
	}

	/**
	 * @return the day
	 */
	public int getDay()
	{
		return day;
	}
	
	/**
	 * @return the time, in milliseconds since January 01, 1970.
	 */
	public long getTime()
	{
		return epochTime;
	}
	 
	/**
	 * Returns a new {@link Date} created from the SimpleDate's epoch time value.
	 * @return the {@link Date}
	 */
	public Date getDate()
	{
		return new Date(epochTime);
	}
	
	/**
	 * Returns a new {@link Calendar} created from the SimpleDate's epochTime value set to UTC timezone.
	 * @return the {@link Calendar}
	 */
	public Calendar getCalendar()
	{
		Calendar calendar = initCalendar();
		calendar.setTimeZone(UTC);
		calendar.setTimeInMillis(epochTime);
		validateCalendar(calendar);
		return calendar;
	}
	
	private void setEpochTime()
	{
		// year 1583 - Integer.MAX
		if (year > GREGORIAN_CUTOVER_YEAR)
		{
			setEpochTimeFromDate();
		}
		// year 0 - year 1581
		else if (year < GREGORIAN_CUTOVER_YEAR)
		{
			setEpochTimeFromCalendar();
		}
		// 1582 Jan 1 - Sep 31
		else if (year == GREGORIAN_CUTOVER_YEAR && month < GREGORIAN_CUTOVER_MONTH)
		{
			setEpochTimeFromCalendar();
		}
		// 1582 from Oct 1 - Oct 25
		else if (year == GREGORIAN_CUTOVER_YEAR && month == GREGORIAN_CUTOVER_MONTH && day <= GREGORIAN_CUTOVER_DAY)
		{
			setEpochTimeFromCalendar();
		}
		//1582 from Oct 26 - Dec 31
		else
		{
			setEpochTimeFromDate();
		}
	}
	
	/**
	 * Uses this year, month, day values to create a UTC Calendar object and set this epochTime.
	 */
	private void setEpochTimeFromCalendar()
	{
		Calendar calendar = initCalendar();
		calendar.setTimeZone(UTC);
		calendar.set(year, (month - 1), day);
		validateCalendar(calendar);
		
		epochTime = calendar.getTimeInMillis();
	}
	
	/**
	 * <b>Note:</b>
	 * Is only valid for dates > 1582-10-25
	 */
	private void setEpochTimeFromDate()
	{
		// move January and February to end of previous year
		int a = (14 - month)/12;
		int adjYear = year - a;
		int yearDays = 365*adjYear + (year/4 - year/100 + year/400);
		
		int adjMonth = month + 12*a - 3; //march = 0, February = 11
		int monthDays = (153*adjMonth + 2)/5; //calculates days since 01-March to beginning of month
		
		int adjDays = day - 1; //remove 1 day and adjust days to start month
		if (isLeapYear(year) && (month < 3))
		{
			adjDays += 59;
		}
		else
		{
			adjDays += 60;
		}
		
		long totalDays = yearDays + monthDays + adjDays;
		
		long daysSinceEpoch = totalDays - DAYS_0_TO_1970;
		
		epochTime = daysSinceEpoch * MILLIS_PER_DAY;
	}
	
	/**
	 * <b>Note:</b>
	 * does not validate dates.
	 */
	private void setDate()
	{
		if (epochTime <= DEFAULT_GREGORIAN_CUTOVER)
	    {
	        setDateFromCalendar();
	    }
		else
		{
			setDateFromEpochTime(epochTime);
		}
	}
	
	/**
	 * Uses this epochTime to create a UTC Calendar object, and set this year, month, day
	 */
	private void setDateFromCalendar()
	{
		Calendar calendar = initCalendar();
		calendar.setTimeZone(UTC);
		calendar.setTimeInMillis(epochTime);
		validateCalendar(calendar);
		
		setDateFromCalendar(calendar);
	}
	
	/**
	 * Creates a new Calendar object and clear any previous values of fields that may be retained 
	 * before setting fields.
	 * @return Calendar calendar
	 */
	public static Calendar initCalendar()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setLenient(false);
		calendar.clear();
		
		return calendar;
	}
	
	/**
	 * The time value in milliseconds is not recomputed until the next get* call.
	 * The get call will also validate the Calendar's field's value.
	 * @param calendar Calendar
	 */
	public static void validateCalendar(Calendar calendar)
	{
		calendar.get(Calendar.YEAR);
		calendar.get(Calendar.MONTH);
	    calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	private void setDateFromCalendar(Calendar calendar)
	{
		year = (short) calendar.get(Calendar.YEAR);
		month = (byte) (calendar.get(Calendar.MONTH) + 1);
		day = (byte) calendar.get(Calendar.DAY_OF_MONTH);
		epochTime = calendar.getTimeInMillis();
	}
		
	/**
	 * <b>Note:</b>
	 * Is only valid for epochTime > {@link DEFAULT_GREGORIAN_CUTOVER}
	 */
	private void setDateFromEpochTime(long epochTime)
	{
		long daysSinceEpoch = epochTime / MILLIS_PER_DAY;
		int totalDays = (int)(daysSinceEpoch + DAYS_0_TO_1970 - 60); //remove 60 days to adjust start month.
		
		// calculate the number of 400 year, 100 year, and 4-year cycles since year 0
		int cycle400 = totalDays / DAYS_PER_400_CYCLE;
		int remainder400 = totalDays - cycle400 * DAYS_PER_400_CYCLE;
		int cycle100 = Math.min(remainder400 / DAYS_PER_100_CYCLE, 3);
		int remainder100 = remainder400 - cycle100 * DAYS_PER_100_CYCLE;
		int cycle4 = remainder100 / DAYS_PER_4_CYCLE;
		int remainder4 = remainder100 - cycle4 * DAYS_PER_4_CYCLE;
		int cycle1 = Math.min(remainder4 / DAYS_PER_YEAR, 3);
		int remainder = remainder4 - cycle1 * DAYS_PER_YEAR;
		
		int totalYears = (400 * cycle400) + (100 * cycle100) + (4 * cycle4) + cycle1;
		int totalMonths = (5*remainder + 2)/153; //calculates months since 01-March
		int days = remainder - (153*totalMonths + 2)/5;
		
		int a = (totalMonths)/10;
		year = (short) (totalYears + a);
		month = (byte) ((totalMonths + 3) - 12*(totalMonths/10));
		day = (byte) (days + 1);
	}
	
	
	
	/**
	 * Valid field values are as follows:
	 * 	<pre>
	 *            Null   Greatest     Least
	 * Field   Minimum    Minimum   Maximum   Maximum
	 * -----   -------   --------   -------   -------
 	 * YEAR          0          1     32767     32767
	 * MONTH         0          1        12        12
	 * DAY           0          1        28*       31
	 * </pre>
	 */
	private void validate(int year, int month, int day) throws IllegalArgumentException
	{
		if (isNullDate(year, month, day))
		{
			return;
		}
		if (year < 1 || year > Short.MAX_VALUE)
		{
			throw new IllegalArgumentException("unsupported year " + year);
		}
		if (month < 1 || month > 12)
		{
			throw new IllegalArgumentException("unsupported month " + month);
		}
		validateDayOfMonth(year, month, day);
	}
	
	/**
	 * A SimpleDate is considered <code>null</code> if
	 * <pre>
	 * year == 0 && month == 0 && day == 0
	 * </pre>
	 * @return true iff this has all zeroed values.
	 */
	protected boolean isNullDate()
	{
		return isNullDate(year, month, day);
	}
	
	private boolean isNullDate(int year, int month, int day)
	{
		if (year == 0 && month == 0 && day == 0)
		{
			return true;
		}
		return false;
	}
	
	private void validateDayOfMonth(int year, int month, int day) throws IllegalArgumentException
	{
		final int[] MONTH_LENGTH = {31,29,31,30,31,30,31,31,30,31,30,31};
		final int FEBRUARY = 2;
		final int LEAP_DAY = 29;
		
		if (day < 1 || day > MONTH_LENGTH[month - 1])
		{
			throw new IllegalArgumentException("unsupported day " + day + " for month " + month);
		}
		if (day == LEAP_DAY && month == FEBRUARY && !isLeapYear(year))
		{
			throw new IllegalArgumentException(year + "is not a leap year, so " + 
					day + " is an unsupported day for month " + month);
		}
	}
	
	/**
	 * A leap year in the Gregorian calendar.
	 * <p>
	 * The Gregorian calendar was first used in 1582.
	 * </br>
	 * The Gregorian calendar removes three leap days every 400 years, 
	 * which is the length of its leap cycle. 
	 * This is done by removing February 29 in the three century years (multiples of 100) 
	 * that cannot be exactly divided by 400.
	 * <pre>
	 * if the year IS NOT divisible by 4, 
	 * then it is NOT a leap year.
	 * 
	 * else if the year IS NOT divisible by 100, OR IS divisible by 400,
	 * then it IS a leap year.
	 * </pre>
	 */
	private boolean isLeapYear(int year)
	{
		if (year % 4 != 0)
        {
            return false;
        }
		if (year >= GREGORIAN_CUTOVER_YEAR)
		{
			return (year % 100 != 0) || (year % 400 == 0); // Gregorian			
		}
		return true; // before Gregorian start, leap years are any years divisible by 4
    }
	
	/**
	 * returns the ISO8601 date format: <b>yyyy-MM-dd</b>
	 */
	@Override
	public String toString()
	{
		return String.format("%04d-%02d-%02d", this.year, this.month, this.day);
	}
	
	@Override
	public int compareTo(SimpleDate that)
	{
		if (this == that || this.equals(that))
		{
			return 0;
		}
		if (this.epochTime == that.epochTime)
		{
			return 0;
		}
		else if (this.epochTime < that.epochTime)
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}
}
package com.novarise.webase.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateHelper {
	/**
	 * 取得当前系统时间
	 */
	public static String getDateTime() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
		return sdf.format(now);
	}

	public static String getDateTimeCN() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日H时m分s秒");
		return sdf.format(now);
	}

	public static String getShortDateTime() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		return sdf.format(now);
	}

	public static String getShowDateTime() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-d HH:mm");
		return sdf.format(now);
	}

	public static String getAppDateTime() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.format(now);
	}

	/*
	 * 取得当前系统日期
	 */
	public static String getDate() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
		return sdf.format(now);
	}

	public static String getDateCN() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日");
		return sdf.format(now);
	}

	public static String getShortDate() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(now);
	}

	public static String getShortDate1() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		return sdf.format(now);
	}

	public static String getShortDateTimeTrim() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(now);
	}

	/*
	 * 取得中文的星期
	 */
	public static String getDayCN() {
		Date now = new Date();
		int day = now.getDay();
		StringBuffer dayStr = new StringBuffer("星期");
		if (day == 0) {
			dayStr.append("天");
		} else if (day == 1) {
			dayStr.append("一");
		} else if (day == 2) {
			dayStr.append("二");
		} else if (day == 3) {
			dayStr.append("三");
		} else if (day == 4) {
			dayStr.append("四");
		} else if (day == 5) {
			dayStr.append("五");
		} else if (day == 6) {
			dayStr.append("六");
		}
		return dayStr.toString();
	}

	/** */
	/**
	 * 取得某天相加(减)後的那一天
	 * 
	 * @param date
	 * @param num
	 *            (可正可负)
	 * @return
	 */
	public static Date getAnotherDate(Date date, int num) {
		Calendar c = Calendar.getInstance();

		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, num);
		return c.getTime();
	}

	/** */
	/**
	 * 取得某月的的最后一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static Date getLastDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);// 年
		cal.set(Calendar.MONTH, month - 1);// 月，因为Calendar里的月是从0开始，所以要减1
		cal.set(Calendar.DATE, 1);// 日，设为一号
		cal.add(Calendar.MONTH, 1);// 月份加一，得到下个月的一号
		cal.add(Calendar.DATE, -1);// 下一个月减一为本月最后一天
		return cal.getTime();// 获得月末是几号
	}

	/** */
	/**
	 * 取得当前月的的最后一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getLastDayOfCurrentMonth() {
		Date now = new Date();

		int year = now.getYear() + 1900;
		int month = now.getMonth() + 1;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);// 年
		cal.set(Calendar.MONTH, month - 1);// 月，因为Calendar里的月是从0开始，所以要减1
		cal.set(Calendar.DATE, 1);// 日，设为一号
		cal.add(Calendar.MONTH, 1);// 月份加一，得到下个月的一号
		cal.add(Calendar.DATE, -1);// 下一个月减一为本月最后一天

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");

		return sdf.format(cal.getTime());// 获得月末是几号
	}

	/** */
	/**
	 * 取得当前月的的第一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getFirstDayOfCurrentMonth() {
		Date now = new Date();

		int year = now.getYear() + 1900;
		int month = now.getMonth() + 1;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);// 年
		cal.set(Calendar.MONTH, month - 1);// 月，因为Calendar里的月是从0开始，所以要减1
		cal.set(Calendar.DATE, 1);// 日，设为一号
		// cal.add(Calendar.MONTH, 1);// 月份加一，得到下个月的一号
		// cal.add(Calendar.DATE, -1);// 下一个月减一为本月最后一天

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");

		return sdf.format(cal.getTime());// 获得月末是几号
	}

	/** */
	/**
	 * 取得某天是一年中的多少周
	 * 
	 * @param date
	 * @return
	 */
	public static int getWeekOfYear(Date date) {
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setMinimalDaysInFirstWeek(7);
		c.setTime(date);
		return c.get(Calendar.WEEK_OF_YEAR);
	}

	/** */
	/**
	 * 取得某天所在周的第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfWeek(Date date) {
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
		return c.getTime();
	}

	/** */
	/**
	 * 取得某天所在周的最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfWeek(Date date) {
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6);
		return c.getTime();
	}

	/** */
	/**
	 * 取得某一年共有多少周
	 * 
	 * @param year
	 * @return
	 */
	public static int getMaxWeekNumOfYear(int year) {
		Calendar c = new GregorianCalendar();
		c.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
		return getWeekOfYear(c.getTime());
	}

	/** */
	/**
	 * 取得某年某周的第一天 对于交叉:2008-12-29到2009-01-04属于2008年的最后一周,2009-01-05为2009年第一周的第一天
	 * 
	 * @param year
	 * @param week
	 * @return
	 */
	public static Date getFirstDayOfWeek(int year, int week) {
		Calendar calFirst = Calendar.getInstance();
		calFirst.set(year, 0, 7);
		Date firstDate = getFirstDayOfWeek(calFirst.getTime());

		Calendar firstDateCal = Calendar.getInstance();
		firstDateCal.setTime(firstDate);

		Calendar c = new GregorianCalendar();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, Calendar.JANUARY);
		c.set(Calendar.DATE, firstDateCal.get(Calendar.DATE));

		Calendar cal = (GregorianCalendar) c.clone();
		cal.add(Calendar.DATE, (week - 1) * 7);
		firstDate = getFirstDayOfWeek(cal.getTime());

		return firstDate;
	}

	/** */
	/**
	 * 取得某年某周的最后一天
	 * 对于交叉:2008-12-29到2009-01-04属于2008年的最后一周,2009-01-04为2008年最后一周的最后一天
	 * 
	 * @param year
	 * @param week
	 * @return
	 */
	public static Date getLastDayOfWeek(int year, int week) {
		Calendar calLast = Calendar.getInstance();
		calLast.set(year, 0, 7);
		Date firstDate = getLastDayOfWeek(calLast.getTime());

		Calendar firstDateCal = Calendar.getInstance();
		firstDateCal.setTime(firstDate);

		Calendar c = new GregorianCalendar();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, Calendar.JANUARY);
		c.set(Calendar.DATE, firstDateCal.get(Calendar.DATE));

		Calendar cal = (GregorianCalendar) c.clone();
		cal.add(Calendar.DATE, (week - 1) * 7);
		Date lastDate = getLastDayOfWeek(cal.getTime());

		return lastDate;
	}

	public static int daysofMonth(int year, int month) {
		int s_month = month - 1;
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year); // 2010年
		c.set(Calendar.MONTH, s_month); // 6 月
		return c.getActualMaximum(Calendar.DAY_OF_MONTH);

	}

	public static String getDayofweek(String date) {
		Calendar cal = Calendar.getInstance();
		// cal.setTime(new Date(System.currentTimeMillis()));
		if (date.equals("")) {
			cal.setTime(new Date(System.currentTimeMillis()));
		} else {
			cal.setTime(new Date(getDateByStr2(date).getTime()));
		}
		int day=cal.get(Calendar.DAY_OF_WEEK);
		
		day=day-1;
		
	    if(day == 0){  
	        day = 7;  
	    }  
		StringBuffer dayStr = new StringBuffer("");
		if (day == 7) {
			dayStr.append("日");
		} else if (day == 1) {
			dayStr.append("一");
		} else if (day == 2) {
			dayStr.append("二");
		} else if (day == 3) {
			dayStr.append("三");
		} else if (day == 4) {
			dayStr.append("四");
		} else if (day == 5) {
			dayStr.append("五");
		} else if (day == 6) {
			dayStr.append("六");
		}
		//System.out.println(weeks);		
		return dayStr.toString();
	}

	public static Date getDateByStr2(String dd) {

		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		try {
			date = sd.parse(dd);
		} catch (Exception e) {
			date = null;
			e.printStackTrace();
		}
		return date;
	}
	
	

	public static void main(String args[]) {
		// System.out.println(DateHelper.getFirstDayOfCurrentMonth());
		// String s="aa|bb|cc";

		// String sf[]=s.split("\\|");
		// System.out.println("dd"+sf[0]);

		System.out.println(DateHelper.getDayofweek("2017-01-07"));
	}

}

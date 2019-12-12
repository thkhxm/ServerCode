package com.lkh.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@Deprecated
public class DateTimeUtil {

//	public static DateTimeFormatter ymd = DateTimeFormatter.ofPattern("yyyyMMdd");
//	public static DateTimeFormatter ymdhms = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//	public static DateTimeFormatter mdhm = DateTimeFormatter.ofPattern("MM-dd HH:mm");
//	public static DateTimeFormatter hms = DateTimeFormatter.ofPattern("HH:mm:ss");
//	public static DateTimeFormatter y_m_d = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//
//	/**
//	 * 两时间差天数
//	 * @param d1 时间1
//	 * @param d2 时间2
//	 * @param hour 小时数基准，0则是以每日0点来计算，分钟，秒数都会重置成0
//	 * @return 相差天数
//	 */
//	public static int getDaysBetweenNum(DateTime d1, DateTime d2, int hour) {
//		d1 = d1.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(hour);
//		d2 = d2.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(hour);
//		return Days.daysBetween(d1, d2).getDays();
//	}
//
//	/**
//	 * 一般用做时间某时间差的开始天数，也就是第一天时间开始天数是1这种情况调用
//	 * 两时间差天数
//	 * @param d1 时间1
//	 * @param d2 时间2
//	 * @param hour 小时数基准，0则是以每日0点来计算，分钟，秒数都会重置成0
//	 * @return 相差天数 + 1
//	 */
//	public static int getDaysStartNum(DateTime d1, DateTime d2, int hour) {
//		return getDaysBetweenNum(d1, d2, hour) + 1;
//	}
//
//	/**
//	 * 两个自然月的时间差，如10和N号和9月N号都是相差一个月
//	 * @param d1
//	 * @param d2
//	 * @return
//	 */
//	public static int getMonthsBetweenNum(DateTime d1, DateTime d2) {
//		d1 = d1.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).withDayOfMonth(1);
//		d2 = d2.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).withDayOfMonth(1);
//		return Months.monthsBetween(d1, d2).getMonths();
//	}
//
//	public static String getNow(){
//		return getString(DateTime.now(),ymd);
//	}
//
//	public static String getNowTime(){
//		return getString(DateTime.now(),ymdhms);
//	}
//
//	public static String getNowTimeDate(){
//		return getString(DateTime.now(),mdhm);
//	}
//
//	public static String getNowTimeDateHms(){
//		return getString(DateTime.now(),hms);
//	}
//
//	public static DateTime getDateTime(String time){
//		LocalDateTime date = LocalDateTime.parse(time, ymdhms);
//		return DateTime.now().withYear(date.getYear()).withMonthOfYear(date.getMonthValue()).withDayOfMonth(date.getDayOfMonth())
//					.withHourOfDay(date.getHour()).withMinuteOfHour(date.getMinute()).withSecondOfMinute(date.getSecond());
//	}
//
//	public static String getString(DateTime dt,DateTimeFormatter dtf){
//		LocalDateTime ldt = LocalDateTime.of(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth(), dt.getHourOfDay(), dt.getMinuteOfHour(), dt.getSecondOfMinute());
//		return ldt.format(dtf);
//	}
//
//	public static String getString(DateTime dt){
//		return getString(dt,ymd);
//	}
//	public static String getStringSql(DateTime dt){
//		return getString(dt,ymdhms);
//	}
//
//	/**
//	 * @param date
//	 * @return
//	 * 计算date到当前时间之间相差多少秒
//	 */
//	public static int secondBetween(Date date){
//		int second = Seconds.secondsBetween(new DateTime(date), DateTime.now()).getSeconds();
//		return second;
//	}
//	/**
//	 * @param date
//	 * @return
//	 * 计算date到当前时间之间相差多少秒
//	 */
//	public static int secondBetween(DateTime date){
//		int second = Seconds.secondsBetween(date, DateTime.now()).getSeconds();
//		return second;
//	}
//
//	/**
//	 * @param date
//	 * @return
//	 * 计算date到当前时间之间相差多少秒
//	 */
//	public static int secondBetween(DateTime start,DateTime end){
//		int second = Seconds.secondsBetween(start, end).getSeconds();
//		return second;
//	}
//	/**
//	 * @param date
//	 * @return
//	 * 计算date到当前时间之间相差多少秒
//	 */
//	public static int minuteBetween(DateTime start,DateTime end){
//		int second = Minutes.minutesBetween(start, end).getMinutes();
//		return second;
//	}
//
//	/**
//	 * @param date
//	 * @return
//	 * 计算date到当前时间之间相差多少秒
//	 */
//	public static int secondBetween(Date start,Date end){
//		int second = Seconds.secondsBetween(new DateTime(start), new DateTime(end)).getSeconds();
//		return second;
//	}
//
//	/**
//	 * @param date
//	 * @return
//	 * 计算date到当前时间之间相差多少小时
//	 */
//	public static int hoursBetween(Date date){
//		int hours = Hours.hoursBetween(new DateTime(date), DateTime.now()).getHours();
//		return hours;
//	}
//	/**
//	 * @param date
//	 * @return
//	 * 计算date到当前时间之间相差多少小时
//	 */
//	public static int hoursBetween(DateTime date){
//		int hours = Hours.hoursBetween(date, DateTime.now()).getHours();
//		return hours;
//	}
//
//
//	public static int dayBetween(Date date){
//		int day = Days.daysBetween(DateTime.now(), new DateTime(date)).getDays();
//		return day;
//	}
//
//	public static DateTime addSecond(int second){
//		return DateTime.now().plusSeconds(second);
//	}
//
//	public static Date addHours(Date date,int hours){
//		DateTime dt = new DateTime(date).plusHours(hours);
//		return dt.toDate();
//	}
//
//	public static Date addHours(int hours){
//		return DateTime.now().plusHours(hours).toDate();
//	}
//
//	public static Date addDay(Date date,int day){
//		DateTime dt = new DateTime(date).plusDays(day);
//		return dt.toDate();
//	}
//
//	public static Date addDay(int day){
//		return DateTime.now().plusDays(day).toDate();
//	}
//
//	/**
//	 * @param date
//	 * @return
//	 * 判断date是否在当前时间之前
//	 */
//	public static boolean isBefore(Date date){
//		return new DateTime(date).isBefore(DateTime.now());
//	}
//
//	/**
//	 * @param date
//	 * @return
//	 * 判断date是否在当前时间之前
//	 */
//	public static boolean isBefore(DateTime date){
//		return date.isBefore(DateTime.now());
//	}
//
//	/**
//	 * @param date
//	 * @return
//	 * 判断date是否在当前时间之后
//	 */
//	public static boolean isAfter(Date date){
//		return new DateTime(date).isAfter(DateTime.now());
//	}
//	/**
//	 * @param date
//	 * @return
//	 * 判断date是否在当前时间之后
//	 */
//	public static boolean isAfter(DateTime date){
//		return new DateTime(date).isAfter(DateTime.now());
//	}
}

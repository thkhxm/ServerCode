package com.lkh.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class LocalDateTimeUtil {

	static private DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	static final public String formatDate(LocalDateTime date){
		return date.format(dt);
	}
	
	static final public String formatDate(){
		return LocalDateTime.now().format(dt);
	}
	static final public LocalDateTime toLocalDateTime(Date time){
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(time.getTime()), ZoneId.systemDefault());
	}
	
	private static ZoneOffset zone = ZoneOffset.of("+8");
	private static DateTimeFormatter defaultFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	
	
	/**
	 * 转换LocalDateTime对象
	 * @param times
	 * @return
	 */
	public static LocalDateTime converLocalDateTime(long times) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(times), ZoneId.systemDefault());
	}
	
	/**
	 * 转换时间戳
	 * @param dateTime
	 * @return
	 */
	public static long converMill(LocalDateTime dateTime) {
		return dateTime.toInstant(zone).toEpochMilli();
	}
	
	/**
	 * 比较日期，是否当天
	 * @param date
	 * @return
	 */
	public static boolean equDate(LocalDateTime date) {
		return LocalDateTime.now().toLocalDate().equals(date.toLocalDate());
	}
	
	/**
	 * 取当前时间时间戳
	 * @return
	 */
	static public long getMill() {
		return Instant.now().toEpochMilli();
	}
	
	
	/**
	 * 日期间隔
	 * @param date
	 * @return
	 */
	static public int betweenDay(LocalDateTime date) {
		return (int)(date.toLocalDate().toEpochDay() - LocalDate.now().toEpochDay());
	}
	
	static public long betweenSecond(LocalDateTime date) {
		return Duration.between(LocalDateTime.now(), date).getSeconds(); 
	}
	
	
	static public LocalDateTime toLocalDateTime(String date) {
		return LocalDateTime.parse(date, defaultFormat);
	}

	/**
	 * 计算两个LocalDate之间的天数间隔
	 * */
	public static int calDays(LocalDate start,LocalDate end){
		Period period = Period.between(start, end);
		return period.getDays();
	}

	/**
	 * 时间戳转LocalDate
	 * */
	public static LocalDate cover2LocalDate(long time){
		LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
		return localDateTime.toLocalDate();
	}

	public static Date conver2Date(LocalDateTime dt){
		ZoneId zoneId = ZoneId.systemDefault();
		LocalDateTime localDateTime = LocalDateTime.now();
		ZonedDateTime zdt = localDateTime.atZone(zoneId);
		Date date = Date.from(zdt.toInstant());
		return date;

	}

	public static void main(String[] args) {
//		String date = "2018-01-01 23:00:00";
//		System.err.println(LocalDateTime.parse(date, defaultFormat));
		LocalDateTime now = LocalDateTime.now().withMonth(9).withYear(2017).withDayOfMonth(16).withHour(1).withMinute(27);
		LocalDateTime ss = LocalDateTime.now();
		Duration d = Duration.between(now, ss);
		System.err.println(d.getSeconds());
//		System.err.println(betweenSecond(now));
	}
	
	
}

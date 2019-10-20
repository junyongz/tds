package com.aloha.tds.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public abstract class DateTimeUtils {

	/**
	 * Obtain a date instance for the provided human-represented date and time,
	 * using default zone
	 * 
	 * @param year the year to represent, from MIN_YEAR to MAX_YEAR
	 * @param month the month-of-year to represent, from 1 (January) to 12
	 *        (December)
	 * @param dayOfMonth the day-of-month to represent, from 1 to 31
	 * @param hour the hour-of-day to represent, from 0 to 23
	 * @param minute the minute-of-hour to represent, from 0 to 59
	 * @return the date instance of the default zone
	 */
	public static Date fromDateTime(int year, int month, int dayOfMonth, int hour, int minute) {
		return Date.from(
				LocalDateTime.of(year, month, dayOfMonth, hour, minute).atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date fromDateTime(ZoneId zone, int year, int month, int dayOfMonth, int hour, int minute) {
		return Date.from(LocalDateTime.of(year, month, dayOfMonth, hour, minute).atZone(zone).toInstant());
	}
	
	public static boolean isTimeClashed(Date fromDate, Date toDate, Date targetedFromDate, Date targetedToDate) {
		// (StartA <= EndB) and (EndA >= StartB)
		return (fromDate.compareTo(targetedToDate) <= 0 && toDate.compareTo(targetedFromDate) >= 0);
	}
}

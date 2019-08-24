package com.aloha.tds.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateTimeUtils {

	public static Date fromDateTime(int year, int month, int dayOfMonth, int hour, int minute) {
		return Date.from(
				LocalDateTime.of(year, month, dayOfMonth, hour, minute).atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date fromDateTime(ZoneId zone, int year, int month, int dayOfMonth, int hour, int minute) {
		return Date.from(LocalDateTime.of(year, month, dayOfMonth, hour, minute).atZone(zone).toInstant());
	}
}

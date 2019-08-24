package com.aloha.tds.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

public class DateTimeUtilsTest {

	@Test
	public void usingLocalDateTime() {
		Date date = new Calendar.Builder().setDate(2019, Calendar.JUNE, 28).setTimeOfDay(9, 30, 0).build().getTime();

		assertThat(DateTimeUtils.fromDateTime(2019, 6, 28, 9, 30), is(date));

		date = new Calendar.Builder().setDate(2019, Calendar.FEBRUARY, 28).setTimeOfDay(16, 30, 0).build().getTime();

		assertThat(DateTimeUtils.fromDateTime(2019, 2, 28, 16, 30), is(date));
	}

	@Test
	public void usingLocalDateTimeAtZone() {
		Date date = new Calendar.Builder().setDate(2019, Calendar.JUNE, 28).setTimeOfDay(9, 30, 0)
				.setTimeZone(TimeZone.getTimeZone(ZoneId.of("America/Jamaica"))).build().getTime();

		assertThat(DateTimeUtils.fromDateTime(ZoneId.of("America/Jamaica"), 2019, 6, 28, 9, 30), is(date));

		date = new Calendar.Builder().setDate(2019, Calendar.FEBRUARY, 28).setTimeOfDay(16, 30, 0)
				.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Jakarta"))).build().getTime();

		assertThat(DateTimeUtils.fromDateTime(ZoneId.of("Asia/Jakarta"), 2019, 2, 28, 16, 30), is(date));
	}

}

package cn.hutool.core.date;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

public class TemporalAccessorUtilTest {

	@Test
	public void formatLocalDateTest(){
		final String format = TemporalAccessorUtil.format(LocalDate.of(2020, 12, 7), DatePattern.NORM_DATETIME_PATTERN);
		assertEquals("2020-12-07 00:00:00", format);
	}

	@Test
	public void formatLocalTimeTest(){
		final String today = TemporalAccessorUtil.format(LocalDate.now(), DatePattern.NORM_DATE_PATTERN);
		final String format = TemporalAccessorUtil.format(LocalTime.MIN, DatePattern.NORM_DATETIME_PATTERN);
		assertEquals(today + " 00:00:00", format);
	}

	@Test
	public void formatCustomTest(){
		final String today = TemporalAccessorUtil.format(
				LocalDate.of(2021, 6, 26), "#sss");
		assertEquals("1624636800", today);

		final String today2 = TemporalAccessorUtil.format(
				LocalDate.of(2021, 6, 26), "#SSS");
		assertEquals("1624636800000", today2);
	}

	@Test
	public void isInTest(){
		final String sourceStr = "2022-04-19 00:00:00";
		final String startTimeStr = "2022-04-19 00:00:00";
		final String endTimeStr = "2022-04-19 23:59:59";
		final boolean between = TemporalAccessorUtil.isIn(
				LocalDateTimeUtil.parse(sourceStr, DatePattern.NORM_DATETIME_FORMATTER),
				LocalDateTimeUtil.parse(startTimeStr, DatePattern.NORM_DATETIME_FORMATTER),
				LocalDateTimeUtil.parse(endTimeStr, DatePattern.NORM_DATETIME_FORMATTER));
		assertTrue(between);
	}
}

package org.brokong.morakbackend.global;

import java.time.format.DateTimeFormatter;

public final class DateTimeFormatters {

	private DateTimeFormatters() {} // 인스턴스화 방지

	public static final DateTimeFormatter MORAK_DATETIME_FORMATTER =
		DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
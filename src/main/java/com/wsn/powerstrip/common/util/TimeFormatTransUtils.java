package com.wsn.powerstrip.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class TimeFormatTransUtils {
    public static Long localDateTime2timeStamp(LocalDateTime localDateTime){
        return localDateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }

    public static long Date2timeStamp(Date date) {
        return date.toInstant().toEpochMilli();
    }
    public static LocalDateTime timestamp2localDateTime(Long timestamp){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("UTC"));
    }

    public static LocalDateTime date2LocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.of("UTC");
        return instant.atZone(zoneId).toLocalDateTime();
    }

    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.of("UTC");
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }

    public static Date timeStamp2Date(Long timestamp) {
        ZoneId zoneId = ZoneId.of("UTC");
        LocalDateTime localDateTime = timestamp2localDateTime(timestamp);
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }

    public static Date minusMinutes(Date date, long minutes) {
        return localDateTime2Date(date2LocalDateTime(date).minusMinutes(minutes));
    }
}

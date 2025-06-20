package com.aiolos.live.common.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.util.ObjectUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateFormatterUtil {

    public static String getUTCDatetime(Date date) {
        return getUTCDatetimeWithCTT(DateUtil.formatDateTime(date));
    }

    public static String getUTCDatetimeWithCTT(String date) {
        if (StringUtils.isBlank(date)) return StringUtils.EMPTY;
        return LocalDateTime.ofInstant(DateUtil.parse(date).toInstant(), ZoneId.of("UTC")).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static String getCurrentUTCDatetime() {
        return getUTCDatetimeWithCTT(DateUtil.now());
    }

    public static String getGMTDatetime(Date date) {
        return DateUtil.format(date, FastDateFormat.getInstance("MMM d, yyyy", TimeZone.getTimeZone("GMT"), Locale.US));
    }

    public static Date parseUTC(String timeUtc) {
        if (ObjectUtil.contains(timeUtc, "CST")) {
            timeUtc = getUTCDatetimeWithCTT(timeUtc);
        }
        if (timeUtc.length() != DatePattern.UTC_WITH_ZONE_OFFSET_PATTERN.length() + 2 && timeUtc.length() != DatePattern.UTC_WITH_ZONE_OFFSET_PATTERN.length() + 3
                && timeUtc.length() != DatePattern.UTC_MS_WITH_ZONE_OFFSET_PATTERN.length() + 2 && timeUtc.length() != DatePattern.UTC_MS_WITH_ZONE_OFFSET_PATTERN.length() + 3
                &&!ObjectUtil.contains(timeUtc, ZoneOffset.UTC.getId())) {
            timeUtc = timeUtc.concat(ZoneOffset.UTC.getId());
        }
        Date date;
        try {
            date = DateUtil.parseUTC(timeUtc);
        } catch (Exception e) {
            date = DateUtil.parse(timeUtc);
        }
        return date;
    }

    /**
     * 获取utc时间的年月日
     * @param timeUtc
     * @return
     */
    public static String formatDate(String timeUtc) {
        FastDateFormat dateFormat = FastDateFormat.getInstance(DatePattern.NORM_DATE_PATTERN, TimeZone.getTimeZone("UTC"));
        return dateFormat.format(parseUTC(timeUtc));
    }

    public static String formatDateByPattern(String timeUtc, String pattern) {
        FastDateFormat dateFormat = FastDateFormat.getInstance(pattern, TimeZone.getTimeZone("UTC"));
        return dateFormat.format(parseUTC(timeUtc));
    }

    /**
     * 获取utc时间的时间
     * @param timeUtc
     * @return
     */
    public static String formatTime(String timeUtc) {
        FastDateFormat dateFormat = FastDateFormat.getInstance(DatePattern.NORM_TIME_PATTERN, TimeZone.getTimeZone("UTC"));
        return dateFormat.format(parseUTC(timeUtc));
    }

    /**
     * utc时间转其他时区年月日
     * @param timeUtc
     * @param zoneId
     * @return
     */
    public static String formatDate(String timeUtc, String zoneId) {
        if (StringUtils.isBlank(zoneId))
            zoneId = ZoneOffset.UTC.getId();

        LocalDateTime localDateTime = LocalDateTime.ofInstant(parseUTC(timeUtc).toInstant(), ZoneId.of(zoneId));
        Date date = Date.from(ZonedDateTime.of(localDateTime, ZoneId.of(zoneId)).toInstant());
        FastDateFormat dateFormat = FastDateFormat.getInstance(DatePattern.NORM_DATE_PATTERN, TimeZone.getTimeZone(zoneId));
        return dateFormat.format(date);
    }

    /**
     * utc时间转其他时区时间
     * @param timeUtc
     * @param zoneId
     * @return
     */
    public static String formatTime(String timeUtc, String zoneId) {
        if (StringUtils.isBlank(zoneId))
            zoneId = ZoneOffset.UTC.getId();

        LocalDateTime localDateTime = LocalDateTime.ofInstant(parseUTC(timeUtc).toInstant(), ZoneId.of(zoneId));
        Date date = Date.from(ZonedDateTime.of(localDateTime, ZoneId.of(zoneId)).toInstant());
        FastDateFormat dateFormat = FastDateFormat.getInstance(DatePattern.NORM_TIME_PATTERN, TimeZone.getTimeZone(zoneId));
        return dateFormat.format(date);
    }
}

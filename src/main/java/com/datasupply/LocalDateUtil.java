package com.datasupply;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * 日期工具类，使用java8新API
 */
public class LocalDateUtil {

    /**
     * 获取时间段时间集合
     *
     * @param beginTime    开始时间
     * @param endTime      结束时间
     * @param originPatten 原时间格式
     * @param destPatten   目标时间格式
     * @return 返回List（String） 时间字符串集合
     */
    public List<String> getDayList(String beginTime, String endTime, String originPatten, String destPatten) {
        DateTimeFormatter originFormat = DateTimeFormatter.ofPattern(originPatten);
        DateTimeFormatter destFormat = DateTimeFormatter.ofPattern(destPatten);
        final List<String> list = new ArrayList();
        for (LocalDate i = LocalDate.parse(beginTime, originFormat); i.compareTo(LocalDate.parse(endTime, originFormat)) <= 0; i = i.plusDays(1)) {
            list.add(i.format(destFormat));
        }
        return list;
    }


    /**
     * 时间格式转换
     *
     * @param date         时间
     * @param originPatten 原格式
     * @param destPatten   目标格式
     * @return 目标格式的时间字符串
     */
    public String convertFormat(String date, String originPatten, String destPatten) {
        if (date == null || "".equals(date)) {
            return "";
        }
        DateTimeFormatter originFormat = DateTimeFormatter.ofPattern(originPatten);
        DateTimeFormatter destFormat = DateTimeFormatter.ofPattern(destPatten);
        final LocalDateTime localDate = LocalDateTime.parse(date, originFormat);
        return localDate.format(destFormat);
    }


    /**
     * 获取指定时间的上月的最后一天
     *
     * @param date         日期
     * @param originPatten 原格式
     * @param destPatten   目标格式
     * @return 获取指定时间的上月的最后一天字符串
     */
    public String getLastDayOfLastMonth(String date, String originPatten, String destPatten) {
        DateTimeFormatter originFormat = DateTimeFormatter.ofPattern(originPatten);
        DateTimeFormatter destFormat = DateTimeFormatter.ofPattern(destPatten);
        final LocalDate originDate = LocalDate.parse(date, originFormat);
        final LocalDate destDate = originDate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        return destDate.format(destFormat);
    }

    /**
     * 获取昨天日期
     *
     * @param patten 格式
     * @return 指定格式昨天日期字符串
     */
    public String getYesterday(String patten) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(patten);
        final LocalDate yesterday = LocalDate.now().minusDays(1);
        return dateTimeFormatter.format(yesterday);
    }


    /**
     * 获取当月第一天
     *
     * @param date         日期
     * @param originPatten 原格式
     * @param destPatten   目标合适
     * @return
     */
    public String getFirstDayOfMonth(String date, String originPatten, String destPatten) {
        DateTimeFormatter originFormat = DateTimeFormatter.ofPattern(originPatten);
        DateTimeFormatter destFormat = DateTimeFormatter.ofPattern(destPatten);
        final LocalDate originDate = LocalDate.parse(date, originFormat);
        final LocalDate firstDayOfMonth = originDate.with(TemporalAdjusters.firstDayOfMonth());
        return firstDayOfMonth.format(destFormat);
    }


    /**
     * 获取当月最后一天
     *
     * @param date         日期
     * @param originPatten 原格式
     * @param destPatten   目标合适
     * @return
     */
    public String getLastDayOfMonth(String date, String originPatten, String destPatten) {
        DateTimeFormatter originFormat = DateTimeFormatter.ofPattern(originPatten);
        DateTimeFormatter destFormat = DateTimeFormatter.ofPattern(destPatten);
        final LocalDate originDate = LocalDate.parse(date, originFormat);
        final LocalDate firstDayOfMonth = originDate.with(TemporalAdjusters.lastDayOfMonth());
        return firstDayOfMonth.format(destFormat);
    }

    public String minusDay(String time, Integer minus, String originPatten, String destPatten) {
        DateTimeFormatter destFormatter = DateTimeFormatter.ofPattern(destPatten);
        DateTimeFormatter originFormatter = DateTimeFormatter.ofPattern(originPatten);
        LocalDate localDate = LocalDate.parse(time, originFormatter);
        return destFormatter.format(localDate.minusDays(minus));
    }

}

package com.vurtnewk.emsgdemo.utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @author 蒋洪波
 * @file DateUtils.java
 * @brief 日期工具类
 * @date 2015-6-10
 * Copyright (c) 2015, 北京球友圈网络科技有限责任公司
 * All rights reserved.
 */
public class DateUtils {

    public DateUtils() {
    }

    public static String getTimestampString(Date date) {
        String s = null;
        long l = date.getTime();
        if (isSameDay(l)) {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(date);
            int i = calendar.get(Calendar.HOUR_OF_DAY);
            if (i > 17)
                s = "晚上 hh:mm";
            else if (i >= 0 && i <= 6)
                s = "凌晨 hh:mm";
            else if (i > 11 && i <= 17)
                s = "下午 hh:mm";
            else
                s = "上午 hh:mm";
        } else if (isYesterday(l))
            s = "昨天 HH:mm";
        else
            s = "M月d日";
        return (new SimpleDateFormat(s, Locale.CHINA)).format(date);
    }

    public static boolean isCloseEnough(long l, long l1) {
        long l2 = l - l1;
        if (l2 < 0L)
            l2 = -l2;
        return l2 < 300l;
    }

    private static boolean isSameDay(long l) {
        TimeInfo timeinfo = getTodayStartAndEndTime();
        return l > timeinfo.getStartTime() && l < timeinfo.getEndTime();
    }

    private static boolean isYesterday(long l) {
        TimeInfo timeinfo = getYesterdayStartAndEndTime();
        return l > timeinfo.getStartTime() && l < timeinfo.getEndTime();
    }

    public static Date StringToDate(String s, String s1) {
        SimpleDateFormat simpledateformat = new SimpleDateFormat(s1);
        Date date = null;
        try {
            date = simpledateformat.parse(s);
        } catch (ParseException parseexception) {
            parseexception.printStackTrace();
        }
        return date;
    }

    public static String toTime(int i) {
        i /= 1000;
        int j = i / 60;
        boolean flag = false;
        if (j >= 60) {
            int k = j / 60;
            j %= 60;
        }
        int l = i % 60;
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(j),
                Integer.valueOf(l)});
    }

    public static String toTimeBySecond(int i) {
        int j = i / 60;
        boolean flag = false;
        if (j >= 60) {
            int k = j / 60;
            j %= 60;
        }
        int l = i % 60;
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(j),
                Integer.valueOf(l)});
    }

    public static TimeInfo getYesterdayStartAndEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();
        long l = date.getTime();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DATE, -1);
        calendar1.set(Calendar.HOUR_OF_DAY, 23);
        calendar1.set(Calendar.MINUTE, 59);
        calendar1.set(Calendar.SECOND, 59);
        calendar1.set(Calendar.MILLISECOND, 999);
        Date date1 = calendar1.getTime();
        long l1 = date1.getTime();
        TimeInfo timeinfo = new TimeInfo();
        timeinfo.setStartTime(l);
        timeinfo.setEndTime(l1);
        return timeinfo;
    }

    public static TimeInfo getTodayStartAndEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();
        long l = date.getTime();
        SimpleDateFormat simpledateformat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss S");
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, 23);
        calendar1.set(Calendar.MINUTE, 59);
        calendar1.set(Calendar.SECOND, 59);
        calendar1.set(Calendar.MILLISECOND, 999);
        Date date1 = calendar1.getTime();
        long l1 = date1.getTime();
        TimeInfo timeinfo = new TimeInfo();
        timeinfo.setStartTime(l);
        timeinfo.setEndTime(l1);
        return timeinfo;
    }

    public static TimeInfo getBeforeYesterdayStartAndEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -2);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();
        long l = date.getTime();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DATE, -2);
        calendar1.set(Calendar.HOUR_OF_DAY, 23);
        calendar1.set(Calendar.MINUTE, 59);
        calendar1.set(Calendar.SECOND, 59);
        calendar1.set(Calendar.MILLISECOND, 999);
        Date date1 = calendar1.getTime();
        long l1 = date1.getTime();
        TimeInfo timeinfo = new TimeInfo();
        timeinfo.setStartTime(l);
        timeinfo.setEndTime(l1);
        return timeinfo;
    }

    public static TimeInfo getCurrentMonthStartAndEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();
        long l = date.getTime();
        Calendar calendar1 = Calendar.getInstance();
        Date date1 = calendar1.getTime();
        long l1 = date1.getTime();
        TimeInfo timeinfo = new TimeInfo();
        timeinfo.setStartTime(l);
        timeinfo.setEndTime(l1);
        return timeinfo;
    }

    public static TimeInfo getLastMonthStartAndEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();
        long l = date.getTime();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.MONTH, -1);
        calendar1.set(Calendar.DATE, 1);
        calendar1.set(Calendar.HOUR_OF_DAY, 23);
        calendar1.set(Calendar.MINUTE, 59);
        calendar1.set(Calendar.SECOND, 59);
        calendar1.set(Calendar.MILLISECOND, 999);
        calendar1.roll(Calendar.DATE, -1);
        Date date1 = calendar1.getTime();
        long l1 = date1.getTime();
        TimeInfo timeinfo = new TimeInfo();
        timeinfo.setStartTime(l);
        timeinfo.setEndTime(l1);
        return timeinfo;
    }

    public static String getTimestampStr() {
        return Long.toString(System.currentTimeMillis());
    }

    private static final long INTERVAL_IN_MILLISECONDS = 30000L;



    public static long stringToLong(String strTime, String formatType)
            throws ParseException {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }
    public static long dateToLong(Date date) {
        return date.getTime();
    }
    /**获取两个时间间隔*/
    public  static String getTwoDay(String sj1, String sj2) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long day = 0;
        try {
            Date date = myFormatter.parse(sj1);
            Date mydate = myFormatter.parse(sj2);

            day = (date.getTime() - mydate.getTime()) / (60*1000);

        } catch (Exception e) {
            return "";
        }

        return day+"";
    }


    /**获取两个时间间隔毫秒数*/
    public  static long getTwoDaysSecond(String sj1, String sj2) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long day = 0;
        try {
            Date date = myFormatter.parse(sj1);
            Date mydate = myFormatter.parse(sj2);

            day = (date.getTime() - mydate.getTime());

        } catch (Exception e) {
            return 0;
        }

        return day;
    }

    /**
     * 获取当天是星期几
     *
     * @param date
     * @return
     */
    public static String getWeekday(String date) {//必须yyyy-MM-dd
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdw = new SimpleDateFormat("E");
        Date d = null;
        try {
            d = sd.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdw.format(d);
    }

}
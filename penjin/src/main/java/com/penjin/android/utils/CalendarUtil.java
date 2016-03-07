package com.penjin.android.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by maotiancai on 2015/12/11.
 */
public class CalendarUtil {

    /**
     * Calendar 1月是0 ，12月是11
     */
    private int daysOfMonth = 0;      //某月的天数
    private int dayOfWeek = 0;        //具体某一天是星期几


    // 判断是否为闰年
    public boolean isLeapYear(int year) {
        if (year % 100 == 0 && year % 400 == 0) {
            return true;
        } else if (year % 100 != 0 && year % 4 == 0) {
            return true;
        }
        return false;
    }

    //得到某月有多少天数
    public int getDaysOfMonth(boolean isLeapyear, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                daysOfMonth = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                daysOfMonth = 30;
                break;
            case 2:
                if (isLeapyear) {
                    daysOfMonth = 29;
                } else {
                    daysOfMonth = 28;
                }

        }
        return daysOfMonth;
    }

    //指定某年中的某月的第一天是星期几
    public int getWeekdayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);
        dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return dayOfWeek;
    }

    public static int getYear(Date date) {
        return date.getYear() + 1900;
    }

    public static int getMonth(Date date) {
        return date.getMonth() + 1;
    }

    public static int getDate(Date date) {
        return date.getDate();
    }

    /**
     * 当天是星期几，返回中文字符串
     *
     * @param date
     * @return
     */
    public static String getCnDay(Date date) {
        switch (date.getDay()) {
            case 1:
                return "周一";
            case 2:
                return "周二";
            case 3:
                return "周三";
            case 4:
                return "周四";
            case 5:
                return "周五";
            case 6:
                return "周六";
            default:
                return "周日";
        }
    }

    public static int getDay(Date date) {
        return date.getDay();
    }

    public static String daysBetween(Date date1, Date date2) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        long time1 = cal.getTimeInMillis();
        cal.setTime(date2);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        long between_hours = ((time2 - time1) - between_days * (1000 * 3600 * 24)) / (1000 * 60 * 60);
        if (between_days > 0) {
            return between_days + "天" + between_hours + "小时";
        } else {
            return between_hours + "小时";
        }
    }
}

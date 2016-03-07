package com.penjin.android.domain;

import com.penjin.android.utils.CalendarUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by maotiancai on 2016/1/12.
 */
public class PenjinDate {

    public int year;//年
    public int month;//月
    public int date;//日
    public int day;//数字星期几，星期一是1，星期天是0
    public String weekDay;//中文星期几
    public int hour;
    public int minute;
    public int seconds;
    public Date javaDate;

    public PenjinDate() {

    }

    public PenjinDate(Date date) {
        this.javaDate = date;
        this.year = CalendarUtil.getYear(date);
        this.month = CalendarUtil.getMonth(date);
        this.date = CalendarUtil.getDate(date);
        this.day = CalendarUtil.getDay(date);
        this.weekDay = CalendarUtil.getCnDay(date);
        this.hour = date.getHours();
        this.minute = date.getMinutes();
        this.seconds = date.getSeconds();
    }

    public static PenjinDate getPreDay(PenjinDate penjinDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(penjinDate.year, penjinDate.month - 1, penjinDate.date);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date date = calendar.getTime();
        PenjinDate preDate = new PenjinDate(date);
        calendar = null;
        return preDate;
    }

    public static PenjinDate getNextDay(PenjinDate penjinDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(penjinDate.year, penjinDate.month - 1, penjinDate.date);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date = calendar.getTime();
        PenjinDate nextDate = new PenjinDate(date);
        calendar = null;
        return nextDate;
    }

    public boolean isSameDay(PenjinDate penjinDate) {
        if (this.year == penjinDate.year && this.month == penjinDate.month && this.date == penjinDate.date) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "PenjinDate{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", weekDay=" + weekDay +
                ", hour=" + hour +
                ", minute=" + minute +
                '}';
    }

}

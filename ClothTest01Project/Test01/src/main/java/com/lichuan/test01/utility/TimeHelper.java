package com.lichuan.test01.utility;

import android.content.Context;

import com.lichuan.test01.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by guoym on 15-6-27.
 */
public class TimeHelper {
    public static String ComputeTimeDisplayStringByModifiedDate(Context c, long tick) {
        Date d = new Date(tick * 1000);
        Date now = new Date();
        if (d.after(now)) {
            return c.getResources().getString(R.string.error_date);
        }

        Calendar calendarSome = new GregorianCalendar();
        calendarSome.setTime(d);
        int yearSome = calendarSome.get(Calendar.YEAR);
        int monthSome = calendarSome.get(Calendar.MONTH);
        int daySome = calendarSome.get(Calendar.DAY_OF_MONTH);
        int weekSome = calendarSome.get(Calendar.WEEK_OF_MONTH);
        int dayOfWeek = calendarSome.get(Calendar.DAY_OF_WEEK);
        int hourSome = calendarSome.get(Calendar.HOUR_OF_DAY);
        int minuteSome = calendarSome.get(Calendar.MINUTE);

        Calendar calendarNow = new GregorianCalendar();
        int year = calendarNow.get(Calendar.YEAR);
        int month = calendarNow.get(Calendar.MONTH);
        int day = calendarNow.get(Calendar.DAY_OF_MONTH);
        int week = calendarNow.get(Calendar.WEEK_OF_MONTH);
        int hour = calendarNow.get(Calendar.HOUR_OF_DAY);
        int minute = calendarNow.get(Calendar.MINUTE);

        StringBuilder sb = new StringBuilder();
        if (yearSome < year) {
            if (yearSome + 1 == year) {
                sb.append(c.getResources().getString(R.string.last_year));
                sb.append(monthSome + 1);
                sb.append(c.getResources().getString(R.string.month_separator));
                sb.append(daySome);
                sb.append(c.getResources().getString(R.string.day_separator));
                return sb.toString();
            } else {
                sb.append(c.getResources().getString(R.string.early_years));
                sb.append(" ");
                sb.append(yearSome);
                sb.append(c.getResources().getString(R.string.year_separator));
                sb.append(monthSome + 1);
                sb.append(c.getResources().getString(R.string.month_separator));
                sb.append(daySome);
                sb.append(c.getResources().getString(R.string.day_separator));
                return sb.toString();
            }
        }

        if (monthSome < month) {
            if (monthSome + 1 == month) {
                sb.append(c.getResources().getString(R.string.last_month));
                sb.append(daySome);
                sb.append(c.getResources().getString(R.string.day_separator));
                return sb.toString();
            } else {
                sb.append(c.getResources().getString(R.string.this_year));
                sb.append(monthSome + 1);
                sb.append(c.getResources().getString(R.string.month_separator));
                sb.append(daySome);
                sb.append(c.getResources().getString(R.string.day_separator));
                return sb.toString();
            }
        }

        if (weekSome < week) {
            if (weekSome + 1 == week) {
                sb.append(c.getResources().getString(R.string.last_week));
                String tmp = ComputeWeekString(c, dayOfWeek);
                sb.append(tmp);
                return sb.toString();
            }
        }

        if (daySome < day) {
            sb.append(c.getResources().getString(R.string.this_month));
            sb.append(daySome);
            sb.append(c.getResources().getString(R.string.day_separator));
            return sb.toString();
        }

        if (hourSome < hour) {
            sb.append(c.getResources().getString(R.string.today));
            sb.append(" ");
            sb.append(hourSome);
            sb.append(c.getResources().getString(R.string.hour_separator));
            return sb.toString();
        }

        if (minuteSome < minute) {
            int diff = minute - minuteSome;
            sb.append(diff);
            sb.append(c.getResources().getString(R.string.minutes));
            return sb.toString();
        }

        return c.getResources().getString(R.string.just_now);
    }

    public static String ComputeWeekString(Context c, int dayOfWeek) {
        if (dayOfWeek == Calendar.SUNDAY) {
            return c.getString(R.string.week_SUNDAY);
        }

        if (dayOfWeek == Calendar.MONDAY) {
            return c.getString(R.string.week_MONDAY);
        }
        if (dayOfWeek == Calendar.TUESDAY) {
            return c.getString(R.string.week_TUESDAY);
        }

        if (dayOfWeek == Calendar.WEDNESDAY) {
            return c.getString(R.string.week_WEDNESDAY);
        }
        if (dayOfWeek == Calendar.THURSDAY) {
            return c.getString(R.string.week_THURSDAY);
        }

        if (dayOfWeek == Calendar.FRIDAY) {
            return c.getString(R.string.week_FRIDAY);
        }

        if (dayOfWeek == Calendar.SATURDAY) {
            return c.getString(R.string.week_SATURDAY);
        }
        return null;
    }
}

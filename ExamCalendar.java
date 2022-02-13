package com.project;

import java.text.*;
import java.util.Calendar;
import java.util.Date;

public class ExamCalendar {

    public static int DAY = 24 * 60 * 60 * 1000;

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static Calendar calendar = Calendar.getInstance();

    private ExamCalendar() {}

    public static Date getActualDateFrom(Date fromDate, int workDays, boolean searchForward) {
//        for (int i = 0; i < workDays + 1; i ++ ) {
//            calendar.setTimeInMillis(fromDate.getTime() + i * DAY);
//            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
//            if (dayOfWeek == Calendar.SATURDAY) {
//                calendar.setTimeInMillis(fromDate.getTime() + (i + 1) * DAY);
//            } else if (dayOfWeek == Calendar.FRIDAY) {
//                calendar.setTimeInMillis(fromDate.getTime() + (i + 2) * DAY);
//            }
//        }
        calendar.setTime(fromDate);
        int direction = Math.abs(workDays) / workDays;
        for (int i = 0; i < Math.abs(workDays); i ++ ) {
            calendar.add(Calendar.DATE, 1 * direction);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SATURDAY) {
                int daysToAdd = searchForward ? (1) : (-2);
                calendar.add(Calendar.DATE, daysToAdd);
            } else if (dayOfWeek == Calendar.FRIDAY) {
                int daysToAdd = searchForward ? (2) : (-1);
                calendar.add(Calendar.DATE, daysToAdd);
            }
        }

        return calendar.getTime();
    }

    public static Date getValidDateFrom(Date fromDate, int calendarDays, boolean searchForward) {
        //calendar.setTimeInMillis(fromDate.getTime() + calendarDays * DAY);
        calendar.setTime(fromDate);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY) {
            int daysToAdd = searchForward ? (calendarDays + 1) : (calendarDays - 2);
            //calendar.setTimeInMillis(fromDate.getTime() + daysToAdd);
            calendar.add(Calendar.DATE, daysToAdd);
        } else if (dayOfWeek == Calendar.FRIDAY) {
            int daysToAdd = searchForward ? (calendarDays + 2) : (calendarDays - 1);
            //calendar.setTimeInMillis(fromDate.getTime() + daysToAdd);
            calendar.add(Calendar.DATE, daysToAdd);
        }
        return calendar.getTime();
    }

    public static Date getNextMonth(Date fromDate) {
//        calendar.setTimeInMillis(fromDate.getTime());
//        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
//        return calendar.getTime();
        calendar.setTime(fromDate);
        calendar.add(Calendar.MONTH, 1);
        return calendar.getTime();
    }

    public static int calculateNumberOfActualDays(Date fromDate, Date toDate, boolean including) {
        // int rawDays = (int) ((double)(toDate.getTime() - fromDate.getTime()) / (double) DAY);
        int rawDays = (int) ((toDate.getTime() - fromDate.getTime()) / DAY);
        // take out all saturdays and fridays:
        // ------------------------------------
        int weeks = rawDays / 7;
        int days = rawDays % 7;
        // remove whole weeks saturdays and fridays:
        int result = (7 - 2) * weeks;
        // start day-of-week
        calendar.setTime(fromDate);
        int startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        // end day-of-week
        calendar.setTime(toDate);
        int endDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (startDayOfWeek > endDayOfWeek) {
            if (startDayOfWeek <= Calendar.FRIDAY) {
                result += (days - 2);
            } else if (startDayOfWeek == Calendar.SATURDAY) {
                result += (days - 1);
            }
        }
        // result is including end day
        if (including && endDayOfWeek != Calendar.FRIDAY && endDayOfWeek != Calendar.SATURDAY) {
            result ++;
        }
        return result;
    }

    public static Date parse(String dateStr) {
        try {
            return simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String format(Date date) {
        return simpleDateFormat.format(date);
    }
}

package com.nuvola.gxpenses.util;

import com.nuvola.gxpenses.shared.type.FrequencyType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {
    public static String getDateToDisplay(Date date, FrequencyType frequency) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        Integer month = calendar.get(Calendar.MONTH);
        Integer year = calendar.get(Calendar.YEAR);
        String dateToDisplay = null;

        switch (frequency) {
            case MONTH:
                SimpleDateFormat dateFormat = new SimpleDateFormat("LLLL yyyy");
                dateToDisplay = dateFormat.format(date);
                break;
            case QUARTER:
                if (month >= Calendar.JANUARY && month <= Calendar.MARCH)
                    dateToDisplay = "First Quarter Of " + year;
                else if (month >= Calendar.APRIL && month <= Calendar.JUNE)
                    dateToDisplay = "Second Quarter Of " + year;
                else if (month >= Calendar.JULY && month <= Calendar.SEPTEMBER)
                    dateToDisplay = "Third Quarter Of " + year;
                else if (month >= Calendar.OCTOBER && month <= Calendar.DECEMBER)
                    dateToDisplay = "Fourth Quarter Of " + year;
                break;
            case SEMESTER:
                if (month >= Calendar.JANUARY && month <= Calendar.JUNE)
                    dateToDisplay = "First Semester Of " + year;
                else if (month >= Calendar.JULY && month <= Calendar.DECEMBER)
                    dateToDisplay = "Second Semester Of " + year;
                break;
            case YEAR:
                dateToDisplay = "Year " + year;
        }

        return dateToDisplay;
    }

    public static Date getNextDate(Date date, FrequencyType frequency) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        Integer month = calendar.get(Calendar.MONTH);
        Integer year = calendar.get(Calendar.YEAR);

        switch (frequency) {
            case MONTH:
                if (month == Calendar.DECEMBER) {
                    calendar.set(Calendar.YEAR, year + 1);
                    calendar.set(Calendar.MONTH, Calendar.JANUARY);
                } else
                    calendar.set(Calendar.MONTH, month + 1);
                break;
            case QUARTER:
                if (month >= Calendar.JANUARY && month <= Calendar.MARCH)
                    calendar.set(Calendar.MONTH, Calendar.APRIL);
                else if (month >= Calendar.APRIL && month <= Calendar.JUNE)
                    calendar.set(Calendar.MONTH, Calendar.JULY);
                else if (month >= Calendar.JULY && month <= Calendar.SEPTEMBER)
                    calendar.set(Calendar.MONTH, Calendar.OCTOBER);
                else if (month >= Calendar.OCTOBER && month <= Calendar.DECEMBER) {
                    calendar.set(Calendar.MONTH, Calendar.JANUARY);
                    calendar.set(Calendar.YEAR, year + 1);
                }
                break;
            case SEMESTER:
                if (month >= Calendar.JANUARY && month <= Calendar.JUNE)
                    calendar.set(Calendar.MONTH, Calendar.JULY);
                else if (month >= Calendar.JULY && month <= Calendar.DECEMBER)
                    calendar.set(Calendar.MONTH, Calendar.JANUARY);
                calendar.set(Calendar.YEAR, year + 1);
                break;
            case YEAR:
                calendar.set(Calendar.YEAR, year + 1);
                break;
        }

        return calendar.getTime();
    }

    public static Date getPreviousDate(Date date, FrequencyType frequency) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        Integer month = calendar.get(Calendar.MONTH);
        Integer year = calendar.get(Calendar.YEAR);

        switch (frequency) {
            case MONTH:
                if (month == Calendar.JANUARY) {
                    calendar.set(Calendar.YEAR, year - 1);
                    calendar.set(Calendar.MONTH, Calendar.DECEMBER);
                } else
                    calendar.set(Calendar.MONTH, month - 1);
                break;
            case QUARTER:
                if (month >= Calendar.JANUARY && month <= Calendar.MARCH) {
                    calendar.set(Calendar.YEAR, year - 1);
                    calendar.set(Calendar.MONTH, Calendar.OCTOBER);
                } else if (month >= Calendar.APRIL && month <= Calendar.JUNE)
                    calendar.set(Calendar.MONTH, Calendar.JANUARY);
                else if (month >= Calendar.JULY && month <= Calendar.SEPTEMBER)
                    calendar.set(Calendar.MONTH, Calendar.APRIL);
                else if (month >= Calendar.OCTOBER && month <= Calendar.DECEMBER)
                    calendar.set(Calendar.MONTH, Calendar.JULY);
                break;
            case SEMESTER:
                if (month >= Calendar.JANUARY && month <= Calendar.JUNE) {
                    calendar.set(Calendar.MONTH, Calendar.JULY);
                    calendar.set(Calendar.YEAR, year - 1);
                } else if (month >= Calendar.JULY && month <= Calendar.DECEMBER)
                    calendar.set(Calendar.MONTH, Calendar.JANUARY);
                break;
            case YEAR:
                calendar.set(Calendar.YEAR, year - 1);
                break;
        }

        return calendar.getTime();
    }
}

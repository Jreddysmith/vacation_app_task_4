package com.example.mobileapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ValidationUtils {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public static boolean isValidDateFormat(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isEndDateAfterStartDate(Date startDate, Date endDate) {
        return endDate.after(startDate);
    }

    public static boolean isDateWithinVacation(Date date, Date startDate, Date endDate) {
        return !date.before(startDate) && !date.after(endDate);
    }
}

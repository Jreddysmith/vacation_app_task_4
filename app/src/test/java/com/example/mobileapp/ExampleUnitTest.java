package com.example.mobileapp;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

import com.example.mobileapp.models.Vacation;

public class ExampleUnitTest {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Date parseDate(String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Test 1: Validate Date Handling (Simple Boolean Logic)
    @Test
    public void testDateValidation() {
        Date startDate = parseDate("2025-06-10");
        Date endDate = parseDate("2025-06-01");
        boolean isValid = !startDate.after(endDate);
        assertFalse(isValid);
    }

    // Test 2: Validate Vacation Title Formatting (Simple String Test)
    @Test
    public void testVacationTitle() {
        Vacation vacation = new Vacation("Hawaii Trip", "hotel",
                parseDate("2025-06-01"),
                parseDate("2025-06-10"),
                1, "Leisure");

        String expectedTitle = "Hawaii Trip";
        assertEquals(expectedTitle, vacation.getTitle());
    }
}

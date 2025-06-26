package com.example.mobileapp.models;

import java.util.Date;

public class LeisureVacation extends Vacation{

    private String activity;

    public LeisureVacation(String title, String hotel, Date startDate, Date endDate, int userId, String activity) {
        // Pass the "Leisure Vacation" type to the superclass constructor
        super(title, hotel, startDate, endDate, userId, "Leisure Vacation");
        this.activity = activity;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    @Override
    public String getVacationType() {
        return "Leisure Vacation";
    }
}

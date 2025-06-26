package com.example.mobileapp.models;

import java.util.Date;

public class BusinessVacation extends Vacation{

    private String businessPurpose;

    public BusinessVacation(String title, String hotel, Date startDate, Date endDate, int userId, String businessPurpose) {
        // Pass the "Business Vacation" type to the superclass constructor
        super(title, hotel, startDate, endDate, userId, "Business Vacation");
        this.businessPurpose = businessPurpose;
    }

    public String getBusinessPurpose() {
        return businessPurpose;
    }

    public void setBusinessPurpose(String businessPurpose) {
        this.businessPurpose = businessPurpose;
    }

    @Override
    public String getVacationType() {
        return "Business Vacation";
    }
}

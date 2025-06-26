package com.example.mobileapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;
import androidx.room.ColumnInfo;

@Entity(tableName = "vacations")
public class Vacation {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String hotel;
    private Date startDate;
    private Date endDate;
    private int userId;

    @ColumnInfo(name = "vacationType")
    private String vacationType; // New field for vacation type

    @Override
    public String toString() {
        return title + " (" + hotel + ")";
    }

    public Vacation(String title, String hotel, Date startDate, Date endDate, int userId, String vacationType) {
        this.title = title;
        this.hotel = hotel;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userId = userId;
        this.vacationType = vacationType; // Initialize the vacation type
    }

    public String getVacationType() {
        return vacationType;
    }

    public void setVacationType(String vacationType) {
        this.vacationType = vacationType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

//    // Polymorphism: Method to get vacation type
//    public String getVacationType() {
//        return "General Vacation";
//    }
}

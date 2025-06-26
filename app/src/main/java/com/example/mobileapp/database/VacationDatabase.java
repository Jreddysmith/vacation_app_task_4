package com.example.mobileapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.mobileapp.dao.ExcursionDao;
import com.example.mobileapp.dao.UserDao;
import com.example.mobileapp.dao.VacationDao;
import com.example.mobileapp.models.Excursion;
import com.example.mobileapp.models.User;
import com.example.mobileapp.models.Vacation;
import com.example.mobileapp.utils.DateConverter;

@Database(entities = {User.class, Vacation.class, Excursion.class}, version = 5, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class VacationDatabase extends RoomDatabase {

    private static VacationDatabase instance;

    public abstract VacationDao vacationDao();
    public abstract ExcursionDao excursionDao();
    public abstract UserDao userDao();

    public static synchronized VacationDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            VacationDatabase.class, "vacation_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}

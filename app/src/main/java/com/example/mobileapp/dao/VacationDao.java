package com.example.mobileapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.mobileapp.models.Vacation;

import java.util.Date;
import java.util.List;

@Dao
public interface VacationDao {

//    @Insert
//    void insert(Vacation vacation);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Vacation vacation);
    @Update
    void update(Vacation vacation);

    @Delete
    void delete(Vacation vacation);

    @Query("SELECT * FROM vacations ORDER BY startDate ASC")
    LiveData<List<Vacation>> getAllVacations();

    @Query("SELECT * FROM vacations WHERE id = :vacationId")
    LiveData<Vacation> getVacationById(int vacationId);

    @Query("SELECT COUNT(*) FROM excursions WHERE vacationId = :vacationId")
    int getExcursionCountForVacation(int vacationId);

    @Query("DELETE FROM vacations WHERE id = :vacationId AND (SELECT COUNT(*) FROM excursions WHERE vacationId = :vacationId) = 0")
    int deleteVacationIfNoExcursions(int vacationId);

    @Query("SELECT * FROM vacations WHERE userId = :userId ORDER BY startDate ASC")
    LiveData<List<Vacation>> getVacationsForUser(int userId);

    @Query("SELECT * FROM vacations WHERE userId = :userId AND startDate >= :startDate AND endDate <= :endDate ORDER BY startDate ASC")
    LiveData<List<Vacation>> searchVacationsByDateRange(int userId, Date startDate, Date endDate);


}

package com.example.mobileapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.mobileapp.models.Excursion;
import java.util.List;

@Dao
public interface ExcursionDao {

    @Insert
    void insert(Excursion excursion);

    @Update
    void update(Excursion excursion);

    @Delete
    void delete(Excursion excursion);

    @Query("SELECT * FROM excursions WHERE vacationId = :vacationId ORDER BY date ASC")
    LiveData<List<Excursion>> getExcursionsForVacation(int vacationId);

    @Query("SELECT * FROM excursions WHERE id = :excursionId")
    LiveData<Excursion> getExcursionById(int excursionId);

    @Query("SELECT * FROM excursions WHERE vacationId = :vacationId")
    LiveData<List<Excursion>> getExcursionsByVacationId(int vacationId);
}

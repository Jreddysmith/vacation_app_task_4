package com.example.mobileapp.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.mobileapp.dao.ExcursionDao;
import com.example.mobileapp.database.VacationDatabase;
import com.example.mobileapp.models.Excursion;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExcursionRepository {

    private ExcursionDao excursionDao;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ExcursionRepository(Application application) {
        VacationDatabase database = VacationDatabase.getInstance(application);
        excursionDao = database.excursionDao();
    }

    public void insert(Excursion excursion) {
        executorService.execute(() -> excursionDao.insert(excursion));
    }

    public void update(Excursion excursion) {
        executorService.execute(() -> excursionDao.update(excursion));
    }

    public void delete(Excursion excursion) {
        executorService.execute(() -> excursionDao.delete(excursion));
    }

    public LiveData<List<Excursion>> getExcursionsForVacation(int vacationId) {
        return excursionDao.getExcursionsForVacation(vacationId);
    }

    public LiveData<List<Excursion>> getExcursionsByVacationId(int vacationId) {
        return excursionDao.getExcursionsByVacationId(vacationId);
    }
}

package com.example.mobileapp.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.mobileapp.dao.VacationDao;
import com.example.mobileapp.database.VacationDatabase;
import com.example.mobileapp.models.Vacation;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VacationRepository {

    private VacationDao vacationDao;
    private LiveData<List<Vacation>> allVacations;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public VacationRepository(Application application) {
        VacationDatabase database = VacationDatabase.getInstance(application);
        vacationDao = database.vacationDao();
        allVacations = vacationDao.getAllVacations();
    }

    public void insert(Vacation vacation) {
        executorService.execute(() -> vacationDao.insert(vacation));
    }

    public void update(Vacation vacation) {
        executorService.execute(() -> vacationDao.update(vacation));
    }

    public void delete(Vacation vacation) {
        executorService.execute(() -> vacationDao.delete(vacation));
    }

    public LiveData<List<Vacation>> getAllVacations() {
        return allVacations;
    }

    public LiveData<Vacation> getVacationById(int vacationId) {
        return vacationDao.getVacationById(vacationId);
    }

    public void getExcursionCountForVacation(int vacationId, Callback<Integer> callback) {
        executorService.execute(() -> {
            int count = vacationDao.getExcursionCountForVacation(vacationId);
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                callback.onResult(count);
            });
        });
    }

    public void deleteVacationById(int vacationId) {
        executorService.execute(() -> vacationDao.deleteVacationIfNoExcursions(vacationId));
    }

    // Define a simple callback interface
    public interface Callback<T> {
        void onResult(T result);
    }

    // Add this method to the VacationRepository class
    public LiveData<List<Vacation>> getVacationsForUser(int userId) {
        return vacationDao.getVacationsForUser(userId);
    }

    public LiveData<List<Vacation>> searchVacationsByDateRange(int userId, Date startDate, Date endDate) {
        return vacationDao.searchVacationsByDateRange(userId, startDate, endDate);
    }

    // Implementing the isDateValid method
    public boolean isDateValid(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }
        return !startDate.after(endDate);
    }



}

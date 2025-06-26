package com.example.mobileapp.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.mobileapp.models.Vacation;
import com.example.mobileapp.repositories.VacationRepository;

import java.util.Date;
import java.util.List;

public class VacationViewModel extends AndroidViewModel {

    private VacationRepository repository;
    private LiveData<List<Vacation>> userVacations;

    public VacationViewModel(@NonNull Application application) {
        super(application);
        repository = new VacationRepository(application);
        userVacations = repository.getAllVacations();
    }

    public void insert(Vacation vacation) {
        repository.insert(vacation);
    }

    public void update(Vacation vacation) {
        repository.update(vacation);
    }

    public void delete(Vacation vacation) {
        repository.delete(vacation);
    }

    public LiveData<List<Vacation>> getUserVacations() {
        return userVacations;
    }

    public LiveData<Vacation> getVacationById(int vacationId) {
        return repository.getVacationById(vacationId);
    }

    public void getExcursionCountForVacation(int vacationId, VacationRepository.Callback<Integer> callback) {
        repository.getExcursionCountForVacation(vacationId, callback);
    }


    public void deleteVacationById(int vacationId) {
        repository.deleteVacationById(vacationId);
    }

    // Method to load vacations for a specific user
    public void loadVacationsForUser(int userId) {
        userVacations = repository.getVacationsForUser(userId);
    }

    public LiveData<List<Vacation>> searchVacationsByDateRange(int userId, Date startDate, Date endDate) {
        return repository.searchVacationsByDateRange(userId, startDate, endDate);
    }



}

package com.example.mobileapp.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.mobileapp.models.Excursion;
import com.example.mobileapp.repositories.ExcursionRepository;
import java.util.List;

public class ExcursionViewModel extends AndroidViewModel {

    private ExcursionRepository repository;
    private LiveData<List<Excursion>> excursionsForVacation;

    public ExcursionViewModel(@NonNull Application application) {
        super(application);
        repository = new ExcursionRepository(application);
    }

    public void setVacationId(int vacationId) {
        excursionsForVacation = repository.getExcursionsByVacationId(vacationId);
    }


    public void insert(Excursion excursion) {
        repository.insert(excursion);
    }

    public void update(Excursion excursion) {
        repository.update(excursion);
    }

    public void delete(Excursion excursion) {
        repository.delete(excursion);
    }

    public LiveData<List<Excursion>> getExcursionsForVacation() {
        return excursionsForVacation;
    }

    public LiveData<List<Excursion>> getExcursionsByVacationId(int vacationId) {
        return repository.getExcursionsByVacationId(vacationId);
    }
}

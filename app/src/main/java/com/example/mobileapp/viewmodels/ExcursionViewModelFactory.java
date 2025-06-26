package com.example.mobileapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ExcursionViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;
    private final int vacationId;

    public ExcursionViewModelFactory(Application application, int vacationId) {
        this.application = application;
        this.vacationId = vacationId;
    }

//    @NonNull
//    @Override
//    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//        if (modelClass.isAssignableFrom(ExcursionViewModel.class)) {
//            ExcursionViewModel viewModel = new ExcursionViewModel(application);
//            viewModel.setVacationId(vacationId);
//            return (T) viewModel;
//        }
//        throw new IllegalArgumentException("Unknown ViewModel class");
//    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ExcursionViewModel.class)) {
            return modelClass.cast(new ExcursionViewModel(application));
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

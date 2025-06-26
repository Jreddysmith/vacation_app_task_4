package com.example.mobileapp.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.example.mobileapp.models.User;
import com.example.mobileapp.repositories.UserRepository;

public class UserViewModel extends AndroidViewModel {

    private UserRepository userRepository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public void insertUser(User user, UserRepository.InsertCallback callback) {
        userRepository.insert(user, callback);
    }

    public void getUserByUsername(String username, UserRepository.UserCallback callback) {
        userRepository.getUserByUsername(username, callback);
    }
}

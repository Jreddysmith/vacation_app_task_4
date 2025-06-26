package com.example.mobileapp.repositories;

import android.app.Application;
import com.example.mobileapp.dao.UserDao;
import com.example.mobileapp.database.VacationDatabase;
import com.example.mobileapp.models.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {

    private UserDao userDao;
    private ExecutorService executorService;

    public UserRepository(Application application) {
        VacationDatabase database = VacationDatabase.getInstance(application);
        userDao = database.userDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(User user, InsertCallback callback) {
        executorService.execute(() -> {
            userDao.insert(user);
            callback.onInsertCompleted();
        });
    }

    public void getUserByUsername(String username, UserCallback callback) {
        executorService.execute(() -> {
            User user = userDao.getUserByUsername(username);
            callback.onUserFetched(user);
        });
    }

    public interface InsertCallback {
        void onInsertCompleted();
    }

    public interface UserCallback {
        void onUserFetched(User user);
    }
}

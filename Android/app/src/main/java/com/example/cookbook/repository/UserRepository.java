package com.example.cookbook.repository;

import android.content.Context;

import com.example.cookbook.AppDatabase;
import com.example.cookbook.StateManager;
import com.example.cookbook.dao.UserDao;
import com.example.cookbook.entity.User;

import java.util.Objects;

public class UserRepository {

    private final UserDao userDao;

    public UserRepository(Context context) {
        this.userDao = AppDatabase.getInstance(context).userDao();
    }

    public User register(String username, String email, String password) throws Exception {
        boolean userExists = userDao.verifyCredentialsUniqueness(username, email);
        if (userExists) {
            throw new Exception();
        }

        User user = new User(username, email, password, true);
        userDao.insert(user);
        return user;
    }

    public User login(String email, String password) throws Exception {
        User user = userDao.findByCredentials(email, password);
        if (user == null) {
            throw new Exception();
        }

        userDao.setIsLoggedIn(user.email, true);
        user.isLoggedIn = true;
        return user;
    }

    public void logout() {
        userDao.setIsLoggedIn(Objects.requireNonNull(StateManager.getLoggedInUser().getValue()).email, false);
    }

    public User getLoggedInUser() {
        return userDao.findLoggedInUser();
    }
}

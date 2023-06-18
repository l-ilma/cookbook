package com.example.cookbook.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cookbook.entity.User;

public class StateManager {
    private static final MutableLiveData<User> loggedInUser = new MutableLiveData<>(null);

    public static LiveData<User> getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User user) {
        loggedInUser.postValue(user);
    }
}

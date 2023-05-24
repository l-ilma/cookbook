package com.example.cookbook;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cookbook.entity.User;

public class StateManager {
    private static final MutableLiveData<User> loggedInUser = new MutableLiveData<>(null);

    public static void setLoggedInUser(User user) {
        loggedInUser.postValue(user);
    }

    public static LiveData<User> getLoggedInUser() {
        return loggedInUser;
    }
}

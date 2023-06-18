package com.example.cookbook.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.cookbook.entity.User;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user WHERE email = :email AND password = :password")
    User findByCredentials(String email, String password);

    @Query("SELECT * FROM user WHERE isLoggedIn = 1")
    User findLoggedInUser();

    @Insert
    void insert(User user);

    @Insert
    void insertAll(List<User> users);

    @Query("UPDATE user SET isLoggedIn = :isLoggedIn WHERE email = :email")
    void setIsLoggedIn(String email, boolean isLoggedIn);

    @Query("SELECT EXISTS(SELECT * FROM user WHERE username = :username OR email = :email)")
    boolean verifyCredentialsUniqueness(String username, String email);

    @Query("SELECT * FROM User WHERE id = :id")
    LiveData<User> findById(long id);
}
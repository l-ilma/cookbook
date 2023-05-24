package com.example.cookbook.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "isLoggedIn")
    public boolean isLoggedIn;

    public User(String username, String email, String password, boolean isLoggedIn) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isLoggedIn = isLoggedIn;
    }
}
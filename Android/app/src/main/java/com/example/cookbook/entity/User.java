package com.example.cookbook.entity;

import androidx.room.Entity;
import androidx.room.Index;

@Entity(indices = {@Index("id")})
public class User extends BaseEntity {
    public String username;

    public String email;

    public String password;

    public boolean isLoggedIn;

    public User(String username, String email, String password, boolean isLoggedIn) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isLoggedIn = isLoggedIn;
    }
}
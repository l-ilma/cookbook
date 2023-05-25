package com.example.cookbook.models;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable {
    public String comment;
    public String user;
    public Date date;

    public Comment(String comment, String user, Date date) {
        this.comment = comment;
        this.user = user;
        this.date = date;
    }
}

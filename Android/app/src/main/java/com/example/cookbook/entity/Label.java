package com.example.cookbook.entity;

import androidx.room.Entity;

@Entity
public class Label extends BaseEntity {
    public String name;
    public String color;

    public Label(String name, String color){
        this.name = name;
        this.color = color;
    }
}

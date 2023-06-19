package com.example.cookbook.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = CASCADE
        )
})
public class Recipe extends BaseEntity {
    public long userId;

    public String name;

    public String instructions;
    public String labels;

    public String imagePath;
    public double price;

    public Recipe(long userId, String name, String instructions, String imagePath, String labels,
                  double price) {
        this.userId = userId;
        this.name = name;
        this.instructions = instructions;
        this.imagePath = imagePath;
        this.labels = labels;
        this.price = price;
    }
}

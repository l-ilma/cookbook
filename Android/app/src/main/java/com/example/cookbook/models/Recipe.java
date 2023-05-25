package com.example.cookbook.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Recipe implements Serializable {
    public int id;
    public byte[] image;
    public String instructions;
    public String name;
    public int likes;
    public List<Ingredient> ingredients;
    public List<Comment> comments;
    public boolean liked;
    public boolean my;

    public Recipe(int id, byte[] image, String instructions, String name, int likes, List<Ingredient> ingredients, boolean liked, boolean my) {
        this.id = id;
        this.image = image;
        this.instructions = instructions;
        this.name = name;
        this.likes = likes;
        this.ingredients = ingredients;
        this.comments = new ArrayList<Comment>();
        this.liked = liked;
        this.my = my;
    }
}

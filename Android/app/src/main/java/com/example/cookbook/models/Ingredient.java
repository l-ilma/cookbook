package com.example.cookbook.models;

import java.io.Serializable;

public class Ingredient implements Serializable {
    public String name;
    public float quantity;
    public String measure;

    public Ingredient(String name, float quantity, String measure) {
        this.name = name;
        this.quantity = quantity;
        this.measure = measure;
    }
}

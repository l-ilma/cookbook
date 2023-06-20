package com.example.cookbook.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.cookbook.entity.Label;

import java.util.List;

@Dao
public interface LabelsDao {
    @Insert
    void insertAll(List<Label> labels);

    @Transaction
    @Query("SELECT * from label")
    List<Label> getAllLabels();
}

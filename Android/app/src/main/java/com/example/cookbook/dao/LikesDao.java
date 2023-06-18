package com.example.cookbook.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;

import com.example.cookbook.entity.UserLikesRecipeCrossRef;

import java.util.List;

@Dao
public interface LikesDao {
    @Insert
    void insertAll(List<UserLikesRecipeCrossRef> likes);

    @Insert
    void like(UserLikesRecipeCrossRef like);

    @Delete
    void unlike(UserLikesRecipeCrossRef like);
}

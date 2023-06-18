package com.example.cookbook.repository;

import android.content.Context;

import com.example.cookbook.AppDatabase;
import com.example.cookbook.dao.LikesDao;
import com.example.cookbook.entity.UserLikesRecipeCrossRef;

public class LikesRepository {
    private final LikesDao likesDao;

    public LikesRepository(Context context) {
        likesDao = AppDatabase.getInstance(context).likesDao();
    }

    public void like(long userId, long recipeId) {
        UserLikesRecipeCrossRef likeRef = new UserLikesRecipeCrossRef(userId, recipeId);
        likesDao.like(likeRef);
    }

    public void unlike(long userId, long recipeId) {
        UserLikesRecipeCrossRef likeRef = new UserLikesRecipeCrossRef(userId, recipeId);
        likesDao.unlike(likeRef);
    }
}

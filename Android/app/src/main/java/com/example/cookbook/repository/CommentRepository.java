package com.example.cookbook.repository;

import android.content.Context;

import com.example.cookbook.AppDatabase;
import com.example.cookbook.dao.CommentDao;
import com.example.cookbook.entity.Comment;

public class CommentRepository {
    private final CommentDao commentDao;

    public CommentRepository(Context context) {
        this.commentDao = AppDatabase.getInstance(context).commentDao();
    }

    public void add(long userId, long recipeId, String text) {
        Comment comment = new Comment(userId, recipeId, text);
        commentDao.insert(comment);
    }
}

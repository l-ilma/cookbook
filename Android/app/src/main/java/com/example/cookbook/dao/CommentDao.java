package com.example.cookbook.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.cookbook.entity.Comment;

import java.util.List;

@Dao
public interface CommentDao {
    @Insert
    void insertAll(List<Comment> comments);

    @Insert
    void insert(Comment comment);
}

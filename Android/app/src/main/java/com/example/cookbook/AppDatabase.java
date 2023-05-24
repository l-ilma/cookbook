package com.example.cookbook;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.cookbook.dao.UserDao;
import com.example.cookbook.entity.User;

@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "cookbook.db";
    private static volatile AppDatabase appDatabase;

    public static synchronized AppDatabase getInstance(Context context){
        if (appDatabase == null) {
            appDatabase = Room
                    .databaseBuilder(context, AppDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return appDatabase;
    }

    public abstract UserDao userDao();
}

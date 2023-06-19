package com.example.cookbook;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.cookbook.dao.CommentDao;
import com.example.cookbook.dao.IngredientsDao;
import com.example.cookbook.dao.LabelsDao;
import com.example.cookbook.dao.LikesDao;
import com.example.cookbook.dao.RecipeDao;
import com.example.cookbook.dao.UserDao;
import com.example.cookbook.entity.Comment;
import com.example.cookbook.entity.Ingredient;
import com.example.cookbook.entity.Label;
import com.example.cookbook.entity.Recipe;
import com.example.cookbook.entity.User;
import com.example.cookbook.entity.UserLikesRecipeCrossRef;
import com.example.cookbook.utils.Converters;

@Database(entities = {User.class, Recipe.class, Comment.class, Ingredient.class, UserLikesRecipeCrossRef.class, Label.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "cookbook.db";
    private static volatile AppDatabase appDatabase;

    public static synchronized AppDatabase getInstance(Context context) {
        if (appDatabase == null) {
            appDatabase = Room
                    .databaseBuilder(context, AppDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(new AppDatabaseSeed(context))
                    .build();
        }
        return appDatabase;
    }

    public abstract UserDao userDao();

    public abstract RecipeDao recipeDao();

    public abstract IngredientsDao ingredientsDao();

    public abstract CommentDao commentDao();

    public abstract LikesDao likesDao();
    public abstract LabelsDao labelsDao();

}

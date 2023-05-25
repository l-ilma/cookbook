package com.example.cookbook.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cookbook.R;
import com.example.cookbook.models.Comment;
import com.example.cookbook.models.Ingredient;
import com.example.cookbook.models.Recipe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {
   private Recipe recipe;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back button
        getSupportActionBar().setTitle(recipe.name);

        getRecipeData();
        getComments();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getRecipeData() {
        recipe = (Recipe) getIntent().getSerializableExtra("recipe");

        ImageView recipeImageView = findViewById(R.id.recipeImage);
        recipeImageView.setImageResource(R.drawable.dish_image);

        LinearLayout linearLayout = findViewById(R.id.ingredientsList);
        for (Ingredient ingredient: recipe.ingredients) {
            TextView ingredientView = new TextView(this);
            ingredientView.setText("- " + ingredient.name + ": " + ingredient.quantity + " " + ingredient.measure);
            ingredientView.setTextSize(16);
            linearLayout.addView(ingredientView);
        }

        TextView instructionsVew = findViewById(R.id.instructions);
        instructionsVew.setText(recipe.instructions);
    }

    private void getComments() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        List<Comment> comments = recipe.comments;
        LinearLayout linearLayout = findViewById(R.id.commentSection);
        LayoutInflater inflater = LayoutInflater.from(this);


        for (Comment comment: comments) {
            View inflatedLayout= inflater.inflate(R.layout.comment_view, null);
            linearLayout.addView(inflatedLayout);

            TextView usernameView = inflatedLayout.findViewById(R.id.username);
            usernameView.setText(comment.user);
            TextView dateView = inflatedLayout.findViewById(R.id.date);
            dateView.setText(df.format(comment.date));
            TextView commentView = inflatedLayout.findViewById(R.id.comment);
            commentView.setText(comment.comment);
        }
    }

    public void onAddCommentClick(View v) {
        v.setVisibility(View.GONE);

        RelativeLayout commentLayout = findViewById(R.id.commentLayout);
        RelativeLayout commentTextView = commentLayout.findViewById(R.id.commentLayout);
        commentLayout.setVisibility(View.VISIBLE);
        commentTextView.requestFocus();
    }
}

package com.example.cookbook.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cookbook.R;
import com.example.cookbook.StateManager;
import com.example.cookbook.entity.User;
import com.example.cookbook.models.Comment;
import com.example.cookbook.models.Ingredient;
import com.example.cookbook.models.Recipe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {
   private Recipe recipe;
   private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
   private User loggedInUser = null;
    LayoutInflater inflater;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        inflater = LayoutInflater.from(this);
        loggedInUser = StateManager.getLoggedInUser().getValue();

        Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back button
        getSupportActionBar().setTitle(recipe.name);

        getRecipeData();
        getComments();

        Button addCommentBtn = findViewById(R.id.addCommentButton);
        if (loggedInUser != null) {
            addCommentBtn.setVisibility(View.VISIBLE);
        } else {
            addCommentBtn.setVisibility(View.GONE);
        }
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
        recipeImageView.setImageResource(recipe.image);

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
        List<Comment> comments = recipe.comments;
        LinearLayout linearLayout = findViewById(R.id.commentSection);

        for (Comment comment: comments) {
            addCommentView(linearLayout, comment);
        }
    }

    public void onAddCommentClick(View v) {
        v.setVisibility(View.GONE);

        RelativeLayout commentLayout = findViewById(R.id.commentLayout);
        EditText commentTextView = commentLayout.findViewById(R.id.commentText);
        commentLayout.setVisibility(View.VISIBLE);
        commentTextView.setText("");
        commentTextView.requestFocus();
        ImageView postCommentBtn = commentLayout.findViewById(R.id.postCommentBtn);
        setOnCommentPostClick(postCommentBtn, commentLayout, commentTextView, v);
    }

    private void addCommentView(LinearLayout parent, Comment comment) {
        View inflatedLayout= inflater.inflate(R.layout.comment_view, null);
        parent.addView(inflatedLayout);

        TextView usernameView = inflatedLayout.findViewById(R.id.username);
        usernameView.setText(comment.user);
        TextView dateView = inflatedLayout.findViewById(R.id.date);
        dateView.setText(df.format(comment.date));
        TextView commentView = inflatedLayout.findViewById(R.id.comment);
        commentView.setText(comment.comment);
    }

    private void setOnCommentPostClick(View view, RelativeLayout commentLayout,
                                       EditText commentTextView, View addCommentBtn) {
        view.setOnClickListener(v -> {
            String comment = commentTextView.getText().toString();
            Date date = new Date();
            String user = loggedInUser.username;
            Comment newComment = new Comment(comment, user, date);

            commentLayout.setVisibility(View.GONE);

            LinearLayout linearLayout = findViewById(R.id.commentSection);
            addCommentView(linearLayout, newComment);
            addCommentBtn.setVisibility(View.VISIBLE);
        });
    }
}

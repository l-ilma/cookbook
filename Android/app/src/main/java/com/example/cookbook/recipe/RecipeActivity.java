package com.example.cookbook.recipe;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.example.cookbook.R;
import com.example.cookbook.entity.Comment;
import com.example.cookbook.entity.Ingredient;
import com.example.cookbook.entity.User;
import com.example.cookbook.models.CompositeRecipe;
import com.example.cookbook.repository.CommentRepository;
import com.example.cookbook.repository.RecipeRepository;
import com.example.cookbook.repository.UserRepository;
import com.example.cookbook.utils.Constants;
import com.example.cookbook.utils.ImageUtils;
import com.example.cookbook.utils.StateManager;

import java.util.List;
import java.util.Objects;

public class RecipeActivity extends AppCompatActivity {

    LayoutInflater inflater;

    private LiveData<CompositeRecipe> compositeRecipe;

    private User loggedInUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        inflater = LayoutInflater.from(this);
        loggedInUser = StateManager.getLoggedInUser().getValue();

        loadCompositeRecipe();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); // back button

        Button addCommentBtn = findViewById(R.id.addCommentButton);
        addCommentBtn.setVisibility(loggedInUser != null ? View.VISIBLE : View.GONE);

        compositeRecipe.observe(this, recipeData -> {
            if (recipeData == null) return;
            renderRecipeData();
            renderComments();
            getSupportActionBar().setTitle(recipeData.recipe.name);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void renderRecipeData() {

        ImageView recipeImageView = findViewById(R.id.recipeImage);
        if (compositeRecipe.getValue().recipe.imagePath == null) {
            recipeImageView.setImageResource(R.drawable.baseline_image_24);
        } else {
            Drawable image = new BitmapDrawable(ImageUtils.loadFile(compositeRecipe.getValue().recipe.imagePath));
            recipeImageView.setImageDrawable(image);
        }


        LinearLayout linearLayout = findViewById(R.id.ingredientsList);
        linearLayout.removeAllViews();

        for (Ingredient ingredient : compositeRecipe.getValue().ingredients) {
            TextView ingredientView = new TextView(this);
            ingredientView.setText("- " + ingredient.name + ": " + ingredient.quantity + " " + ingredient.measure);
            ingredientView.setTextSize(16);
            linearLayout.addView(ingredientView);
        }

        TextView instructionsVew = findViewById(R.id.instructions);
        instructionsVew.setText(compositeRecipe.getValue().recipe.instructions);
    }

    private void renderComments() {
        List<Comment> comments = compositeRecipe.getValue().comments;
        LinearLayout linearLayout = findViewById(R.id.commentSection);
        linearLayout.removeAllViews();

        for (Comment comment : comments) {
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
        View inflatedLayout = inflater.inflate(R.layout.comment_view, null);
        parent.addView(inflatedLayout);

        new UserRepository(getApplicationContext()).findById(comment.userId).observe(this, user -> {
            TextView usernameView = inflatedLayout.findViewById(R.id.username);
            usernameView.setText(user.username);
        });


        TextView dateView = inflatedLayout.findViewById(R.id.date);
        dateView.setText(Constants.DATE_FORMAT.format(comment.createdAt));
        TextView commentView = inflatedLayout.findViewById(R.id.comment);
        commentView.setText(comment.text);

        // hide keyboard
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(commentView.getWindowToken(), 0);
    }

    private void setOnCommentPostClick(View view, RelativeLayout commentLayout,
                                       EditText commentTextView, View addCommentBtn) {
        view.setOnClickListener(v -> {
            String comment = commentTextView.getText().toString();
            commentLayout.setVisibility(View.GONE);
            addCommentBtn.setVisibility(View.VISIBLE);

            AsyncTask.execute(() -> {
                new CommentRepository(getApplicationContext()).add(
                        loggedInUser.id,
                        compositeRecipe.getValue().recipe.id,
                        comment
                );
            });

        });
    }

    private void loadCompositeRecipe() {
        long recipeId = getIntent().getLongExtra(Constants.RECIPE_EXTRA_KEY, -1);
        compositeRecipe = new RecipeRepository(getApplicationContext()).findById(recipeId);
    }
}

package com.example.cookbook.recipe;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cookbook.R;
import com.example.cookbook.models.Ingredient;
import com.example.cookbook.models.Recipe;

import java.util.Arrays;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {
    private final List<String> ingredients = Arrays.asList("200g risotto rice", "2 garlic cloves",
            "8 spring onions", "25g butter", "2 tsp ground cumin", "1 tbsp olive oil", "1 small pumpkin", "50g grated parmesan ");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back button
        getSupportActionBar().setTitle(recipe.name);

        getRecipeData();
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
        Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe");

        ImageView recipeImageView = findViewById(R.id.recipeImage);
        recipeImageView.setImageResource(R.drawable.dish_image);

        LinearLayout linearLayout = findViewById(R.id.ingredientsList);
        for (Ingredient ingredient: recipe.ingredients) {
            TextView ingredientView = new TextView(this);
            ingredientView.setText("- " + ingredient.quantity + " " + ingredient.measure + " of " + ingredient.name);
            ingredientView.setTextSize(16);
            linearLayout.addView(ingredientView);
        }

        TextView instructionsVew = findViewById(R.id.instructions);
        instructionsVew.setText(recipe.instructions);
    }
}

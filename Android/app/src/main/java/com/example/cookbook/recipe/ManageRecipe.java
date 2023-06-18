package com.example.cookbook.recipe;

import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.example.cookbook.R;
import com.example.cookbook.entity.Ingredient;
import com.example.cookbook.models.CompositeRecipe;
import com.example.cookbook.repository.RecipeRepository;
import com.example.cookbook.utils.Constants;

public class ManageRecipe extends AppCompatActivity {
    private LiveData<CompositeRecipe> recipe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_recipe);

        loadCompositeRecipe();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back button

        recipe.observe(this, recipeData -> {
            if (recipeData == null) {
                addNewRecipe();
            } else {
                editExistingRecipe();
            }
        });
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

    public void onSaveClick(View view) {
    }

    private void loadCompositeRecipe() {
        long recipeId = getIntent().getLongExtra(Constants.RECIPE_EXTRA_KEY, -1);
        recipe = new RecipeRepository(getApplicationContext()).findById(recipeId);
    }

    void editExistingRecipe() {
        CompositeRecipe compositeRecipe = recipe.getValue();
        getSupportActionBar().setTitle(compositeRecipe.recipe.name);
        ImageView recipeImageView = findViewById(R.id.recipeImage);
        // TODO: load image
//        recipeImageView.setImageResource(compositeRecipe.image);

        LinearLayout linearLayout = findViewById(R.id.ingredientsList);
        LinearLayout linearLayoutHeader = linearLayout.findViewById(R.id.ingredientsHeader);
        TextView nameLabelView = linearLayoutHeader.findViewById(R.id.ingredientName);
        TextView quantityLabelView = linearLayoutHeader.findViewById(R.id.ingredientQuantity);
        TextView measureLabelView = linearLayoutHeader.findViewById(R.id.ingredientMeasure);
        TextView actionLabelView = linearLayoutHeader.findViewById(R.id.ingredientAction);

        for (Ingredient ingredient : compositeRecipe.ingredients) {
            LinearLayout linearLayoutIngredient = new LinearLayout(this);
            linearLayoutIngredient.setLayoutParams(linearLayoutHeader.getLayoutParams());
            linearLayoutIngredient.setOrientation(LinearLayout.HORIZONTAL);
            linearLayoutIngredient.setGravity(Gravity.CENTER_VERTICAL);

            EditText nameView = createEditTextForIngredient(ingredient.name,
                    View.TEXT_ALIGNMENT_VIEW_START, nameLabelView.getLayoutParams());

            EditText quantityView = createEditTextForIngredient(String.valueOf(ingredient.quantity),
                    View.TEXT_ALIGNMENT_VIEW_END, quantityLabelView.getLayoutParams());

            EditText measureView = createEditTextForIngredient(ingredient.measure,
                    View.TEXT_ALIGNMENT_VIEW_END, measureLabelView.getLayoutParams());

            ImageView actionView = new ImageView(this);
            actionView.setImageResource(R.drawable.baseline_delete_24);
            actionView.setBaselineAlignBottom(true);

            linearLayoutIngredient.addView(nameView);
            linearLayoutIngredient.addView(quantityView);
            linearLayoutIngredient.addView(measureView);
            linearLayoutIngredient.addView(actionView);

            linearLayout.addView(linearLayoutIngredient);
        }

        EditText instructionsVew = findViewById(R.id.instructions);
        instructionsVew.setText(compositeRecipe.recipe.instructions);

    }

    EditText createEditTextForIngredient(String text, int textAlignment, ViewGroup.LayoutParams layoutParams) {
        EditText view = new EditText(this);
        view.setLayoutParams(layoutParams);
        view.setText(text);
        view.setTextSize(16);
        view.setTextAlignment(textAlignment);
        view.setInputType(InputType.TYPE_CLASS_TEXT);

        return view;
    }

    void addNewRecipe() {
        getSupportActionBar().setTitle(R.string.new_recipe);
    }
}

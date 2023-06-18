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

import com.example.cookbook.R;
import com.example.cookbook.models.Ingredient;
import com.example.cookbook.models.Recipe;

public class ManageRecipe extends AppCompatActivity {
    private Recipe recipe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_recipe);

        recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back button

        if (recipe != null) {
            editExistingRecipe();
        } else {
            addNewRecipe();
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

    public void onSaveClick(View view) {
    }

    void editExistingRecipe() {
        getSupportActionBar().setTitle(recipe.name);
        ImageView recipeImageView = findViewById(R.id.recipeImage);
        recipeImageView.setImageResource(recipe.image);

        LinearLayout linearLayout = findViewById(R.id.ingredientsList);
        LinearLayout linearLayoutHeader = linearLayout.findViewById(R.id.ingredientsHeader);
        TextView nameLabelView = linearLayoutHeader.findViewById(R.id.ingredientName);
        TextView quantityLabelView = linearLayoutHeader.findViewById(R.id.ingredientQuantity);
        TextView measureLabelView = linearLayoutHeader.findViewById(R.id.ingredientMeasure);
        TextView actionLabelView = linearLayoutHeader.findViewById(R.id.ingredientAction);

        for (Ingredient ingredient : recipe.ingredients) {
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
        instructionsVew.setText(recipe.instructions);

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

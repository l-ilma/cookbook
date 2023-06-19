package com.example.cookbook.recipe;

import android.os.AsyncTask;
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
import com.example.cookbook.entity.Recipe;
import com.example.cookbook.models.CompositeRecipe;
import com.example.cookbook.repository.IngredientsRepository;
import com.example.cookbook.repository.RecipeRepository;
import com.example.cookbook.utils.Constants;
import com.example.cookbook.utils.StateManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ManageRecipeActivity extends AppCompatActivity {
    LiveData<CompositeRecipe> existingCompositeRecipeLiveData;
    CompositeRecipe newCompositeRecipe;
    LinearLayout ingredientsLayoutHeader;
    LinearLayout ingredientListLayout;
    TextView nameLabelView;
    TextView quantityLabelView;
    TextView measureLabelView;
    EditText recipeNameView;
    EditText recipeInstructionsView;
    RecipeRepository recipeRepository;
    IngredientsRepository ingredientsRepository;
    int nameViewId = View.generateViewId();
    int quantityViewId = View.generateViewId();
    int measureViewId = View.generateViewId();
    boolean recipeAdded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_recipe);
        recipeRepository = new RecipeRepository(getApplicationContext());
        ingredientsRepository = new IngredientsRepository(getApplicationContext());

        loadCompositeRecipe();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back button

        setupViews();

        existingCompositeRecipeLiveData.observe(this, recipeData -> {
            if (recipeData == null) {
                if (!recipeAdded) {
                    addNewRecipe();
                }
            } else {
                ingredientListLayout.removeAllViews();
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
        AsyncTask.execute(() -> {
            long recipeId = existingCompositeRecipeLiveData.getValue() == null ?
                    newCompositeRecipe.recipe.id : existingCompositeRecipeLiveData.getValue().recipe.id;
            recipeRepository.updateRecipe(
                    recipeId,
                    recipeNameView.getText().toString(),
                    recipeInstructionsView.getText().toString());
            List<Ingredient> updatedIngredientsData = getUpdatedIngredients();
            List<Ingredient> existingIngredients = existingCompositeRecipeLiveData.getValue() == null ?
                    newCompositeRecipe.ingredients : existingCompositeRecipeLiveData.getValue().ingredients;

            for (int i = 0; i < existingIngredients.size(); i++) {
                Ingredient updatedIngredient = updatedIngredientsData.get(i);
                Ingredient existingIngredient = existingIngredients.get(i);
                if (!Objects.equals(existingIngredient.measure, updatedIngredient.measure) ||
                        !Objects.equals(existingIngredient.name, updatedIngredient.name) ||
                        existingIngredient.quantity != updatedIngredient.quantity) {

                    ingredientsRepository.updateIngredientForRecipe(existingIngredient.id, recipeId,
                            updatedIngredient.name, updatedIngredient.measure, updatedIngredient.quantity);
                }
            }

            finish();
        });
    }

    public void onAddIngredientClick(View v) {
        ingredientsLayoutHeader.setVisibility(View.VISIBLE);
        CompositeRecipe compositeRecipe = existingCompositeRecipeLiveData.getValue() != null ?
                existingCompositeRecipeLiveData.getValue() : newCompositeRecipe;
        Ingredient newIngredient = new Ingredient(compositeRecipe.recipe.id, "", 0, "");
        AsyncTask.execute(() -> {
            long id = ingredientsRepository.addIngredientToRecipe(newIngredient);
            newIngredient.id = id;
            compositeRecipe.ingredients.add(newIngredient);
        });
        renderIngredient(newIngredient);
    }

    void loadCompositeRecipe() {
        long recipeId = getIntent().getLongExtra(Constants.RECIPE_EXTRA_KEY, -1);
        existingCompositeRecipeLiveData = recipeRepository.findById(recipeId);
    }

    void editExistingRecipe() {
        CompositeRecipe existingCompositeRecipe = existingCompositeRecipeLiveData.getValue();
        getSupportActionBar().setTitle(existingCompositeRecipe.recipe.name);
        recipeNameView.setText(existingCompositeRecipe.recipe.name);
        ImageView recipeImageView = findViewById(R.id.recipeImage);
        // TODO: load image
//        recipeImageView.setImageResource(existingCompositeRecipe.image);

        for (Ingredient ingredient : existingCompositeRecipe.ingredients) {
            renderIngredient(ingredient);
        }

        EditText instructionsVew = findViewById(R.id.instructions);
        instructionsVew.setText(existingCompositeRecipe.recipe.instructions);
    }

    void renderIngredient(Ingredient ingredient) {
        ingredientListLayout.setVisibility(View.VISIBLE);
        LinearLayout linearLayoutIngredient = new LinearLayout(this);
        linearLayoutIngredient.setLayoutParams(ingredientsLayoutHeader.getLayoutParams());
        linearLayoutIngredient.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutIngredient.setGravity(Gravity.CENTER_VERTICAL);

        EditText nameView = createEditTextForIngredient(nameViewId, ingredient.name,
                View.TEXT_ALIGNMENT_VIEW_START, nameLabelView.getLayoutParams());

        EditText quantityView = createEditTextForIngredient(quantityViewId, String.valueOf(ingredient.quantity),
                View.TEXT_ALIGNMENT_VIEW_END, quantityLabelView.getLayoutParams());

        EditText measureView = createEditTextForIngredient(measureViewId, ingredient.measure,
                View.TEXT_ALIGNMENT_VIEW_END, measureLabelView.getLayoutParams());

        ImageView actionView = createDeleteIconForIngredient(linearLayoutIngredient, ingredient);

        linearLayoutIngredient.addView(nameView);
        linearLayoutIngredient.addView(quantityView);
        linearLayoutIngredient.addView(measureView);
        linearLayoutIngredient.addView(actionView);

        ingredientListLayout.addView(linearLayoutIngredient);
    }

    List<Ingredient> getUpdatedIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        long recipeId = existingCompositeRecipeLiveData.getValue() == null ?
                newCompositeRecipe.recipe.id : existingCompositeRecipeLiveData.getValue().recipe.id;
        for (int i = 0; i < ingredientListLayout.getChildCount(); i++) {
            View childView = ingredientListLayout.getChildAt(i);
            if (childView.getId() == ingredientsLayoutHeader.getId()) {
                continue;
            }
            String name = ((EditText) childView.findViewById(nameViewId)).getText().toString();
            int quantity = Integer.parseInt(((EditText) childView.findViewById(quantityViewId)).getText().toString());
            String measure = ((EditText) childView.findViewById(measureViewId)).getText().toString();
            ingredients.add(new Ingredient(recipeId, name, quantity, measure));
        }

        return ingredients;
    }

    EditText createEditTextForIngredient(int id, String text, int textAlignment, ViewGroup.LayoutParams layoutParams) {
        EditText view = new EditText(this);
        view.setId(id);
        view.setLayoutParams(layoutParams);
        view.setText(text);
        view.setTextSize(16);
        view.setTextAlignment(textAlignment);
        view.setInputType(InputType.TYPE_CLASS_TEXT);

        return view;
    }

    ImageView createDeleteIconForIngredient(LinearLayout container, Ingredient ingredient) {
        ImageView actionView = new ImageView(this);
        actionView.setImageResource(R.drawable.baseline_delete_24);
        actionView.setBaselineAlignBottom(true);

        actionView.setOnClickListener(v -> {
            onDeleteClicked(container, ingredient);
        });

        return actionView;
    }

    void onDeleteClicked(LinearLayout container, Ingredient ingredient) {
        List<Ingredient> ingredients = existingCompositeRecipeLiveData.getValue() != null ?
                existingCompositeRecipeLiveData.getValue().ingredients
                : newCompositeRecipe.ingredients;
        container.removeAllViews();
        ((LinearLayout) container.getParent()).removeView(container);

        AsyncTask.execute(() -> {
            ingredientsRepository.removeIngredient(ingredient.id);
            ingredients.remove(ingredient);
        });

        if (ingredients.size() == 0) {
            ingredientsLayoutHeader.setVisibility(View.GONE);
        }
    }

    void addNewRecipe() {
        getSupportActionBar().setTitle(R.string.new_recipe);
        Recipe recipe = new Recipe(StateManager.getLoggedInUser().getValue().id,
                getString(R.string.new_recipe), "", "", "", 0);
        AsyncTask.execute(() -> {
            long id = recipeRepository.add(recipe);
            recipe.id = id;
            recipeAdded = true;
            newCompositeRecipe = new CompositeRecipe(recipe, new ArrayList<>(), new ArrayList<>());
        });
        recipeNameView.setText(R.string.new_recipe);
    }

    void setupViews() {
        ingredientListLayout = findViewById(R.id.ingredientList);
        recipeNameView = findViewById(R.id.recipeName);
        recipeInstructionsView = findViewById(R.id.instructions);

        ingredientsLayoutHeader = ingredientListLayout.findViewById(R.id.ingredientsHeader);
        nameLabelView = ingredientsLayoutHeader.findViewById(R.id.ingredientName);
        quantityLabelView = ingredientsLayoutHeader.findViewById(R.id.ingredientQuantity);
        measureLabelView = ingredientsLayoutHeader.findViewById(R.id.ingredientMeasure);
    }
}

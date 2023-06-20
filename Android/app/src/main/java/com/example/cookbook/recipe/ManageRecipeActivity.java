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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cookbook.R;
import com.example.cookbook.entity.Ingredient;
import com.example.cookbook.entity.Label;
import com.example.cookbook.models.CompositeRecipe;
import com.example.cookbook.repository.IngredientsRepository;
import com.example.cookbook.repository.LabelRepository;
import com.example.cookbook.repository.RecipeRepository;
import com.example.cookbook.ui.custom.MultiSelectionSpinner;
import com.example.cookbook.utils.Constants;
import com.example.cookbook.utils.ImageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class ManageRecipeActivity extends AppCompatActivity {
    private final List<Ingredient> deletedIngredients = new ArrayList<>();
    private final LiveData<List<Label>> allLabels = new MutableLiveData<>();
    LiveData<CompositeRecipe> recipeLiveData;
    LinearLayout ingredientsLayoutHeader;
    LinearLayout ingredientListLayout;
    TextView nameLabelView;
    TextView quantityLabelView;
    TextView measureLabelView;
    EditText recipeNameView;
    EditText recipePriceView;
    EditText recipeInstructionsView;
    MultiSelectionSpinner multiSelectionSpinnerView;
    RecipeRepository recipeRepository;
    LabelRepository labelRepository;
    IngredientsRepository ingredientsRepository;
    String loadedImagePath;
    int nameViewId = View.generateViewId();
    int quantityViewId = View.generateViewId();
    int measureViewId = View.generateViewId();

    MutableLiveData<List<MultiSelectionSpinner.ItemWithCheckedOptions>> initialSpinnerItems = new MutableLiveData<>();
    ActivityResultLauncher<String> imageUploader = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                try {
                    loadedImagePath = ImageUtils.uploadLocalImageFromUri(getContentResolver(), uri);
                    ((ImageView) findViewById(R.id.recipeImage)).setImageBitmap(ImageUtils.loadFile(loadedImagePath));
                } catch (Exception e) {
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                }
            });
    private List<Ingredient> renderedIngredients;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_recipe);
        recipeRepository = new RecipeRepository(getApplicationContext());
        ingredientsRepository = new IngredientsRepository(getApplicationContext());
        labelRepository = new LabelRepository(getApplicationContext());
        loadCompositeRecipe();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back button

        setupViews();

        recipeLiveData.observe(this, recipeData -> {
            if (recipeData == null) return;

            removeAllViewsExceptFirst();
            renderedIngredients = new ArrayList<>(recipeData.ingredients);
            Collections.copy(renderedIngredients, recipeData.ingredients);
            editRecipe();
        });

        findViewById(R.id.recipeImageEdit).setOnClickListener(view -> {
            imageUploader.launch("image/*");
        });

        initialSpinnerItems.observe(this, items -> {
            if (items == null) return;

            MultiSelectionSpinner.MultiSpinnerListener multiSpinnerListener = selected -> {
                System.out.println(selected);
            };
            multiSelectionSpinnerView.setItemsWithMap(items, "None selected", multiSpinnerListener);
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

    public void onSaveClick(View view) {
        AsyncTask.execute(() -> {
            CompositeRecipe compositeRecipe = recipeLiveData.getValue();
            StringJoiner labels = new StringJoiner(",");

            for (String name : multiSelectionSpinnerView.getSelected()) {
                labels.add(name);
            }


            recipeRepository.updateRecipe(
                    compositeRecipe.recipe.id,
                    recipeNameView.getText().toString(),
                    recipeInstructionsView.getText().toString(),
                    loadedImagePath != null ? loadedImagePath : compositeRecipe.recipe.imagePath,
                    labels.toString(),
                    Double.parseDouble(recipePriceView.getText().toString())
            );

            getUpdatedIngredients();

            List<Ingredient> updatedIngredients = new ArrayList<>();
            List<Ingredient> addedIngredients = new ArrayList<>();

            renderedIngredients.forEach(i -> {
                if (i.id > 0) {
                    updatedIngredients.add(i);
                } else {
                    addedIngredients.add(i);
                }
            });

            AsyncTask.execute(() -> {
                ingredientsRepository.removeAll(deletedIngredients);
                ingredientsRepository.updateAll(updatedIngredients);
                ingredientsRepository.insertAll(addedIngredients);
                finish();
            });
        });
    }

    public void onDeleteClick(View view) {
        AsyncTask.execute(() -> {
            recipeRepository.deleteById(recipeLiveData.getValue().recipe.id);
            finish();
        });
    }

    public void onAddIngredientClick(View v) {
        ingredientsLayoutHeader.setVisibility(View.VISIBLE);
        CompositeRecipe compositeRecipe = recipeLiveData.getValue();
        Ingredient newIngredient = new Ingredient(compositeRecipe.recipe.id, "", 0, "");
        renderedIngredients.add(newIngredient);
        renderIngredient(newIngredient);
    }

    void loadCompositeRecipe() {
        long recipeId = getIntent().getLongExtra(Constants.RECIPE_EXTRA_KEY, -1);
        recipeLiveData = recipeRepository.findById(recipeId);
    }

    void editRecipe() {
        CompositeRecipe compositeRecipe = recipeLiveData.getValue();
        getSupportActionBar().setTitle(compositeRecipe.recipe.name);
        recipeNameView.setText(compositeRecipe.recipe.name);
        recipePriceView.setText(String.valueOf(compositeRecipe.recipe.price));

        AsyncTask.execute(() -> {
            final List<Label> labels = labelRepository.getAllLabels();
            List<String> acquiredLabels = Arrays.stream(compositeRecipe.recipe.labels.split(",")).collect(Collectors.toList());
            initialSpinnerItems.postValue(labels.stream().map(l -> new MultiSelectionSpinner.ItemWithCheckedOptions(l.name, acquiredLabels.contains(l.name))).collect(Collectors.toList()));
        });

        ImageUtils.setImageView(findViewById(R.id.recipeImage), compositeRecipe.recipe.imagePath);

        for (Ingredient ingredient : renderedIngredients) {
            renderIngredient(ingredient);
        }

        EditText instructionsVew = findViewById(R.id.instructions);
        instructionsVew.setText(compositeRecipe.recipe.instructions);
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

    void getUpdatedIngredients() {
        for (int i = 1; i < ingredientListLayout.getChildCount(); i++) {
            View childView = ingredientListLayout.getChildAt(i);
            String name = ((EditText) childView.findViewById(nameViewId)).getText().toString();
            int quantity = Integer.parseInt(((EditText) childView.findViewById(quantityViewId)).getText().toString());
            String measure = ((EditText) childView.findViewById(measureViewId)).getText().toString();
            renderedIngredients.get(i - 1).measure = measure;
            renderedIngredients.get(i - 1).quantity = quantity;
            renderedIngredients.get(i - 1).name = name;
        }
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
        container.removeAllViews();
        ((LinearLayout) container.getParent()).removeView(container);

        if (ingredient.id > 0) {
            deletedIngredients.add(ingredient);
            renderedIngredients.remove(ingredient);
        }

        if (renderedIngredients.size() == 0) {
            ingredientsLayoutHeader.setVisibility(View.GONE);
        }
    }

    void setupViews() {
        ingredientListLayout = findViewById(R.id.ingredientList);
        recipeNameView = findViewById(R.id.recipeName);
        recipePriceView = findViewById(R.id.price);
        recipeInstructionsView = findViewById(R.id.instructions);
        multiSelectionSpinnerView = findViewById(R.id.labels_dropdown);

        ingredientsLayoutHeader = ingredientListLayout.findViewById(R.id.ingredientsHeader);
        nameLabelView = ingredientsLayoutHeader.findViewById(R.id.ingredientName);
        quantityLabelView = ingredientsLayoutHeader.findViewById(R.id.ingredientQuantity);
        measureLabelView = ingredientsLayoutHeader.findViewById(R.id.ingredientMeasure);
    }

    private void removeAllViewsExceptFirst() {
        int count = ingredientListLayout.getChildCount();
        ingredientListLayout.removeViews(1, count - 1);
    }
}

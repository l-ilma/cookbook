package com.example.cookbook;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cookbook.databinding.ActivityMainBinding;
import com.example.cookbook.entity.Label;
import com.example.cookbook.entity.User;
import com.example.cookbook.models.RecipeFilter;
import com.example.cookbook.models.RecipeWithLikes;
import com.example.cookbook.recipe.ManageRecipeActivity;
import com.example.cookbook.recipe.RecipeActivity;
import com.example.cookbook.repository.IngredientRepository;
import com.example.cookbook.repository.LabelRepository;
import com.example.cookbook.repository.RecipeRepository;
import com.example.cookbook.repository.UserRepository;
import com.example.cookbook.ui.authentication.LoginActivity;
import com.example.cookbook.ui.custom.MultiSelectionSpinner;
import com.example.cookbook.utils.Constants;
import com.example.cookbook.utils.NavRecipeFilter;
import com.example.cookbook.utils.StateManager;
import com.example.cookbook.utils.Utils;
import com.google.android.material.navigation.NavigationView;

import org.apache.commons.lang3.StringUtils;
import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


public class MainActivity extends AppCompatActivity {
    private final MutableLiveData<NavRecipeFilter> navFilter = new MutableLiveData<>(NavRecipeFilter.NONE);
    private final MutableLiveData<Boolean> arePermissionsAllowed = new MutableLiveData<>(Utils.isExternalWriteEnabled());
    private final MutableLiveData<RecipeFilter> recipeFilter = new MutableLiveData<>(null);
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private ActivityMainBinding mainBinding;
    private LiveData<List<RecipeWithLikes>> currentlyShowingRecipes = new MutableLiveData<>(new ArrayList<>());
    private RecipeRepository recipeRepository;
    private UserRepository userRepository;
    private IngredientRepository ingredientRepository;
    private LabelRepository labelRepository;

    private NavigationView navigationView;
    private LayoutInflater layoutInflater;
    private LinearLayout mainLinearLayout;
    private final List<Integer> labelIds = Arrays.asList(
            R.id.label_1, R.id.label_2, R.id.label_3, R.id.label_4,
            R.id.label_5, R.id.label_6, R.id.label_7, R.id.label_8,
            R.id.label_9, R.id.label_10, R.id.label_11, R.id.label_12,
            R.id.label_13, R.id.label_14, R.id.label_15, R.id.label_16
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        attachPermissionsObserver();
        mainLinearLayout = findViewById(R.id.lin_lay_main);

        layoutInflater = LayoutInflater.from(this);

        View.OnClickListener filterButtonListener = v -> {
            onFilterClick();
        };
        ImageButton filterButton = findViewById(R.id.filter_button);
        filterButton.setOnClickListener(filterButtonListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        arePermissionsAllowed.postValue(Utils.isExternalWriteEnabled());
    }

    private void attachPermissionsObserver() {
        arePermissionsAllowed.observe(this, value -> {
            if (value) {
                Utils.dismissAllDialogs(getSupportFragmentManager());
                mainBinding.dishList.setBackgroundColor(Color.parseColor("#ffffff"));
                renderApplicationView();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    PermissionsDialogFragment dialogFragment = new PermissionsDialogFragment();
                    dialogFragment.show(getSupportFragmentManager(), PermissionsDialogFragment.TAG);
                    mainBinding.dishList.setBackgroundColor(Color.parseColor("#808080"));
                }
            }
        });
    }

    private void renderApplicationView() {
        recipeRepository = new RecipeRepository(getApplicationContext());
        userRepository = new UserRepository(getApplicationContext());
        ingredientRepository = new IngredientRepository(getApplicationContext());
        labelRepository = new LabelRepository(getApplicationContext());

        // to make the Navigation drawer icon always appear on the action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        doLoggedInUserLookup();
        setupNavigationViewItemSelectedListener();
        setupDrawerLayout();
        loadHomeRecipes();
        attachFiltersObserver();
        StateManager.getLoggedInUser().observe(this, this::setupMenuContentVisibility);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openRecipe(RecipeWithLikes recipe) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra(Constants.RECIPE_EXTRA_KEY, recipe.recipe.id);
        startActivity(intent);
    }

    public void addNewRecipe(View view) {
        Intent intent = new Intent(this, ManageRecipeActivity.class);
        startActivity(intent);
    }

    void doLoggedInUserLookup() {
        AsyncTask.execute(() -> {
            User loggedInUser = userRepository.getLoggedInUser();
            StateManager.setLoggedInUser(loggedInUser);
        });
    }

    void setupMenuContentVisibility(User user) {
        Menu menu = mainBinding.navigationView.getMenu();

        menu.findItem(R.id.nav_login).setVisible(user == null);
        menu.findItem(R.id.nav_logout).setVisible(user != null);
        menu.findItem(R.id.nav_favourites).setVisible(user != null);
        menu.findItem(R.id.nav_my_recipes).setVisible(user != null);
        menu.findItem(R.id.nav_account).setVisible(user != null);
    }

    private void setupNavigationViewItemSelectedListener() {
        mainBinding.navigationView.setNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.nav_login) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            } else if (menuItem.getItemId() == R.id.nav_logout) {
                logoutMenuItemClick();
            } else if (menuItem.getItemId() == R.id.nav_favourites) {
                navFilter.postValue(NavRecipeFilter.FAVOURITES);
            } else if (menuItem.getItemId() == R.id.nav_home) {
                loadHomeRecipes();
                navFilter.postValue(NavRecipeFilter.NONE);
            } else if (menuItem.getItemId() == R.id.nav_my_recipes) {
                navFilter.postValue(NavRecipeFilter.MY);
            } else {
                Toast.makeText(this, "Functionality not yet implemented!", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        });
    }

    private void setupDrawerLayout() {
        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void loadHomeRecipes() {
        currentlyShowingRecipes = recipeRepository.getAllRecipes();
    }

    private void attachFiltersObserver() {
        currentlyShowingRecipes.observe(this, recipes -> navFilter.observe(this, filterValue -> {
            User loggedInUser = StateManager.getLoggedInUser().getValue();

            ListView recipeList = findViewById(R.id.dishList);
            List<RecipeWithLikes> renderedRecipes = recipes;
            boolean useLikeBtn = true;
            View addRecipeBtnView = findViewById(R.id.addRecipeButtonLayout);
            addRecipeBtnView.setVisibility(View.GONE);

            if (loggedInUser != null) {
                long id = loggedInUser.id;
                DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);
                if (filterValue == NavRecipeFilter.FAVOURITES) {
                    getSupportActionBar().setTitle(R.string.my_favorites);
                    renderedRecipes = recipes.stream().filter(r -> r.likes.stream().anyMatch(l -> l.id == id)).collect(Collectors.toList());
                } else if (filterValue == NavRecipeFilter.MY) {
                    useLikeBtn = false;
                    getSupportActionBar().setTitle(R.string.my_recipes);
                    params.setMargins(0, 0, 0, 100);
                    addRecipeBtnView.setVisibility(View.VISIBLE);
                    renderedRecipes = recipes.stream().filter(r -> r.recipe.userId == id).collect(Collectors.toList());
                }
                mainLinearLayout.setLayoutParams(params);
            }

            recipeList.setAdapter(new RecipeArrayAdapter(this, renderedRecipes, useLikeBtn));
        }));

        currentlyShowingRecipes.observe(this, recipes -> recipeFilter.observe(this, filter -> {
            ListView recipeList = findViewById(R.id.dishList);
            List<RecipeWithLikes> renderedRecipes = recipes;
            List<RecipeWithLikes> filteredRecipes;
            if (filter != null) {
                filteredRecipes = filterRecipesByName(renderedRecipes, filter.name);
                filteredRecipes = filterRecipesByLabel(filteredRecipes, filter.labels);
                filteredRecipes = filterRecipesByPrice(filteredRecipes, filter.priceMax, filter.priceMin);
                filteredRecipes = filterRecipesByIngredient(filteredRecipes, filter.ingredients);
                filteredRecipes = filterRecipesByMyAndLiked(filteredRecipes, filter.my, filter.liked);

                renderedRecipes = filteredRecipes;
                recipeList.setAdapter(new RecipeArrayAdapter(this, renderedRecipes, true));
            }
        }));
    }

    private List<RecipeWithLikes> filterRecipesByMyAndLiked(List<RecipeWithLikes> recipes,
                                                            boolean my, boolean liked) {
        User loggedInUser = StateManager.getLoggedInUser().getValue();
        if (loggedInUser == null) return recipes;
        List<RecipeWithLikes> filteredRecipes = new ArrayList<>();
        if (my) {
            for (RecipeWithLikes recipe : recipes) {
                if (recipe.recipe.userId == loggedInUser.id) {
                    filteredRecipes.add(recipe);
                }
            }
        }

        if (liked) {
            for (RecipeWithLikes recipe : recipes) {
                if (recipe.likes.contains(loggedInUser)) {
                    filteredRecipes.add(recipe);
                }
            }
        }

        if (!my && !liked) filteredRecipes = recipes;

        return filteredRecipes;
    }

    private List<RecipeWithLikes> filterRecipesByIngredient(List<RecipeWithLikes> recipes,
                                                            List<String> ingredients) {
        List<RecipeWithLikes> filteredRecipes = new ArrayList<>();
        for (RecipeWithLikes recipe : recipes) {
            AtomicReference<List<String>> recipeIngredients = new AtomicReference<>();
            Thread getIngredientsForRecipeThread = new Thread(() -> {
                recipeIngredients.set(ingredientRepository.getIngredientForRecipe(recipe.recipe.id));
            });

            getIngredientsForRecipeThread.start();
            try {
                getIngredientsForRecipeThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (recipeIngredients.get().stream().anyMatch(ingredients::contains)) {
                filteredRecipes.add(recipe);
            }
        }

        return filteredRecipes;
    }

    private List<RecipeWithLikes> filterRecipesByPrice(List<RecipeWithLikes> recipes, int priceMax,
                                                       int priceMin) {
        List<RecipeWithLikes> filteredRecipes = new ArrayList<>();
        for (RecipeWithLikes recipe : recipes) {
            if (recipe.recipe.price >= priceMin && recipe.recipe.price <= priceMax) {
                filteredRecipes.add(recipe);
            }
        }

        return filteredRecipes;
    }

    private List<RecipeWithLikes> filterRecipesByName(List<RecipeWithLikes> recipes, String name) {
        List<RecipeWithLikes> filteredRecipes = new ArrayList<>();
        if (name != null && !name.equals("")) {
            for (RecipeWithLikes recipe : recipes) {
                String[] titleWords = recipe.recipe.name.split(" ");
                for (String word : titleWords) {
                    int levenshteinDistance = StringUtils.getLevenshteinDistance(name, word);
                    int maxDistance = Math.max(name.length(), word.length());

                    double similarity = 1.0 - (double) levenshteinDistance / maxDistance;
                    if (similarity >= 0.5) {
                        filteredRecipes.add(recipe);
                        break;
                    }
                }

            }
        } else {
            return recipes;
        }
        return filteredRecipes;
    }

    private List<RecipeWithLikes> filterRecipesByLabel(List<RecipeWithLikes> recipes, List<String> labels) {
        List<RecipeWithLikes> filteredRecipes = new ArrayList<>();
        if (labels.size() > 0) {
            for (RecipeWithLikes recipe : recipes) {
                List<String> recipeLabels = Arrays.asList(recipe.recipe.labels.split(","));
                if (recipeLabels.stream().anyMatch(labels::contains)) {
                    filteredRecipes.add(recipe);
                }
            }
        } else {
            return recipes;
        }

        return filteredRecipes;
    }

    private void logoutMenuItemClick() {
        AsyncTask.execute(() -> {
            userRepository.logout();
            StateManager.setLoggedInUser(null);
            this.runOnUiThread(() ->
                    Toast.makeText(this, R.string.successfully_logged_out, Toast.LENGTH_SHORT).show()
            );
        });
    }

    private void onFilterClick() {
        ImageButton filterButton = findViewById(R.id.filter_button);
        if (!filterButton.isEnabled()) return;
        filterButton.setEnabled(false);

        View inflatedView = layoutInflater.inflate(R.layout.filter_view, null);

        ListView dishList = findViewById(R.id.dishList);
        LinearLayout addRecipeBtnView = findViewById(R.id.addRecipeButtonLayout);
        dishList.setVisibility(View.GONE);
        if (addRecipeBtnView != null) {
            addRecipeBtnView.setVisibility(View.GONE);
        }

        mainLinearLayout.addView(inflatedView);

        Thread setupLabelCheckboxesThread = new Thread(() ->
        {
            setLabelCheckboxes();
        });

        User loggedInUser = StateManager.getLoggedInUser().getValue();
        if (loggedInUser == null) {
            CheckBox myRecipes = findViewById(R.id.my_recipes);
            myRecipes.setVisibility(View.GONE);
            CheckBox likedRecipes = findViewById(R.id.liked_recipes);
            likedRecipes.setVisibility(View.GONE);
        }

        AtomicReference<List<Double>> prices = new AtomicReference<>();
        Thread getAllPricesThread = new Thread(() -> {
            prices.set(recipeRepository.getAllPrices());
        });
        getAllPricesThread.start();
        try {
            getAllPricesThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        RangeSeekBar priceSeekbar = findViewById(R.id.priceRangeSeekBar);
        priceSeekbar.setRangeValues(Collections.min(prices.get()), Collections.max(prices.get()));


        setupLabelCheckboxesThread.start();
        try {
            setupLabelCheckboxesThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        View.OnClickListener applyButtonListener = v -> {
            onFilterApplyClick();
        };
        Button applyFilterButton = findViewById(R.id.apply_button);
        applyFilterButton.setOnClickListener(applyButtonListener);

        Thread setupIngredientsDropdownThread = new Thread(() ->
        {
            MultiSelectionSpinner dropdown = findViewById(R.id.ingredient_dropdown);
            final List<String> ingredients = ingredientRepository.getAllIngredientNames();
            MultiSelectionSpinner.MultiSpinnerListener multiSpinnerListener = selected -> {
            };
            dropdown.setItems(ingredients, "Choose Ingredients", multiSpinnerListener);
        });

        setupIngredientsDropdownThread.start();
        try {
            setupIngredientsDropdownThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ImageButton exitFilterButton = findViewById(R.id.exit_filter);
        exitFilterButton.setVisibility(View.VISIBLE);
        View.OnClickListener exitListener = view -> {
            onExitFilterClick();
        };
        exitFilterButton.setOnClickListener(exitListener);
    }

    private void onExitFilterClick() {
        View filterView = findViewById(R.id.filter_view);
        ViewGroup parent = (ViewGroup) filterView.getParent();
        parent.removeView(filterView);

        ImageButton filterButton = findViewById(R.id.filter_button);
        filterButton.setEnabled(true);
        ListView dishList = findViewById(R.id.dishList);
        dishList.setVisibility(View.VISIBLE);

        ImageButton exitButton = findViewById(R.id.exit_filter);
        exitButton.setVisibility(View.GONE);
        LinearLayout addRecipeBtnView = findViewById(R.id.addRecipeButtonLayout);
        if (addRecipeBtnView != null) {
            addRecipeBtnView.setVisibility(View.VISIBLE);
        }

    }

    private void onFilterApplyClick() {
        EditText recipeName = findViewById(R.id.name_of_recipe);
        RangeSeekBar rangeSeekBar = findViewById(R.id.priceRangeSeekBar);
        MultiSelectionSpinner multiSelectionSpinner = findViewById(R.id.ingredient_dropdown);
        CheckBox myRecipes = findViewById(R.id.my_recipes);
        CheckBox likedRecipes = findViewById(R.id.liked_recipes);

        RecipeFilter recipeFilter = new RecipeFilter();
        recipeFilter.name = recipeName.getText().toString();
        recipeFilter.priceMin = rangeSeekBar.getSelectedMinValue().intValue();
        recipeFilter.priceMax = rangeSeekBar.getSelectedMaxValue().intValue();
        recipeFilter.my = myRecipes.isChecked();
        recipeFilter.liked = likedRecipes.isChecked();
        recipeFilter.ingredients = multiSelectionSpinner.getSelected();
        recipeFilter.labels = getLabels();

        this.recipeFilter.postValue(recipeFilter);

        onExitFilterClick();
    }

    public List<String> getLabels() {
        AtomicReference<List<Label>> atomicAllLabels = new AtomicReference<>();
        Thread getAllLabelsThread = new Thread(() -> {
            atomicAllLabels.set(labelRepository.getAllLabels());
        });

        getAllLabelsThread.start();
        try {
            getAllLabelsThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Label> allLabels = atomicAllLabels.get();
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < labelIds.size(); i++) {
            CheckBox labelCheckBox = findViewById(labelIds.get(i));
            if (labelCheckBox.isChecked())
                labels.add(Long.toString(allLabels.get(i).id));
        }

        return labels;
    }

    private void setLabelCheckboxes() {
        List<Label> labels = labelRepository.getAllLabels();
        for (int i = 0; i < labelIds.size(); i++) {
            CheckBox checkBox = findViewById(labelIds.get(i));
            checkBox.setText(labels.get(i).name);
        }
    }
}
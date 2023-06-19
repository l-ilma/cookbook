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
import com.example.cookbook.entity.User;
import com.example.cookbook.recipe.ManageRecipeActivity;
import com.example.cookbook.models.RecipeWithLikes;
import com.example.cookbook.recipe.RecipeActivity;
import com.example.cookbook.repository.IngredientRepository;
import com.example.cookbook.repository.RecipeRepository;
import com.example.cookbook.models.RecipeFilter;
import com.example.cookbook.repository.UserRepository;
import com.example.cookbook.ui.authentication.LoginActivity;
import com.example.cookbook.ui.custom.MultiSelectionSpinner;
import com.google.android.material.navigation.NavigationView;
import com.example.cookbook.utils.Constants;
import com.example.cookbook.utils.NavRecipeFilter;
import com.example.cookbook.utils.StateManager;
import com.example.cookbook.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Arrays;

import org.florescu.android.rangeseekbar.RangeSeekBar;

public class MainActivity extends AppCompatActivity {
    private final MutableLiveData<NavRecipeFilter> filter = new MutableLiveData<>(NavRecipeFilter.NONE);
    private final MutableLiveData<Boolean> arePermissionsAllowed = new MutableLiveData<>(Utils.isExternalWriteEnabled());
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private ActivityMainBinding mainBinding;
    private LiveData<List<RecipeWithLikes>> currentlyShowingRecipes = new MutableLiveData<>(new ArrayList<>());
    private RecipeRepository recipeRepository;
    private UserRepository userRepository;
    private IngredientRepository ingredientRepository;

    private NavigationView navigationView;
    private LayoutInflater layoutInflater;
    private LinearLayout mainLinearLayout;
    private List<Integer> labelIds = Arrays.asList(
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
            OnFilterClick();
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
                filter.postValue(NavRecipeFilter.FAVOURITES);
            } else if (menuItem.getItemId() == R.id.nav_home) {
                loadHomeRecipes();
                filter.postValue(NavRecipeFilter.NONE);
            } else if (menuItem.getItemId() == R.id.nav_my_recipes) {
                filter.postValue(NavRecipeFilter.MY);
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
        currentlyShowingRecipes.observe(this, recipes -> filter.observe(this, filterValue -> {
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

    private void OnFilterClick() {
        ImageButton filterButton = findViewById(R.id.filter_button);
        if(!filterButton.isEnabled()) return;
        filterButton.setEnabled(false);

        View inflatedView = layoutInflater.inflate(R.layout.filter_view, null);

        ListView dishList = findViewById(R.id.dishList);
        LinearLayout addRecipeBtnView = findViewById(R.id.addRecipeButtonLayout);
        dishList.setVisibility(View.GONE);
        if (addRecipeBtnView != null) {
            addRecipeBtnView.setVisibility(View.GONE);
        }

        mainLinearLayout.addView(inflatedView);

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

    private void onExitFilterClick(){
        View filterView = findViewById(R.id.filter_view);
        ViewGroup parent = (ViewGroup)filterView.getParent();
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

    private void onFilterApplyClick(){
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
        //TODO add logic for filtering
        onExitFilterClick();
    }

    public List<String> getLabels(){
        List<String> labels = new ArrayList<>();
        for(int labelId : labelIds){
            CheckBox labelCheckBox = findViewById(labelId);
            if(labelCheckBox.isChecked())
                labels.add(labelCheckBox.getText().toString());
        }

        return labels;
    }
}
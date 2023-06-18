package com.example.cookbook;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.example.cookbook.recipe.ManageRecipe;
import com.example.cookbook.models.RecipeWithLikes;
import com.example.cookbook.recipe.RecipeActivity;
import com.example.cookbook.repository.RecipeRepository;
import com.example.cookbook.repository.UserRepository;
import com.example.cookbook.ui.authentication.LoginActivity;
import com.example.cookbook.utils.Constants;
import com.example.cookbook.utils.NavRecipeFilter;
import com.example.cookbook.utils.StateManager;
import com.example.cookbook.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private final MutableLiveData<NavRecipeFilter> filter = new MutableLiveData<>(NavRecipeFilter.NONE);
    private final MutableLiveData<Boolean> arePermissionsAllowed = new MutableLiveData<>(Utils.isExternalWriteEnabled());
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private ActivityMainBinding mainBinding;
    private LiveData<List<RecipeWithLikes>> currentlyShowingRecipes = new MutableLiveData<>(new ArrayList<>());
    private RecipeRepository recipeRepository;
    private UserRepository userRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        attachPermissionsObserver();
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
        Intent intent = new Intent(this, ManageRecipe.class);
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
                if (filterValue == NavRecipeFilter.FAVOURITES) {
                    getSupportActionBar().setTitle(R.string.my_favorites);
                    renderedRecipes = recipes.stream().filter(r -> r.likes.stream().anyMatch(l -> l.id == id)).collect(Collectors.toList());
                } else if (filterValue == NavRecipeFilter.MY) {
                    useLikeBtn = false;
                    getSupportActionBar().setTitle(R.string.my_recipes);
                    addRecipeBtnView.setVisibility(View.VISIBLE);
                    renderedRecipes = recipes.stream().filter(r -> r.recipe.userId == id).collect(Collectors.toList());
                }
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
}
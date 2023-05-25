package com.example.cookbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.cookbook.entity.User;
import com.example.cookbook.repository.UserRepository;
import com.example.cookbook.ui.authentication.LoginActivity;
import com.google.android.material.navigation.NavigationView;

import com.example.cookbook.models.Recipe;
import com.example.cookbook.recipe.RecipeActivity;
import com.example.cookbook.recipe.RecipeLoader;

import java.text.ParseException;
import com.example.cookbook.recipe.RecipeRepository;

public class MainActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView = findViewById(R.id.navigation_view);

        doLoggedInUserLookup();
        setupNavigationViewItemSelectedListener();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView recipeList = findViewById(R.id.dishList);
        try {
            recipeList.setAdapter(new RecipeArrayAdapter(this, RecipeRepository.getInstance().getRecipes()));
            StateManager.getLoggedInUser().observe(this, user -> setupMenuContentVisibility());
        } catch (ParseException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openRecipe(Recipe recipe) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("recipe", recipe); // use ID instead
        startActivity(intent);
    }

    private void doLoggedInUserLookup() {
        AsyncTask.execute(() -> {
            User loggedInUser = new UserRepository(getApplicationContext()).getLoggedInUser();
            StateManager.setLoggedInUser(loggedInUser);
        });
    }

    private void setupMenuContentVisibility() {
        MenuItem login = navigationView.getMenu().findItem(R.id.nav_login);
        MenuItem logout = navigationView.getMenu().findItem(R.id.nav_logout);
        User user = StateManager.getLoggedInUser().getValue();

        login.setVisible(user == null);
        logout.setVisible(user != null);
    }

    private void setupNavigationViewItemSelectedListener() {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            try {
                if (menuItem.getItemId() == R.id.nav_login) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else if (menuItem.getItemId() == R.id.nav_logout) {
                    AsyncTask.execute(() -> {
                        new UserRepository(getApplicationContext()).logout();
                        StateManager.setLoggedInUser(null);
                        this.runOnUiThread(() ->
                                Toast.makeText(this, R.string.successfully_logged_out, Toast.LENGTH_SHORT).show()
                        );
                    });
                } else if(menuItem.getItemId() == R.id.nav_favourites) {
                    ListView favReceipeList = findViewById(R.id.dishList);
                    favReceipeList.setAdapter(new RecipeArrayAdapter(this,
                            RecipeRepository.getInstance().getFavourites()));
                } else if(menuItem.getItemId() == R.id.nav_home) {
                    ListView receipeList = findViewById(R.id.dishList);
                    receipeList.setAdapter(new RecipeArrayAdapter(this,
                            RecipeRepository.getInstance().getRecipes()));
                } else if(menuItem.getItemId() == R.id.nav_my_recipes) {
                    ListView myReceipeList = findViewById(R.id.dishList);
                    myReceipeList.setAdapter(new RecipeArrayAdapter(this,
                            RecipeRepository.getInstance().getMyRecipes()));
                } else {
                    Toast.makeText(this, "Functionality not yet implemented!", Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {
                System.out.println(e.getLocalizedMessage());
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        });
    }
}
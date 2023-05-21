package com.example.cookbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

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

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<Integer> dishes = new ArrayList<>();

        dishes.add(R.drawable.dish);
        dishes.add(R.drawable.dish);
        dishes.add(R.drawable.dish);
        dishes.add(R.drawable.dish);
        dishes.add(R.drawable.dish);
        dishes.add(R.drawable.dish);
        dishes.add(R.drawable.dish);

        dishes.add(R.drawable.dish_image);
        dishes.add(R.drawable.dish_image);
        dishes.add(R.drawable.dish_image);
        dishes.add(R.drawable.dish_image);
        dishes.add(R.drawable.dish_image);
        dishes.add(R.drawable.dish_image);
        dishes.add(R.drawable.dish_image);

        ListView receiptList = findViewById(R.id.dishList);
        receiptList.setAdapter(new RecipeArrayAdapter(getApplicationContext(), dishes));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
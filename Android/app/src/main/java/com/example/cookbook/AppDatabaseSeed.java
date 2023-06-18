package com.example.cookbook;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase.Callback;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.cookbook.entity.Comment;
import com.example.cookbook.entity.Ingredient;
import com.example.cookbook.entity.Recipe;
import com.example.cookbook.entity.User;
import com.example.cookbook.entity.UserLikesRecipeCrossRef;
import com.example.cookbook.utils.ImageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

public class AppDatabaseSeed extends Callback {

    private final Context context;

    private AppDatabase appDb;

    public AppDatabaseSeed(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(@NonNull SupportSQLiteDatabase db) {
        super.onCreate(db);
        Executors.newSingleThreadScheduledExecutor().execute(() -> {
            appDb = AppDatabase.getInstance(context);


            try {
                populateUsers();
                populateRecipesWithIngredients();
                populateComments();
                populateLikes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void populateUsers() {
        appDb.userDao().insertAll(
                Arrays.asList(
                        new User("ColinWilli", "colin.willi@gmail.com", "password", false),
                        new User("JacquelineMeyer", "jacqueline.meyer@gmail.com", "password", false),
                        new User("AnthonyMurray", "anthony.murray@gmail.com", "password", false),
                        new User("MichaelBurns", "michael.burns@gmail.com", "password", false),
                        new User("ThomasJohnson", "thomas.johnson", "password", false)
                ));
    }

    private void populateRecipesWithIngredients() throws IOException {
        List<Ingredient> ingredients = new ArrayList<>();
        List<Recipe> recipes = new ArrayList<>();


        // ******** Recipe 1 ******** //
        String chocolateChipCookiesInstructions = "1. Preheat the oven to 350°F (175°C).\n" +
                "2. In a large mixing bowl, combine the flour and sugar.\n" +
                "3. Add the remaining ingredients and mix until well combined.\n" +
                "4. Drop spoonfuls of dough onto a greased baking sheet.\n" +
                "5. Bake for 10-12 minutes or until golden brown.";

        Recipe chocolateCookies = new Recipe(
                1,
                "Chocolate Chip Cookies",
                chocolateChipCookiesInstructions,
                ImageUtils.createFile(
                        "chocolate_cookies-" + UUID.randomUUID(),
                        ImageUtils.getDrawableAsByteArray(context, R.drawable.chocolate_chip_cookies)
                )
        );

        ingredients.add(new Ingredient(1, "Flour", 2, "cups"));
        ingredients.add(new Ingredient(1, "Sugar", 1, "cup"));
        recipes.add(chocolateCookies);

        // ******** Recipe 2 ******** //
        String spaghettiInstructions = "1. Cook the spaghetti according to package instructions.\n" +
                "2. In a separate pan, cook the ground beef until browned.\n" +
                "3. Drain the spaghetti and add it to the pan with the ground beef.\n" +
                "4. Add your favorite sauce and simmer for 5 minutes.\n" +
                "5. Serve hot and garnish with grated cheese.";

        Recipe spaghetti = new Recipe(
                2,
                "Spaghetti and Meatballs",
                spaghettiInstructions,
                ImageUtils.createFile(
                        "spaghetti_meatballs-" + UUID.randomUUID(),
                        ImageUtils.getDrawableAsByteArray(context, R.drawable.spaghetti_meatballs)
                )
        );

        ingredients.add(new Ingredient(2, "Spaghetti", 8, "ounces"));
        ingredients.add(new Ingredient(2, "Ground Beef", 1, "pound"));
        recipes.add(spaghetti);

        // ******** Recipe 3 ******** //
        String cucumberSaladInstructions = "1. Slice the cucumbers and red onion into thin rounds.\n" +
                "2. In a mixing bowl, combine the cucumbers and red onion.\n" +
                "3. In a separate bowl, mix together the dressing ingredients.\n" +
                "4. Pour the dressing over the cucumber and onion mixture.\n" +
                "5. Toss well to coat the vegetables. Refrigerate for 1 hour before serving.";

        Recipe cucumberSalad = new Recipe(
                3,
                "Cucumber Salad",
                cucumberSaladInstructions,
                ImageUtils.createFile(
                        "cucumber_salad-" + UUID.randomUUID(),
                        ImageUtils.getDrawableAsByteArray(context, R.drawable.cucumber_salad)
                )


        );

        ingredients.add(new Ingredient(3, "Cucumber", 2, "medium-sized"));
        ingredients.add(new Ingredient(3, "Red Onion", 1, "small"));
        recipes.add(cucumberSalad);

        // ******** Recipe 4 ******** //
        String pizzaInstructions = "1. Preheat the oven to the recommended temperature for your pizza dough.\n" +
                "2. Roll out the pizza dough into your desired shape.\n" +
                "3. Spread the tomato sauce evenly over the dough.\n" +
                "4. Add your favorite toppings such as cheese, vegetables, and meats.\n" +
                "5. Bake the pizza in the oven for the recommended time or until the crust is golden brown.";

        Recipe pizza = new Recipe(
                4,
                "Homemade Pizza",
                pizzaInstructions,
                ImageUtils.createFile(
                        "pizza-" + UUID.randomUUID(),
                        ImageUtils.getDrawableAsByteArray(context, R.drawable.pizza)
                )
        );

        ingredients.add(new Ingredient(4, "Pizza Dough", 1, "ball"));
        ingredients.add(new Ingredient(4, "Tomato Sauce", 1, "cup"));
        recipes.add(pizza);

        // ******** Recipe 5 ******** //
        String pancakesInstructions = "1. In a mixing bowl, whisk together the flour, milk, and other ingredients.\n" +
                "2. Heat a non-stick pan or griddle over medium heat.\n" +
                "3. Pour a ladleful of the batter onto the pan and spread it evenly.\n" +
                "4. Cook for a few minutes until bubbles form on the surface.\n" +
                "5. Flip the pancake and cook for another minute or until golden brown.";

        Recipe pancakes = new Recipe(
                5,
                "Pancakes",
                pancakesInstructions,
                null
        );

        ingredients.add(new Ingredient(5, "Flour", 1, "cup"));
        ingredients.add(new Ingredient(5, "Milk", 1, "cup"));
        recipes.add(pancakes);

        appDb.recipeDao().insertAll(recipes);
        appDb.ingredientsDao().insertAll(ingredients);
    }

    private void populateComments() {
        appDb.commentDao().insertAll(
                Arrays.asList(
                        new Comment(1, 2, "This dish is bursting with flavor! It's a culinary delight."),
                        new Comment(1, 3, "The presentation of this food is simply stunning."),
                        new Comment(1, 4, "I love how this dish combines different textures to create a harmonious blend."),
                        new Comment(2, 1, "Every bite of this food is like a party in my mouth."),
                        new Comment(2, 5, "The balance of spices in this dish is perfect. It's not too overpowering but still adds a kick."),
                        new Comment(3, 2, "The aroma of this food is so inviting. It's hard to resist."),
                        new Comment(3, 3, "The ingredients in this dish complement each other beautifully."),
                        new Comment(3, 4, "The chef's attention to detail is evident in every aspect of this meal."),
                        new Comment(3, 5, "This food is a true comfort food. It brings back nostalgic memories."),
                        new Comment(4, 1, "The texture of this food is so velvety and smooth. It's a pleasure to eat."),
                        new Comment(5, 3, "I appreciate the creativity behind this food. It's unlike anything I've tasted before.")
                ));
    }

    private void populateLikes() {
        appDb.likesDao().insertAll(
                Arrays.asList(
                        new UserLikesRecipeCrossRef(1, 2),
                        new UserLikesRecipeCrossRef(1, 3),
                        new UserLikesRecipeCrossRef(1, 4),
                        new UserLikesRecipeCrossRef(2, 1),
                        new UserLikesRecipeCrossRef(2, 5),
                        new UserLikesRecipeCrossRef(3, 2),
                        new UserLikesRecipeCrossRef(3, 3),
                        new UserLikesRecipeCrossRef(3, 4),
                        new UserLikesRecipeCrossRef(3, 5),
                        new UserLikesRecipeCrossRef(4, 1),
                        new UserLikesRecipeCrossRef(5, 3)
                )
        );
    }
}

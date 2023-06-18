package com.example.cookbook;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.cookbook.entity.User;
import com.example.cookbook.models.RecipeWithLikes;
import com.example.cookbook.repository.LikesRepository;
import com.example.cookbook.utils.ImageUtils;
import com.example.cookbook.utils.StateManager;

import java.util.List;
import java.util.Objects;

//custom adapter for mascot images
public class RecipeArrayAdapter extends BaseAdapter {
    private final Context context;
    // Keep all Images in array
    private final List<RecipeWithLikes> recipes;

    // Constructor
    public RecipeArrayAdapter(Context c, List<RecipeWithLikes> recipes) {
        context = c;
        this.recipes = recipes;
    }

    public int getCount() {
        return recipes.size();
    }

    public RecipeWithLikes getItem(int position) {
        return recipes.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.card_layout, parent, false);
        }

        RecipeWithLikes item = getItem(position);
        ImageButton likeBtn = convertView.findViewById(R.id.likeButton);

        User loggedInUser = StateManager.getLoggedInUser().getValue();

        if (loggedInUser != null) {
            likeBtn.setVisibility(View.VISIBLE);
            boolean likedByLoggedInUser = item.likes.stream().anyMatch(like -> Objects.equals(like.username, loggedInUser.username));

            if (likedByLoggedInUser) {
                likeBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.like_filled_24));
                likeBtn.setTag(R.drawable.like_filled_24);
            } else {
                likeBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.like_empty_24));
                likeBtn.setTag(R.drawable.like_empty_24);
            }
        } else {
            likeBtn.setVisibility(View.INVISIBLE);
        }

        View.OnClickListener likeClickListener = v -> {
            if (v.getTag().equals(R.drawable.like_empty_24)) {
                AsyncTask.execute(() ->
                        new LikesRepository(context.getApplicationContext()).like(
                                StateManager.getLoggedInUser().getValue().id,
                                item.recipe.id
                        ));

            } else {
                AsyncTask.execute(() ->
                        new LikesRepository(context.getApplicationContext()).unlike(
                                StateManager.getLoggedInUser().getValue().id,
                                item.recipe.id
                        ));
            }
        };
        likeBtn.setOnClickListener(likeClickListener);

        TextView recipeDesc = convertView.findViewById(R.id.recipeDesc);
        recipeDesc.setText(item.recipe.instructions);

        TextView recipeTitle = convertView.findViewById(R.id.recipeTitle);
        recipeTitle.setText(item.recipe.name);

        TextView likeCountTextView = convertView.findViewById(R.id.likeCountText);
        likeCountTextView.setText(String.format(context.getString(R.string.people_like), item.likes.size()));

        ImageView receiptImageView = convertView.findViewById(R.id.recipeImage);

        if (item.recipe.imagePath != null) {
            Drawable image = new BitmapDrawable(ImageUtils.loadFile(item.recipe.imagePath));
            receiptImageView.setImageDrawable(image);
        } else {
            receiptImageView.setImageResource(R.drawable.baseline_image_24);
        }


        setOnRecipeClick(convertView, item);
        return convertView;
    }

    private void setOnRecipeClick(final View view, RecipeWithLikes recipe) {
        view.setOnClickListener(v -> ((MainActivity) context).openRecipe(recipe));
    }
}

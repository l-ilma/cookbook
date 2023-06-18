package com.example.cookbook;

import android.content.Context;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.cookbook.models.Recipe;
import com.example.cookbook.recipe.ManageRecipe;
import com.example.cookbook.recipe.RecipeRepository;

import java.text.ParseException;
import java.util.List;

//custom adapter for mascot images
public class RecipeArrayAdapter extends BaseAdapter {
    Context context;
    // Keep all Images in array
    List<Recipe> recipes;
    boolean useLikeBtn = true;

    // Constructor
    public RecipeArrayAdapter(Context c, List<Recipe> recipes, boolean useLikeButton) {
        context = c;
        this.recipes = recipes;
        useLikeBtn = useLikeButton; // if set to false, uses edit button
    }

    public int getCount() {
        return recipes.size();
    }

    public Recipe getItem(int position) {
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

        ImageButton likeEditBtn = convertView.findViewById(R.id.likeEditButton);
        if (useLikeBtn) {
            setupLikeButton(likeEditBtn, position);
        } else {
            setupEditButton(likeEditBtn, position);
        }

        Recipe item = getItem(position);
        TextView recipeDesc = convertView.findViewById(R.id.recipeDesc);
        recipeDesc.setText(item.instructions);

        TextView recipeTitle = convertView.findViewById(R.id.recipeTitle);
        recipeTitle.setText(item.name);

        TextView likeCountTextView = convertView.findViewById(R.id.likeCountText);
        likeCountTextView.setText(String.format(context.getString(R.string.people_like), item.likes));

        ImageView receiptImageView = convertView.findViewById(R.id.recipeImage);
        receiptImageView.setImageResource(item.image);

        setOnRecipeClick(convertView, item);
        return convertView;
    }

    private void setOnRecipeClick(final View view, Recipe recipe) {
        view.setOnClickListener(v -> ((MainActivity) context).openRecipe(recipe));
    }

    private void setupLikeButton(View likeBtn, int position) {
        Recipe item = getItem(position);
        if (item.liked) {
            likeBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.like_filled_24));
            likeBtn.setTag(R.drawable.like_filled_24);
        } else {
            likeBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.like_empty_24));
            likeBtn.setTag(R.drawable.like_empty_24);
        }

        View.OnClickListener likeClickListener = v -> {
            try {
                if (v.getTag().equals(R.drawable.like_empty_24)) {
                    v.setBackground(ContextCompat.getDrawable(context, R.drawable.like_filled_24));
                    RecipeRepository.getInstance().likeRecipe(item.id);

                } else {
                    v.setBackground(ContextCompat.getDrawable(context, R.drawable.like_empty_24));
                    RecipeRepository.getInstance().unlikeRecipe(item.id);
                }
            } catch (ParseException e) {
                System.out.println(e.getLocalizedMessage());
            }
        };
        likeBtn.setOnClickListener(likeClickListener);
    }

    private void setupEditButton(View editBtn, int position) {
        editBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.baseline_edit_24));

        View.OnClickListener editClickListener = v -> {
            Intent intent = new Intent(context, ManageRecipe.class);
            intent.putExtra("recipe", getItem(position));
            context.startActivity(intent);
        };
        editBtn.setOnClickListener(editClickListener);
    }
}

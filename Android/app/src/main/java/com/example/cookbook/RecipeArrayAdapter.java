package com.example.cookbook;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.cookbook.models.Recipe;
import com.example.cookbook.recipe.RecipeRepository;

import java.util.List;

//custom adapter for mascot images
public class RecipeArrayAdapter extends BaseAdapter {
    private Context context;
    // Keep all Images in array
    private List<Recipe> recipes;

    // Constructor
    public RecipeArrayAdapter(Context c, List<Recipe> recipes) {
        context = c;
        this.recipes = recipes;
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

        ImageButton likeBtn = convertView.findViewById(R.id.likeButton);

        if(getItem(position).liked){
            likeBtn.setBackground(ContextCompat.getDrawable(context,R.drawable.like_filled_24));
        }

        View.OnClickListener likeClickListener = v -> {
            if (v.getBackground().getConstantState().equals(
                    ContextCompat.getDrawable(context, R.drawable.like_empty_24).getConstantState())
            ) {
                v.setBackground(ContextCompat.getDrawable(context, R.drawable.like_filled_24));
                RecipeRepository.getInstance().likeRecipe(getItem(position).id);

            } else {
                v.setBackground(ContextCompat.getDrawable(context, R.drawable.like_empty_24));
                RecipeRepository.getInstance().unlikeRecipe(getItem(position).id);
            }

        };
        likeBtn.setOnClickListener(likeClickListener);

        TextView recipeDesc = convertView.findViewById(R.id.recipeDesc);
        recipeDesc.setText(getItem(position).instructions);

        TextView recipeTitle = convertView.findViewById(R.id.recipeTitle);
        recipeTitle.setText(getItem(position).name);

        TextView likeCountTextView = convertView.findViewById(R.id.likeCountText);
        likeCountTextView.setText(String.format(context.getString(R.string.people_like), 320));

        ImageView receiptImageView = convertView.findViewById(R.id.recipeImage);
        receiptImageView.setImageResource(R.drawable.dish_image);

        setOnRecipeClick(convertView, getItem(position));
        return convertView;
    }

    private void setOnRecipeClick(final View view, Recipe recipe) {
        view.setOnClickListener(v -> ((MainActivity) context).openRecipe(recipe));
    }
}

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

import java.util.List;

//custom adapter for mascot images
public class RecipeArrayAdapter extends BaseAdapter {
    private Context mContext;
    // Keep all Images in array
    private List<Integer> mThumbIds;

    // Constructor
    public RecipeArrayAdapter(Context c, List<Integer> pizzas) {
        mContext = c;
        mThumbIds = pizzas;
    }

    public int getCount() {
        return mThumbIds.size();
    }

    public Integer getItem(int position) {
        return mThumbIds.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.card_layout, parent, false);
        }

        ImageButton likeBtn = convertView.findViewById(R.id.likeButton);
        likeBtn.setOnClickListener(likeClickListener);

        TextView likeCountTextView = convertView.findViewById(R.id.likeCountText);
        likeCountTextView.setText(String.format(mContext.getString(R.string.people_like), 320));

        ImageView receiptImageView = convertView.findViewById(R.id.recipeImage);
        receiptImageView.setImageResource(getItem(position));

        setOnRecipeClick(convertView, position);
        return convertView;
    }

    private View.OnClickListener likeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getBackground().getConstantState().equals(
                    ContextCompat.getDrawable(mContext, R.drawable.like_empty_24).getConstantState())
            ) {
                v.setBackground(ContextCompat.getDrawable(mContext, R.drawable.like_filled_24));

            } else {
                v.setBackground(ContextCompat.getDrawable(mContext, R.drawable.like_empty_24));
            }

        }
    };

    private void setOnRecipeClick(final View view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)mContext).openRecipe(position);
            }
        });
    }
}

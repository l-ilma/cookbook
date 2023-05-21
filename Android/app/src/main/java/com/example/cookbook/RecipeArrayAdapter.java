package com.example.cookbook;
import android.app.ActionBar;
import android.content.Context;

import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.List;

//custom adapter for mascot images
public class RecipeArrayAdapter extends BaseAdapter {
    private Context mContext;

    // Constructor
    public RecipeArrayAdapter(Context c, List<Integer> pizzas) {
        mContext = c;
        mThumbIds = pizzas;
    }

    public int getCount() {
        return mThumbIds.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        CardView cardView = new CardView(mContext);
        final float scale = mContext.getResources().getDisplayMetrics().density;
        cardView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (200 * scale + 0.5f)));

        LinearLayout listEntry = new LinearLayout(mContext);
        listEntry.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        listEntry.setOrientation(LinearLayout.VERTICAL);
        listEntry.setWeightSum(2);

        LinearLayout listEntryUpper = new LinearLayout(mContext);
        LinearLayout.LayoutParams listEntryUpperParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (150 * scale + 0.5f));
        listEntryUpperParams.weight = 1;
        listEntryUpper.setLayoutParams(listEntryUpperParams);
        listEntryUpper.setOrientation(LinearLayout.HORIZONTAL);

        ImageView recipeImage = new ImageView(mContext);
        recipeImage.setLayoutParams(new FrameLayout.LayoutParams((int) (150 * scale + 0.5f), (int) (150 * scale + 0.5f)));
        recipeImage.setId(position);
        recipeImage.setImageResource(mThumbIds.get(position));

        TextView dishDesc = new TextView(mContext);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int) (150 * scale + 0.5f));
        dishDesc.setLayoutParams(nameParams);
        dishDesc.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");

        listEntryUpper.addView(recipeImage);
        listEntryUpper.addView(dishDesc);
        listEntry.addView(listEntryUpper);

        LinearLayout listEntryLower = new LinearLayout(mContext);
        LinearLayout.LayoutParams listEntryLowerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        listEntryLowerParams.gravity = Gravity.CENTER;
        listEntryLowerParams.weight = 1;
        listEntryLower.setLayoutParams(listEntryLowerParams);
        listEntryLower.setGravity(Gravity.CENTER);
        listEntryLower.setOrientation(LinearLayout.HORIZONTAL);

        Button likeButton = new Button(mContext);
        LinearLayout.LayoutParams likeButtonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int) (35 * scale + 0.5f));
        likeButton.setLayoutParams(likeButtonParams);
        likeButton.setText("Like");
        likeButton.setTextColor(mContext.getResources().getColor(R.color.white));
        likeButton.setBackgroundColor(mContext.getResources().getColor(R.color.teal_700));

        Button commentButton = new Button(mContext);
        LinearLayout.LayoutParams commentButtonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int) (35 * scale + 0.5f));
        commentButtonParams.setMarginStart((int) (15 * scale + 0.5f));
        commentButtonParams.gravity = Gravity.CENTER_HORIZONTAL;
        commentButton.setTextColor(mContext.getResources().getColor(R.color.white));
        commentButton.setLayoutParams(commentButtonParams);

        commentButton.setText("Comment");
        commentButton.setBackgroundColor(mContext.getResources().getColor(R.color.teal_700));

        listEntryLower.addView(likeButton);
        listEntryLower.addView(commentButton);

        listEntry.addView(listEntryLower);
        cardView.addView(listEntry);

        likeButton.setGravity(Gravity.CENTER);
        commentButton.setGravity(Gravity.CENTER);

        ((LinearLayout.LayoutParams)listEntryLower.getLayoutParams()).gravity = Gravity.CENTER;
        ((LinearLayout.LayoutParams)likeButton.getLayoutParams()).gravity = Gravity.CENTER;
        ((LinearLayout.LayoutParams)commentButton.getLayoutParams()).gravity = Gravity.CENTER;

        return cardView;
    }

    // Keep all Images in array
    private List<Integer> mThumbIds;
}

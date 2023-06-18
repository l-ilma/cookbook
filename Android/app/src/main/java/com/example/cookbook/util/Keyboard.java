package com.example.cookbook.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Keyboard {

    public static void hideKeyboard(Context ctx, View view) {
        InputMethodManager manager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

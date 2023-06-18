package com.example.cookbook.utils;

import android.os.Build;
import android.os.Environment;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class Utils {
    public static void dismissAllDialogs(FragmentManager fm) {
        fm.getFragments().forEach(fragment -> {
            DialogFragment dialogFragment = (DialogFragment) fragment;
            dialogFragment.dismissAllowingStateLoss();
        });
    }

    public static boolean isExternalWriteEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }

        return true;
    }
}

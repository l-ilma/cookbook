package com.example.cookbook;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class PermissionsDialogFragment extends DialogFragment {
    public static String TAG = "Permissions_Dialog";

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_allow_management, container, false);
        Button openSettingsButton = view.findViewById(R.id.button_open_settings);

        openSettingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivity(intent);
        });

        Objects.requireNonNull(getDialog()).setCanceledOnTouchOutside(false);
        return view;
    }

}

package com.example.eventplanner.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.eventplanner.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

// TO DO: create functions for password change and account deletion

public class SettingsActivity extends AppCompatActivity {

    Toolbar toolbar;
    SwitchCompat darkModeSwitch;
    boolean isNightModeOn, isNotifsOn;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView textView, changePass, notifButton, deleteAccount;

    int NOTIFICATION_PERMISSION_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = findViewById(R.id.settingsToolbar);
        textView = findViewById(R.id.logout_button);
        darkModeSwitch = findViewById(R.id.dark_mode_switch);
        changePass = findViewById(R.id.change_password);
        notifButton = findViewById(R.id.notifications);


        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setTitle(R.string.settings);

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });

        sharedPreferences = getSharedPreferences("MODE", MODE_PRIVATE);
        isNightModeOn = sharedPreferences.getBoolean("NightMode", false);

        if(isNightModeOn) {
            darkModeSwitch.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        darkModeSwitch.setOnClickListener(v -> {
            if(isNightModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor = sharedPreferences.edit();
                editor.putBoolean("NightMode", false);
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor = sharedPreferences.edit();
                editor.putBoolean("NightMode", true);
            }
            editor.apply();
        });

        notifButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.POST_NOTIFICATIONS, NOTIFICATION_PERMISSION_CODE);
            }
        });

        changePass.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        });

        textView.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == NOTIFICATION_PERMISSION_CODE) {
            if(grantResults.length > 0 && grantResults [0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(SettingsActivity.this, "Notifications Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(SettingsActivity.this, "Notifications Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
    }

    public void checkPermission(String permission, int requestCode) {
        if(ContextCompat.checkSelfPermission(SettingsActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(SettingsActivity.this, new String[] {permission}, requestCode);
        }
        else {
            Toast.makeText(SettingsActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

}
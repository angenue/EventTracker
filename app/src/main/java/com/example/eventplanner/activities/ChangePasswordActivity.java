package com.example.eventplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.eventplanner.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView textView;

    FirebaseAuth auth;
    FirebaseUser user;
    String userEmail;
    TextInputEditText oldPass, newPass, newPassConfirm;
    TextInputLayout oldPassLayout, newPassLayout, newPassConfirmLayout;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        toolbar = findViewById(R.id.changePassToolbar);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.user_email);
        oldPass = findViewById(R.id.old_password);
        newPass = findViewById(R.id.new_password);
        newPassConfirm = findViewById(R.id.new_password_confirm);
        oldPassLayout = findViewById(R.id.old_password_layout);
        newPassLayout = findViewById(R.id.new_password_layout);
        newPassConfirmLayout = findViewById(R.id.new_password_layout_confirm);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userEmail = user.getEmail();
        textView.setText(userEmail);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        });
    }

    // Inflating the menu items from the menu_items.xml file
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.change_pass_app_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Handling the click events of the menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Switching on the item id of the menu item
        if (item.getItemId() == R.id.update_button) {
            oldPassLayout.setError(null);
            newPassLayout.setError(null);
            newPassConfirmLayout.setError(null);
            progressBar.setVisibility(View.VISIBLE);
            String oldPassword,newPassword, newPasswordConfirm;
            oldPassword = String.valueOf(oldPass.getText());
            newPassword = String.valueOf(newPass.getText());
            newPasswordConfirm = String.valueOf(newPassConfirm.getText());

            if (TextUtils.isEmpty(oldPassword)) {
                oldPassLayout.setError("Empty Password Field is Not Allowed");
                //Toast.makeText(Register.this, "Enter email", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return false;
            }
            if (TextUtils.isEmpty(newPassword)) {
                newPassLayout.setError("Empty Password Field is Not Allowed");
                progressBar.setVisibility(View.GONE);
                return false;
            }
            if(TextUtils.isEmpty(newPasswordConfirm)) {
                newPassConfirmLayout.setError("Empty Password Field is Not Allowed");
                progressBar.setVisibility(View.GONE);
                return false;
            }
            if(!newPasswordConfirm.equals(newPassword)) {
                newPassLayout.setError("Password do not Match");
                newPassConfirmLayout.setError("Passwords do not Match");
                progressBar.setVisibility(View.GONE);
                return false;

            }

            AuthCredential credential = EmailAuthProvider.getCredential(userEmail, oldPassword);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()) {
                    user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                        if(!task1.isSuccessful()){
                            try
                            {
                                throw Objects.requireNonNull(task1.getException());
                            }
                            catch (FirebaseAuthWeakPasswordException weakPassword)
                            {
                                newPassLayout.setError("Password Needs To Be More Than 6 Characters");
                                newPassConfirmLayout.setError("Password Needs To Be More Than 6 Characters");
                            }
                            catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }else {
                            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                            startActivity(intent);
                            finish();

                            Toast.makeText(ChangePasswordActivity.this, "Password Changed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    oldPassLayout.setError("Incorrect Password");
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
}
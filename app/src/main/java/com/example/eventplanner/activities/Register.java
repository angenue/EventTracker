package com.example.eventplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextPasswordVerify;
    TextInputLayout emailLayout, passwordLayout, passwordVerifyLayout;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextPasswordVerify = findViewById(R.id.passwordVerify);
        buttonReg = findViewById(R.id.register_button);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.go_back);
        emailLayout = findViewById(R.id.email_layout);
        passwordLayout = findViewById(R.id.password_layout);
        passwordVerifyLayout = findViewById(R.id.password_verify_layout);

        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailLayout.setError(null);
                passwordLayout.setError(null);
                passwordVerifyLayout.setError(null);
                progressBar.setVisibility(View.VISIBLE);
                String email,password, passwordVerify;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                passwordVerify = String.valueOf(editTextPasswordVerify.getText());

                if (TextUtils.isEmpty(email)) {
                    emailLayout.setError("Empty Email Field is Not Allowed");
                    //Toast.makeText(Register.this, "Enter email", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordLayout.setError("Empty Password Field is Not Allowed");
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if(TextUtils.isEmpty(passwordVerify)) {
                    passwordVerifyLayout.setError("Empty Password Field is Not Allowed");
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if(!passwordVerify.equals(password)) {
                    passwordLayout.setError("Password do not Match");
                    passwordVerifyLayout.setError("Passwords do not Match");
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                    startActivity(intent);
                                    finish();

                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(Register.this, "Account created.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    try
                                    {
                                        throw Objects.requireNonNull(task.getException());
                                    }
                                    catch (FirebaseAuthWeakPasswordException weakPassword)
                                    {
                                        passwordLayout.setError("Password Needs To Be More Than 6 Characters");
                                    }
                                    catch (FirebaseAuthUserCollisionException existEmail)
                                    {
                                        emailLayout.setError("Email Already Taken");

                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }


                                }
                            }
                        });
            }
        });
    }
}
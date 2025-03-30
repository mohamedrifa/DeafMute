package com.example.deafandmute;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class LoginPage extends AppCompatActivity {
    private TextInputEditText passwordEditText, userEditText;
    private TextView reqemail, reqpassword;
    private ImageView visibilityToggle;
    private Button buttonLogin;
    private ProgressBar progress;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        progress = findViewById(R.id.progressBar);
        passwordEditText = findViewById(R.id.password);
        userEditText = findViewById(R.id.username);
        visibilityToggle = findViewById(R.id.visibility_toggle);
        buttonLogin = findViewById(R.id.btnLogin);
        reqemail = findViewById(R.id.emailreq);
        reqpassword = findViewById(R.id.passwordreq);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Set onClickListener for the visibility toggle
        visibilityToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                String user, password;
                user = String.valueOf(userEditText.getText());
                password = String.valueOf(passwordEditText.getText());
                if(TextUtils.isEmpty(user)){
                    reqemail.setVisibility(View.VISIBLE);progress.setVisibility(View.GONE);
                }
                if(TextUtils.isEmpty(password)){
                    reqpassword.setVisibility(View.VISIBLE);progress.setVisibility(View.GONE);
                }
                if(TextUtils.isEmpty(user)||TextUtils.isEmpty(password))return;


                mAuth.signInWithEmailAndPassword(user, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progress.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        String userId = firebaseUser.getUid();
                                        String language = getString(R.string.lang);
                                        String hashedPassword = hashPassword(password);
                                        databaseReference.child(userId).child("password").get()
                                                .addOnCompleteListener(passwordCheckTask -> {
                                                    if (!passwordCheckTask.isSuccessful()) {
                                                        Toast.makeText(LoginPage.this, "Failed to check password", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                    Map<String, Object> updates = new HashMap<>();
                                                    if (!passwordCheckTask.getResult().exists()) {
                                                        updates.put("password", hashedPassword);
                                                    }
                                                    updates.put("language", language);
                                                    databaseReference.child(userId).updateChildren(updates)
                                                            .addOnCompleteListener(updateTask -> {
                                                                if (updateTask.isSuccessful()) {
                                                                    Toast.makeText(LoginPage.this, R.string.logged_in,
                                                                            Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(getApplicationContext(), HomePage.class));
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(LoginPage.this, "Failed to update language",
                                                                            Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                });
                                    }
                                } else {
                                    Toast.makeText(LoginPage.this, R.string.invalid_email_or_password,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
    public void signUp(View v)
    {
        Intent i = new Intent(this, SignUp.class);
        startActivity(i);
    }

    private void togglePasswordVisibility() {
        if (passwordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        passwordEditText.setSelection(passwordEditText.getText().length());
    }

    private String hashPassword(String password) {
        int length = password.length();
        StringBuilder hashedPassword = new StringBuilder();
        for(int i=1; i<length; i+=2){
            hashedPassword.append(password.charAt(i));
        }
        for(int i=0; i<length; i+=2){
            hashedPassword.append(password.charAt(i));
        }
        StringBuilder hashedPassword1 = new StringBuilder();
        for(int i=1; i<length; i+=2){
            hashedPassword1.append(hashedPassword.charAt(i));
        }
        for(int i=0; i<length; i+=2){
            hashedPassword1.append(hashedPassword.charAt(i));
        }
        return hashedPassword1.toString();
    }
}
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

public class LoginPage extends AppCompatActivity {
    private TextInputEditText passwordEditText, userEditText;
    private TextView reqemail, reqpassword;
    private ImageView visibilityToggle;
    private Button buttonLogin;
    private ProgressBar progress;
    private FirebaseAuth mAuth;
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
                                if (task.isSuccessful()){
                                    Toast.makeText(LoginPage.this, "Logged In",
                                            Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getApplicationContext(), HomePage.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Toast.makeText(LoginPage.this, "Invalid Email or Password.",
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

}
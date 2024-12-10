package com.example.deafandmute;

import android.annotation.SuppressLint;
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

public class SignUp extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private TextInputEditText password1, password2, mobileNo, mailId, user;
    private Button buttonSignup;
    private ImageView visibilityToggle1, visibilityToggle2;
    private TextView requser, reqemail, reqmobile, reqpassword, reqconpassword;
    private ProgressBar progress;
    private FirebaseAuth mAuth;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        requser = findViewById(R.id.userreq);
        reqemail = findViewById(R.id.emailreq);
        reqmobile = findViewById(R.id.mobilereq);
        reqpassword = findViewById(R.id.passwordreq);
        reqconpassword = findViewById(R.id.conpasswordreq);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        user = findViewById(R.id.username);
        mailId = findViewById(R.id.email);
        mobileNo = findViewById(R.id.Mobile);
        password1 = findViewById(R.id.password);
        password2 = findViewById(R.id.conpassword);
        buttonSignup = findViewById(R.id.btnSignup);
        progress = findViewById(R.id.progressBar);
        visibilityToggle1 = findViewById(R.id.visibility_toggle1);
        visibilityToggle2 = findViewById(R.id.visibility_toggle2);

        visibilityToggle1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility1();
            }
        });
        visibilityToggle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility2();
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                String email, password, confirmPassword, username, mobile;
                username = String.valueOf(user.getText());
                email = String.valueOf(mailId.getText());
                mobile = String.valueOf(mobileNo.getText());
                password = String.valueOf(password1.getText());
                confirmPassword = String.valueOf(password2.getText());
                int flag =1;

                if(TextUtils.isEmpty(username)){
                    requser.setVisibility(View.VISIBLE);progress.setVisibility(View.GONE);flag=0;
                } else{
                    requser.setVisibility(View.INVISIBLE);
                }
                if(!email.contains("@gmail.com")) {
                    if(TextUtils.isEmpty(email))
                        reqemail.setText(R.string.required_field);
                    else
                        reqemail.setText(R.string.valid_email_id);
                    reqemail.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                    flag=0;
                } else{
                    reqemail.setVisibility(View.INVISIBLE);
                }
                if(mobile.length() != 10) {
                    if(TextUtils.isEmpty(mobile))
                        reqmobile.setText(R.string.required_field);
                    else
                        reqmobile.setText(R.string.valid_mobile_no);
                    reqmobile.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);flag=0;
                } else{
                    reqmobile.setVisibility(View.INVISIBLE);
                }
                if (TextUtils.isEmpty(password)) {
                    reqpassword.setText(R.string.required_field);
                    reqpassword.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);flag=0;
                } else if (password.length() < 8) {
                    reqpassword.setText(R.string.password_must_have_at_least_8_characters);
                    reqpassword.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);flag=0;
                } else if (!password.matches(".*[A-Z].*")) {
                    reqpassword.setText(R.string.password_must_contain_an_uppercase_letter);
                    reqpassword.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);flag=0;
                } else if (!password.matches(".*[a-z].*")) {
                    reqpassword.setText(R.string.password_must_contain_a_lowercase_letter);
                    reqpassword.setVisibility(View.VISIBLE);flag=0;
                    progress.setVisibility(View.GONE);
                } else if (!password.matches(".*[0-9].*")) {
                    reqpassword.setText(R.string.password_must_contain_a_number);
                    reqpassword.setVisibility(View.VISIBLE);flag=0;
                    progress.setVisibility(View.GONE);
                } else if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
                    reqpassword.setText(R.string.password_must_contain_a_special_character);
                    reqpassword.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);flag=0;
                } else {
                    reqpassword.setVisibility(View.INVISIBLE);
                    progress.setVisibility(View.VISIBLE);
                }
                if(!password.equals(confirmPassword)){
                    if(TextUtils.isEmpty(confirmPassword))
                        reqconpassword.setText(R.string.required_field);
                    else
                        reqconpassword.setText(R.string.passwords_didn_t_match);
                    reqconpassword.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);flag=0;
                } else{
                    reqconpassword.setVisibility(View.INVISIBLE);
                }

                if(flag==0)
                    return;

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                if (firebaseUser != null) {
                                    String userId = firebaseUser.getUid();
                                    // Save additional data to Firebase Database
                                    User newUser = new User(username, email, mobile);
                                    databaseReference.child(userId).setValue(newUser);
                                }
                                if (task.isSuccessful()) {
                                    progress.setVisibility(View.GONE);
                                    Toast.makeText(SignUp.this, R.string.account_created,
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    progress.setVisibility(View.GONE);
                                    Toast.makeText(SignUp.this, R.string.authentication_failed,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void togglePasswordVisibility1() {
        if (password1.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            password1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            password1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        password1.setSelection(password1.getText().length());
    }
    private void togglePasswordVisibility2() {
        if (password2.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            password2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            password2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        password2.setSelection(password2.getText().length());
    }
}

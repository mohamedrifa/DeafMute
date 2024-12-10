package com.example.deafandmute;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class LanguageSelection extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent i = new Intent(getApplicationContext(), HomePage.class);
            startActivity(i);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_language_selection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void tamil(View v)
    {
        String lang = "ta";
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        config.locale = locale; // For older APIs
        resources.updateConfiguration(config, displayMetrics);
        // Restart MainActivity to apply changes
        Intent i = new Intent(LanguageSelection.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
    public void english(View v)
    {
        String lang = "en";
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        config.locale = locale; // For older APIs
        resources.updateConfiguration(config, displayMetrics);
        // Restart MainActivity to apply changes
        Intent i = new Intent(LanguageSelection.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
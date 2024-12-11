package com.example.deafandmute;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class HomePage extends AppCompatActivity {
    FirebaseAuth mAuth;DatabaseReference databaseReference;
    RelativeLayout HomePage, FavouritePage, GamesPage, CommunityPage, ProfilePage;
    ImageView Icon1, Icon2, Icon3, Icon4, Icon5;
    FrameLayout fragmentContainer;
    TextView username;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        fragmentContainer = findViewById(R.id.fragment_container);

        HomePage = findViewById(R.id.homeRoute);
        FavouritePage = findViewById(R.id.favouriteRoute);
        GamesPage = findViewById(R.id.gamesRoute);
        CommunityPage = findViewById(R.id.communityRoute);
        ProfilePage = findViewById(R.id.profileRoute);

        Icon1 = findViewById(R.id.icon1);
        Icon2 = findViewById(R.id.icon2);
        Icon3 = findViewById(R.id.icon3);
        Icon4 = findViewById(R.id.icon4);
        Icon5 = findViewById(R.id.icon5);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        username = findViewById(R.id.user);
        user = mAuth.getCurrentUser();

        if(user==null)
        {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }
        else {
            String userId = user.getUid();
            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userData = snapshot.getValue(User.class);
                    if (userData != null) {
                        String currentLang = getString(R.string.lang); // Retrieve the current language string
                        if (!userData.language.equals(currentLang)) {
                            setLanguage(userData.language); // Dynamically set the language
                        }
                        username.setText(getString(R.string.hello) + ", " + userData.username + " !");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(HomePage.this, R.string.failed_to_load_user_data, Toast.LENGTH_SHORT).show();
                }
            });
        }


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new Home())
                .commit();


        HomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Icon1.setVisibility(View.VISIBLE);
                Icon2.setVisibility(View.GONE);
                Icon3.setVisibility(View.GONE);
                Icon4.setVisibility(View.GONE);
                Icon5.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Home())
                        .commit();
            }
        });
        FavouritePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Icon1.setVisibility(View.GONE);
                Icon2.setVisibility(View.VISIBLE);
                Icon3.setVisibility(View.GONE);
                Icon4.setVisibility(View.GONE);
                Icon5.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Favourite())
                        .commit();
            }
        });
        GamesPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Icon1.setVisibility(View.GONE);
                Icon2.setVisibility(View.GONE);
                Icon3.setVisibility(View.VISIBLE);
                Icon4.setVisibility(View.GONE);
                Icon5.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Games())
                        .commit();
            }
        });
        CommunityPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Icon1.setVisibility(View.GONE);
                Icon2.setVisibility(View.GONE);
                Icon3.setVisibility(View.GONE);
                Icon4.setVisibility(View.VISIBLE);
                Icon5.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Community())
                        .commit();
            }
        });
        ProfilePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Icon1.setVisibility(View.GONE);
                Icon2.setVisibility(View.GONE);
                Icon3.setVisibility(View.GONE);
                Icon4.setVisibility(View.GONE);
                Icon5.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Profile())
                        .commit();
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    public void onBackPressed() {
        if (!(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof Home)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new Home())
                    .commit();
            Icon1.setVisibility(View.VISIBLE);
            Icon2.setVisibility(View.GONE);
            Icon3.setVisibility(View.GONE);
            Icon4.setVisibility(View.GONE);
            Icon5.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    private void setLanguage(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        config.setLocale(locale); // For newer APIs
        resources.updateConfiguration(config, displayMetrics);

        recreate(); // Reloads the current activity to apply language changes
    }

}
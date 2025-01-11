package com.example.deafandmute;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class HomePage extends AppCompatActivity {
    CardView profileLess, cardProfile;
    ImageView profile;
    TextView textProfile, userProfile;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    RelativeLayout HomePage, FavouritePage, GamesPage, CommunityPage, ProfilePage, profileImg, Main;
    LinearLayout TaskBar, LayoutProfile;
    ImageView Icon1, Icon2, Icon3, Icon4, Icon5, profShare, profEdit, profMenu;
    FrameLayout fragmentContainer;
    TextView username;
    FirebaseUser user;

    @SuppressLint("MissingInflatedId")
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
        TaskBar = findViewById(R.id.taskbar);
        cardProfile = findViewById(R.id.profileCard);
        profileImg = findViewById(R.id.photo);
        Main = findViewById(R.id.main);
        userProfile = findViewById(R.id.profileUser);

        LayoutProfile = findViewById(R.id.profileLayout);
        profShare = findViewById(R.id.profile_share);
        profEdit = findViewById(R.id.profile_edit);
        profMenu = findViewById(R.id.profile_menu);

        Icon1 = findViewById(R.id.icon1);
        Icon2 = findViewById(R.id.icon2);
        Icon3 = findViewById(R.id.icon3);
        Icon4 = findViewById(R.id.icon4);
        Icon5 = findViewById(R.id.icon5);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        username = findViewById(R.id.user);
        user = mAuth.getCurrentUser();

        profileLess = findViewById(R.id.without_profile);
        profile = findViewById(R.id.profileImage);
        textProfile = findViewById(R.id.profile_text);

        final String[] userName = new String[1];

        if(user==null)
        {
            Intent i = new Intent(getApplicationContext(), LanguageSelection.class);
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
                        if(userData.profilePhoto.isEmpty()){
                            char profile = userData.username.charAt(0);
                            profileLess.setVisibility(View.VISIBLE);
                            textProfile.setText(String.valueOf(profile));
                        }else{
                            profile.setVisibility(View.VISIBLE);
                            Glide.with(HomePage.this).load(userData.getprofilePhoto()).into(profile);
                        }
                        userName[0] = userData.username;
                        username.setText(getString(R.string.hello) + ", " + userName[0] + " !");
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
                profileImg.setTranslationX(0f); // Reset X-axis
                profileImg.setTranslationY(0f);
                LayoutProfile.setVisibility(View.GONE);
                TaskBar.setVisibility(View.VISIBLE);
                cardProfile.setVisibility(View.GONE);
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
                profileImg.setTranslationX(0f); // Reset X-axis
                profileImg.setTranslationY(0f);
                LayoutProfile.setVisibility(View.GONE);
                TaskBar.setVisibility(View.VISIBLE);
                cardProfile.setVisibility(View.GONE);
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
                profileImg.setTranslationX(0f); // Reset X-axis
                profileImg.setTranslationY(0f);
                LayoutProfile.setVisibility(View.GONE);
                TaskBar.setVisibility(View.VISIBLE);
                cardProfile.setVisibility(View.GONE);
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
                profileImg.setTranslationX(0f); // Reset X-axis
                profileImg.setTranslationY(0f);
                LayoutProfile.setVisibility(View.GONE);
                TaskBar.setVisibility(View.VISIBLE);
                cardProfile.setVisibility(View.GONE);
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
                int parentWidth = Main.getWidth();
                int centerX = (parentWidth - profileImg.getWidth()) / 2 - 54;
                ObjectAnimator animatorX = ObjectAnimator.ofFloat(profileImg, "translationX", centerX);
                ObjectAnimator animatorY = ObjectAnimator.ofFloat(profileImg, "translationY", 145f);

                // Combine both animations using AnimatorSet
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(animatorX, animatorY); // Play both animations together
                animatorSet.setDuration(500); // Animation duration in milliseconds
                animatorSet.start();

                userProfile.setText(userName[0]);
                TaskBar.setVisibility(View.GONE);
                cardProfile.setVisibility(View.VISIBLE);
                Icon1.setVisibility(View.GONE);
                Icon2.setVisibility(View.GONE);
                Icon3.setVisibility(View.GONE);
                Icon4.setVisibility(View.GONE);
                Icon5.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Profile())
                        .commit();
                LayoutProfile.postDelayed(() -> LayoutProfile.setVisibility(View.VISIBLE), 500);
            }
        });

        profEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new profile_edit())
                        .commit();
            }
        });
        profMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new profile_menu())
                        .commit();
            }
        });
        profShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomePage.this,"Share Button didn't Developed Yet...",
                        Toast.LENGTH_SHORT).show();
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
            profileImg.setTranslationX(0f); // Reset X-axis
            profileImg.setTranslationY(0f);
            LayoutProfile.setVisibility(View.GONE);
            TaskBar.setVisibility(View.VISIBLE);
            cardProfile.setVisibility(View.GONE);
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
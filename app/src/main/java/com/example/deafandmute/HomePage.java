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
import android.util.TypedValue;
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

public class HomePage extends AppCompatActivity implements profile_edit.OnDataPass{
    CardView profileLess, cardProfile;
    ImageView profile;
    TextView textProfile, userProfile;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    RelativeLayout HomePage, FavouritePage, GamesPage, CommunityPage, ProfilePage, profileImg, Main, Logout;
    LinearLayout TaskBar, LayoutProfile, buttonTamil, buttonEnglish, menuProfile, TextBars;
    ImageView Icon1, Icon2, Icon3, Icon4, Icon5, profShare, profEdit, profMenu;
    FrameLayout fragmentContainer;
    TextView username, textTamil, textEnglish;
    FirebaseUser user;

    @Override
    public void onDataPass(String data) {
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
                    username.setText(getString(R.string.hello) + ", " + userData.username + " !");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomePage.this, R.string.failed_to_load_user_data, Toast.LENGTH_SHORT).show();
            }
        });
    }
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
        Logout = findViewById(R.id.logout);

        LayoutProfile = findViewById(R.id.profileLayout);
        profShare = findViewById(R.id.profile_share);
        profEdit = findViewById(R.id.profile_edit);
        profMenu = findViewById(R.id.profile_menu);

        textTamil = findViewById(R.id.tamilText);
        textEnglish = findViewById(R.id.englishText);
        buttonEnglish = findViewById(R.id.englishBox);
        buttonTamil = findViewById(R.id.tamilBox);
        menuProfile = findViewById(R.id.profileMenu);
        TextBars = findViewById(R.id.textBars);

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
            if (getString(R.string.lang).equals("ta")) {
                buttonEnglish.setBackgroundColor(Color.parseColor("#FFFFFF"));
                textEnglish.setTextColor(Color.parseColor("#3B3B3B"));
                buttonTamil.setBackgroundColor(Color.parseColor("#6AAD2B"));
                textTamil.setTextColor(Color.parseColor("#FFFFFF"));
            }
            else {
                buttonEnglish.setBackgroundColor(Color.parseColor("#6AAD2B"));
                textEnglish.setTextColor(Color.parseColor("#FFFFFF"));
                buttonTamil.setBackgroundColor(Color.parseColor("#FFFFFF"));
                textTamil.setTextColor(Color.parseColor("#3B3B3B"));
            }
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

        final int[] containerId = new int[1];
        HomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuProfile.setVisibility(View.GONE);
                TextBars.setVisibility(View.VISIBLE);
                containerId[0] = 0;
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
                menuProfile.setVisibility(View.GONE);
                TextBars.setVisibility(View.VISIBLE);
                containerId[0] = 0;
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
                menuProfile.setVisibility(View.GONE);
                TextBars.setVisibility(View.VISIBLE);
                containerId[0] = 0;
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
                menuProfile.setVisibility(View.GONE);
                TextBars.setVisibility(View.VISIBLE);
                containerId[0] = 0;
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
                menuProfile.setVisibility(View.GONE);
                TextBars.setVisibility(View.VISIBLE);
                if(containerId[0] == 2131296489){
                    return;
                }
                containerId[0] = findViewById(R.id.fragment_container).getId();
                float screenWidth = Main.getWidth();
                float imageWidth = profileImg.getWidth();
                float initialX = profileImg.getX();
                float centerX = (screenWidth / 2) - (imageWidth / 2);
                float translationX = centerX - initialX;

                float initialY = profileImg.getY();
                float targetY = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 125, getResources().getDisplayMetrics()
                );
                float translationY = targetY - initialY;

                ObjectAnimator animatorX = ObjectAnimator.ofFloat(profileImg, "translationX", translationX);
                ObjectAnimator animatorY = ObjectAnimator.ofFloat(profileImg, "translationY", translationY);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(animatorX, animatorY);
                animatorSet.setDuration(500);
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
                containerId[0] = 0;
                profileImg.setTranslationX(0f); // Reset X-axis
                profileImg.setTranslationY(0f);
                TextBars.setVisibility(View.GONE);
                LayoutProfile.setVisibility(View.GONE);
                TaskBar.setVisibility(View.VISIBLE);
                cardProfile.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new profile_edit())
                        .commit();
            }
        });
        profMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuProfile.setVisibility(View.VISIBLE);
            }
        });
        profShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomePage.this,"Share Button didn't Developed Yet...",
                        Toast.LENGTH_SHORT).show();
            }
        });

        buttonTamil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonEnglish.setBackgroundColor(Color.parseColor("#FFFFFF"));
                textEnglish.setTextColor(Color.parseColor("#3B3B3B"));
                buttonTamil.setBackgroundColor(Color.parseColor("#6AAD2B"));
                textTamil.setTextColor(Color.parseColor("#FFFFFF"));
                setLocale("ta");
            }
        });
        buttonEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonEnglish.setBackgroundColor(Color.parseColor("#6AAD2B"));
                textEnglish.setTextColor(Color.parseColor("#FFFFFF"));
                buttonTamil.setBackgroundColor(Color.parseColor("#FFFFFF"));
                textTamil.setTextColor(Color.parseColor("#3B3B3B"));
                setLocale("en");
            }
        });

        Logout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent i = new Intent(this, LanguageSelection.class);
            startActivity(i);
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

    private void setLocale(String language) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            // Update the language preference for the user in Firebase Realtime Database
            databaseReference.child(userId).child("language").setValue(language)
                    .addOnSuccessListener(aVoid -> {
                        // Restart the LanguageSelection activity with updated locale
                        Intent intent = new Intent(this, LanguageSelection.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        // Show an error message if updating fails
                        Toast.makeText(this, R.string.failed_to_update_language, Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, R.string.user_not_logged_in, Toast.LENGTH_SHORT).show();
        }
    }

}
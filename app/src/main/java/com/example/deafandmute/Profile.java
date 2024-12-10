package com.example.deafandmute;

import android.content.Intent;
import android.content.res.Resources;
import android.media.VolumeShaper;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class Profile extends Fragment {
    private Button englishButton, tamilButton;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private Button logoutbtn;

    public Profile() {
        // Required empty public constructor
    }

    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the logout button
        logoutbtn = view.findViewById(R.id.logout);
        englishButton = view.findViewById(R.id.englishButton);
        tamilButton = view.findViewById(R.id.tamilButton);

        englishButton.setOnClickListener(v -> setLocale("en"));
        tamilButton.setOnClickListener(v -> setLocale("ta"));
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out from Firebase Authentication
                FirebaseAuth.getInstance().signOut();
                // Redirect to MainActivity
                Intent intent = new Intent(requireContext(), LanguageSelection.class);
                startActivity(intent);
                // Close the current activity hosting the fragment
                requireActivity().finish();
            }
        });
    }
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = getResources();
        android.content.res.Configuration config = resources.getConfiguration(); // Correct configuration class
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        config.setLocale(locale); // Use setLocale for modern API levels
        resources.updateConfiguration(config, displayMetrics);
        // Restart the activity hosting the fragment to apply changes
        Intent intent = new Intent(requireContext(), LanguageSelection.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish(); // Properly finish the hosting activity
    }
}

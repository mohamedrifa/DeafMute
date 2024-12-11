package com.example.deafandmute;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends Fragment {
    private Button englishButton, tamilButton;
    private Button logoutbtn;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    public Profile() {
        // Required empty public constructor
    }

    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth
        databaseReference = FirebaseDatabase.getInstance().getReference("Users"); // Initialize DatabaseReference
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        logoutbtn = view.findViewById(R.id.logout);
        englishButton = view.findViewById(R.id.englishButton);
        tamilButton = view.findViewById(R.id.tamilButton);

        // Set up listeners for language buttons
        englishButton.setOnClickListener(v -> setLocale("en"));
        tamilButton.setOnClickListener(v -> setLocale("ta"));

        // Set up listener for logout button
        logoutbtn.setOnClickListener(v -> {
            // Sign out from Firebase Authentication
            mAuth.signOut();
            // Redirect to LanguageSelection activity
            Intent intent = new Intent(requireContext(), LanguageSelection.class);
            startActivity(intent);
            // Close the current activity hosting the fragment
            requireActivity().finish();
        });
    }

    private void setLocale(String language) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            // Update the language preference for the user in Firebase Realtime Database
            databaseReference.child(userId).child("language").setValue(language)
                    .addOnSuccessListener(aVoid -> {
                        // Restart the LanguageSelection activity with updated locale
                        Intent intent = new Intent(requireContext(), LanguageSelection.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    })
                    .addOnFailureListener(e -> {
                        // Show an error message if updating fails
                        Toast.makeText(requireContext(), R.string.failed_to_update_language, Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(requireContext(), R.string.user_not_logged_in, Toast.LENGTH_SHORT).show();
        }
    }
}

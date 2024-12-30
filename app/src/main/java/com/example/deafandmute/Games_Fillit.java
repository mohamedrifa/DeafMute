package com.example.deafandmute;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Games_Fillit extends Fragment {
    ImageView back, imgShow;
    TextInputEditText checktext;
    Button submit;
    DatabaseReference databaseReference;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Games_Fillit() {
        // Required empty public constructor
    }
    public static Games_Fillit newInstance(String param1, String param2) {
        Games_Fillit fragment = new Games_Fillit();
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games__fillit, container, false);
        String language = getString(R.string.lang);
        back = view.findViewById(R.id.backtogame);
        imgShow = view.findViewById(R.id.img);
        checktext = view.findViewById(R.id.textIn);
        submit = view.findViewById(R.id.Submit);

        databaseReference = FirebaseDatabase.getInstance().getReference("Games").child("assets");
        Random random = new Random();
        final int[] number = new int[1];

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long assetCount = dataSnapshot.getChildrenCount();
                number[0] = random.nextInt((int) assetCount) + 1;
                // Load Image
                databaseReference.child(String.valueOf(number[0]))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String imgUrl = snapshot.child("img").getValue(String.class);
                                if (imgUrl != null) {
                                    Glide.with(Games_Fillit.this).load(imgUrl).into(imgShow);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable word2 = checktext.getText();
                databaseReference = FirebaseDatabase.getInstance().getReference("Games").child("assets").child(String.valueOf(number[0]));
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String word1 = snapshot.child(language).getValue(String.class);
                        if (word1 != null && word1.equalsIgnoreCase(word2.toString())) {
                            // Correct answer
                            Bundle bundle = new Bundle();
                            bundle.putString("key", "fillit"); // Replace "fillit" with the actual value
                            Games_matchNext nextFragment = new Games_matchNext();
                            nextFragment.setArguments(bundle);
                            requireActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, nextFragment)
                                    .commit();
                        } else {
                            // Incorrect answer
                            Vibrator vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
                            if (vibrator != null) { // Always check for null to avoid crashes
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    // For API 26 and above
                                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                                } else {
                                    // For older devices
                                    vibrator.vibrate(500); // Vibrate for 500 milliseconds
                                }
                            }
                            Toast.makeText(getActivity(), R.string.wrong_answer, Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, new Games_Fillit()) // Replace with a new instance
                                    .commit();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Games())
                        .commit();
            }
        });
        return view;
    }
}
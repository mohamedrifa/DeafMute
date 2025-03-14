package com.example.deafandmute;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Games_Match extends Fragment {
    TextView showText;
    TextView s1, s2, s3;
    ImageView im1, im2, im3, im4, back, im1_ch, im2_ch, im3_ch, im4_ch;
    Button submit;
    private DatabaseReference databaseReference;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public Games_Match() {
        // Required empty public constructor
    }

    public static Games_Match newInstance(String param1, String param2) {
        Games_Match fragment = new Games_Match();
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
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games__match, container, false);
        String language = getString(R.string.lang); // Correctly fetch the language resource.

        // Initialize views
        back = view.findViewById(R.id.backtogame);
        showText = view.findViewById(R.id.showcasetext);
        submit = view.findViewById(R.id.Submit);
        im1 = view.findViewById(R.id.img1);
        im2 = view.findViewById(R.id.img2);
        im3 = view.findViewById(R.id.img3);
        im4 = view.findViewById(R.id.img4);

        im1_ch = view.findViewById(R.id.img1_ch);
        im2_ch = view.findViewById(R.id.img2_ch);
        im3_ch = view.findViewById(R.id.img3_ch);
        im4_ch = view.findViewById(R.id.img4_ch);

        // Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Games").child("assets");

        // Generate random asset numbers
        Random random = new Random();
        List<Integer> assetNumbers = new ArrayList<>();
        final Integer[][] arr1 = {new Integer[4]};
        final Integer[][] arr2 = {new Integer[4]};
        Integer[][] result = {new Integer[4]};

        final int[] count = {0};

        if(language.equals("ta"))
            showText.setTextSize(15);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long assetCount = dataSnapshot.getChildrenCount();
                // Generate 4 unique random numbers
                while (assetNumbers.size() < 4) {
                    int randomNumber = random.nextInt((int) assetCount) + 1;
                    if (!assetNumbers.contains(randomNumber))
                        assetNumbers.add(randomNumber);
                }
                // Shuffle and assign arrays
                Collections.shuffle(assetNumbers);
                arr1[0] = assetNumbers.toArray(new Integer[0]);
                Collections.shuffle(assetNumbers);
                arr2[0] = assetNumbers.toArray(new Integer[0]);
                // Ensure correct order for text fetching
                List<String> orderedWords = new ArrayList<>(Collections.nCopies(4, ""));
                for (int i = 0; i < arr1[0].length; i++) {
                    final int index = i; // Preserve index for async call
                    databaseReference.child(String.valueOf(arr1[0][i])).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String word = dataSnapshot.child(language).getValue(String.class);
                            if (word != null) {
                                orderedWords.set(index, word); // Place the word in the correct order
                            }
                            // Display text after all words are fetched
                            if (!orderedWords.contains("")) {
                                showText.setText(String.join(", ", orderedWords));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error if necessary
                        }
                    });
                }
                // Fetch images
                for (int i = 0; i < arr2[0].length; i++) {
                    final int index = i; // For use inside the async callback
                    databaseReference.child(String.valueOf(arr2[0][i])).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String imgUrl = dataSnapshot.child("img").getValue(String.class);
                            if (imgUrl != null) {
                                switch (index) {
                                    case 0: Glide.with(Games_Match.this).load(imgUrl).into(im1); break;
                                    case 1: Glide.with(Games_Match.this).load(imgUrl).into(im2); break;
                                    case 2: Glide.with(Games_Match.this).load(imgUrl).into(im3); break;
                                    case 3: Glide.with(Games_Match.this).load(imgUrl).into(im4); break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error if necessary
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if necessary
            }
        });
        ArrayList<Integer> selected = new ArrayList<>();

        im1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected.contains(1)){
                    switch (count[0] +1){
                        case 1:im1_ch.setImageResource(R.drawable.game_one); im1_ch.setVisibility(View.VISIBLE);break;
                        case 2:im1_ch.setImageResource(R.drawable.game_two); im1_ch.setVisibility(View.VISIBLE);break;
                        case 3:im1_ch.setImageResource(R.drawable.game_three); im1_ch.setVisibility(View.VISIBLE);break;
                        case 4:im1_ch.setImageResource(R.drawable.game_four); im1_ch.setVisibility(View.VISIBLE);break;
                    }
                    result[0][count[0]]=arr2[0][0];
                    count[0]++;
                    selected.add(1);
                }
            }
        });
        im2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected.contains(2)){
                    switch (count[0] +1){
                        case 1:im2_ch.setImageResource(R.drawable.game_one); im2_ch.setVisibility(View.VISIBLE);break;
                        case 2:im2_ch.setImageResource(R.drawable.game_two); im2_ch.setVisibility(View.VISIBLE);break;
                        case 3:im2_ch.setImageResource(R.drawable.game_three); im2_ch.setVisibility(View.VISIBLE);break;
                        case 4:im2_ch.setImageResource(R.drawable.game_four); im2_ch.setVisibility(View.VISIBLE);break;
                    }
                    result[0][count[0]]=arr2[0][1];
                    count[0]++;
                    selected.add(2);
                }
            }
        });
        im3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected.contains(3)){
                    switch (count[0] +1){
                        case 1:im3_ch.setImageResource(R.drawable.game_one); im3_ch.setVisibility(View.VISIBLE);break;
                        case 2:im3_ch.setImageResource(R.drawable.game_two); im3_ch.setVisibility(View.VISIBLE);break;
                        case 3:im3_ch.setImageResource(R.drawable.game_three); im3_ch.setVisibility(View.VISIBLE);break;
                        case 4:im3_ch.setImageResource(R.drawable.game_four); im3_ch.setVisibility(View.VISIBLE);break;
                    }
                    result[0][count[0]]=arr2[0][2];
                    count[0]++;
                    selected.add(3);
                }
            }
        });
        im4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected.contains(4)){
                    switch (count[0] +1){
                        case 1:im4_ch.setImageResource(R.drawable.game_one); im4_ch.setVisibility(View.VISIBLE);break;
                        case 2:im4_ch.setImageResource(R.drawable.game_two); im4_ch.setVisibility(View.VISIBLE);break;
                        case 3:im4_ch.setImageResource(R.drawable.game_three); im4_ch.setVisibility(View.VISIBLE);break;
                        case 4:im4_ch.setImageResource(R.drawable.game_four); im4_ch.setVisibility(View.VISIBLE);break;
                    }
                    result[0][count[0]]=arr2[0][3];
                    count[0]++;
                    selected.add(4);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean areEqual = true; // Flag to track equality
                for (int i = 0; i < 4; i++) {
                    Integer a = arr1[0][i];
                    Integer b = result[0][i];
                    if (!a.equals(b)) {
                        areEqual = false;
                        break;
                    }
                }
                if (areEqual) {
                    Bundle bundle = new Bundle();
                    bundle.putString("key", "match"); // Replace "YourValue" with the actual value you want to pass
                    Games_matchNext nextFragment = new Games_matchNext();
                    nextFragment.setArguments(bundle);
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, nextFragment)
                            .commit();
                } else {
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
                            .replace(R.id.fragment_container, new Games_Match()) // Replace with a new instance
                            .commit();

                }
            }
        });
        back.setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new Games())
                .commit());

        return view;
    }
}

package com.example.deafandmute;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Games_Choose extends Fragment {

    ImageView back, imgShow;
    RadioGroup groupRadio;
    Button submit;
    RadioButton rad1, rad2, rad3, rad4;
    DatabaseReference databaseReference;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Games_Choose() {
        // Required empty public constructor
    }

    public static Games_Choose newInstance(String param1, String param2) {
        Games_Choose fragment = new Games_Choose();
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
        View view = inflater.inflate(R.layout.fragment_games__choose, container, false);

        // Initialize Views
        String language = getString(R.string.lang);
        back = view.findViewById(R.id.backtogame);
        imgShow = view.findViewById(R.id.img);
        groupRadio = view.findViewById(R.id.radioGroup);
        rad1 = view.findViewById(R.id.radio1);
        rad2 = view.findViewById(R.id.radio2);
        rad3 = view.findViewById(R.id.radio3);
        rad4 = view.findViewById(R.id.radio4);
        submit = view.findViewById(R.id.Submit);

        // Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Games").child("assets");

        Random random = new Random();
        Set<Integer> uniqueNumbers = new HashSet<>();
        final int[] randomNumber = new int[1];
        final Integer[][] arr1 = {new Integer[4]};

        // Fetch Data from Firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long assetCount = dataSnapshot.getChildrenCount();

                // Generate 4 unique random numbers
                while (uniqueNumbers.size() < 4) {
                    int number = random.nextInt((int) assetCount) + 1;
                    uniqueNumbers.add(number);
                }

                List<Integer> assetNumbers = new ArrayList<>(uniqueNumbers);
                Collections.shuffle(assetNumbers);
                randomNumber[0] = random.nextInt(4);
                arr1[0] = assetNumbers.toArray(new Integer[0]);

                // Set RadioButton Text
                for (int i = 0; i < arr1[0].length; i++) {
                    final int index = i; // Preserve index for async call
                    databaseReference.child(String.valueOf(arr1[0][i]))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String word = snapshot.child(language).getValue(String.class);
                                    if (word != null) {
                                        switch (index) {
                                            case 0: rad1.setText(word); break;
                                            case 1: rad2.setText(word); break;
                                            case 2: rad3.setText(word); break;
                                            case 3: rad4.setText(word); break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                // Load Image
                databaseReference.child(String.valueOf(arr1[0][randomNumber[0]]))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String imgUrl = snapshot.child("img").getValue(String.class);
                                if (imgUrl != null) {
                                    Glide.with(Games_Choose.this).load(imgUrl).into(imgShow);
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

        // Submit Button Logic
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = groupRadio.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(getContext(), "Please select a choice!", Toast.LENGTH_SHORT).show();
                } else {
                    RadioButton selectedRadioButton = view.findViewById(selectedId);
                    String selectedText = getResources().getResourceEntryName(selectedRadioButton.getId());
                    int choice;

                    switch (selectedText) {
                        case "radio1": choice = 0; break;
                        case "radio2": choice = 1; break;
                        case "radio3": choice = 2; break;
                        case "radio4": choice = 3; break;
                        default: throw new IllegalStateException("Unexpected value: " + selectedText);
                    }
                    if (choice == randomNumber[0]) {
                        Bundle bundle = new Bundle();
                        bundle.putString("key", "choose"); // Replace "YourValue" with the actual value you want to pass
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
                                .replace(R.id.fragment_container, new Games_Choose()) // Replace with a new instance
                                .commit();
                    }
                }
            }
        });

        // Back Button Logic
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

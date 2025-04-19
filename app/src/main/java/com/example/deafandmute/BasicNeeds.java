package com.example.deafandmute;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BasicNeeds extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    HashMap<String, ArrayList<String>> translationKeys = new HashMap<>();
    RecyclerView parentRecyclerView;

    public BasicNeeds() {
        // Required empty public constructor
    }

    public static BasicNeeds newInstance(String param1, String param2) {
        BasicNeeds fragment = new BasicNeeds();
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
        View view = inflater.inflate(R.layout.fragment_basic_needs, container, false);
        parentRecyclerView = view.findViewById(R.id.parentRecyclerView);
        addArrayList();

        ParentAdapter adapter = new ParentAdapter(translationKeys);
        parentRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        parentRecyclerView.setAdapter(adapter);
        return view;
    }



    void addArrayList() {
        translationKeys.put("Travel", new ArrayList<>(Arrays.asList(
                "taxi",
                "busstop",
                "train",
                "fare",
                "airport",
                "correct_route"
        )));
        translationKeys.put("Food", new ArrayList<>(Arrays.asList(
                "hungry",
                "vegetarian",
                "no_spicy",
                "allergy_nuts",
                "water_bottle",
                "restaurant"
        )));
        translationKeys.put("Emergency", new ArrayList<>(Arrays.asList(
                "help",
                "police",
                "lost_wallet",
                "fire",
                "injured",
                "ambulance"
        )));
        translationKeys.put("Direction", new ArrayList<>(Arrays.asList(
                "restroom",
                "place",
                "left_or_right",
                "how_far",
                "walkable"
        )));
        translationKeys.put("Shopping", new ArrayList<>(Arrays.asList(
                "cost",
                "size",
                "cards",
                "billing_counter",
                "help_item"
        )));
    }
}
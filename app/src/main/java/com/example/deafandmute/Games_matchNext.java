package com.example.deafandmute;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class Games_matchNext extends Fragment {

    ImageView back;
    Button submit;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Games_matchNext() {
        // Required empty public constructor
    }
    public static Games_matchNext newInstance(String param1, String param2) {
        Games_matchNext fragment = new Games_matchNext();
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

        View view = inflater.inflate(R.layout.fragment_games_match_next, container, false);
        String language = getString(R.string.lang); // Correctly fetch the language resource.
        // Initialize views
        back = view.findViewById(R.id.backtogame);
        submit = view.findViewById(R.id.Submit);

        submit.setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new Games_Match())
                .commit());

        back.setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new Games())
                .commit());
        return view;
    }
}
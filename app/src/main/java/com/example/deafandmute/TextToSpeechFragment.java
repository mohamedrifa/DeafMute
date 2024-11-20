package com.example.deafandmute;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.speech.tts.TextToSpeech;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TextToSpeechFragment extends Fragment {

    private TextToSpeech textToSpeech;
    private TextInputEditText inputText;
    private ImageButton speakButton;
    private RecyclerView savedTextsRecyclerView;
    private List<String> savedTexts;
    private SavedTextAdapter savedTextAdapter;
    private LinearLayout inputLayout;
    private DatabaseReference database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_to_speech, container, false);

        // Initialize Views
        inputText = view.findViewById(R.id.inputText);
        speakButton = view.findViewById(R.id.speakButton);
        savedTextsRecyclerView = view.findViewById(R.id.savedTextsRecyclerView);
        inputLayout = view.findViewById(R.id.inputLayout);

        savedTexts = new ArrayList<>();
        savedTextAdapter = new SavedTextAdapter(savedTexts, new SavedTextAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String text) {
                speakOut(text);
            }
        });
        savedTextsRecyclerView.setAdapter(savedTextAdapter);
        savedTextsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupKeyboardVisibilityListener(view, inputLayout);

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Save the entered text to Firebase when speak button is clicked
        speakButton.setOnClickListener(v -> {
            String text = inputText.getText().toString();
            if (!text.isEmpty()) {
                database.child("users").child(userId).child("savedTexts").push().setValue(text)
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getContext(), "Failed...", Toast.LENGTH_SHORT).show();
                            }
                        });
                speakOut(text);
                inputText.setText("");
            } else {
                Toast.makeText(getContext(), "Please enter text to speak", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(getContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int langResult = textToSpeech.setLanguage(Locale.US);
                if (langResult == TextToSpeech.LANG_MISSING_DATA | langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(getContext(), "Language not supported or missing data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Initialization failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Load saved texts from Firebase
        loadSavedTexts(userId);

        return view;
    }

    // Method to load saved texts from Firebase
    private void loadSavedTexts(String userId) {
        database.child("users").child(userId).child("savedTexts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                savedTexts.clear(); // Clear the list before adding new texts
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String savedText = snapshot.getValue(String.class);
                    savedTexts.add(savedText); // Add the saved text to the list
                }
                savedTextAdapter.notifyDataSetChanged();
                if (savedTextAdapter.getItemCount() > 0) {
                    savedTextsRecyclerView.scrollToPosition(savedTextAdapter.getItemCount() - 1);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load saved texts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to speak the input text
    private void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }
    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private void setupKeyboardVisibilityListener(View rootView, View parentLayout) {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) { // Keyboard is visible
                parentLayout.setTranslationY(-30); // Directly move up by keypad height
            } else {
                parentLayout.setTranslationY(0); // Reset to default position
            }
        });
    }
}

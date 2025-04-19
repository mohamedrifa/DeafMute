package com.example.deafandmute;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechToText extends Fragment {
    private TextView tvResult;
    private ImageView Mic, MicWorks;
    private SpeechRecognizer speechRecognizer;

    public SpeechToText() {
        // Required empty public constructor
    }

    public static SpeechToText newInstance(String param1, String param2) {
        SpeechToText fragment = new SpeechToText();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_speech_to_text, container, false);

        tvResult = view.findViewById(R.id.tvResult);
        Mic = view.findViewById(R.id.mic);
        MicWorks = view.findViewById(R.id.micworks);
        // Initialize SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());

        // Set up the RecognitionListener
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                tvResult.setText(R.string.listening);
            }
            @Override
            public void onBeginningOfSpeech() {
                tvResult.setText(R.string.listening);
            }
            @Override
            public void onRmsChanged(float rmsdB) {
                // You can show mic level if needed
            }
            @Override
            public void onBufferReceived(byte[] buffer) {
            }
            @Override
            public void onEndOfSpeech() {
                tvResult.setText(R.string.processing);
            }
            @Override
            public void onError(int error) {
                Toast.makeText(getContext(), getString(R.string.error_occurred) + error, Toast.LENGTH_SHORT).show();
                tvResult.setText(R.string.speech_result_will_appear_here);
            }
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    tvResult.setText(matches.get(0));
                } else {
                    tvResult.setText(R.string.no_speech_detected);
                }
            }
            @Override
            public void onPartialResults(Bundle partialResults) {
            }
            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });
        String lang = getString(R.string.lang);

        Mic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // User started pressing the button
                        MicWorks.setVisibility(View.VISIBLE);
                        if(lang.equals("en")){
                            startListeningEn();
                        } else {
                            startListeningTa();
                        }
                        tvResult.setText(R.string.listening);
                        return true;

                    case MotionEvent.ACTION_UP:
                        // User released the button
                        MicWorks.setVisibility(View.INVISIBLE);
                        stopListening();
                        tvResult.setText(R.string.processing);
                        return true;
                }
                return false;
            }
        });

        return view;
    }
    private void stopListening() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
        }
    }
    private void startListeningEn() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 100);
            return;
        }
        // Use Tamil Locale
        Locale selectedLocale = new Locale("en"); // Tamil locale
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLocale);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, selectedLocale);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, true);
        speechRecognizer.startListening(intent);
    }
    private void startListeningTa() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 100);
            return;
        }

        // ✅ Use Tamil Locale
        Locale selectedLocale = new Locale("ta", "IN");

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLocale.toString()); // "ta-IN"
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, selectedLocale.toString());
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, true);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "தமிழில் பேசவும்...");
        speechRecognizer.startListening(intent);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}

package com.example.deafandmute;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder> {
    private List<String> childList;
    private Context context;
    private TextToSpeech textToSpeech;
    public ChildAdapter(Context context, List<String> childList) {
        this.context = context;
        this.childList = childList;
        textToSpeech = new TextToSpeech(context, status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.US);
            }
        });
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_child, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String category = childList.get(position);

        int stringId = context.getResources().getIdentifier(category.toLowerCase(), "string", context.getPackageName());
        String textToSpeak;
        if (stringId != 0) {
            textToSpeak = context.getString(stringId);
            holder.itemTextView.setText("\"" + textToSpeak + "\"");
        } else {
            textToSpeak = category;
            holder.itemTextView.setText("\"" + category + "\"");
        }

        int drawableId = context.getResources().getIdentifier(category.toLowerCase(), "drawable", context.getPackageName());
        Glide.with(context)
                .load(drawableId)
                .into(holder.icon);

        holder.DotLayout.setTag(holder); // set ViewHolder tag to access later

        holder.Card.setOnClickListener(v -> speakOut(textToSpeak, holder));
    }
    @Override
    public int getItemCount() {
        return childList.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextView;
        ImageView icon;
        LinearLayout Card, DotLayout;
        View[] dots;
        private Handler handler = new Handler(Looper.getMainLooper());
        private int currentDot = 0;
        private Runnable dotAnimationRunnable;

        ViewHolder(View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.itemTextView);
            icon = itemView.findViewById(R.id.icon);
            Card = itemView.findViewById(R.id.card);
            DotLayout = itemView.findViewById(R.id.dotLayout);
            dots = new View[]{
                    itemView.findViewById(R.id.dot1),
                    itemView.findViewById(R.id.dot2),
                    itemView.findViewById(R.id.dot3)
            };
        }

        void startDotAnimation() {
            stopDotAnimation();
            dotAnimationRunnable = new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < dots.length; i++) {
                        dots[i].setBackgroundResource(i == currentDot ? R.drawable.dot_active : R.drawable.dot_inactive);
                    }
                    currentDot = (currentDot + 1) % dots.length;
                    handler.postDelayed(this, 500);
                }
            };
            handler.post(dotAnimationRunnable);
        }

        void stopDotAnimation() {
            if (dotAnimationRunnable != null) {
                handler.removeCallbacks(dotAnimationRunnable);
            }
        }
    }

    private void speakOut(String text, ViewHolder holder) {
        if (text == null || text.isEmpty()) {
            Toast.makeText(context, "Please enter text to speak", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isTamil = text.matches(".*[\\u0B80-\\u0BFF].*");
        int langResult;
        if (isTamil) {
            langResult = textToSpeech.setLanguage(new Locale("ta", "IN"));
        } else {
            langResult = textToSpeech.setLanguage(Locale.US);
        }

        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            Intent installIntent = new Intent();
            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            context.startActivity(installIntent);
            return;
        }

        String utteranceId = UUID.randomUUID().toString();

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    holder.Card.setBackgroundResource(R.drawable.playing_basic_needs);
                    holder.icon.setVisibility(View.GONE);
                    holder.DotLayout.setVisibility(View.VISIBLE);
                    holder.startDotAnimation();
                });
            }

            @Override
            public void onDone(String utteranceId) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    holder.stopDotAnimation();
                    holder.Card.setBackgroundResource(R.drawable.course_background);
                    holder.DotLayout.setVisibility(View.GONE);
                    holder.icon.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onError(String utteranceId) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(context, "Error during speech", Toast.LENGTH_SHORT).show()
                );
            }
        });

        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId);
    }
}





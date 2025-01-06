package com.example.deafandmute;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class profile_edit extends Fragment {

    private static final String API_KEY = "3dc87bb28dfe90ea4796500177225b7b";
    private String uploadedImageUrl;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private Button photoChange, nameChange;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    uploadImageToImgBB(imageUri);
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        mAuth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        photoChange = view.findViewById(R.id.profileChange);
        nameChange = view.findViewById(R.id.NameChange);

        photoChange.setOnClickListener(v -> pickImageFromGallery());

        return view;
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void uploadImageToImgBB(Uri imageUri) {
        new Thread(() -> {
            try {
                // Convert the image to Base64
                InputStream inputStream = requireActivity().getContentResolver().openInputStream(imageUri);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                String encodedImage = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);

                // Create a POST request
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("key", API_KEY)
                        .add("image", encodedImage)
                        .build();

                Request request = new Request.Builder()
                        .url("https://api.imgbb.com/1/upload")
                        .post(formBody)
                        .build();

                // Execute the request
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                    uploadedImageUrl = jsonResponse.getAsJsonObject("data").get("url").getAsString();

                    requireActivity().runOnUiThread(() -> {
                        firebaseupdate(uploadedImageUrl);
                        Toast.makeText(requireContext(), "Image Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Upload Failed: " + response.message(), Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "An error occurred while uploading the image", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void firebaseupdate(String imageUrl){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            // Update the language preference for the user in Firebase Realtime Database
            databaseReference.child(userId).child("profilePhoto").setValue(imageUrl)
                    .addOnSuccessListener(aVoid -> {
                        // Restart the LanguageSelection activity with updated locale
                        Intent intent = new Intent(requireContext(), profile_edit.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    })
                    .addOnFailureListener(e -> {
                        // Show an error message if updating fails
                        Toast.makeText(requireContext(), R.string.failed_to_upload_profile, Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(requireContext(), R.string.user_not_logged_in, Toast.LENGTH_SHORT).show();
        }
    }
}

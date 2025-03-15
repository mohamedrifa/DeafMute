package com.example.deafandmute;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private Button submitData;
    private ImageView photoChange, visibilityToggle1;
    private Uri imageUri;
    private TextInputEditText password1, mobileNo, userName;
    private TextView mailId;
    boolean passwordFieldEmpty = true;
    private String oldPassword;
    FirebaseUser user;
    CardView profileLess;
    ImageView profile;
    TextView textProfile;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    profile = requireView().findViewById(R.id.profileImage);
                    profileLess = requireView().findViewById(R.id.without_profile);
                    imageUri = result.getData().getData();
                    profile.setVisibility(View.VISIBLE);
                    profileLess.setVisibility(View.GONE);
                    requireActivity().runOnUiThread(() -> {
                        Glide.with(requireContext())
                                .load(imageUri)
                                .into(profile);
                    });
                }
            });



    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        mAuth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        photoChange = view.findViewById(R.id.profileChange);
        submitData = view.findViewById(R.id.Submit);

        profileLess = view.findViewById(R.id.without_profile);
        profile = view.findViewById(R.id.profileImage);
        textProfile = view.findViewById(R.id.profile_text);


        userName = view.findViewById(R.id.username);
        mailId = view.findViewById(R.id.email);
        mobileNo = view.findViewById(R.id.Mobile);
        password1 = view.findViewById(R.id.password);
        visibilityToggle1 = view.findViewById(R.id.visibility_toggle1);


        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        user = mAuth.getCurrentUser();
        if(user==null)
        {
            Intent i = new Intent(requireActivity(), LanguageSelection.class);
            startActivity(i);
        }
        else {
            String userId = user.getUid();
            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userData = snapshot.getValue(User.class);
                    if (userData != null) {
                        if(userData.profilePhoto.isEmpty()){
                            char profile = userData.username.charAt(0);
                            profileLess.setVisibility(View.VISIBLE);
                            textProfile.setText(String.valueOf(profile));
                        }else{
                            profile.setVisibility(View.VISIBLE);
                            Glide.with(requireActivity()).load(userData.getprofilePhoto()).into(profile);
                        }
                        userName.setText(userData.username);
                        mailId.setText(userData.email);
                        mobileNo.setText(userData.mobile);
                        password1.setText(userData.password);
                        oldPassword = String.valueOf(userData.password);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(requireActivity(), R.string.failed_to_load_user_data, Toast.LENGTH_SHORT).show();
                }
            });
        }

        password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (passwordFieldEmpty && s.length() == 0) {
                    passwordFieldEmpty = false; // User cleared the field
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        visibilityToggle1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility1();
            }
        });
        photoChange.setOnClickListener(v -> pickImageFromGallery());

        submitData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri == null){
                    Toast.makeText(requireContext(), R.string.please_select_a_image, Toast.LENGTH_SHORT).show();
                    return;
                }
                uploadImageToImgBB(imageUri);
            }
        });

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
                        Toast.makeText(requireContext(), R.string.image_uploaded_successfully, Toast.LENGTH_SHORT).show();
                    });
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), getString(R.string.upload_failed) + response.message(), Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), R.string.an_error_occurred_while_uploading_the_image, Toast.LENGTH_SHORT).show()
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

    private void togglePasswordVisibility1() {
        if(password1.length() == 0){
            passwordFieldEmpty = false;
        }
        if(passwordFieldEmpty){
            return;
        }
        if (password1.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            password1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            password1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        password1.setSelection(password1.getText().length());
    }
}

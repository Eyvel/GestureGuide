package com.example.guestureguide;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.android.volley.toolbox.Volley;

import java.io.File;

public class ProfilePictureActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_PERMISSION_CODE = 100;
    private Uri selectedImageUri;
    private ImageView profileImageView;
    private Button uploadImageButton, saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);

        // Initialize UI components
        profileImageView = findViewById(R.id.profile_image_view);
        uploadImageButton = findViewById(R.id.upload_image_button);
        saveButton = findViewById(R.id.save_button);

        // Set up listeners for buttons
        uploadImageButton.setOnClickListener(v -> {
            Log.d("ProfilePictureActivity", "Upload image button clicked.");
            Log.d("ProfilePictureActivity", "Permission check: " + ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE));

            if (checkAndRequestPermissions()) {
                // Permission already granted, open image picker
                Log.d("ProfilePictureActivity", "Permission check: " + ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE));

                openImagePicker();
            }
        });


        saveButton.setOnClickListener(v -> {
            Log.d("ProfilePictureActivity", "Save button clicked.");
            saveProfileImage();
        });
    }

    /**
     * Checks and requests permissions for image access.
     * @return true if permissions are granted, false otherwise
     */
    private boolean checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
                return false;
            }
        }
        return true; // Permissions are granted for older versions
    }


    /**
     * Handles the result of the permission request.
     */
    // Update the onRequestPermissionsResult to handle opening the image picker after permission is granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, now open the image picker
                openImagePicker();
            } else {
                // Permission denied, show toast
                Toast.makeText(this, "Permission denied. Cannot select image.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Opens the image picker to allow the user to select an image.
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result of the image picker (when an image is selected).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            Log.d("ProfilePictureActivity", "Image selected: " + selectedImageUri);
            profileImageView.setImageURI(selectedImageUri); // Display the selected image
        } else {
            Log.e("ProfilePictureActivity", "Failed to select image or no image selected.");
        }
    }

    /**
     * Retrieves the real file path from the URI of the selected image.
     */
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        try (Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            Log.e("ProfilePictureActivity", "Error retrieving real path from URI", e);
        }
        return null;
    }

    /**
     * Saves and uploads the profile image.
     */
    private void saveProfileImage() {
        if (selectedImageUri != null) {
            String filePath = getRealPathFromURI(selectedImageUri);
            if (filePath != null) {
                Log.d("ProfilePictureActivity", "File path: " + filePath);
                File file = new File(filePath);
                if (file.exists()) {
                    Log.d("ProfilePictureActivity", "File exists, uploading...");
                    uploadProfileImage(file);
                } else {
                    Log.e("ProfilePictureActivity", "File not found: " + file.getAbsolutePath());
                    Toast.makeText(this, "File not found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("ProfilePictureActivity", "File path is null.");
                Toast.makeText(this, "Failed to retrieve file path.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("ProfilePictureActivity", "No image selected.");
            Toast.makeText(this, "No image selected. Using default profile picture.", Toast.LENGTH_SHORT).show();
            navigateToNextActivity();
        }
    }

    /**
     * Uploads the profile image to the server.
     */
    private void uploadProfileImage(File file) {
        Log.d("ProfilePictureActivity", "Uploading profile image...");
        String url = "https://gestureguide.com/auth/mobile/upload_profile_pic.php"; // Replace with your actual PHP URL for image upload

        // Create a MultipartRequest to upload the image file
        MultipartRequest request = new MultipartRequest(
                this,  // Pass the Context here
                url,
                file,
                response -> {
                    Log.d("Upload Response", "Server response: " + response);
                    Toast.makeText(this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                    navigateToNextActivity();
                },
                error -> {
                    Log.e("Upload Error", "Error uploading image: " + error.getMessage(), error);
                    Toast.makeText(this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                }
        );

        // Add the request to the request queue
        Volley.newRequestQueue(this).add(request);
    }

    /**
     * Navigates to the next activity (TeacherIDInputActivity).
     */
    private void navigateToNextActivity() {
        Intent intent = new Intent(ProfilePictureActivity.this, TeacherIDInputActivity.class);
        startActivity(intent);
    }
}

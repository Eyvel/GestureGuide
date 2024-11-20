package com.example.guestureguide;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.io.File;

public class ProfilePictureActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_PERMISSION_CODE = 100;
    private Uri selectedImageUri;
    private ImageView profileImageView;
    private Button uploadImageButton, saveButton;
    private File selectedImageFile; // This will hold the image file for uploading
    private String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);

        // Initialize UI components
        profileImageView = findViewById(R.id.profile_image_view);
        uploadImageButton = findViewById(R.id.upload_image_button);
        saveButton = findViewById(R.id.save_button);

        // Set up listeners for buttons
        uploadImageButton.setOnClickListener(v -> checkAndRequestPermission());
        saveButton.setOnClickListener(v -> saveProfileImage());
    }

    /**
     * Checks and requests the READ_EXTERNAL_STORAGE permission.
     */
    private void checkAndRequestPermission() {
        // Check if the permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED)) {

            // Show the permission request dialog if not granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Use READ_MEDIA_IMAGES for Android 13 and above
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_PERMISSION_CODE);
            } else {
                // Use READ_EXTERNAL_STORAGE for below Android 13
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_CODE);
            }
        } else {
            Log.d("ProfilePictureActivity", "Permission already granted.");
            openImagePicker(); // If permission is granted, proceed to image picker
        }
    }

    /**
     * Handles the result of the permission request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open image picker
                Log.d("ProfilePictureActivity", "Permission granted.");
                openImagePicker();
            } else {
                // Permission denied, check if "Don't ask again" was selected
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Log.d("ProfilePictureActivity", "Permission denied, but not permanently.");
                    Toast.makeText(this, "Storage permission is required to select an image.", Toast.LENGTH_LONG).show();
                } else {
                    // Permission permanently denied
                    Log.d("ProfilePictureActivity", "Permission permanently denied. Showing settings message.");
                    Toast.makeText(this, "Permission permanently denied. Please enable it in app settings.", Toast.LENGTH_LONG).show();
                    openAppSettings();
                }
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

            // Check if the file exists
            File file = new File(getRealPathFromURI(selectedImageUri));
            if (file.exists()) {
                // Use Glide to load the selected image into the ImageView
                Glide.with(this)
                        .load(selectedImageUri)
                        .into(profileImageView);

                // Update selectedImageFile with the file for upload
                selectedImageFile = file;
            } else {
                Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("ProfilePictureActivity", "Failed to select image or no image selected.");
        }
    }

    private void saveProfileImage() {
        if (selectedImageFile != null && selectedImageFile.exists()) {
            // Upload the selected image if available
            uploadProfileImage(selectedImageFile);
        } else {
            // No image selected, use the default image URL
            String defaultImageUrl = "https://gestureguide.com/auth/mobile/uploads/profile_images/profile.png";
            Toast.makeText(this, "No image selected. Using default profile image.", Toast.LENGTH_SHORT).show();
            useDefaultProfileImage(defaultImageUrl);
        }
    }

    /**
     * Handles setting the default profile image.
     */
    private void useDefaultProfileImage(String defaultImageUrl) {
        // You can update the user's profile in your backend with the default image URL
        Log.d("ProfilePictureActivity", "Using default profile image: " + defaultImageUrl);

        // Example action: Navigate to the next activity or update locally stored profile data
        navigateToNextActivity();
    }



    /**
     * Converts URI to real path (for file upload).
     */
    private String getRealPathFromURI(Uri uri) {
        String filePath = "";
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return filePath;
    }


    /**
     * Saves the profile image.
     */


    /**
     * Uploads the profile image to the server.
     */
    private void uploadProfileImage(File file) {
        Log.d("ProfilePictureActivity", "Uploading profile image...");
        String url = "https://gestureguide.com/auth/mobile/upload_profile_pic.php";

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppName", MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id", "").trim();
        String studentId = user_id; // Retrieve student ID from SharedPreferences

        // Log the studentId to check if it's correct
        Log.d("ProfilePictureActivity", "Student ID: " + studentId);

        if (studentId.isEmpty()) {
            Log.e("ProfilePictureActivity", "Student ID is missing");
            Toast.makeText(this, "Student ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the MultipartRequest
        MultipartRequest request = new MultipartRequest(
                url,               // URL to the server
                file,              // File object for the selected image
                studentId,         // Student ID
                response -> {
                    // Handle success
                    Toast.makeText(ProfilePictureActivity.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
                    Log.d("ProfilePictureActivity", "Response: " + response);
                    navigateToNextActivity();
                },
                error -> {
                    // Handle error
                    Toast.makeText(ProfilePictureActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                    Log.e("ProfilePictureActivity", "Error: " + error.toString());
                }
        );

        // Set retry policy to handle network timeouts
        int socketTimeout = 30000; // 30 seconds
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        request.setRetryPolicy(retryPolicy);

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(request);
    }




    /**
     * Navigate to the next activity after image upload.
     */
    private void navigateToNextActivity() {
        Intent intent = new Intent(this, TeacherIDInputActivity.class); // Replace with your next activity
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Log permission status
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("PermissionCheck", "Permission granted after opening settings");
        } else {
            Log.d("PermissionCheck", "Permission still denied after opening settings");
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

}


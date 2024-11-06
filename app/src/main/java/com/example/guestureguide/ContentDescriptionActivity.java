package com.example.guestureguide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.source.LoopingMediaSource;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContentDescriptionActivity extends AppCompatActivity {

    private ImageView contentImageView;
    private Button learnButton;
    private ImageButton backContentButton;
    private ImageButton nextContentButton;
    private String categoryId, contentNameString, categoryNameString;
    private TextView lessonProgress, contentName, categoryName;

    private ArrayList<Content> contentList;
    private int currentIndex;
    private boolean isLearned = false; // Track if current content is learned
    private RequestQueue requestQueue; // Volley request queue for saving progress
    private float currentPlaybackSpeed = 1.0f; // Normal speed
    private PlayerView contentVideoView; // Change to PlayerView
    private ExoPlayer player; // ExoPlayer instance
    private Button toggleSpeedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_description);


        contentVideoView = findViewById(R.id.contentVideoView);
        toggleSpeedButton = findViewById(R.id.speedToggleButton);
        toggleSpeedButton.setOnClickListener(v -> togglePlaybackSpeed());

        categoryId = getIntent().getStringExtra("id");
        contentNameString = getIntent().getStringExtra("contentName");
        categoryNameString = getIntent().getStringExtra("category_name");

        // Initialize views
        contentImageView = findViewById(R.id.contentImageView);
        learnButton = findViewById(R.id.learnButton);
        nextContentButton = findViewById(R.id.next_content_button);
        backContentButton = findViewById(R.id.previous_content_button);
        lessonProgress = findViewById(R.id.lessonProgress);
        contentName = findViewById(R.id.contentName);
        categoryName = findViewById(R.id.categoryName);

        contentList = (ArrayList<Content>) getIntent().getSerializableExtra("content_list");
        currentIndex = getIntent().getIntExtra("current_index", 0);

        if (contentList == null || contentList.isEmpty()) {
            Toast.makeText(this, "No content to display", Toast.LENGTH_SHORT).show();
            return;
        }



        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        // Set up back button to return to category
        ImageButton backToContentButton = findViewById(R.id.back_to_content_button);
        backToContentButton.setOnClickListener(v -> navigateBackToCategory());

        loadContent(currentIndex);

        // Next content button
        nextContentButton.setOnClickListener(view -> {
            if (currentIndex < contentList.size() - 1) {
                currentIndex++;
                loadContent(currentIndex);
                lessonProgress.setText((currentIndex + 1) + "/" + contentList.size());
            } else {
                Toast.makeText(ContentDescriptionActivity.this, "No more content available!", Toast.LENGTH_SHORT).show();
                navigateBackToCategory();
            }
        });

        // Previous content button
        backContentButton.setOnClickListener(view -> {
            if (currentIndex > 0) {
                currentIndex--;
                loadContent(currentIndex);
                lessonProgress.setText((currentIndex + 1) + "/" + contentList.size());
            }
        });

        // Learn button click listener
        learnButton.setOnClickListener(view -> {
            if (isLearned) {
                isLearned = false;
                learnButton.setText(R.string.learn);  // Change button text back to "Learn"
                updateProgressOnServer(currentIndex, false);  // Mark as unlearned on the server
            } else {
                isLearned = true;
                learnButton.setText(R.string.learned);  // Change button text to "Learned"
                updateProgressOnServer(currentIndex, true);  // Mark as learned on the server
            }
        });
    }

    // Function to load content based on the current index
    private void loadContent(int index) {
        Content currentContent = contentList.get(index);

        contentName.setText(currentContent.getName());
        categoryName.setText(currentContent.getCategoryName());

        String imageUrl = currentContent.getImageUrl();
        String videoUrl = currentContent.getVideoUrl();
        lessonProgress.setText((currentIndex + 1) + "/" + contentList.size());

        // Load image
        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(contentImageView);
        } else {
            Toast.makeText(this, "Image URL is missing", Toast.LENGTH_SHORT).show();
        }

        // Load video
        if (videoUrl != null) {
            initializePlayer(videoUrl);
        } else {
            Toast.makeText(this, "Video URL is missing", Toast.LENGTH_SHORT).show();
        }

        // Reset learned state for new content

        learnButton.setText(isLearned ? R.string.learned : R.string.learn);
    }



    private float getPlaybackSpeed() {
        SharedPreferences prefs = getSharedPreferences("MyAppName", MODE_PRIVATE); // Use consistent key
        return prefs.getFloat("playback_speed", 1.0f); // Default to normal speed
    }


    private void initializePlayer(String videoUrl) {
        // Check if the player is already created
        if (player == null) {
            // Create a new SimpleExoPlayer instance
            player = new SimpleExoPlayer.Builder(this).build();
            contentVideoView.setPlayer(player);
        }

        // Create a DefaultMediaSourceFactory
        DefaultMediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(this);

        // Create a MediaItem
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));

        // Create a MediaSource using the factory
        MediaSource mediaSource = mediaSourceFactory.createMediaSource(mediaItem);

        // Wrap the MediaSource in a LoopingMediaSource
        LoopingMediaSource loopingSource = new LoopingMediaSource(mediaSource);

        // Set the looping media source to the player
        player.setMediaSource(loopingSource);
        player.prepare();
        player.setPlaybackSpeed(getPlaybackSpeed()); // Set the retrieved playback speed
        player.play();
    }


    // Function to update learning progress on the server
    private void updateProgressOnServer(int contentIndex, boolean learned) {
        Content currentContent = contentList.get(contentIndex);
        String url = "https://192.168.100.72/gesture/save_content_progress.php"; // Your PHP URL here

        // Prepare the POST request with necessary parameters
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("Progress Update", "Response: " + response);
                    Toast.makeText(ContentDescriptionActivity.this, "Progress updated!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("Progress Update", "Error: " + error.getMessage());
                    Toast.makeText(ContentDescriptionActivity.this, "Failed to update progress", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("user_id", "33");  // Replace with actual user ID

                //params.put("content_id", String.valueOf(currentContent.getContentId()));

                params.put("category_name", categoryId);
                params.put("progress", learned ? "1" : "0"); // 1 for learned, 0 for unlearned
                params.put("total_lessons", String.valueOf(contentList.size()));
                return params;
            }
        };

        // Add the request to the queue
        requestQueue.add(stringRequest);
    }

    private void navigateBackToCategory() {
        Intent intent = new Intent(ContentDescriptionActivity.this, ContentActivity.class);
        intent.putExtra("id", categoryId);
        Log.d("CategoryID", "Navigating back with categoryId: " + categoryId);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void togglePlaybackSpeed() {
        if (currentPlaybackSpeed == 1.0f) {
            currentPlaybackSpeed = 0.5f; // Change to half speed
            toggleSpeedButton.setText("0.5x Speed");
        } else {
            currentPlaybackSpeed = 1.0f; // Change to normal speed
            toggleSpeedButton.setText("1.0x Speed");
        }

        // Save the current speed in SharedPreferences
        savePlaybackSpeed(currentPlaybackSpeed);

        // Set the playback speed on the ExoPlayer
        if (player != null) {
            player.setPlaybackSpeed(currentPlaybackSpeed);
        }
    }


    private void savePlaybackSpeed(float speed) {
        SharedPreferences prefs = getSharedPreferences("MyAppName", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("playback_speed", speed);
        editor.apply();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release(); // Release the player when the activity is stopped
            player = null;
        }
    }

}

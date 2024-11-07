package com.example.guestureguide;

import android.content.Context;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.ui.PlayerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContentDescriptionActivity extends AppCompatActivity {

    private Content currentContent;

    private ImageView contentImageView;
    private Button learnButton;
    private ImageButton backContentButton;
    private ImageButton nextContentButton;
    private String categoryId, contentNameString, categoryNameString, contentId;
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
            if (currentContent != null) { // Ensure currentContent is assigned
                isLearned = !isLearned;
                learnButton.setText(isLearned ? R.string.learned : R.string.learn);
                updateProgressOnServer(isLearned ? 1 : 0); // Send status as 1 for learned and 0 for unlearned
                saveLearnedStatus(currentContent.getContentId(), isLearned ? 1 : 0); // Save learned status
            } else {
                Toast.makeText(this, "Content not loaded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to load content based on the current index
    private void loadContent(int index) {
        currentContent = contentList.get(index);

        contentName.setText(currentContent.getName());
        categoryName.setText(currentContent.getCategoryName());

        SharedPreferences prefs = getSharedPreferences("MyAppName", MODE_PRIVATE);
        isLearned = prefs.getInt("learned_" + currentContent.getContentId(), 0) == 1; // 1 means learned
        learnButton.setText(isLearned ? R.string.learned : R.string.learn);


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

        learnButton.setText(isLearned ? R.string.learned : R.string.learn);
    }
    private float getPlaybackSpeed() {
        SharedPreferences prefs = getSharedPreferences("MyAppName", MODE_PRIVATE); // Use consistent key
        return prefs.getFloat("playback_speed", 1.0f); // Default to normal speed
    }

    private void initializePlayer(String videoUrl) {
        if (player == null) {
            player = new SimpleExoPlayer.Builder(this).build();
            contentVideoView.setPlayer(player);

            // Set a listener to loop the video
            player.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if (playbackState == Player.STATE_ENDED) {
                        player.seekTo(0); // Seek to the beginning
                        player.play();    // Play again
                    }
                }
            });
        }
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
        player.setMediaItem(mediaItem);
        player.setPlaybackSpeed(getPlaybackSpeed());
        player.prepare();
        player.play();
    }


    // Function to update learning progress on the server
    private void updateProgressOnServer(int status) {
        Content currentContent = contentList.get(currentIndex);
        String url = "http://192.168.100.72/gesture/save_content_progress.php";

        JSONObject jsonObject = new JSONObject();
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppName", Context.MODE_PRIVATE);
            String userId = sharedPreferences.getString("user_id", "");

            jsonObject.put("user_id", userId); // Replace with actual user ID
            jsonObject.put("content_id", currentContent.getContentId());
            jsonObject.put("category_id", categoryId);
            jsonObject.put("status", status);
            jsonObject.put("total_content", contentList.size());

        } catch (JSONException e) {
            Log.e("Progress Update", "JSON Exception: " + e.getMessage());
            return; // Return early if JSON creation fails
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    Log.d("Progress Update", "Response: " + response.toString());
                    Toast.makeText(ContentDescriptionActivity.this, "Progress updated!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("Progress Update", "Error: " + error.getMessage());
                    Toast.makeText(ContentDescriptionActivity.this, "Failed to update progress", Toast.LENGTH_SHORT).show();
                });

        // Add the request to the queue
        requestQueue.add(jsonObjectRequest);
    }

    private void saveLearnedStatus(String contentId, int status) {
        SharedPreferences prefs = getSharedPreferences("MyAppName", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("learned_" + contentId, status);
        editor.apply();
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
            currentPlaybackSpeed = 0.5f;
            toggleSpeedButton.setText("0.5x Speed");
        } else {
            currentPlaybackSpeed = 1.0f;
            toggleSpeedButton.setText("1.0x Speed");
        }

        savePlaybackSpeed(currentPlaybackSpeed);

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
            player.release();
            player = null;
        }
    }
}

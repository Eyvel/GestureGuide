package com.example.guestureguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private MediaPlayer mediaPlayer;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_description);
        // Initialize the Volley request queue
        requestQueue = Volley.newRequestQueue(this);

            if (requestQueue == null) {

                Log.e("RequestQueueError", "RequestQueue is null!");
        }


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

        sharedPreferences = getSharedPreferences("MyAppName", MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", "");
        if (sharedPreferences != null) {
            Log.d("SharedprefVal",userId);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // your code here
        } else {
            Log.e("SharedPreferencesError", "SharedPreferences is null!");
            // Handle the error gracefully (e.g., initialize SharedPreferences or show a message to the user)
        }


        // Check if the user ID exists before making the network request
        if (!userId.isEmpty()) {
            fetchUserProgress(userId); // Fetch user progress if logged in
        } else {
            Log.e("User Progress", "User not logged in");
            // Optionally, handle the case when user is not logged in
        }

        contentList = (ArrayList<Content>) getIntent().getSerializableExtra("content_list");
        currentIndex = getIntent().getIntExtra("current_index", 0);

        if (contentList == null || contentList.isEmpty()) {
            Toast.makeText(this, "No content to display", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize Volley request queue


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
        // Learn button click listener
                learnButton.setOnClickListener(view -> {
            if (currentContent != null) { // Ensure currentContent is assigned
                // Fetch the current content progress from SharedPreferences or database result
                String contentId = currentContent.getContentId();

                // Check the progress data to find the matching content ID
                try {
                    String progressData = sharedPreferences.getString("user_progress", "[]"); // Retrieve saved progress
                    JSONArray progressArray = new JSONArray(progressData);

                    // Look for the matching content ID in the progress array
                    for (int i = 0; i < progressArray.length(); i++) {
                        JSONObject progressItem = progressArray.getJSONObject(i);
                        if (progressItem.getString("content_id").equals(contentId)) {
                            int status = progressItem.getInt("status");

                            // Update the button text based on the status
                            isLearned = (status == 1);
                            learnButton.setText(isLearned ? R.string.learned : R.string.learn);
                            break;
                        }
                    }

                    // Toggle learned status
                    isLearned = !isLearned;
                    learnButton.setText(isLearned ? R.string.learned : R.string.learn);

                    // Save learned status to the server and locally
                    updateProgressOnServer(isLearned ? 1 : 0);
                    saveLearnedStatus(contentId, isLearned ? 1 : 0);
                    logSharedPreferencesAsJson();

                    // Display a message and optionally play sound for "learned"
                    if (isLearned) {
                        Toast.makeText(this, "\"" + currentContent.getName() + "\" learned!", Toast.LENGTH_SHORT).show();
                        playLearnSound();
                    }

                } catch (JSONException e) {
                    Log.e("LearnButton", "Error parsing progress data: " + e.getMessage());
                    Toast.makeText(this, "Error updating progress", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Content not loaded", Toast.LENGTH_SHORT).show();
            }
        });
        ;
        ;
    }
    private void playLearnSound() {

        if (mediaPlayer != null) {
            mediaPlayer.release(); // Release any existing MediaPlayer instance
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.clap_sound); // Initialize MediaPlayer
        mediaPlayer.setOnCompletionListener(mp -> {
            mp.release(); // Release resources after completion
            mediaPlayer = null; // Set to null to avoid memory leaks
        });
        mediaPlayer.start();
    }



    // Function to load content based on the current index
    // Function to load content based on the current index
    private void loadContent(int index) {
        currentContent = contentList.get(index);

        contentName.setText(currentContent.getName());
        categoryName.setText(currentContent.getCategoryName());

        // Retrieve the learned status from SharedPreferences
        logSharedPreferencesAsJson();
        isLearned = sharedPreferences.getInt("learned_" + currentContent.getContentId(), 0) == 1; // 1 means learned
        learnButton.setText(isLearned ? R.string.learned : R.string.learn);

        // Retrieve content image and video URLs
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
        String url = "https://gestureguide.com/auth/mobile/save_content_progress.php";

        JSONObject jsonObject = new JSONObject();
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppName", Context.MODE_PRIVATE);
            String userId = sharedPreferences.getString("user_id", "");

            jsonObject.put("user_id", userId); // Replace with actual user ID
            jsonObject.put("content_id", currentContent.getContentId());
            jsonObject.put("category_id", categoryId);
            jsonObject.put("status", status);
            jsonObject.put("total_content", contentList.size());


            Log.d("Progress", userId);
            Log.d("Content", currentContent.getContentId());
            Log.d("CategoryId", categoryId);
            Log.d("stats",String.valueOf(status));
            Log.d("total", String.valueOf(contentList.size()));


        } catch (JSONException e) {
            Log.e("Progress Update", "JSON Exception: " + e.getMessage());
            return; // Return early if JSON creation fails
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    Log.d("Progress Update", "Response: " + response.toString());

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
    private void fetchUserProgress(String userId) {
        Log.d("FetchUserProgress", "User ID: " + userId);

        String url_progress = "https://gestureguide.com/auth/mobile/get_user_progress.php?user_id=" + userId;

        StringRequest progressRequest = new StringRequest(Request.Method.GET, url_progress,
                response -> {
                    Log.d("ProgressResponse", response);  // Log response for debugging
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");

                        if (success) {
                            JSONArray progressArray = jsonObject.optJSONArray("data");

                            if (progressArray != null) {
                                // Save progress in SharedPreferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                for (int i = 0; i < progressArray.length(); i++) {
                                    JSONObject progressItem = progressArray.getJSONObject(i);
                                    String contentId = progressItem.getString("content_id");
                                    int status = progressItem.getInt("status");

                                    // Save learned status for each content
                                    editor.putInt("learned_" + contentId, status);
                                    Log.d("Progress", "Saved learned_" + contentId + " with status: " + status);
                                }
                                editor.apply();

                                // Optionally refresh UI with the updated progress
                                loadContent(currentIndex);
                            }
                        } else {
                            String message = jsonObject.getString("message");
                            Log.e("Progress", "Error: " + message);
                        }
                    } catch (Exception e) {
                        Log.e("Progress", "Error parsing progress: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("Progress", "Error fetching progress: " + error.toString());
                });

        requestQueue.add(progressRequest);
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
            Toast.makeText(ContentDescriptionActivity.this,"0.5x Speed",Toast.LENGTH_SHORT).show();
        } else {
            currentPlaybackSpeed = 1.0f;
            toggleSpeedButton.setText("1.0x Speed");
            Toast.makeText(ContentDescriptionActivity.this,"1.0x Speed",Toast.LENGTH_SHORT).show();

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
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Release the MediaPlayer to stop the sound
            mediaPlayer = null;
        }
    }
    private void logSharedPreferencesAsJson() {
        SharedPreferences prefs = getSharedPreferences("MyAppName", MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();

        // Log all SharedPreferences entries
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("SharedPrefAll", "Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }

        JSONObject jsonObject = new JSONObject();

        // Filter keys starting with "learned_"
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith("learned_")) {
                try {
                    jsonObject.put(entry.getKey(), entry.getValue());
                } catch (JSONException e) {
                    Log.e("SharedPrefJson", "Error creating JSON: " + e.getMessage());
                }
            }
        }

        // Log the filtered JSON object
        Log.d("SharedPrefProgressJson", jsonObject.toString());
    }


}

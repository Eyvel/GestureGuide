package com.example.guestureguide;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContentDescriptionActivity extends AppCompatActivity {

    private ImageView contentImageView;
    private VideoView contentVideoView;
    private Button learnButton;
    private ImageButton backContentButton;
    private ImageButton nextContentButton;
    private String categoryId, contentNameString;
    private TextView lessonProgress, contentName;

    private ArrayList<Content> contentList;
    private int currentIndex;
    private boolean isLearned = false; // Track if current content is learned
    private RequestQueue requestQueue; // Volley request queue for saving progress

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_description);

        categoryId = getIntent().getStringExtra("id");
        contentNameString = getIntent().getStringExtra("contentName");

        // Initialize views
        contentImageView = findViewById(R.id.contentImageView);
        contentVideoView = findViewById(R.id.contentVideoView);
        learnButton = findViewById(R.id.learnButton);
        nextContentButton = findViewById(R.id.next_content_button);
        backContentButton = findViewById(R.id.previous_content_button);
        lessonProgress = findViewById(R.id.lessonProgress);
        contentName = findViewById(R.id.contentName);

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

        updateBackButtonVisibility();
        loadContent(currentIndex);

        // Next content button
        nextContentButton.setOnClickListener(view -> {
            if (currentIndex < contentList.size() - 1) {
                currentIndex++;
                loadContent(currentIndex);
                updateBackButtonVisibility();
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
                updateBackButtonVisibility();
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
            contentVideoView.setVideoURI(Uri.parse(videoUrl));
            contentVideoView.start();
        } else {
            Toast.makeText(this, "Video URL is missing", Toast.LENGTH_SHORT).show();
        }

        // Reset learned state for new content
        isLearned = currentContent.isLearned();
        learnButton.setText(isLearned ? R.string.learned : R.string.learn);
    }

    // Function to update learning progress on the server
    private void updateProgressOnServer(int contentIndex, boolean learned) {
        Content currentContent = contentList.get(contentIndex);
        String url = "https://192.168.8.20/gesture/save_content_progress.php"; // Your PHP URL here

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
                params.put("content_id", String.valueOf(currentContent.getContentId()));
                params.put("category_name", categoryId);
                params.put("progress", learned ? "1" : "0"); // 1 for learned, 0 for unlearned
                params.put("total_lessons", String.valueOf(contentList.size()));
                return params;
            }
        };

        // Add the request to the queue
        requestQueue.add(stringRequest);
    }

    private void updateBackButtonVisibility() {
        backContentButton.setVisibility(currentIndex == 0 ? View.GONE : View.VISIBLE);
    }

    private void navigateBackToCategory() {
        Intent intent = new Intent(ContentDescriptionActivity.this, ContentActivity.class);
        intent.putExtra("id", categoryId);
        Log.d("CategoryID", "Navigating back with categoryId: " + categoryId);
        setResult(RESULT_OK, intent);
        finish();
    }
}

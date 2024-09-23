package com.example.guestureguide;

import android.content.Intent;
import android.media.MediaPlayer;
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

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ContentDescriptionActivity extends AppCompatActivity {

    private ImageView contentImageView;
    private VideoView contentVideoView;
    private Button learnButton;
    private Button backButton;  // Back button
    private String categoryId, contentNameString;  // class variable for categoryId
    private TextView lessonProgress, contentName;

    private ArrayList<Content> contentList;  // List of content items
    private int currentIndex;  // Current index of the content being displayed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_description);

        categoryId = getIntent().getStringExtra("id");//get frmom intent
        contentNameString = getIntent().getStringExtra("contentName");


        ImageButton backToContentButton = findViewById(R.id.back_to_content_button);
        backToContentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the Category activity
                // Create an intent to return the category ID
                Intent resultIntent = new Intent();
                resultIntent.putExtra("id", categoryId);  // Pass the category ID back
                setResult(RESULT_OK, resultIntent);  // Set the result to OK
                Log.d("CategoryID", "Navigating back with categoryId: " + categoryId);

                finish();  // Finish this activity and return to the previous one
            }
        });

        // Initialize views
        contentImageView = findViewById(R.id.contentImageView);
        contentVideoView = findViewById(R.id.contentVideoView);
        learnButton = findViewById(R.id.learnButton);
        backButton = findViewById(R.id.previousContentButton);
        lessonProgress = findViewById(R.id.lessonProgress);
        contentName = findViewById(R.id.contentName);

        contentName.setText(contentNameString);// conv string to text view

        // Get content list and current index from intent
        contentList = (ArrayList<Content>) getIntent().getSerializableExtra("content_list");
        currentIndex = getIntent().getIntExtra("current_index", 0);


        // Check if contentList is null
        if (contentList == null || contentList.isEmpty()) {
            Toast.makeText(this, "No content to display", Toast.LENGTH_SHORT).show();
            return;  // Stop execution if there's no content
        }

        // Load the current content and set the visibility of the back button
        updateBackButtonVisibility();
        loadContent(currentIndex);

        // Set up Learn button click listener
        learnButton.setOnClickListener(view -> {
            // Proceed to the next content
            if (currentIndex < contentList.size() - 1) {
                currentIndex++;
                loadContent(currentIndex);
                updateBackButtonVisibility();  // Update visibility when going forward
                lessonProgress.setText(currentIndex+ 1 + "/" + contentList.size());

            } else {
                Toast.makeText(ContentDescriptionActivity.this, "No more content available!", Toast.LENGTH_SHORT).show();
                navigateBackToCategory();
            }
        });

        // Set up Back button click listener
        backButton.setOnClickListener(view -> {
            // Go to the previous content
            if (currentIndex > 0) {
                currentIndex--;
                loadContent(currentIndex);
                updateBackButtonVisibility();  // Update visibility when going back
                lessonProgress.setText(currentIndex + 1 + "/" + contentList.size());

            }
        });
    }

    // Function to load content based on the current index
    private void loadContent(int index) {
        Content currentContent = contentList.get(index);
        String imageUrl = currentContent.getImageUrl();
        String videoUrl = currentContent.getVideoUrl();
        lessonProgress.setText(currentIndex+ 1 + "/" + contentList.size());


        if (imageUrl != null) {
            // Load image using Glide
            Glide.with(this)
                    .load(imageUrl)
                    .into(contentImageView);
        } else {
            Toast.makeText(this, "Image URL is missing", Toast.LENGTH_SHORT).show();
        }

        if (videoUrl != null) {
            // Load and play video
            contentVideoView.setVideoURI(Uri.parse(videoUrl));
            contentVideoView.start();
        } else {
            Toast.makeText(this, "Video URL is missing", Toast.LENGTH_SHORT).show();
        }

        contentVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                // Log the error details or display a message
                Toast.makeText(ContentDescriptionActivity.this, "Error playing video: " + what + ", " + extra, Toast.LENGTH_LONG).show();
                return true;  // Return true if you handled the error
            }
        });

    }

    // Function to update the visibility of the back button
    private void updateBackButtonVisibility() {
        if (currentIndex == 0) {
            backButton.setVisibility(View.GONE);  // Hide back button on the first index
        } else {
            backButton.setVisibility(View.VISIBLE);  // Show back button for other indexes
        }
    }

    private void navigateBackToCategory() {
        if (contentList == null || contentList.isEmpty()) {
            // If no content is available, just finish the activity and return to ContentActivity
            Intent intent = new Intent(ContentDescriptionActivity.this, ContentActivity.class);
            intent.putExtra("id", categoryId);  // Pass the category ID
            Log.d("CategoryID", "Navigating back with categoryId: " + categoryId);

            setResult(RESULT_OK, intent);  // Send the category ID back to the ContentActivity
            finish();  // Close this activity and go back
        } else {
            // Navigate back to ContentActivity if content is available
            Intent intent = new Intent(ContentDescriptionActivity.this, ContentActivity.class);
            intent.putExtra("id", categoryId);  // Pass the category ID
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            // Log the categoryId to ensure it's not null
            Log.d("CategoryID", "Navigating back with categoryId: " + categoryId);

            startActivity(intent);
            finish();  // Finish the current activity
        }

    }
}

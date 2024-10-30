package com.example.guestureguide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ContentAdapter contentAdapter;
    private ArrayList<Content> contentList;
    private String categoryId, contentName, categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewContents);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // Use 'this' for Activity context
        recyclerView.setLayoutManager(gridLayoutManager);

        categoryId = getIntent().getStringExtra("id");
//back for activity
        ImageButton backButton = findViewById(R.id.back_to_content_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                /*
                Intent intent = new Intent(ContentActivity.this, MainActivity.class);
                intent.putExtra("id", categoryId);  // Pass the category ID
                startActivityForResult(intent, 1);  // Request code 1

                 */
            }
        });

        // Initialize content list and adapter
        contentList = new ArrayList<>();
        contentAdapter = new ContentAdapter(this, contentList, categoryId,contentName,categoryName);
        recyclerView.setAdapter(contentAdapter);

        Log.d("ContentActivity", "Received Category ID: " + categoryId);

        // Fetch and populate content list
        if (categoryId != null) {
            fetchContent(categoryId);
        } else {
            Toast.makeText(this, "Category ID is missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchContent(String categoryId) {
        String url = "http://192.168.8.20/gesture/getContent.php?category_id=" + categoryId;  // Adjust URL as needed
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        contentList.clear();
                        try {
                            Log.d("ImageURL", "Loading image from URL: " + url);

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject contentObject = response.getJSONObject(i);
                                String contentName = contentObject.getString("content_name");
                                String contentImage = contentObject.getString("content_image");
                                String contentVideo = contentObject.getString("content_video");
                                String categoryName = contentObject.getString("category_name");





                                String imageUrl = "http://192.168.8.20/" + contentImage;
                                String videoUrl = "http://192.168.8.20/" + contentVideo;

                                contentList.add(new Content(contentName, imageUrl, videoUrl, categoryName));
                            }
                            contentAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String returnedCategoryId = data.getStringExtra("id");  // Retrieve the returned category ID
            if (returnedCategoryId != null) {
                // Use the returned category ID (for example, reload content)
                fetchContent(returnedCategoryId);
            } else {
                Toast.makeText(this, "Category ID is missing", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

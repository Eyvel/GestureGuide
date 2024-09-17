package com.example.guestureguide;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewContents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize content list and adapter
        contentList = new ArrayList<>();
        contentAdapter = new ContentAdapter(this, contentList);
        recyclerView.setAdapter(contentAdapter);

        // Get the category ID from the intent
        String categoryId = getIntent().getStringExtra("id");

        // Fetch and populate content list
        if (categoryId != null) {
            fetchContent(categoryId);
        } else {
            Toast.makeText(this, "Category ID is missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchContent(String categoryId) {
        String url = "http://192.168.8.7/gesture/getContent.php?category_id=" + categoryId;  // Adjust URL as needed
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

                                String imageUrl = "http://192.168.8.7/" + contentImage;

                                contentList.add(new Content(contentName, imageUrl));
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
}

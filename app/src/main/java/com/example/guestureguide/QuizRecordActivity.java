package com.example.guestureguide;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class QuizRecordActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QuizRecordAdapter adapter;
    private List<QuizRecord> quizRecordList;
    private String user_id;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_record);
//back for acitivity
        ImageButton backButton = findViewById(R.id.back_to_records_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });


        // Edge-to-edge UI setup
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sharedPreferences = getSharedPreferences("MyAppName", MODE_PRIVATE);

        //get user id in shared pref
        user_id = sharedPreferences.getString("user_id", "").trim();
        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize quiz records list and adapter
        quizRecordList = new ArrayList<>();
        adapter = new QuizRecordAdapter(quizRecordList);
        recyclerView.setAdapter(adapter);

        // Fetch quiz records
        fetchQuizRecords();
    }

    private void fetchQuizRecords() {
        Log.d("QuizRecordActivity",user_id);

        String url = "http://192.168.8.20/gesture/fetch_quiz_records.php?user_id=" +user_id;


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray recordsArray = response.getJSONArray("quiz_records");

                            for (int i = 0; i < recordsArray.length(); i++) {
                                JSONObject recordObj = recordsArray.getJSONObject(i);

                                String quizTitle = recordObj.getString("quiz_title");
                                int score = recordObj.getInt("total_score");
                                int totalQuestions = recordObj.getInt("total_items");
                                String dateTaken = recordObj.getString("quiz_answered_date");

                                // Create a QuizRecord object and add it to the list
                                quizRecordList.add(new QuizRecord(quizTitle, score, totalQuestions, dateTaken));
                            }

                            // Set the adapter for RecyclerView
                            adapter = new QuizRecordAdapter(quizRecordList);
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(QuizRecordActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(QuizRecordActivity.this, "Error fetching records", Toast.LENGTH_SHORT).show();
            }
        });

        // Add request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}


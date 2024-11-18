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

public class LessonRecordActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LessonRecordAdapter adapter;
    private List<LessonRecord> lessonRecordList;
    private String user_id;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_record);

        ImageButton backButton = findViewById(R.id.back_to_records_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("MyAppName", MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id", "").trim();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        lessonRecordList = new ArrayList<>();
        adapter = new LessonRecordAdapter(lessonRecordList);
        recyclerView.setAdapter(adapter);

        fetchLessonRecords();
    }

    private void fetchLessonRecords() {
        Log.d("LessonRecordActivity", "Fetching data for user: " + user_id);
        String url = "https://gestureguide.com/auth/mobile/fetch_lesson_progress_summary.php?user_id=" + user_id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray recordsArray = response.getJSONArray("lesson_records");

                            for (int i = 0; i < recordsArray.length(); i++) {
                                JSONObject recordObj = recordsArray.getJSONObject(i);

                                String categoryName = recordObj.getString("category_name");
                                int totalCompleted = recordObj.getInt("total_completed");
                                int overallContent = recordObj.getInt("overall_content");
                                String dateTaken = recordObj.getString("date_taken");

                                lessonRecordList.add(new LessonRecord(categoryName, totalCompleted, overallContent, dateTaken));
                            }

                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LessonRecordActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LessonRecordActivity.this, "Error fetching records", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}

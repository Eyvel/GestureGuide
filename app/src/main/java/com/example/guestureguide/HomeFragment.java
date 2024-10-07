package com.example.guestureguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {

    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private ArrayList<Category> categories;
    private Handler handler;
    private Runnable runnable;
    private final int UPDATE_INTERVAL = 5000; // 5 seconds
    private String username;
    private TextView greeting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        categories = new ArrayList<>();

        greeting = view.findViewById(R.id.greeting);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppName", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        greeting.setText(username);
        Log.d("HomeFragment", "Retrieved username: " + username);

        TextView seeAll = view.findViewById(R.id.seeAll);
        Button learnMore = view.findViewById(R.id.learn_more_button);

        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCategoryFragment();
            }
        });
        learnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCategoryFragment();
            }
        });

        categoryAdapter = new CategoryAdapter(getContext(), categories, this, username);
        recyclerView.setAdapter(categoryAdapter);

        handler = new Handler();
        startAutoUpdate();
        return view;
    }

    private void navigateToCategoryFragment() {
        NavigationActivity activity = (NavigationActivity) getActivity();
        if (activity != null) {
            activity.binding.bottomNavigationView.setVisibility(View.GONE); // Hide the navigation view
        }
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, new CategoryFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void fetchCategories() {
        ApiService apiService = RetrofitClient.getInstance(getContext()).create(ApiService.class);
        Call<List<Category>> call = apiService.getCategories();

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories.clear();
                    categories.addAll(response.body());
                    categoryAdapter.setCategories(categories);
                } else {
                    Toast.makeText(getContext(), "Failed to fetch categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCategoryClick(Category category) {
        Intent intent = new Intent(getActivity(), ContentActivity.class);
        intent.putExtra("category_name", category.getCategory_name());
        intent.putExtra("id", category.getId());
        startActivity(intent);
        Log.d("ContentActivity", "Starting ContentActivity with category: " + category.getCategory_name());
        Toast.makeText(getContext(), "Clicked: " + category.getCategory_name(), Toast.LENGTH_SHORT).show();
    }

    private void startAutoUpdate() {
        runnable = new Runnable() {
            @Override
            public void run() {
                fetchCategories();
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        };
        handler.postDelayed(runnable, UPDATE_INTERVAL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAutoUpdate();
    }

    private void stopAutoUpdate() {
        handler.removeCallbacks(runnable);
    }
}

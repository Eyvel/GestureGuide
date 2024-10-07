package com.example.guestureguide;

import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface ApiService {
    @GET("getCategories.php")  // Assuming this is your API endpoint
    Call<List<Category>> getCategories();
}



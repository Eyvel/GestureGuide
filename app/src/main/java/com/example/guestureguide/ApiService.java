package com.example.guestureguide;

import retrofit2.Call;
import retrofit2.http.GET;
import java.util.ArrayList;

public interface ApiService {
    @GET("gesture/getCategories.php")
    Call<ArrayList<Category>> getCategories();
}

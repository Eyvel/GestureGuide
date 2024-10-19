package com.example.guestureguide;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.content.Context;
import java.io.File;
import java.io.IOException;

public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        // Define the cache size and directory
        File httpCacheDirectory = new File(context.getCacheDir(), "http-cache");
        int cacheSize = 10 * 1024 * 1024; // 10 MB cache
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        // Create an interceptor to handle caching policy
        Interceptor cacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                // Force cache if no network available
                if (!NetworkUtil.isNetworkAvailable(context)) {
                    request = request.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=604800") // Cache for 1 week
                            .build();
                }

                Response response = chain.proceed(request);
                return response.newBuilder()
                        .header("Cache-Control", request.cacheControl().toString())
                        .build();
            }
        };

        // Build OkHttpClient with cache and interceptor
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(cacheInterceptor)
                .build();

        // Build Retrofit
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.8.20/")  // Replace with your base URL
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

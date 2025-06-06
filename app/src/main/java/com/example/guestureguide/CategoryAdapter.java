package com.example.guestureguide;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private ArrayList<Category> categoryList;
    private OnCategoryClickListener onCategoryClickListener;
    private String username;

    // Constructor accepting Context, Category List, and OnCategoryClickListener
    public CategoryAdapter(Context context, ArrayList<Category> categoryList, OnCategoryClickListener onCategoryClickListener, String username) {
        this.context = context;
        this.categoryList = categoryList;
        this.onCategoryClickListener = onCategoryClickListener;
        this.username = username;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_content, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryTextView.setText(category.getName());

        // Load the category image using Glide
        String fullImageUrl = "https://gestureguide.com/" + category.getImageUrl();
        Glide.with(context)
                .load(fullImageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Caches both the original and the resized image
                .skipMemoryCache(false)
                .into(holder.categoryImageView);

        // Define the colors array
        String[] colors = {
                "#FF8A8A", "#B1AFFF", "#EF9C66", "#78ABA8",
                "#B5C0D0", "#D5F0C1", "#AC87C5", "#A1EEBD"
        };

        // Apply the background color and border with rounded corners
        String color = colors[position % colors.length]; // Cycle through the colors array
        int borderRadius = 48; // You can adjust the radius value
        setBorderRadius(holder, color, borderRadius);  // Set the background color and radius

        // Set click listener for the entire item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCategoryClickListener != null) {
                    onCategoryClickListener.onCategoryClick(category);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView categoryImageView;
        TextView categoryTextView;
        LinearLayout parentLinearLayout;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImageView = itemView.findViewById(R.id.imageViewContent);
            categoryTextView = itemView.findViewById(R.id.textViewContentName);
            parentLinearLayout = itemView.findViewById(R.id.parentLinearLayout);
        }
    }

    // Interface to handle category clicks
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    // Call this method to prefetch images before setting up the RecyclerView
    public void prefetchImages(ArrayList<Category> categories) {
        for (Category category : categories) {
            String fullImageUrl = "https://gestureguide.com/" + category.getImageUrl();
            Glide.with(context)
                    .load(fullImageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache images for faster access
                    .preload(); // Preload into cache
        }
    }

    // Method to set background color and corner radius
    // Method to set background color and corner radius
    private void setBorderRadius(CategoryViewHolder holder, String color, int radius) {
        // Create a GradientDrawable to apply background color and rounded corners
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setColor(Color.parseColor(color));  // Set background color
        backgroundDrawable.setCornerRadius(radius);  // Set corner radius
        holder.parentLinearLayout.setBackground(backgroundDrawable);  // Set the background drawable
    }

}


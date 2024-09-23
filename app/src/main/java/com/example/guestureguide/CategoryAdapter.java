package com.example.guestureguide;

import android.content.Context;
import android.graphics.Color;
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

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private ArrayList<Category> categoryList;
    private OnCategoryClickListener onCategoryClickListener;

    // Constructor accepting Context, Category List, and OnCategoryClickListener
    public CategoryAdapter(Context context, ArrayList<Category> categoryList, OnCategoryClickListener onCategoryClickListener) {
        this.context = context;
        this.categoryList = categoryList;
        this.onCategoryClickListener = onCategoryClickListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryTextView.setText(category.getName());

        // Load the category image using Glide
        String fullImageUrl = "http://192.168.8.7/" + category.getImageUrl();
        Glide.with(context)
                .load(fullImageUrl)
                .into(holder.categoryImageView);

        if (position % 2 == 0) {
            holder.parentLinearLayout.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.YellowOrange));
        } else {
            holder.parentLinearLayout.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.dark_blue));
        }

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
            categoryImageView = itemView.findViewById(R.id.categoryImageView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            parentLinearLayout = itemView.findViewById(R.id.parentLinearLayout);
        }
    }

    // Interface to handle category clicks
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }
}

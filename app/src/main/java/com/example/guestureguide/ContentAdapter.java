package com.example.guestureguide;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    private Context context;
    private ArrayList<Content> contentList;
    private String categoryId, contentName, categoryName;

    // Define the colors array
    private final String[] colors = {
            "#FF8A8A", "#B1AFFF", "#EF9C66", "#78ABA8",
            "#B5C0D0", "#D5F0C1", "#AC87C5", "#A1EEBD"
    };

    public ContentAdapter(Context context, ArrayList<Content> contentList, String categoryId, String contentName, String categoryName) {
        this.context = context;
        this.contentList = contentList;
        this.categoryId = categoryId;
        this.contentName = contentName;
        this.categoryName = categoryName;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_content, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        Content content = contentList.get(position);
        holder.contentTextView.setText(content.getName());

        // Load image using Glide
        Glide.with(context)
                .load(content.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Caches both the original and the resized image
                .skipMemoryCache(false)
                .into(holder.contentImageView);

        // Set dynamic border radius and background color
        int borderRadius = (position % 2 == 0) ? 48 : 48; // Example: alternate radius between items
        String color = colors[position % colors.length]; // Cycle through the colors
        setBorderRadius(holder, borderRadius, color);  // Pass the color to the setBorderRadius method

        // Set click listener for each content item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ContentDescriptionActivity.class);
            intent.putExtra("content_list", contentList);  // Pass the content list
            intent.putExtra("current_index", position);    // Pass the current index
            intent.putExtra("id", categoryId);
            intent.putExtra("content_name", contentName);
            intent.putExtra("category_name", categoryName);

            context.startActivity(intent);
        });
    }



    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder {

        ImageView contentImageView;
        TextView contentTextView;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            contentImageView = itemView.findViewById(R.id.imageViewContent);
            contentTextView = itemView.findViewById(R.id.textViewContentName);
        }
    }
    private void setBorderRadius(ContentViewHolder holder, int radius, String color) {
        // Check if the background is a GradientDrawable
        Drawable background = holder.itemView.getBackground();
        if (background instanceof GradientDrawable) {
            GradientDrawable borderDrawable = (GradientDrawable) background;
            borderDrawable.setColor(Color.parseColor(color));  // Set the background color
            borderDrawable.setCornerRadius(radius);  // Set corner radius
        } else {
            // If it's not a GradientDrawable, create a new one and set it as the background
            GradientDrawable borderDrawable = new GradientDrawable();
            borderDrawable.setColor(Color.parseColor(color));  // Set background color
            borderDrawable.setStroke(4, Color.parseColor("#FF8A8A"));  // Set border color
            borderDrawable.setCornerRadius(radius);  // Set corner radius
            holder.itemView.setBackground(borderDrawable);
        }
    }



}


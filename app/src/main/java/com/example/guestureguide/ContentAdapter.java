package com.example.guestureguide;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    private Context context;
    private ArrayList<Content> contentList;
    private String categoryId, contentName;


    public ContentAdapter(Context context, ArrayList<Content> contentList, String categoryId, String contentName) {
        this.context = context;
        this.contentList = contentList;
        this.categoryId = categoryId;
        this.contentName = contentName;
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
                .into(holder.contentImageView);

        // Set click listener for each content item
        holder.itemView.setOnClickListener(v -> {
            // Show a Toast when an item is clicked
            Toast.makeText(context, "Clicked on: " + content.getName(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, ContentDescriptionActivity.class);
            intent.putExtra("content_list", contentList);  // Pass the content list
            intent.putExtra("current_index", position);    // Pass the current index
            intent.putExtra("id", categoryId);
            intent.putExtra("content_name", contentName);


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
}

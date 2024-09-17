package com.example.guestureguide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    private Context context;
    private ArrayList<Content> contentList;

    public ContentAdapter(Context context, ArrayList<Content> contentList) {
        this.context = context;
        this.contentList = contentList;
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

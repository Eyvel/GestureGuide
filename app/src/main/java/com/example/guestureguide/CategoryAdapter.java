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
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories = new ArrayList<>(); // Initialize the list
    private Context context;
    private OnCategoryClickListener onCategoryClickListener;
    private String username;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(Context context, List<Category> categories, OnCategoryClickListener listener, String username) {
        this.context = context;
        if (categories != null) {
            this.categories = categories; // Only set if it's not null
        }
        this.onCategoryClickListener = listener;
        this.username = username;
    }

    public void setCategories(List<Category> categories) {
        if (categories != null) {
            this.categories.clear();
            this.categories.addAll(categories);
        } else {
            this.categories.clear(); // Clear categories if null
        }
        notifyDataSetChanged(); // Notify adapter to refresh
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.categoryName.setText(category.getCategory_name());

        // Load image using Glide
        Glide.with(context)
                .load(category.getCategory_image())
                .into(holder.categoryImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCategoryClickListener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size(); // This will return 0 if categories is empty
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView categoryImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryTextView);
            categoryImage = itemView.findViewById(R.id.categoryImageView);
        }
    }
}

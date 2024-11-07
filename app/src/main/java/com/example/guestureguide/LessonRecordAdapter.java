package com.example.guestureguide;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LessonRecordAdapter extends RecyclerView.Adapter<LessonRecordAdapter.ViewHolder> {

    private List<LessonRecord> lessonRecordList;

    public LessonRecordAdapter(List<LessonRecord> lessonRecordList) {
        this.lessonRecordList = lessonRecordList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LessonRecord record = lessonRecordList.get(position);
        holder.quizTitleTextView.setText(record.getCategoryName());

        // Set score and total questions in the format "score/total questions"
        holder.scoreTotalQuestionsTextView.setText(record.getTotalCompleted() + "/" + record.getOverallContent());

        holder.dateTakenTextView.setText(record.getDateTaken());
    }

    @Override
    public int getItemCount() {
        return lessonRecordList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView quizTitleTextView, scoreTotalQuestionsTextView, dateTakenTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            quizTitleTextView = itemView.findViewById(R.id.quiz_title);
            scoreTotalQuestionsTextView = itemView.findViewById(R.id.quiz_score); // Updated field
            dateTakenTextView = itemView.findViewById(R.id.quiz_date);
        }
    }
}

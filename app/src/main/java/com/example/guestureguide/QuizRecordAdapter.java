package com.example.guestureguide;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuizRecordAdapter extends RecyclerView.Adapter<QuizRecordAdapter.ViewHolder> {

    private List<QuizRecord> quizRecordList;

    public QuizRecordAdapter(List<QuizRecord> quizRecordList) {
        this.quizRecordList = quizRecordList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizRecord record = quizRecordList.get(position);
        holder.quizTitleTextView.setText(record.getQuizTitle());

        // Set score and total questions in the format "score/total questions"
        holder.scoreTotalQuestionsTextView.setText(record.getScore() + "/" + record.getTotalQuestions());

        holder.dateTakenTextView.setText(record.getDateTaken());
    }

    @Override
    public int getItemCount() {
        return quizRecordList.size();
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

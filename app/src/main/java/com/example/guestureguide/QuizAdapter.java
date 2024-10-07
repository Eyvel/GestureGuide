package com.example.guestureguide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private Context context;
    private ArrayList<Quiz> quizzes;
    private OnQuizClickListener onQuizClickListener;

    public QuizAdapter(Context context, ArrayList<Quiz> quizzes, OnQuizClickListener onQuizClickListener) {
        this.context = context;
        this.quizzes = quizzes;
        this.onQuizClickListener = onQuizClickListener;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizzes.get(position);
        holder.quizTitle.setText(quiz.getQuizTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onQuizClickListener.onQuizClick(quiz);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    public class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView quizTitle;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            quizTitle = itemView.findViewById(R.id.quizTitle);
        }
    }

    // Interface for handling click events
    public interface OnQuizClickListener {
        void onQuizClick(Quiz quiz);
    }
}

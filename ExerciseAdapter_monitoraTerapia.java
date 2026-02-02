package com.example.gfm_pronuntiapp_appfinale_esame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExerciseAdapter_monitoraTerapia extends RecyclerView.Adapter<ExerciseAdapter_monitoraTerapia.ViewHolder> {

    private Context mContext;
    private ArrayList<Exercise> mExercises;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int exerciseId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public ExerciseAdapter_monitoraTerapia(Context context, ArrayList<Exercise> exercises) {
        mContext = context;
        mExercises = exercises;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_exercise, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise currentExercise = mExercises.get(position);
        holder.nameTextView.setText(currentExercise.getName());
        holder.dateTextView.setText(currentExercise.getDate());
        holder.correctionTextView.setText(currentExercise.getCorrection());
        holder.itemView.setTag(currentExercise.getId()); // Set the exercise ID as tag
    }

    @Override
    public int getItemCount() {
        return mExercises.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView dateTextView;
        public TextView correctionTextView;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            correctionTextView = itemView.findViewById(R.id.correctionTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            int exerciseId = (int) v.getTag(); // Get the exercise ID from the tag
                            listener.onItemClick(exerciseId);
                        }
                    }
                }
            });
        }
    }
}



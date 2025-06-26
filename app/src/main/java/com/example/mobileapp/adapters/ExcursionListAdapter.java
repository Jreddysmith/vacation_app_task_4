package com.example.mobileapp.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.models.Excursion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ExcursionListAdapter extends RecyclerView.Adapter<ExcursionListAdapter.ExcursionViewHolder> {

    private List<Excursion> excursions = new ArrayList<>();
    private OnItemClickListener listener;
    private Excursion selectedExcursion;

    public void setExcursions(List<Excursion> excursions) {
        this.excursions = excursions;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Excursion excursion);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_excursion, parent, false);
        return new ExcursionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        Excursion excursion = excursions.get(position);
        holder.titleTextView.setText(excursion.getTitle());
        holder.dateTextView.setText(new SimpleDateFormat("yyyy-MM-dd").format(excursion.getDate()));

        // Set click listener for selection
        holder.itemView.setOnClickListener(v -> {
            selectedExcursion = excursion;
            notifyDataSetChanged(); // Refresh the list to show selection

            // Ensure the listener is called correctly
            if (listener != null) {
                listener.onItemClick(excursion);
            }
        });

        // Highlight the selected item (Optional for better UX)
        holder.itemView.setBackgroundColor(
                selectedExcursion == excursion ? Color.LTGRAY : Color.TRANSPARENT
        );
    }

    @Override
    public int getItemCount() {
        return excursions.size();
    }

    public Excursion getSelectedExcursion() {
        return selectedExcursion;
    }

    public class ExcursionViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, dateTextView;

        public ExcursionViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.excursion_title);
            dateTextView = itemView.findViewById(R.id.excursion_date);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(excursions.get(position));
                }
            });
        }
    }
}


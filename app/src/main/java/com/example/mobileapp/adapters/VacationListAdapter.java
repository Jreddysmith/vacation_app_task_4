package com.example.mobileapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;

import com.example.mobileapp.R;
import com.example.mobileapp.models.Vacation;

import java.util.List;

public class VacationListAdapter extends ArrayAdapter<Vacation> {

    public VacationListAdapter(@NonNull Context context, @NonNull List<Vacation> vacations) {
        super(context, 0, vacations);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_vacation, parent, false);
        }

        Vacation vacation = getItem(position);

        TextView titleTextView = convertView.findViewById(R.id.vacation_title_text);
        TextView hotelTextView = convertView.findViewById(R.id.vacation_hotel_text);

        if (vacation != null) {
            titleTextView.setText(vacation.getTitle());
            hotelTextView.setText(vacation.getHotel());
        }

        return convertView;
    }
}

package com.example.mobileapp.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileapp.R;
import com.example.mobileapp.models.Vacation;
import com.example.mobileapp.utils.ValidationUtils;
import com.example.mobileapp.viewmodels.VacationViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddEditVacationActivity extends AppCompatActivity {

    private EditText titleInput, hotelInput, startDateInput, endDateInput;
    private Button saveButton;
    private VacationViewModel vacationViewModel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_vacation);

        titleInput = findViewById(R.id.vacation_title);
        hotelInput = findViewById(R.id.vacation_hotel);
        startDateInput = findViewById(R.id.vacation_start_date);
        endDateInput = findViewById(R.id.vacation_end_date);
        saveButton = findViewById(R.id.save_vacation_button);




        vacationViewModel = new ViewModelProvider(this).get(VacationViewModel.class);

        startDateInput.setOnClickListener(view -> showDatePicker(startDateInput));
        endDateInput.setOnClickListener(view -> showDatePicker(endDateInput));

        saveButton.setOnClickListener(view -> saveVacation());
    }

    private void showDatePicker(EditText dateInput) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    String selectedDate = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", dayOfMonth);
                    dateInput.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void saveVacation() {
        String title = titleInput.getText().toString().trim();
        String hotel = hotelInput.getText().toString().trim();
        String startDateStr = startDateInput.getText().toString().trim();
        String endDateStr = endDateInput.getText().toString().trim();
//        String vacationType = "General Vacation"; // Default type or get from UI dropdown

        String vacationType = "";
        // Get the selected vacation type
        RadioGroup vacationTypeRadioGroup = findViewById(R.id.vacation_type_radio_group);
        int selectedId = vacationTypeRadioGroup.getCheckedRadioButtonId();

        if (selectedId == R.id.radio_business) {
            vacationType = "Business";
        } else if (selectedId == R.id.radio_leisure) {
            vacationType = "Leisure";
        } else {
            Toast.makeText(this, "Please select a vacation type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty() || hotel.isEmpty() || startDateStr.isEmpty() || endDateStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);

            int userId = getSharedPreferences("VacationApp", MODE_PRIVATE)
                    .getInt("loggedInUserId", -1);

            // Create the vacation object with the vacation type
            Vacation vacation = new Vacation(title, hotel, startDate, endDate, userId, vacationType);

            vacationViewModel.insert(vacation);
            finish();

        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
        }
    }
}

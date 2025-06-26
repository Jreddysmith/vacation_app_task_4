package com.example.mobileapp.activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.mobileapp.Manifest;
import android.Manifest;
import com.example.mobileapp.R;
import com.example.mobileapp.adapters.ExcursionListAdapter;
import com.example.mobileapp.models.Excursion;
import com.example.mobileapp.models.Vacation;
import com.example.mobileapp.receivers.ExcursionAlertReceiver;
import com.example.mobileapp.viewmodels.ExcursionViewModel;
import com.example.mobileapp.viewmodels.ExcursionViewModelFactory;
import com.example.mobileapp.viewmodels.VacationViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ExcursionActivity extends AppCompatActivity {

    private EditText titleInput, dateInput;
    private Button saveButton, deleteButton, setAlertButton, newExcursionButton;
    private ExcursionViewModel excursionViewModel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private int vacationId = -1; // Default value
    private RecyclerView excursionListView;
    private ExcursionListAdapter excursionListAdapter;

    private Excursion selectedExcursion = null;

    private Date startDate;
    private Date endDate;

    private VacationViewModel vacationViewModel;

    private Vacation selectedVacation;

    private static final int REQUEST_NOTIFICATION_PERMISSION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion);


        newExcursionButton = findViewById(R.id.new_excursion_button);

        // Clear selection when the "New Excursion" button is clicked
        newExcursionButton.setOnClickListener(view -> clearInputFields());


        // Check and request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            }
        }
        setAlertButton = findViewById(R.id.alert_button);

        setAlertButton.setOnClickListener(view -> {
            if (selectedExcursion != null && selectedExcursion.getDate() != null) {
                scheduleExcursionAlert(selectedExcursion.getTitle(), selectedExcursion.getDate());
                Toast.makeText(this, "Alert set for excursion: " + selectedExcursion.getTitle(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please select an excursion with a valid date to set an alert.", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize the VacationViewModel
        vacationViewModel = new ViewModelProvider(this).get(VacationViewModel.class);

        // Get the vacation ID from the Intent
        if (getIntent().hasExtra("vacationId")) {
            vacationId = getIntent().getIntExtra("vacationId", -1);
            Log.d("DateValidation", "Received Vacation ID: " + vacationId);

            // Fetch the vacation details using the ViewModel
            vacationViewModel.getVacationById(vacationId).observe(this, vacation -> {
                if (vacation != null) {
                    selectedVacation = vacation;
                    startDate = selectedVacation.getStartDate();
                    endDate = selectedVacation.getEndDate();

                    Log.d("DateValidation", "Loaded Vacation: " + selectedVacation.getTitle());
                    Log.d("DateValidation", "Start Date: " + startDate);
                    Log.d("DateValidation", "End Date: " + endDate);
                } else {
                    Log.d("DateValidation", "No vacation found for ID: " + vacationId);
                }
            });
        } else {
            Log.d("DateValidation", "No vacation ID found in Intent!");
        }

        // Initialize the ViewModel with the custom factory
        ExcursionViewModelFactory factory = new ExcursionViewModelFactory(getApplication(), vacationId);
        excursionViewModel = new ViewModelProvider(this, factory).get(ExcursionViewModel.class);

        // Set up RecyclerView for excursions
        excursionListView = findViewById(R.id.excursion_list_view);
        excursionListView.setLayoutManager(new LinearLayoutManager(this));
        excursionListAdapter = new ExcursionListAdapter();
        excursionListView.setAdapter(excursionListAdapter);

        excursionViewModel.getExcursionsByVacationId(vacationId).observe(this, excursions -> {
            excursionListAdapter.setExcursions(excursions);
        });

        excursionListAdapter.setOnItemClickListener(excursion -> {
            selectedExcursion = excursion;
            titleInput.setText(excursion.getTitle());
            dateInput.setText(excursion.getFormattedDate());
            Toast.makeText(this, "Editing excursion: " + excursion.getTitle(), Toast.LENGTH_SHORT).show();
        });

        // Retrieve start and end dates of the associated vacation from Intent
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String startDateString = getIntent().getStringExtra("startDate");
            String endDateString = getIntent().getStringExtra("endDate");

            if (startDateString != null) {
                startDate = sdf.parse(startDateString);
            }
            if (endDateString != null) {
                endDate = sdf.parse(endDateString);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid vacation date range", Toast.LENGTH_SHORT).show();
        }

        // Observe excursions for the specific vacation
        if (vacationId != -1) {
            excursionViewModel.getExcursionsForVacation().observe(this, excursions -> {
                excursionListAdapter.setExcursions(excursions);
            });
        }

        titleInput = findViewById(R.id.excursion_title);
        dateInput = findViewById(R.id.excursion_date);
        saveButton = findViewById(R.id.save_excursion_button);
        deleteButton = findViewById(R.id.delete_button);

        excursionViewModel = new ViewModelProvider(this).get(ExcursionViewModel.class);

//        test

        excursionViewModel.getExcursionsByVacationId(vacationId).observe(this, excursions -> {
            excursionListAdapter.setExcursions(excursions);
        });

        dateInput.setOnClickListener(view -> showDatePicker(dateInput));

//        saveButton.setOnClickListener(view -> saveExcursion());
        saveButton.setOnClickListener(view -> saveOrUpdateExcursion());

        deleteButton.setOnClickListener(view -> deleteButton());
    }

    // Method to schedule the notification
    private void scheduleExcursionAlert(String excursionTitle, Date excursionDate) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(this, ExcursionAlertReceiver.class);
        intent.putExtra("excursionTitle", excursionTitle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(excursionDate);
        calendar.set(Calendar.HOUR_OF_DAY, 9); // Set the notification time (8 AM)
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Log.d("AlertScheduling", "Alert set for: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));


        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            Toast.makeText(this, "The selected date is in the past. Please select a future date.", Toast.LENGTH_SHORT).show();
            return;
        }

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(this, "Alert set for " + excursionTitle + " at 9 AM on " + dateFormat.format(excursionDate), Toast.LENGTH_SHORT).show();
    }

    private boolean isDateWithinVacation(Date excursionDate) {
        Log.d("DateValidation", "=====================");
        Log.d("DateValidation", "Excursion Date: " + excursionDate);
        Log.d("DateValidation", "Vacation Start Date: " + startDate);
        Log.d("DateValidation", "Vacation End Date: " + endDate);

        if (startDate == null || endDate == null || excursionDate == null) {
            Log.d("DateValidation", "One of the dates is null!");
            return false;
        }

        boolean isValid = !excursionDate.before(startDate) && !excursionDate.after(endDate);
        Log.d("DateValidation", "Is Date Within Vacation: " + isValid);
        Log.d("DateValidation", "=====================");

        return isValid;
    }



    private void deleteButton() {
        Excursion selectedExcursion = excursionListAdapter.getSelectedExcursion();
        if (selectedExcursion != null) {
            excursionViewModel.delete(selectedExcursion);
            Toast.makeText(this, "Excursion deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No excursion selected", Toast.LENGTH_SHORT).show();
        }
    }


    private void showDatePicker(EditText dateInput) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                    dateInput.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void saveOrUpdateExcursion() {
        String title = titleInput.getText().toString().trim();
        String dateString = dateInput.getText().toString().trim();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date excursionDate = sdf.parse(dateString);
            if (isDateWithinVacation(excursionDate)) {
                if (selectedExcursion == null) {
                    // Adding a new excursion
                    Excursion newExcursion = new Excursion(vacationId, title, excursionDate);
                    excursionViewModel.insert(newExcursion);
                    Toast.makeText(this, "Excursion added", Toast.LENGTH_SHORT).show();
                } else {
                    // Updating an existing excursion
                    selectedExcursion.setTitle(title);
                    selectedExcursion.setDate(excursionDate);
                    excursionViewModel.update(selectedExcursion);
                    Toast.makeText(this, "Excursion updated", Toast.LENGTH_SHORT).show();
                    selectedExcursion = null; // Reset selection after update
                }
                clearInputFields();
            } else {
                Toast.makeText(this, "Excursion date must be within vacation dates", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format. Use YYYY-MM-DD.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to clear the selected excursion
    private void clearInputFields() {
        selectedExcursion = null;
        titleInput.setText("");
        dateInput.setText("");
        excursionListAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Selection cleared. Ready to add a new excursion.", Toast.LENGTH_SHORT).show();
    }
}

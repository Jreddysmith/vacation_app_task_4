package com.example.mobileapp.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.adapters.ExcursionListAdapter;
import com.example.mobileapp.models.Vacation;
import com.example.mobileapp.receivers.VacationAlertReceiver;
import com.example.mobileapp.utils.ValidationUtils;
import com.example.mobileapp.viewmodels.ExcursionViewModel;
import com.example.mobileapp.viewmodels.VacationViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class VacationDetailActivity extends AppCompatActivity {

    private EditText titleInput, hotelInput, startDateInput, endDateInput;
    private Button updateButton, deleteButton, setAlertButton, shareButton, viewExcursionsButton;
    private VacationViewModel vacationViewModel;
    private int vacationId = -1;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private RecyclerView excursionListView;

    private ExcursionListAdapter excursionListAdapter;

    private ExcursionViewModel excursionViewModel;

    private RadioGroup vacationTypeRadioGroup;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_detail);

        excursionListView = findViewById(R.id.excursion_list_view);
        excursionListView.setLayoutManager(new LinearLayoutManager(this));

        excursionListAdapter = new ExcursionListAdapter();
        excursionListView.setAdapter(excursionListAdapter);

        excursionViewModel = new ViewModelProvider(this).get(ExcursionViewModel.class);

        if (vacationId != -1) {
            excursionViewModel.getExcursionsByVacationId(vacationId).observe(this, excursions -> {
                excursionListAdapter.setExcursions(excursions);
            });
        }

        // Initialize the share button
        shareButton = findViewById(R.id.share_vacation_button);

        // Set up the sharing functionality
        shareButton.setOnClickListener(view -> shareVacationDetails());

        // Request notification permission for Android 13+
        requestNotificationPermission(this);

        titleInput = findViewById(R.id.vacation_title);
        hotelInput = findViewById(R.id.vacation_hotel);
        startDateInput = findViewById(R.id.vacation_start_date);
        endDateInput = findViewById(R.id.vacation_end_date);
        updateButton = findViewById(R.id.update_vacation_button);
        deleteButton = findViewById(R.id.delete_vacation_button);
        setAlertButton = findViewById(R.id.set_alert_button);
        viewExcursionsButton = findViewById(R.id.view_excursions_button);
        // Initialize the RadioGroup for Vacation Type
        vacationTypeRadioGroup = findViewById(R.id.vacation_type_radio_group);


        viewExcursionsButton.setOnClickListener(view -> {
            Intent intent = new Intent(VacationDetailActivity.this, ExcursionActivity.class);
            intent.putExtra("vacationId", vacationId);
            Log.d("see_vacation_id", String.valueOf(vacationId));

            startActivity(intent);
        });


        vacationViewModel = new ViewModelProvider(this).get(VacationViewModel.class);

        if (getIntent().hasExtra("vacationId")) {
            vacationId = getIntent().getIntExtra("vacationId", -1);
            loadVacationDetails(vacationId);
        }

        startDateInput.setOnClickListener(view -> showDatePicker(startDateInput));
        endDateInput.setOnClickListener(view -> showDatePicker(endDateInput));

        updateButton.setOnClickListener(view -> updateVacation());
        deleteButton.setOnClickListener(view -> deleteVacation());
        setAlertButton.setOnClickListener(view -> setAlertsForVacation());
    }

    private void setAlertsForVacation() {
        String title = titleInput.getText().toString().trim();

        try {
            Date startDate = dateFormat.parse(startDateInput.getText().toString().trim());
            Date endDate = dateFormat.parse(endDateInput.getText().toString().trim());

            if (startDate != null) {
                scheduleAlert(startDate, title, "Vacation Starting");
            }
            if (endDate != null) {
                scheduleAlert(endDate, title, "Vacation Ending");
            }

            showSuccessMessage("Alerts set for vacation start and end dates");

        } catch (ParseException e) {
            showErrorMessage("Invalid date format");
        }
    }

    private void scheduleAlert(Date vacationDate, String title, String status) {
        Intent intent = new Intent(this, VacationAlertReceiver.class);
        intent.putExtra("vacationTitle", title);
        intent.putExtra("vacationStatus", status);

        int requestCode = (title + status).hashCode();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );


        // Set the alert to trigger at 9 AM on the specified date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(vacationDate);
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            showErrorMessage("The selected date is in the past. Please select a future date.");
            return;
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.d("VacationAlert", "Alert set for " + status + " " + title + " at 9 AM on " + dateFormat.format(vacationDate));
        }
    }

    private void showSuccessMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    private void showErrorMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(Color.RED)
                .show();
    }

    private void loadVacationDetails(int vacationId) {
        vacationViewModel.getVacationById(vacationId).observe(this, vacation -> {
            if (vacation != null) {
                titleInput.setText(vacation.getTitle());
                hotelInput.setText(vacation.getHotel());
                startDateInput.setText(dateFormat.format(vacation.getStartDate()));
                endDateInput.setText(dateFormat.format(vacation.getEndDate()));

                // Add log to see the loaded vacation type
                Log.d("VacationType", "Loaded Vacation Type: " + vacation.getVacationType());

                // Pre-select the appropriate RadioButton for the vacation type
                if ("Business".equalsIgnoreCase(vacation.getVacationType())) {
                    vacationTypeRadioGroup.check(R.id.radio_business);
                } else if ("Leisure".equalsIgnoreCase(vacation.getVacationType())) {
                    vacationTypeRadioGroup.check(R.id.radio_leisure);
                } else {
                    vacationTypeRadioGroup.clearCheck();
                }
            }
        });
    }

    private void showDatePicker(EditText dateInput) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    String selectedDate = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", dayOfMonth);
                    dateInput.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateVacation() {
        String title = titleInput.getText().toString().trim();
        String hotel = hotelInput.getText().toString().trim();
        String startDateStr = startDateInput.getText().toString().trim();
        String endDateStr = endDateInput.getText().toString().trim();

        if (title.isEmpty() || hotel.isEmpty() || startDateStr.isEmpty() || endDateStr.isEmpty()) {
            showErrorMessage("All fields must be filled");
            return;
        }

        try {
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);

            if (!ValidationUtils.isEndDateAfterStartDate(startDate, endDate)) {
                showErrorMessage("End date must be after start date");
                return;
            }

            int userId = getSharedPreferences("VacationApp", MODE_PRIVATE)
                    .getInt("loggedInUserId", -1);

//            String vacationType = "General Vacation"; // Set a default vacation type or retrieve from UI input

            // Retrieve the selected vacation type from the RadioButtons
            RadioGroup vacationTypeRadioGroup = findViewById(R.id.vacation_type_radio_group);
            int selectedId = vacationTypeRadioGroup.getCheckedRadioButtonId();

            String vacationType = "";
            if (selectedId == R.id.radio_business) {
                vacationType = "Business";
            } else if (selectedId == R.id.radio_leisure) {
                vacationType = "Leisure";
            } else {
                showErrorMessage("Please select a vacation type");
                return;
            }

            // Pass the vacationType when creating the Vacation object
            Vacation vacation = new Vacation(title, hotel, startDate, endDate, userId, vacationType);
            vacation.setId(vacationId);
            vacationViewModel.update(vacation);
            showSuccessMessage("Vacation updated successfully");
            finish();

        } catch (ParseException e) {
            showErrorMessage("Invalid date format");
        }
    }


    private void deleteVacation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Vacation")
                .setMessage("Are you sure you want to delete this vacation?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    vacationViewModel.getExcursionCountForVacation(vacationId, count -> {
                        if (count > 0) {
                            showErrorMessage("Cannot delete vacation with associated excursions");
                        } else {
                            vacationViewModel.deleteVacationById(vacationId);
                            showSuccessMessage("Vacation deleted successfully");
                            finish();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSuccessMessage("Notification permission granted");
            } else {
                showErrorMessage("Notification permission denied");
            }
        }
    }

    private void requestNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (activity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void shareVacationDetails() {
        String title = titleInput.getText().toString().trim();
        String hotel = hotelInput.getText().toString().trim();
        String startDate = startDateInput.getText().toString().trim();
        String endDate = endDateInput.getText().toString().trim();

        String vacationDetails = "Vacation Title: " + title + "\n" +
                "Hotel/Stay: " + hotel + "\n" +
                "Start Date: " + startDate + "\n" +
                "End Date: " + endDate;

        String[] shareOptions = {"Share via Email", "Copy to Clipboard", "Send via SMS"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Sharing Method");
        builder.setItems(shareOptions, (dialog, which) -> {
            switch (which) {
                case 0: // Share via Email
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Vacation Details: " + title);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, vacationDetails);
                    startActivity(Intent.createChooser(shareIntent, "Share via Email"));
                    break;

                case 1: // Copy to Clipboard
                    copyToClipboard(vacationDetails);
                    break;

                case 2: // Send via SMS
                    sendViaSms(vacationDetails);
                    break;
            }
        });
        builder.show();
    }


    private void copyToClipboard(String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Vacation Details", text);
        clipboard.setPrimaryClip(clip);
        showSuccessMessage("Vacation details copied to clipboard");
    }

    private void sendViaSms(String message) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("sms_body", message);
        startActivity(smsIntent);
    }



}

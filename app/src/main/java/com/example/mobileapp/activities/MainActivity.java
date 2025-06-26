package com.example.mobileapp.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.mobileapp.R;
import com.example.mobileapp.adapters.VacationListAdapter;
import com.example.mobileapp.models.BusinessVacation;
import com.example.mobileapp.models.LeisureVacation;
import com.example.mobileapp.models.Vacation;
import com.example.mobileapp.viewmodels.VacationViewModel;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.os.Environment;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private VacationViewModel vacationViewModel;
    private EditText startDateInput;
    private EditText endDateInput;
    private Button clearSearchButton;

    private Button generateReportButton;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve the user's name from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("VacationApp", MODE_PRIVATE);
        String userName = sharedPreferences.getString("loggedInUserName", "Welcome");

        // Set the ActionBar title with the user's name
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Welcome, " + userName + "!");
        }

        ListView vacationListView = findViewById(R.id.vacation_list_view);
        Button addVacationButton = findViewById(R.id.add_vacation_button);
        Button logoutButton = findViewById(R.id.logout_button); // New Logout Button
        Button searchButton = findViewById(R.id.search_button); // New Search Button
        clearSearchButton = findViewById(R.id.clear_search_button);
        startDateInput = findViewById(R.id.start_date_input);
        endDateInput = findViewById(R.id.end_date_input);
        generateReportButton = findViewById(R.id.generate_report_button);

        startDateInput.setOnClickListener(v -> showDatePickerDialog(startDateInput));
        endDateInput.setOnClickListener(v -> showDatePickerDialog(endDateInput));

        // To check report go to View->Tool Window -> Device Explorer
        // Then go to /storage/emulated/0/Android/data/com.example.mobileapp/files/Documents/
        // You should find the reports there to double check if the information is correct on them.
        // You can also right click on the file to save locally by clicking save as. This way you can double check on your local machine.
        generateReportButton.setOnClickListener(v -> generateReport());

        vacationViewModel = new ViewModelProvider(this).get(VacationViewModel.class);

        int userId = getSharedPreferences("VacationApp", MODE_PRIVATE)
                .getInt("loggedInUserId", -1);

        if (userId != -1) {
            vacationViewModel.loadVacationsForUser(userId); // Force refresh data
            vacationViewModel.getUserVacations().observe(this, vacations -> {
                VacationListAdapter adapter = new VacationListAdapter(MainActivity.this, vacations);
                vacationListView.setAdapter(adapter);
            });
        }

//        if (userId != -1) {
//            // Observe vacations and display Vacation objects directly
//            vacationViewModel.getUserVacations().observe(this, vacations -> {
//                // ✅ Correct: Use the VacationListAdapter with Vacation objects
//                VacationListAdapter adapter = new VacationListAdapter(MainActivity.this, vacations);
//                vacationListView.setAdapter(adapter);
//            });
//        }

        addVacationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddEditVacationActivity.class));
            }
        });

        // Handle Logout Button Click
        logoutButton.setOnClickListener(v -> {
            // Clear the logged-in user ID from SharedPreferences
            getSharedPreferences("VacationApp", MODE_PRIVATE)
                    .edit()
                    .remove("loggedInUserId")
                    .apply();

            // Clear the displayed vacations from the ListView
            VacationListAdapter emptyAdapter = new VacationListAdapter(MainActivity.this, new ArrayList<>());
            vacationListView.setAdapter(emptyAdapter);

            // Navigate back to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity
        });

        searchButton.setOnClickListener(v -> {
            String startDateStr = startDateInput.getText().toString().trim();
            String endDateStr = endDateInput.getText().toString().trim();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            try {
                Date startDate = dateFormat.parse(startDateStr);
                Date endDate = dateFormat.parse(endDateStr);

                if (startDate == null || endDate == null) {
                    Toast.makeText(this, "Please select both start and end dates.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validation: End date should not be before start date
                if (endDate.before(startDate)) {
                    Toast.makeText(this, "End date cannot be before the start date.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (userId != -1) {
                    vacationViewModel.searchVacationsByDateRange(userId, startDate, endDate)
                            .observe(this, vacations -> {
                                VacationListAdapter adapter = new VacationListAdapter(MainActivity.this, vacations);
                                vacationListView.setAdapter(adapter);
                            });
                }
            } catch (ParseException e) {
                Toast.makeText(this, "Invalid date format. Use yyyy-MM-dd", Toast.LENGTH_SHORT).show();
            }
        });

        vacationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Vacation selectedVacation = (Vacation) parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, VacationDetailActivity.class);
                intent.putExtra("vacationId", selectedVacation.getId());
                startActivity(intent);
            }
        });

        // Clear search and show all vacations
        clearSearchButton.setOnClickListener(v -> {
            startDateInput.setText("");
            endDateInput.setText("");
            if (userId != -1) {
                vacationViewModel.loadVacationsForUser(userId); // Load all vacations
                vacationViewModel.getUserVacations().observe(this, vacations -> {
                    VacationListAdapter adapter = new VacationListAdapter(MainActivity.this, vacations);
                    vacationListView.setAdapter(adapter);
                });
            }
        });
    }

    // Method to show DatePickerDialog and set selected date to the EditText
    private void showDatePickerDialog(EditText dateInput) {
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

    // Method to Generate a CSV Report
    private void generateReport() {
        int userId = getSharedPreferences("VacationApp", MODE_PRIVATE)
                .getInt("loggedInUserId", -1);

        if (userId != -1) {
            vacationViewModel.getUserVacations().observe(this, vacations -> {
                if (vacations != null && !vacations.isEmpty()) {
                    File reportFile = createCsvReport(vacations);
                    if (reportFile != null) {
                        Toast.makeText(this, "Report generated: " + reportFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Failed to generate report.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No vacation data available for report generation.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    // Create a CSV Report with Vacation Data
    private File createCsvReport(List<Vacation> vacations) {
        String reportTitle = "Vacation Report";
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String fileName = "Vacation_Report_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".csv";

        File reportFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);

        try (FileWriter writer = new FileWriter(reportFile)) {
            // Report Header
            writer.append(reportTitle).append("\n");
            writer.append("Generated on: ").append(timestamp).append("\n\n");

            // Column Headers
            writer.append("ID,Title,Hotel,Start Date,End Date\n");

            // Rows of Vacation Data
            for (Vacation vacation : vacations) {
                writer.append(String.valueOf(vacation.getId())).append(",")
                        .append(vacation.getTitle()).append(",")
                        .append(vacation.getHotel()).append(",")
                        .append(dateFormat.format(vacation.getStartDate())).append(",")
                        .append(dateFormat.format(vacation.getEndDate())).append("\n");
            }

            writer.flush();
            return reportFile;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // you can test this functionality in the logs for code including inheritance, polymorphism, and encapsulation task.
    // This is a sample of what the output should look like.
    //Vacation: Conference, Type: Business Vacation
    //Vacation: Beach Holiday, Type: Leisure Vacation
    private void demonstratePolymorphism() {
        List<Vacation> vacations = new ArrayList<>();

        // Using polymorphism to handle different vacation types
        vacations.add(new BusinessVacation("Conference", "Marriott", new Date(), new Date(), 1, "Tech Conference"));
        vacations.add(new LeisureVacation("Beach Holiday", "Hilton", new Date(), new Date(), 1, "Snorkeling"));

        for (Vacation vacation : vacations) {
            Log.d("VacationType", "Vacation: " + vacation.getTitle() + ", Type: " + vacation.getVacationType());
        }
    }


}

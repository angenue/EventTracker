package com.example.eventplanner.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.eventplanner.Event;
import com.example.eventplanner.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AddEventActivity extends AppCompatActivity {

    TextInputEditText eventDate, eventTime, eventName, eventDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        eventName = findViewById(R.id.event_name);
        eventDate = findViewById(R.id.date_picker);
        eventTime = findViewById(R.id.time_picker);
        eventDescription = findViewById(R.id.event_description);

        // Navigation bar customization
        Toolbar toolbar = (Toolbar) findViewById(R.id.addEventToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });
        //end nav bar customization


    }

    public void datePickerInput(View view) {
        Calendar calendar = Calendar.getInstance();

        //int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // on below line we are creating a variable for date picker dialog.
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                // on below line we are passing context.
                AddEventActivity.this,
                (view1, year1, month1, dayOfMonth) -> {
                    calendar.set(Calendar.MONTH, month1);
                    calendar.set(Calendar.YEAR, year1);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy '('EEE')'", Locale.getDefault());

                    String date = format.format(calendar.getTime());

                    eventDate.setText(date); },
                // on below line we are passing year,
                // month and day for selected date in our date picker.
                year, month, day);
        // at last we are calling show to
        // display our date picker dialog.
        datePickerDialog.show();
    }

    public void timePickerInput(View view) {
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        // at last we are calling show to
// display our time picker dialog.

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                AddEventActivity.this, style, (view1, hourOfDay, minute1) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute1);
            SimpleDateFormat format = new SimpleDateFormat("hh:mm aa", Locale.getDefault());

            String time = format.format(calendar.getTime());

            eventTime.setText( time);
        }, hour, minute, false);
        timePickerDialog.show();
    }

    // Inflating the menu items from the menu_items.xml file
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_event_app_bar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        String name = String.valueOf(eventName.getText());
        String date = String.valueOf(eventDate.getText());
        String time = String.valueOf(eventTime.getText());

        MenuItem item = menu.findItem(R.id.add_button);
        invalidateOptionsMenu();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(date) && !TextUtils.isEmpty(time)) {
            item.setEnabled(true);
            item.getIcon().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.blue), PorterDuff.Mode.MULTIPLY);
        }
        return true;
    }

    // Handling the click events of the menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Switching on the item id of the menu item
        if (item.getItemId() == R.id.add_button) {
            saveData();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddEventActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        uploadData();
        dialog.dismiss();
    }

    public void uploadData() {
        String title = eventName.getText().toString();
        String date = eventDate.getText().toString();
        String time = eventTime.getText().toString();
        String description = eventDescription.getText().toString();

        Event event = new Event(title, date, time, description);

        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String useruid=user.getUid();

        FirebaseDatabase.getInstance().getReference("users").child(useruid).child(currentDate)
                .setValue(event).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(AddEventActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(e -> Toast.makeText(AddEventActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());

    }
}
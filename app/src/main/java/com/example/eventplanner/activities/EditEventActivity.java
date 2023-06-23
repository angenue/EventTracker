package com.example.eventplanner.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.eventplanner.Event;
import com.example.eventplanner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class EditEventActivity extends AppCompatActivity {

    TextView deleteEvent;
    TextInputEditText eventDate, eventTitle, eventTime, eventDescription;
    String key = "";
    Toolbar toolbar;
    DatabaseReference reference;
    String useruid;
    FirebaseUser user;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        eventDate = findViewById(R.id.event_date);
        eventTitle = findViewById(R.id.event_name);
        eventTime = findViewById(R.id.event_time);
        eventDescription = findViewById(R.id.event_description);
        toolbar = findViewById(R.id.editEventToolbar);
        deleteEvent = findViewById(R.id.delete_event);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        useruid = user.getUid();

        //Toolbar

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });

        //end toolbar

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            eventDate.setText(bundle.getString("Date"));
            eventTitle.setText(bundle.getString("Title"));
            eventTime.setText(bundle.getString("Time"));
            eventDescription.setText(bundle.getString("Description"));
            key = bundle.getString("Key");
        }

        deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(eventTitle.getContext());
                builder.setTitle("Are you sure?");
                builder.setMessage("Deleted event can't be undone.");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        reference = FirebaseDatabase.getInstance().getReference("users").child(useruid);

                        reference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //reference.child(key).removeValue();
                                Toast.makeText(EditEventActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        });

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(eventTitle.getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();

            }
        });
    }

    // Inflating the menu items from the menu_items.xml file
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_event_app_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        String name = String.valueOf(eventTitle.getText());
        String date = String.valueOf(eventDate.getText());
        String time = String.valueOf(eventTime.getText());

        MenuItem item = menu.findItem(R.id.update_button);

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
            item.setEnabled(false);
            item.getIcon().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            invalidateOptionsMenu();
        }
        else {
            invalidateOptionsMenu();
            item.setEnabled(true);
            item.getIcon().clearColorFilter();

        }
        return true;
    }

    // Handling the click events of the menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Switching on the item id of the menu item
        if (item.getItemId() == R.id.update_button) {
            saveData();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void datePickerInput(View view) {
        eventDate.setText(null);

        Calendar calendar = Calendar.getInstance();

        //int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // on below line we are creating a variable for date picker dialog.
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                // on below line we are passing context.
                EditEventActivity.this,
                (view1, year1, month1, dayOfMonth) -> {
                    calendar.set(Calendar.MONTH, month1);
                    calendar.set(Calendar.YEAR, year1);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy '('EEE')'", Locale.getDefault());

                    String date = format.format(calendar.getTime());
                    eventDate.setText(date);
                    },
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

        int style = android.app.AlertDialog.THEME_HOLO_LIGHT;

        // at last we are calling show to
// display our time picker dialog.

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                EditEventActivity.this, style, (view1, hourOfDay, minute1) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute1);
            SimpleDateFormat format = new SimpleDateFormat("hh:mm aa", Locale.getDefault());

            String time = format.format(calendar.getTime());

            eventTime.setText( time);
            }, hour, minute, false);
        timePickerDialog.show();
    }

    public void saveData() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditEventActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
        updateData();
        dialog.dismiss();
    }

    public void updateData() {
        String title = eventTitle.getText().toString().trim();
        String date = eventDate.getText().toString().trim();
        String time = eventTime.getText().toString();
        String description = eventDescription.getText().toString().trim();

        Event event = new Event(title, date, time, description);

        reference = FirebaseDatabase.getInstance().getReference("users").child(useruid).child(key);


        reference.setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(EditEventActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditEventActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
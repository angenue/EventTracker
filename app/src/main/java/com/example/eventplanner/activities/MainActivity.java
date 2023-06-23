package com.example.eventplanner.activities;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.Event;
import com.example.eventplanner.EventAdapter;
import com.example.eventplanner.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    NotificationManager notificationManager;
    FirebaseUser user;
    RecyclerView recyclerView;
    List<Event> eventList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    DatabaseError databaseError;
    LinearLayoutManager linearLayoutManager;
    EventAdapter adapter;
    Event event;
    Intent intent;
    PendingIntent pendingIntent;
    NotificationCompat.Builder mBuilder;
    FloatingActionButton floatingActionButton;
    SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButton = findViewById(R.id.add_button);
        event = new Event();
        eventList = new ArrayList<>();
        dateFormat = new SimpleDateFormat("MMM dd, yyyy '('EEE')'", Locale.getDefault());

        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle(R.string.event_tracker);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String useruid = user.getUid();


        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }

        /*****/
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        adapter = new EventAdapter(MainActivity.this, eventList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(useruid);

        eventListener = databaseReference.orderByChild("eventDate")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventList.clear();

                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    event = itemSnapshot.getValue(Event.class);
                    sortTime();
                    sortDate();
                    try {
                        recyclerView.scrollToPosition(getItemPosition());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    event.setKey(itemSnapshot.getKey());
                    eventList.add(event);
                }

                adapter.notifyDataSetChanged();
                dialog.dismiss();

                /*try {
                    if(eventList.size() != 0) {

                        if (getTodaysDate().equals(eventList.get(getItemPosition()).getEventDate())) {
                            createNotif();
                        }
                    }

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }*/

                for (Event event: eventList) {
                    if (getTodaysDate().equals(event.getEventDate())) {
                        createNotif(event);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: " + databaseError.getCode());
                dialog.dismiss();
            }
        });

    }

    private String getTodaysDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy '('EEE')'", Locale.getDefault());

        Instant now = Instant.now();
        Date date = Date.from(now);

        return dateFormat.format(date);
    }

    public void addButtonClick(View view) {
        Intent intent = new Intent(this, AddEventActivity.class);
        startActivity(intent);
    }

    // Inflating the menu items from the menu_items.xml file
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Handling the click events of the menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Switching on the item id of the menu item
        if (item.getItemId() == R.id.settings_button) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sortDate() {
        eventList.sort((o1, o2) -> {
            try {
                return dateFormat.parse(o1.getEventDate()).compareTo(dateFormat.parse(o2.getEventDate()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void sortTime() {
        eventList.sort((o1, o2) -> {
            SimpleDateFormat format = new SimpleDateFormat("hh:mm aa", Locale.getDefault());

            try {
                return format.parse(o1.getEventTime()).compareTo(format.parse(o2.getEventTime()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public int getItemPosition() throws ParseException {
        Instant now = Instant.now();
        Instant yesterdayDate = now.minus(1, ChronoUnit.DAYS);

        for(int i = 0; i < eventList.size(); i++) {

            if(dateFormat.parse(eventList.get(i).getEventDate()).after(Date.from(yesterdayDate))) {
                return i;
            }
        }
        return -1;
    }

    public void createNotif (Event event) {
        String notificationTitle = "You have an event today: " + event.getTitle();
        String notificationMessage = event.getEventDate() + " at " + event.getEventTime();

        intent = new Intent(MainActivity.this, EditEventActivity.class);

        intent.putExtra("Title", event.getTitle());
        intent.putExtra("Date", event.getEventDate());
        intent.putExtra("Time", event.getEventTime());
        intent.putExtra("Description", event.getDescription());

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        mBuilder = new NotificationCompat.Builder(MainActivity.this, "event")
                .setSmallIcon(R.drawable.baseline_event_24)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                //24 hours to milliseconds
                .setTimeoutAfter(86544000);
        createNotificationChannel();
        notificationManager.notify(0, mBuilder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("event", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

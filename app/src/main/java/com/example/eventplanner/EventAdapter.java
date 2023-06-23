package com.example.eventplanner;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.activities.EditEventActivity;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context context;
    private List<Event> eventList;

    public EventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.eventDate.setText(eventList.get(position).getEventDate());
        holder.eventTitle.setText(eventList.get(position).getTitle());
        holder.eventTime.setText(eventList.get(position).getEventTime());

        holder.eventCard.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditEventActivity.class);
            intent.putExtra("Title", eventList.get(holder.getAdapterPosition()).getTitle());
            intent.putExtra("Date", eventList.get(holder.getAdapterPosition()).getEventDate());
            intent.putExtra("Time", eventList.get(holder.getAdapterPosition()).getEventTime());
            intent.putExtra("Description", eventList.get(holder.getAdapterPosition()).getDescription());
            intent.putExtra("Key",eventList.get(holder.getAdapterPosition()).getKey());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

}


class MyViewHolder extends RecyclerView.ViewHolder {

    TextView eventDate, eventTitle, eventTime, eventDescription;
    LinearLayout eventCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        eventDate = itemView.findViewById(R.id.event_date);
        eventTitle = itemView.findViewById(R.id.event_name);
        eventTime = itemView.findViewById(R.id.event_time);
        eventCard = itemView.findViewById(R.id.event_card);
        eventDescription = itemView.findViewById(R.id.event_description);
    }
}

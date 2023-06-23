package com.example.eventplanner;

public class Event {
    private String title, description;
    private String eventTime;
    private String eventDate;
    private String key;

    public Event() {

    }

    public Event(String title, String eventDate, String eventTime, String description) {
        this.title = title;
        this.description = description;
        this.eventTime = eventTime;
        this.eventDate = eventDate;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }
}

package com.BH44IT.volunteam.models;

public class Volunteer {

    private String eventName;
    private String volunteer;

    public Volunteer(String eventName, String volunteer) {
        this.eventName = eventName;
        this.volunteer = volunteer;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(String volunteer) {
        this.volunteer = volunteer;
    }
}

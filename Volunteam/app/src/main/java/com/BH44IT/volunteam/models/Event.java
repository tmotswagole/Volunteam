package com.BH44IT.volunteam.models;

import android.location.Location;

import java.io.File;
import java.util.Date;

public class Event {

    private String name;
    private String desc;
    private File picture;
    private Location location;
    private String type;
    private String hostEmail;
    private int hostContact;
    private Date date;

    public Event(String name, String desc, File picture, Location location, String type, String hostEmail, int hostContact, Date date) {
        this.name = name;
        this.desc = desc;
        this.picture = picture;
        this.location = location;
        this.type = type;
        this.hostEmail = hostEmail;
        this.hostContact = hostContact;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public File getPicture() {
        return picture;
    }

    public void setPicture(File picture) {
        this.picture = picture;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHostEmail() {
        return hostEmail;
    }

    public void setHostEmail(String hostEmail) {
        this.hostEmail = hostEmail;
    }

    public int getHostContact() {
        return hostContact;
    }

    public void setHostContact(int hostContact) {
        this.hostContact = hostContact;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

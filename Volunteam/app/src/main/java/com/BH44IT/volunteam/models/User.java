package com.BH44IT.volunteam.models;

public class User {

    private String email;
    private String password;
    private String fname;
    private String lname;
    private int contacts;
    private String type;

    public User(String email, String password, String fname, String lname, int contacts, String type) {
        this.email = email;
        this.password = password;
        this.fname = fname;
        this.lname = lname;
        this.contacts = contacts;
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public int getContacts() {
        return contacts;
    }

    public void setContacts(int contacts) {
        this.contacts = contacts;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

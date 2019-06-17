package com.BH44IT.volunteam.databaseAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import com.BH44IT.volunteam.models.Event;
import com.BH44IT.volunteam.models.Review;
import com.BH44IT.volunteam.models.User;
import com.BH44IT.volunteam.models.Volunteer;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseAccess extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseAccess";

    private static final String DATABASE_NAME = "Volunteam";
    private static final int DATABASE_Version = 2;

    private Context context;

    public DatabaseAccess(Context context)
    {
        super(context, DATABASE_NAME + ".db", null, DATABASE_Version);
        this.context = context;
    }

    public User getUser(String email) {
        User user = null;
        Cursor cursor = this.getReadableDatabase().rawQuery("select * from User where email='"+email+"';", null);
        while(cursor.moveToNext()) {
            user = new User(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getString(5));
        }

        if ( cursor.moveToFirst() ) {
            return user;
        } else {
            return null;
        }
        //return user;
    }

    public Review[] getReview(String organizer) {

        Log.d(TAG, "getReview: called");

        Review[] review = null;
        Cursor cursor = this.getReadableDatabase().rawQuery("select * from Review where organizer='"+organizer+"';", null);
        int i = 0;
        review = new Review[cursor.getCount()];
        while ((cursor.moveToNext())) {
            review[i] = new Review(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
            i++;
        }

        if ( cursor.moveToFirst() ) {
            return review;
        } else {
            return null;
        }
        //return review;
    }

    public User[] getAllOrganizers() {
        Log.d(TAG, "getAllOrganizers: called");

        User[] user = null;
        Cursor cursor = this.getReadableDatabase().rawQuery("select * from User where type='Organizer';", null);
        int i = 0;
        user = new User[cursor.getCount()];
        while ((cursor.moveToNext())) {
            user[i] = new User(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getString(5));
            i++;
        }

        if ( cursor.moveToFirst() ) {
            return user;
        } else {
            return null;
        }
        //return user;
    }

    public String getAverageRating(String organizer) {

        Log.d(TAG, "getListOfVolunteers: called");

        String avgRating = "n/a";

        Cursor cursor = this.getReadableDatabase().rawQuery("select avg(rating) from Review where organizer='"+organizer+"';", null);
        while ((cursor.moveToNext())) {
            avgRating = cursor.getString(0);
        }

        return avgRating;
    }

    public void registerUser(String email, String password, String fname, String lname, int contacts, String type) {

        try {
            Log.d(TAG, "registerUser: called");

            ContentValues cv = new ContentValues();
            cv.put("email", email);
            cv.put("password", password);
            cv.put("fname", fname);
            cv.put("lname", lname);
            cv.put("contacts", contacts);
            cv.put("type", type);
            this.getWritableDatabase().insertOrThrow("User", "", cv);
        } catch(Exception e) {
            Log.e(TAG, "registerUser: Exception: " + e.getMessage());
        }
    }

    public void reviewOrganizer(int rating, String reviewer, String organizer, String comment) {

        Log.d(TAG, "reviewOrganizer: called");

        ContentValues cv = new ContentValues();
        cv.put("rating", rating);
        cv.put("reviewer", reviewer);
        cv.put("organizer", organizer);
        cv.put("comment", comment);
    }

    public  void updateUser(String email, String password, int contacts, String fname, String lname) {

        Log.d(TAG, "updateUser: called");

        this.getWritableDatabase().execSQL("update User set password='"+password+"', contacts="+contacts+", lname='"+lname+"', fname='"+fname+"' where email='"+email+"'");
    }

    // EVENT METHODS

    public Event getEvent(String name) throws ParseException {

        Log.d(TAG, "getEvent: called");

        Location location = new Location("");
        File image;
        Event event = null;
        Cursor cursor = this.getReadableDatabase().rawQuery("select * from Event where name='"+name+"';", null);
        while ((cursor.moveToNext())) {
            image = new File(cursor.getString(2));
            location.setLongitude(cursor.getInt(3));
            location.setLatitude(cursor.getInt(4));
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(cursor.getString(7));
            event = new Event(cursor.getString(0), cursor.getString(1), image, location, cursor.getString(4), cursor.getString(5), cursor.getInt(6), date);
        }

        if ( cursor.moveToFirst() ) {
            return event;
        } else {
            return null;
        }
        //return event;
    }

    public Event[] getOrganizerEvents(String email) throws ParseException {

        Log.d(TAG, "getOrganizerEvents: called");

        Location location = new Location("");
        File image;
        Event[] events = null;
        Cursor cursor = this.getReadableDatabase().rawQuery("select * from Event where hostEmail='"+email+"';", null);
        int i = 0;
        if(cursor.getCount() == 0) return null;
        events = new Event[cursor.getCount()];
        while ((cursor.moveToNext())) {
            image = new File(cursor.getString(2));
            location.setLongitude(cursor.getInt(3));
            location.setLatitude(cursor.getInt(4));
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(cursor.getString(7));
            events[i] = new Event(cursor.getString(0), cursor.getString(1), image, location, cursor.getString(4), cursor.getString(5), cursor.getInt(6), date);
            i++;
        }

        if ( cursor.moveToFirst() ) {
            return events;
        } else {
            return null;
        }
        //return events;
    }

    public Event[] getAllEvents() throws ParseException {

        Log.d(TAG, "getAllEvents: called");

        Location location = new Location("");
        File image;
        Event[] events = null;
        Cursor cursor = this.getReadableDatabase().rawQuery("select * from Event;", null);
        int i = 0;
        events = new Event[cursor.getCount()];
        while ((cursor.moveToNext())) {
            image = new File(cursor.getString(2));
            location.setLongitude(cursor.getInt(3));
            location.setLatitude(cursor.getInt(4));
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(cursor.getString(7));
            events[i] = new Event(cursor.getString(0), cursor.getString(1), image, location, cursor.getString(4), cursor.getString(5), cursor.getInt(6), date);
            i++;
        }

        if ( cursor.moveToFirst() ) {
            return events;
        } else {
            return null;
        }
        //return events;
    }

    public String getNumberOfVolunteers(String eventName) {

        Log.d(TAG, "getNumberOfVolunteers: called");

        String volunteers = "";
        Cursor cursor = this.getReadableDatabase().rawQuery("select count(eventName) from Volunteer where eventName='"+eventName+"';", null);
        while ((cursor.moveToNext())) {
            volunteers = cursor.getString(0);
        }

        if ( cursor.moveToFirst() ) {
            return volunteers;
        } else {
            return null;
        }
        //return volunteers;
    }

    public String[] getListOfVolunteers(String eventName) {

        Log.d(TAG, "getListOfVolunteers: called");

        String[] volunteers = null;
        Cursor cursor = this.getReadableDatabase().rawQuery("select volunteer from Volunteer where eventName='"+eventName+"';", null);
        int i = 0;
        volunteers = new String[cursor.getCount()];
        while ((cursor.moveToNext())) {
            volunteers[i] = cursor.getString(0);
            i++;
        }

        if ( cursor.moveToFirst() ) {
            return volunteers;
        } else {
            return null;
        }
        //return volunteers;
    }

    public Volunteer[] getEventVolunteers(String eventName) {

        Log.d(TAG, "getEventVolunteers: called");

        Volunteer[] volunteers = null;
        Cursor cursor = this.getReadableDatabase().rawQuery("select * from Volunteer where eventName='"+eventName+"';", null);
        int i = 0;
        volunteers = new Volunteer[cursor.getCount()];
        while ((cursor.moveToNext())) {
            volunteers[i] = new Volunteer(cursor.getString(0), cursor.getString(1));
            i++;
        }

        if ( cursor.moveToFirst() ) {
            return volunteers;
        } else {
            return null;
        }
        //return volunteers;
    }

    public Event[] getEventsYoureVolunteering(String volunteer) throws ParseException {

        Log.d(TAG, "getEventsYoureVolunteering: called");

        Location location = new Location("");
        File image;
        Event[] events = null;
        Cursor cursor = this.getReadableDatabase().rawQuery("select Event.* from Event, Volunteer where Volunteer.volunteer='"+volunteer+"' and volunteer.eventName=event.name;", null);
        int i = 0;
        events = new Event[cursor.getCount()];
        while ((cursor.moveToNext())) {
            image = new File(cursor.getString(2));
            location.setLongitude(cursor.getInt(3));
            location.setLatitude(cursor.getInt(4));
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(cursor.getString(7));
            events[i] = new Event(cursor.getString(0), cursor.getString(1), image, location, cursor.getString(4), cursor.getString(5), cursor.getInt(6), date);
            i++;
        }

        if ( cursor.moveToFirst() ) {
            return events;
        } else {
            return null;
        }
        //return events;
    }

    public void startEvent(String name, String desc, File picture, Location location, String type, String hostEmail, int hostContact, Date date) {

        Log.d(TAG, "startEvent: called");

        String path = picture.getAbsolutePath();

        String dateString = date.getDay() + "/" + date.getMonth() + "/" + date.getYear();

        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("description", desc);
        cv.put("picture", path);
        cv.put("longitude", location.getLongitude());
        cv.put("latitude", location.getLatitude());
        cv.put("type", type);
        cv.put("hostEmail", hostEmail);
        cv.put("hostContact", hostContact);
        cv.put("event_date", dateString);
        this.getWritableDatabase().insertOrThrow("Event", "", cv);
    }

    public void deleteEvent(String name) {

        Log.d(TAG, "deleteEvent: called");

        this.getWritableDatabase().delete("Event", "name = '" + name + "'", null);
    }

    public void editEvent(String name, String desc, File picture, Location location, String type, String hostEmail, int hostContact, Date date) {

        Log.d(TAG, "deleteEvent: called");

        String dateString = date.getDay() + "/" + date.getMonth() + "/" + date.getYear();

        this.getWritableDatabase().execSQL("update event set description = '"+desc+"', picture = '"+picture.getAbsolutePath()+"', longitude = "+location.getLongitude()+", latitude = "+location.getLatitude()+", type = '"+type+"', hostContact = "+hostContact+", event_date = '"+dateString+"' where name='"+name+"';");
    }

    public void volunteerForEvent(String eventName, String volunteer) {

        Log.d(TAG, "volunteerForEvent: called");

        ContentValues cv = new ContentValues();
        cv.put("eventName", eventName);
        cv.put("volunteer", volunteer);
        this.getWritableDatabase().insertOrThrow("Volunteer", "", cv);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(TAG, "onCreate: called");

        db.execSQL("create table User(email varchar(50) Primary key, password varchar(100), fname varchar(50), lname varchar(50), contacts integer(20), type varchar(20));");
        db.execSQL("create table Review(rating integer, reviewer varchar(50), organizer varchar(50), comment varchar(200), PRIMARY KEY (reviewer, organizer));");

        db.execSQL("create table Event(name varchar(50), description varchar(100), picture varchar(500), longitude integer, latitude integer, type varchar(50), hostEmail varchar(50), hostContact int, event_date varchar(50));");
        db.execSQL("create table Volunteer(eventName varchar(50), volunteer varchar(50));");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(TAG, "onUpgrade: called");

        db.execSQL("drop table if exists User;");
        db.execSQL("drop table if exists Review;");
        db.execSQL("drop table if exists Event;");
        db.execSQL("drop table if exists Volunteer;");
        onCreate(db);
    }
}

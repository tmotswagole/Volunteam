package com.BH44IT.volunteam.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.BH44IT.volunteam.R;
import com.BH44IT.volunteam.databaseAccess.DatabaseAccess;
import com.BH44IT.volunteam.fragments.CreateEventFragment;
import com.BH44IT.volunteam.fragments.EditEventFragment;
import com.BH44IT.volunteam.fragments.EventsListFragment;
import com.BH44IT.volunteam.fragments.MyEditEventRecyclerViewAdapter;
import com.BH44IT.volunteam.fragments.OrganizerEventListFragment;
import com.BH44IT.volunteam.models.Event;
import com.BH44IT.volunteam.models.Review;
import com.BH44IT.volunteam.models.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OrganizerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "OrganizerActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private Intent intent;
    private Context context;
    private String email;
    private DatabaseAccess databaseAccess;

    // objects
    private Event[] events;
    private User user;
    private Bitmap imageBitmap;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Location location;
    public Location eventLocation;

    // var
    private FragmentManager manager;
    private String calledCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        location = new Location("");

        manager = getSupportFragmentManager();

        intent = getIntent();
        email = intent.getStringExtra("email");

        databaseAccess = new DatabaseAccess(context);

        try {
            events = null;
            user = databaseAccess.getUser(email);
            if(databaseAccess.getOrganizerEvents(email) != null) { events = databaseAccess.getOrganizerEvents(email); }
        } catch (Exception e) {
            e.printStackTrace();
        }

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {

        Log.d(TAG, "onBackPressed: back button pressed");

        /*DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.d(TAG, "onCreateOptionsMenu: called");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.organizer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG, "onOptionsItemSelected: called");

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Log.d(TAG, "onNavigationItemSelected: called");

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_org_home) {

            Log.d(TAG, "onNavigationItemSelected: nav_org_home: selected");

        } else if (id == R.id.nav_org_event) {


            Log.d(TAG, "onNavigationItemSelected: nav_org_event: selected");

            // call fragment: fragment_create_event.xml  CreateEventFragment.java

            CreateEventFragment create = new CreateEventFragment();
            manager.beginTransaction()
                    .replace(R.id.org_main_layout, create, create.getTag())
                    .commit();


        } else if (id == R.id.nav_org_list) {

            Log.d(TAG, "onNavigationItemSelected: nav_org_list: selected");

            try {
                if(databaseAccess.getOrganizerEvents(user.getEmail()) != null) {
                    OrganizerEventListFragment create = new OrganizerEventListFragment();
                    manager.beginTransaction()
                            .replace(R.id.org_main_layout, create, create.getTag())
                            .commit();
                } else {
                    Toast.makeText(this, "No events!", Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else if (id == R.id.nav_org_logout) {

            Log.d(TAG, "onNavigationItemSelected: nav_org_logout: selected");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*----------------------EDIT METHODS---------------------------------------*/

    /*private void updateEditLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edit_date_pick.setText(sdf.format(myCalendar.getTime()));
    }

    private void editChooseDate(View view) {

        edit_date_pick = findViewById(R.id.edit_date_pick);


        editDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                datePicker = view;
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateEditLabel();
            }
        };

        edit_date_pick.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(OrganizerActivity.this, editDate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    } */

    public void editEvent(View view) {

        Log.d(TAG, "editEvent: called");

        location.setLatitude(eventLocation.getLatitude());
        location.setLongitude(eventLocation.getLongitude());

        EditText event_name, event_desc, event_task;
        DatePicker edit_date_pick;
        Spinner type_spinner;
        ImageView preview_image;

        String name, desc, task, type, imgLink;
        Bitmap image;

        event_name = findViewById(R.id.edit_event_name);
        event_desc = findViewById(R.id.edit_event_desc);
        event_task = findViewById(R.id.edit_event_task);
        edit_date_pick = findViewById(R.id.edit_date_pick);
        type_spinner = findViewById(R.id.edit_type_spinner);
        preview_image = findViewById(R.id.edit_preview_image);

        name = event_name.getText().toString();
        desc = event_desc.getText().toString();
        task = event_task.getText().toString();
        type = type_spinner.getSelectedItem().toString();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) preview_image.getDrawable();
        image = bitmapDrawable.getBitmap();

        File img = new File(saveToInternalStorage(image));

        DatabaseAccess databaseAccess = new DatabaseAccess(this);

        Date date = getDateFromDatePicker(edit_date_pick);

        databaseAccess.editEvent(name, desc, img, location, type, user.getEmail(), user.getContacts(), date);

        Toast.makeText(this, "Edit the event", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, OrganizerActivity.class);
        startActivity(intent);

    }

    public static java.util.Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    public String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(this);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory,"event.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public Bitmap loadImageFromStorage(String path)
    {

        try {
            File f = new File(path, "event.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return null;

    }

    public void editUploadImage(View view) {
        Log.d(TAG, "uploadImage: called");

        calledCamera = "edit";

        dispatchTakePictureIntent();

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "An error occurred while opening the camera", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            if(calledCamera.equals("create")) {
                ImageView preview_image = this.findViewById(R.id.preview_image);
                preview_image.setImageBitmap(imageBitmap);
            } else {
                ImageView edit_preview_image = this.findViewById(R.id.edit_preview_image);
                edit_preview_image.setImageBitmap(imageBitmap);
            }

        }
    }

    public boolean isServicesOk() {
        Log.d(TAG, "isServicesOk: checking Google services version.");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if(available == ConnectionResult.SUCCESS) {
            // user can make map requests
            Log.d(TAG, "isServicesOk: Google Play services is working");
            return true;
        } else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // an error occurred but can be resolved
            Log.d(TAG, "isServicesOk: Error occurred but can be fixed.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    /*----------------------CREATE METHODS---------------------------------------*/

    public void createEvent(View view) {
        Log.d(TAG, "createEvent: called");

        location.setLatitude(eventLocation.getLatitude());
        location.setLongitude(eventLocation.getLongitude());

        EditText event_name, event_desc, event_task;
        Spinner type_spinner;
        ImageView preview_image;
        DatePicker date_pick;

        String name, desc, task, type, imgLink;
        Bitmap image;

        event_name = findViewById(R.id.event_name);
        event_desc = findViewById(R.id.event_desc);
        event_task = findViewById(R.id.event_task);
        date_pick = findViewById(R.id.date_pick);
        type_spinner = findViewById(R.id.type_spinner);
        preview_image = findViewById(R.id.preview_image);

        name = event_name.getText().toString();
        desc = event_desc.getText().toString();
        task = event_task.getText().toString();
        type = type_spinner.getSelectedItem().toString();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) preview_image.getDrawable();
        image = bitmapDrawable.getBitmap();

        File img = new File(saveToInternalStorage(image));

        DatabaseAccess databaseAccess = new DatabaseAccess(this);

        Date date = getDateFromDatePicker(date_pick);

        Toast.makeText(this, "locations: " + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();

        databaseAccess.startEvent(name, desc, img, location, type, user.getEmail(), user.getContacts(), date);

        Toast.makeText(this, "Event created!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, OrganizerActivity.class);
        startActivity(intent);

    }

    /*private void chooseDate(View view) {

        date_pick = findViewById(R.id.date_pick);


        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                datePicker = view;
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        date_pick.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(OrganizerActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        date_pick.setText(sdf.format(myCalendar.getTime()));
    } */

    public void uploadImage(View view) {
        Log.d(TAG, "uploadImage: called");

        calledCamera = "create";

        dispatchTakePictureIntent();

    }

    public void openMap(View view) {
        Log.d(TAG, "openMap: called");

        if(isServicesOk()) init();

    }

    private void init() {

        Log.d(TAG, "init: called");

        Intent intent = new Intent(OrganizerActivity.this, EventMapActivity.class);
        startActivity(intent);

    }

    public void switchContent(int id, Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(id, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void populateEventData(String eventName) {
        DatabaseAccess databaseAccess = new DatabaseAccess(context);
        Event event = null;

        try {
            event = databaseAccess.getEvent(eventName);
        } catch(Exception e) {
            e.printStackTrace();
        }


        EditText event_name, event_desc, edit_location_holder_long, edit_location_holder_lat;
        DatePicker date_picker;
        Spinner type_spinner;
        ImageView preview_image;

        event_name = findViewById(R.id.edit_event_name);
        event_desc = findViewById(R.id.edit_event_desc);
        type_spinner = findViewById(R.id.edit_type_spinner);
        preview_image = findViewById(R.id.edit_preview_image);
        date_picker = findViewById(R.id.edit_date_pick);
        edit_location_holder_long = findViewById(R.id.edit_location_holder_long);
        edit_location_holder_lat = findViewById(R.id.edit_location_holder_lat);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type_arrays, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(event.getType());
        type_spinner.setSelection(spinnerPosition);

        Date date = event.getDate();

        event_name.setText(event.getName());
        event_desc.setText(event.getDesc());
        date_picker.updateDate(date.getYear(), date.getMonth(), date.getDay());

        String lat = Double.toString(event.getLocation().getLatitude());
        String lng = Double.toString(event.getLocation().getLongitude());

        edit_location_holder_long.setText(lng);
        edit_location_holder_lat.setText(lat);

        if(event.getPicture().exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(event.getPicture().getAbsolutePath());

            preview_image.setImageBitmap(myBitmap);

        }

    }

}

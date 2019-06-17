package com.BH44IT.volunteam.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuInflater;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.BH44IT.volunteam.R;
import com.BH44IT.volunteam.databaseAccess.DatabaseAccess;
import com.BH44IT.volunteam.fragments.MyEventsListRecyclerViewAdapter;
import com.BH44IT.volunteam.fragments.OrganizerEventListFragment;
import com.BH44IT.volunteam.fragments.OrganizerItemFragment;
import com.BH44IT.volunteam.models.Event;
import com.BH44IT.volunteam.models.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.text.ParseException;
import java.util.ArrayList;

public class VolunteerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "VolunteerActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String CHANNEL_ID = "channel_01";


    private Intent intent;
    private Context context;
    private String email;
    private DatabaseAccess databaseAccess;

    // objects
    private User user;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private MyEventsListRecyclerViewAdapter adapter;
    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult connectionResult;
    private Location mLastLocation;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer);

        context = this;

        intent = getIntent();
        email = intent.getStringExtra("email");

        manager = getSupportFragmentManager();

        databaseAccess = new DatabaseAccess(context);

        notifyUser();

        user = databaseAccess.getUser(email);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void init() {

        Log.d(TAG, "init: called");

        Intent intent = new Intent(VolunteerActivity.this, MapDisplayActivity.class);
        startActivity(intent);
    }

    public boolean isServicesOk() {
        Log.d(TAG, "isServicesOk: checking Google services version.");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(VolunteerActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            // user can make map requests
            Log.d(TAG, "isServicesOk: Google Play services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // an error occurred but can be resolved
            Log.d(TAG, "isServicesOk: Error occurred but can be fixed.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(VolunteerActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_vol_home) {


        } else if (id == R.id.nav_vol_near_by_list) {

            Log.d(TAG, "onNavigationItemSelected: near by list clicked");

            try {
                if (databaseAccess.getAllEvents() == null) {
                    Toast.makeText(context, "There are currently no events!", Toast.LENGTH_SHORT).show();
                } else {
                    if(isServicesOk()) {
                        init();
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else if (id == R.id.nav_vol_event_list) {

            Log.d(TAG, "onNavigationItemSelected: event list clicked");

            try {
                if (databaseAccess.getAllEvents() == null) {
                    Toast.makeText(context, "There are currently no events!", Toast.LENGTH_SHORT).show();
                } else {
                    OrganizerItemFragment create = new OrganizerItemFragment();
                    manager.beginTransaction()
                            .replace(R.id.org_main_layout, create, create.getTag())
                            .commit();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else if (id == R.id.nav_vol_users) {

            Log.d(TAG, "onNavigationItemSelected: nav_vol_users clicked");

            // open review list: OrganizerItemFragment
            if(databaseAccess.getAllOrganizers() != null) {
                OrganizerItemFragment create = new OrganizerItemFragment();
                manager.beginTransaction()
                        .replace(R.id.org_main_layout, create, create.getTag())
                        .commit();
            } else {
                Toast.makeText(this, "No events!", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_vol_logout) {

            Log.d(TAG, "onNavigationItemSelected: nav_vol_logout: selected");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void notifyUser() {
        try {
            if (databaseAccess.getAllEvents() != null) {
                Event[] events = databaseAccess.getAllEvents();

                int numberOfEvents = 0;

                if (mGoogleApiClient == null) {
                    mGoogleApiClient = new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
                }
                if (mGoogleApiClient != null) {
                    mGoogleApiClient.connect();
                }

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                for (int i = 0; i < events.length; i++) {
                    float distance = currentLocation.distanceTo(events[i].getLocation());
                    if (100 > distance) numberOfEvents++;
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Events")
                        .setContentText("There are " + numberOfEvents + " events near you! Check the the events list to see!")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Events located in 100km!"))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        this.connectionResult = connectionResult;
    }

    public void switchContent(int id, Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(id, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void populateUserData(String email) {
        EditText email_data = findViewById(R.id.organizer_email);
        email_data.setText(email);
    }

    public void submitReview(View view) {
        RatingBar rating = findViewById(R.id.rating);
        EditText comment = findViewById(R.id.comment);
        EditText organizer_email = findViewById(R.id.organizer_email);

        databaseAccess.reviewOrganizer(rating.getNumStars(), user.getEmail(), organizer_email.getText().toString(), comment.getText().toString());
        Toast.makeText(this, "Review submitted!", Toast.LENGTH_SHORT).show();
    }

    public void volunteer(String eventName) {
        DatabaseAccess databaseAccess = new DatabaseAccess(this);
        databaseAccess.volunteerForEvent(eventName, user.getEmail());
    }

    /*private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }*/

}

package com.BH44IT.volunteam.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.BH44IT.volunteam.R;
import com.BH44IT.volunteam.databaseAccess.DatabaseAccess;
import com.BH44IT.volunteam.models.Event;
import com.BH44IT.volunteam.models.PlaceInfo;
import com.BH44IT.volunteam.models.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.ParseException;

public class MapDisplayActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MDisplayActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136)
    );

    private boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private PlaceInfo mPlace;
    private Marker mMarker;

    // widgets
    private ImageView mGps;

    // var
    Intent intent;
    Event[] events;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_display);

        mGps = findViewById(R.id.ic_gps_displayEvent);

        getLocationPermission();


        intent = getIntent();
        String email = intent.getStringExtra("email");

        DatabaseAccess databaseAccess = new DatabaseAccess(this);

        user = databaseAccess.getUser(email);

        try {
            events = databaseAccess.getAllEvents();
        } catch(ParseException e) {
            Log.e(TAG, "getAllEvents: ParseException: " + e.getMessage());
        }
    }

    private void init() {
        Log.d(TAG, "init: Initializing");

        GoogleApiClient mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "init: onClick: clicked GPS icon");
                getDeviceLocation();
            }
        });

        hideSoftKeyboard();
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: Getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        Log.d(TAG, "onRequestPermissionsResult: Called");

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: Permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: Permission granted");
                    mLocationPermissionGranted = true;
                    // initialize our map
                    initMap();
                }
            }
        }
    }

    private void initMap() {
        Log.d(TAG, "initMap: Initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.event_display_map);

        mapFragment.getMapAsync(MapDisplayActivity.this);

        // loop to populate map with markers of events
        for(int i = 0; i < events.length; i++) {
            MarkerOptions options = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_users))
                    .position(new LatLng(events[i].getLocation().getLatitude(), events[i].getLocation().getLongitude()))
                    .title(events[i].getName());

            mMap.addMarker(options);
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: Getting the device's current location");

        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Found location");
                            Location currentLocation = (Location)task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My location");
                        } else {
                            Log.d(TAG, "onComplete: Current location is null");
                            Toast.makeText(MapDisplayActivity.this, "Couldn't get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: Move the camera to Lat: '" + latLng.latitude + "' and Long: '" + latLng.longitude + "'");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        // Will use to mark multiple event areas
        MarkerOptions options = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_users))
                .position(latLng)
                .title(title);

        mMap.addMarker(options);

        hideSoftKeyboard();
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(mMarker)) {
            // marker clicked
            LatLng latLng = marker.getPosition();

            // loop to check which of the events have that position
            for(int i = 0; i < events.length; i++) {
                if(events[i].getLocation().getLongitude() == latLng.longitude && events[i].getLocation().getLatitude() == latLng.latitude) {

                    volunteer(events[i].getHostEmail());
                    return true;
                }
            }

        }
        return false;
    }

    public void volunteer(String s) {
        final DatabaseAccess d = new DatabaseAccess(this);
        d.volunteerForEvent(s, user.getEmail());
        Event event = null;
        try {
            event = d.getEvent(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Contact");
        builder.setMessage("How would you like to contact the organizer?");

        // add the buttons
        final Event finalEvent2 = event;
        builder.setPositiveButton("SMS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                User u = d.getUser(finalEvent2.getHostEmail());
                smsUser(u.getContacts());
            }
        });

        final Event finalEvent1 = event;
        builder.setPositiveButton("Email", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                emailUser(finalEvent1.getHostEmail());
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void emailUser(String email) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, email); // organizer email
        intent.putExtra(Intent.EXTRA_SUBJECT, "User " + user.getEmail() + " Joining As Volunteer"); // subject message
        intent.putExtra(Intent.EXTRA_TEXT, "Volunteer " + user.getFname() + " would like to volunteer. Number: " + user.getContacts()); // message

        intent.setType("message/rfc822");
        this.startActivity(Intent.createChooser(intent, "Choose an email client"));

    }

    public void smsUser(int contacts) {

        try{
            SmsManager smgr = SmsManager.getDefault();
            smgr.sendTextMessage(String.valueOf(contacts),null,"HHi, I'm a new volunteer! My name is " + user.getFname(),null,null);
            Toast.makeText(this, "SMS Sent Successfully!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(this, "SMS Failed to Send, Please try again.", Toast.LENGTH_SHORT).show();
        }

    }

}

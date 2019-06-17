package com.BH44IT.volunteam.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.BH44IT.volunteam.R;
import com.BH44IT.volunteam.activities.EventMapActivity;
import com.BH44IT.volunteam.activities.OrganizerActivity;
import com.BH44IT.volunteam.databaseAccess.DatabaseAccess;
import com.BH44IT.volunteam.models.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class CreateEventFragment extends Fragment {

    private static final String TAG = "CreateEventFragment";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    final Calendar myCalendar = Calendar.getInstance();

    private Bitmap imageBitmap;
    private EditText date_pick;
    private DatePickerDialog.OnDateSetListener date;
    private Location location;
    private User user;
    private DatePicker datePicker;

    public CreateEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();

        location = new Location("");

        DatabaseAccess databaseAccess = new DatabaseAccess(getContext());
        user = databaseAccess.getUser(intent.getStringExtra("email"));

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_event, container, false);
    }

    public void chooseDate(View view) {

        date_pick = getActivity().findViewById(R.id.date_pick);


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
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        date_pick.setText(sdf.format(myCalendar.getTime()));
    }

    public void createEvent(View view) {
        Log.d(TAG, "createEvent: called");

        EditText longitude = getView().findViewById(R.id.location_holder_long);
        EditText latitude = getView().findViewById(R.id.location_holder_lat);

        String lat = latitude.getText().toString();
        String lng = longitude.getText().toString();

        location.setLongitude(Double.parseDouble(lng));
        location.setLatitude(Double.parseDouble(lat));

        EditText event_name, event_desc, event_task;
        Spinner type_spinner;
        ImageView preview_image;

        String name, desc, task, type, imgLink;
        Bitmap image;

        event_name = getView().findViewById(R.id.event_name);
        event_desc = getView().findViewById(R.id.event_desc);
        event_task = getView().findViewById(R.id.event_task);
        type_spinner = getView().findViewById(R.id.type_spinner);
        preview_image = getView().findViewById(R.id.preview_image);

        name = event_name.getText().toString();
        desc = event_desc.getText().toString();
        task = event_task.getText().toString();
        type = type_spinner.getSelectedItem().toString();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) preview_image.getDrawable();
        image = bitmapDrawable.getBitmap();

        File img = saveToInternalStorage(image);

        DatabaseAccess databaseAccess = new DatabaseAccess(getContext());

        Date date = getDateFromDatePicker(datePicker);

        databaseAccess.startEvent(name, desc, img, location, type, user.getEmail(), user.getContacts(), date);

        Toast.makeText(getContext(), "Edit the event", Toast.LENGTH_SHORT).show();


        Intent intent = new Intent(getContext(), OrganizerActivity.class);
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

    private File saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getContext());
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
        return directory;
    }

    public void uploadImage(View view) {
        Log.d(TAG, "uploadImage: called");

        dispatchTakePictureIntent();

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(getContext(), "An error occurred while opening the camera", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            ImageView preview_image = getActivity().findViewById(R.id.preview_image);
            preview_image.setImageBitmap(imageBitmap);
            saveToInternalStorage(imageBitmap);

        }
    }

    public void openMap(View view) {
        Log.d(TAG, "openMap: called");

        if(isServicesOk()) init();

    }

    private void init() {

        Log.d(TAG, "init: called");

        Intent intent = new Intent(getActivity(), EventMapActivity.class);
        intent.putExtra("fragment", "create");
        startActivity(intent);

    }

    private boolean isServicesOk() {
        Log.d(TAG, "isServicesOk: checking Google services version.");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());

        if(available == ConnectionResult.SUCCESS) {
            // user can make map requests
            Log.d(TAG, "isServicesOk: Google Play services is working");
            return true;
        } else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // an error occurred but can be resolved
            Log.d(TAG, "isServicesOk: Error occurred but can be fixed.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getContext(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

}

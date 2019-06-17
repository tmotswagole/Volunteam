package com.BH44IT.volunteam.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.BH44IT.volunteam.R;
import com.BH44IT.volunteam.databaseAccess.DatabaseAccess;
import com.BH44IT.volunteam.models.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private String email, password;
    private Context context;
    private TextInputEditText login_email, login_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Log.d(TAG, "onCreate: start");

        context = this;

        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);

    }

    public void login(View view) {

        Log.d(TAG, "login: called");

        email = login_email.getText().toString().trim().toLowerCase();
        password = login_password.getText().toString();

        if (email.equals("")) {
            login_email.setError("no email");
        } else if(password.equals("")) {
            login_password.setError("no password");
        } else {

            DatabaseAccess userDAO = new DatabaseAccess(this);

            if (userDAO.getUser(email) != null) {
                User user = userDAO.getUser(email);
                if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                    if (user.getType().equals("Organizer")) {
                        Intent intent = new Intent(context, OrganizerActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(context, VolunteerActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Email and password don't match!", Toast.LENGTH_SHORT).show();
                    login_password.setError("error");
                    login_password.setBackgroundResource(R.drawable.login_error);
                }
            } else {
                Toast.makeText(getApplicationContext(), "This user doesn't exist", Toast.LENGTH_SHORT).show();
                login_email.setError("error");
                login_email.setBackgroundResource(R.drawable.login_error);
            }
        }

    }

    public void moveToRegistration(View view) {
        Log.d(TAG, "moveToRegistration: called");
        Intent intent = new Intent(context, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }

}

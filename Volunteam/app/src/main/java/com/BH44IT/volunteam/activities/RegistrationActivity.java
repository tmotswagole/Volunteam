package com.BH44IT.volunteam.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.BH44IT.volunteam.R;
import com.BH44IT.volunteam.databaseAccess.DatabaseAccess;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";

    EditText reg_full_name, reg_contacts, reg_email, reg_password, reg_re_password;
    RadioGroup reg_type;
    String full_name, contacts, email, password, re_password, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Log.d(TAG, "onCreate: start");

        reg_full_name = findViewById(R.id.reg_full_name);
        reg_contacts = findViewById(R.id.reg_contacts);
        reg_email = findViewById(R.id.reg_email);
        reg_password = findViewById(R.id.reg_password);
        reg_re_password = findViewById(R.id.reg_re_password);
        reg_type = findViewById(R.id.reg_gender);

    }

    public void register(View view) {

        Log.d(TAG, "register: called");

        int selectedId = reg_type.getCheckedRadioButtonId();
        RadioButton typeButton = findViewById(selectedId);

        full_name = reg_full_name.getText().toString().trim();
        contacts = reg_contacts.getText().toString();
        email = reg_email.getText().toString().trim().replace(" ", "").toLowerCase();
        password = reg_password.getText().toString().trim().replace(" ", "");
        re_password = reg_re_password.getText().toString().trim().replace(" ", "");
        type = typeButton.getText().toString();
        String[] splitName = full_name.split("\\s", 2);

        DatabaseAccess databaseAccess = new DatabaseAccess(this);

        if(full_name.equals("")
        || contacts.equals("")
        || email.equals("")
        || password.equals("")
        || re_password.equals("")) {
            Toast.makeText(getApplicationContext(), "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
        } else {

            if (password.equals(re_password)) {
                if (databaseAccess.getUser(email) == null) {
                    databaseAccess.registerUser(email.trim().toLowerCase(), password, splitName[0], splitName[1], Integer.parseInt(contacts), type);
                    Intent intent = new Intent(this, LoginActivity.class);
                    Toast.makeText(getApplicationContext(), "Registered successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "This email was registered as a " + databaseAccess.getUser(email).getType() + " already!", Toast.LENGTH_SHORT).show();
                    reg_email.setError("error");
                    reg_email.setBackgroundResource(R.drawable.login_error);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Passwords don't match!", Toast.LENGTH_SHORT).show();
                reg_password.setError("error");
                reg_password.setBackgroundResource(R.drawable.login_error);
                reg_re_password.setError("error");
                reg_re_password.setBackgroundResource(R.drawable.login_error);
            }
        }

    }

    public void moveToLogin(View view) {
        Log.d(TAG, "moveToLogin: called");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

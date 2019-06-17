package com.BH44IT.volunteam.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.BH44IT.volunteam.R;
import com.BH44IT.volunteam.databaseAccess.DatabaseAccess;
import com.BH44IT.volunteam.models.Event;
import com.BH44IT.volunteam.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrganizerItemFragment extends Fragment {

    private static final String TAG = "OrganizerItemFragment";

    private Intent intent;

    private ArrayList<String> firstName;
    private ArrayList<String> lastName;
    private ArrayList<String> userEventsList;
    private ArrayList<String> emails;

    private MyOrganizerItemRecyclerViewAdapter adapter;
    private String organizer;

    public OrganizerItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getActivity().getIntent();
        organizer = intent.getStringExtra("organizer");
        populateLists();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_list, container, false);
        intent = getActivity().getIntent();
        organizer = intent.getStringExtra("organizer");
        populateLists();
        return view;
    }

    private void populateLists() {

        Log.d(TAG, "populateLists: called");

        /*User[] users = new User[0];
        String[] userEvents = new String[0];

        try {

            DatabaseAccess databaseAccess = new DatabaseAccess(getContext());
            users = databaseAccess.getAllOrganizers();
            userEvents = new String[users.length];

            for(int i = 0; i < users.length; i++) {

                if(databaseAccess.getOrganizerEvents(users[i].getEmail()) != null) {

                    Event[] events = databaseAccess.getOrganizerEvents(users[i].getEmail());

                    for (int inc = 0; inc < events.length; inc++) {
                        if (userEvents[i] == null || userEvents[i].contains(""))
                            userEvents[i] = events[inc].getName() + ", ";
                        else userEvents[i] = userEvents[i - 1] + events[inc].getName() + ", ";
                    }
                } else {
                    userEvents[i] = "no events";
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "populateLists: for: " + e.getMessage());
        }*/

        firstName = new ArrayList<>();
        lastName = new ArrayList<>();
        userEventsList = new ArrayList<>();
        emails = new ArrayList<>();

        /*for(int i = 0; i < users.length; i++) {

            Log.d(TAG, "populateLists: for: user: " + users[i].getEmail());

            firstName.add(users[i].getFname());
            lastName.add(users[i].getLname());
            emails.add(users[i].getEmail());
            userEventsList.add(userEvents[i]);
        }*/

        initRecyclerView();

    }

    private void initRecyclerView() {

        Log.d(TAG, "initRecyclerView: called");

        RecyclerView recyclerView = Objects.requireNonNull(getView()).findViewById(R.id.org_list);
        adapter = new MyOrganizerItemRecyclerViewAdapter(firstName, lastName, userEventsList, emails, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ConstraintLayout item = getView().findViewById(R.id.main_org_layout);
        View child = getLayoutInflater().inflate(R.layout.fragment_organizer_list, null);
        item.addView(child);

    }


}

package com.BH44IT.volunteam.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import com.BH44IT.volunteam.R;
import com.BH44IT.volunteam.activities.EventMapActivity;
import com.BH44IT.volunteam.activities.VolunteerActivity;
import com.BH44IT.volunteam.databaseAccess.DatabaseAccess;
import com.BH44IT.volunteam.models.Event;
import com.BH44IT.volunteam.models.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.text.ParseException;
import java.util.ArrayList;

public class EventsListFragment extends Fragment {

    private static final String TAG = "EventsListFragment";

    private ArrayList<String> mImages;
    private ArrayList<String> mName;
    private ArrayList<String> mDesc;
    private ArrayList<String> mType;
    private ArrayList<String> avgRating;

    private String email;
    private User user;
    private Event[] events;
    private DatabaseAccess databaseAccess;
    private MyEventsListRecyclerViewAdapter adapter;

    public EventsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        email = intent.getStringExtra("email");
        user = new DatabaseAccess(getContext()).getUser(email);

        databaseAccess = new DatabaseAccess(getContext());
        populateLists();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eventslist_list, container, false);



        Intent intent = getActivity().getIntent();
        email = intent.getStringExtra("email");
        user = new DatabaseAccess(getContext()).getUser(email);

        databaseAccess = new DatabaseAccess(getContext());
        populateLists();

        return view;
    }

    private void populateLists() {

        Log.d(TAG, "populateLists: called");


        try {
            events = databaseAccess.getAllEvents();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mImages = new ArrayList<>();
        mName = new ArrayList<>();
        mDesc = new ArrayList<>();
        mType = new ArrayList<>();
        avgRating = new ArrayList<>();

        for(int i = 0; i < events.length; i++) {

            Log.d(TAG, "populateLists: for: image path: " + events[i].getPicture().getAbsolutePath());

            String rating = "n/a";

            if(null != databaseAccess.getAverageRating(events[i].getHostEmail())) rating = databaseAccess.getAverageRating(events[i].getHostEmail());

            mImages.add(events[i].getPicture().getAbsolutePath());
            mName.add(events[i].getName());
            mDesc.add(events[i].getName());
            mType.add(events[i].getName());
            avgRating.add(rating);
        }

        initRecyclerView();

    }

    private void initRecyclerView() {

        Log.d(TAG, "initRecyclerView: called");

        RecyclerView recyclerView = getView().findViewById(R.id.select_events_list);
        adapter = new MyEventsListRecyclerViewAdapter(mImages, mName, mDesc, mType, avgRating, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ConstraintLayout item = getView().findViewById(R.id.main_org_layout);
        View child = getLayoutInflater().inflate(R.layout.fragment_eventslist_list, null);
        item.addView(child);

    }

}

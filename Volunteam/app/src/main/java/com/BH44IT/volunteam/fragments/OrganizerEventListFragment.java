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

public class OrganizerEventListFragment extends Fragment {

    private static final String TAG = "OrgEventListFragment";

    private ArrayList<String> mImages;
    private ArrayList<String> mName;
    private ArrayList<String> mDesc;
    private ArrayList<String> mType;
    private ArrayList<String> mVolunteers;

    private String email;
    private User user;
    private Event[] events;
    private DatabaseAccess databaseAccess;
    private MyEditEventRecyclerViewAdapter adapter;

    private Context context;

    public OrganizerEventListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_event_list, container, false);

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
            events = databaseAccess.getOrganizerEvents(user.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mImages = new ArrayList<>();
        mName = new ArrayList<>();
        mDesc = new ArrayList<>();
        mType = new ArrayList<>();
        mVolunteers = new ArrayList<>();

        for(int i = 0; i < events.length; i++) {

            Log.d(TAG, "populateLists: for: image path: " + events[i].getPicture().getAbsolutePath());

            String numberOfVolunteers = "0";

            if(databaseAccess.getNumberOfVolunteers(events[i].getName()) != null) numberOfVolunteers = databaseAccess.getNumberOfVolunteers(events[i].getName());

            mImages.add(events[i].getPicture().getAbsolutePath());
            mName.add(events[i].getName());
            mDesc.add(events[i].getName());
            mType.add(events[i].getName());
            mVolunteers.add(numberOfVolunteers);
        }

        initRecyclerView();

    }

    private void initRecyclerView() {

        Log.d(TAG, "initRecyclerView: called");

        RecyclerView recyclerView = getView().findViewById(R.id.edit_events_list);
        adapter = new MyEditEventRecyclerViewAdapter(mImages, mName, mDesc, mType, mVolunteers, getContext());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ConstraintLayout item = getView().findViewById(R.id.main_org_layout);
        View child = getLayoutInflater().inflate(R.layout.fragment_edit_event_list, null);
        item.addView(child);

    }

}

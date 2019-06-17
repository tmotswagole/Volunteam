package com.BH44IT.volunteam.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.BH44IT.volunteam.R;
import com.BH44IT.volunteam.activities.OrganizerActivity;
import com.BH44IT.volunteam.activities.VolunteerActivity;

import java.util.ArrayList;
import java.util.List;

public class MyOrganizerItemRecyclerViewAdapter extends RecyclerView.Adapter<MyOrganizerItemRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<String> firstName;
    private ArrayList<String> lastName;
    private ArrayList<String> userEventsList;
    private ArrayList<String> emails;

    public MyOrganizerItemRecyclerViewAdapter(ArrayList<String> firstName, ArrayList<String> lastName, ArrayList<String> userEventsList, ArrayList<String> emails, Context context) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userEventsList = userEventsList;
        this.emails = emails;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_organizer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.firstName.setText(firstName.get(position));
        holder.lastName.setText(lastName.get(position));
        holder.eventsHosted.setText(userEventsList.get(position));
        holder.orgEmail.setText(emails.get(position));

        holder.userOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            switchFragments(emails.get(position));
            }
        });
    }

    private void switchFragments(String email) {

        OrganizerItemFragment fragment = new OrganizerItemFragment();
        VolunteerActivity volunteerActivity = (VolunteerActivity) context;
        Fragment frag = fragment;
        volunteerActivity.populateUserData(email);
        volunteerActivity.switchContent(R.id.user_review_layout, frag);

    }

    @Override
    public int getItemCount() {
        return firstName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView firstName;
        final TextView lastName;
        final TextView eventsHosted;
        final TextView orgEmail;
        final View userOrg;
        final View mView;

        ViewHolder(View view) {
            super(view);
            mView = view;
            userOrg = view.findViewById(R.id.userOrg);
            firstName = view.findViewById(R.id.firstName);
            lastName = view.findViewById(R.id.lastName);
            orgEmail = view.findViewById(R.id.orgEmail);
            eventsHosted = view.findViewById(R.id.eventsHosted);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + lastName.getText() + "'";
        }
    }
}

package com.BH44IT.volunteam.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.BH44IT.volunteam.R;
import com.BH44IT.volunteam.activities.OrganizerActivity;
import com.BH44IT.volunteam.databaseAccess.DatabaseAccess;
import com.BH44IT.volunteam.models.Event;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;


public class MyEditEventRecyclerViewAdapter extends RecyclerView.Adapter<MyEditEventRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "MyEditEventRVAdapter";


    private ArrayList<String> mImages;
    private ArrayList<String> mName;
    private ArrayList<String> mDesc;
    private ArrayList<String> mType;
    private ArrayList<String> mVolunteers;
    private Context context;

    public MyEditEventRecyclerViewAdapter(ArrayList<String> mImages, ArrayList<String> mName, ArrayList<String> mDesc, ArrayList<String> mType, ArrayList<String> mVolunteers, Context context) {
        this.mImages = mImages;
        this.mName = mName;
        this.mDesc = mDesc;
        this.mType = mType;
        this.mVolunteers = mVolunteers;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_edit_events_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Log.d(TAG, "onBindViewHolder: called at position: " + position);

//        Glide.with(context)
//                .asBitmap()
//                .load(mImages.get(position))
//                .into(holder.card_image);

        holder.card_image.setImageBitmap(loadImageFromStorage(mImages.get(position)));

        String volunteerNumber = "Volunteers: " + mVolunteers.get(position);

        holder.card_event_name.setText(mName.get(position));
        holder.card_event_desc.setText(mDesc.get(position));
        holder.card_event_type.setText(mType.get(position));
        holder.card_number_volunteers.setText(volunteerNumber);

        holder.card_view_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onBindViewHolder: setOnClickListener: clicked on " + mName.get(position));

                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(mName.get(position));
                builder.setMessage("What would you like to do?");

                // add the buttons
                builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // redirect
                        Toast.makeText(context, "Edit event: " + mName.get(position), Toast.LENGTH_SHORT).show();

                        switchFragments(mName.get(position));

                    }
                });


                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Log.d(TAG, "onBindViewHolder: setOnClickListener: builder.setNegativeButton: delete user " + mName.get(position));

                        DatabaseAccess eventDAO = new DatabaseAccess(context);
                        eventDAO.deleteEvent(mName.get(position));
                        Toast.makeText(context, "Deleted event: " + mName.get(position), Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

    }

    private Bitmap loadImageFromStorage(String path)
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

    private void switchFragments(String name) {

        OrganizerEventListFragment fragment = new OrganizerEventListFragment();
        OrganizerActivity organizerActivity = (OrganizerActivity) context;
        Fragment frag = fragment;
        organizerActivity.populateEventData(name);
        organizerActivity.switchContent(R.id.edit_events_list, frag);

    }

    @Override
    public int getItemCount() {
        return mName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final CardView card_view_layout;
        final ImageView card_image;
        final TextView card_event_name, card_event_desc, card_event_type, card_number_volunteers;

        ViewHolder(View view) {
            super(view);
            mView = view;
            card_view_layout = view.findViewById(R.id.card_view_layout);
            card_image = view.findViewById(R.id.card_image);
            card_event_name = view.findViewById(R.id.card_event_name);
            card_event_desc = view.findViewById(R.id.card_event_desc);
            card_event_type = view.findViewById(R.id.card_event_type);
            card_number_volunteers = view.findViewById(R.id.card_number_volunteers);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + card_event_name.getText() + "'";
        }
    }
}

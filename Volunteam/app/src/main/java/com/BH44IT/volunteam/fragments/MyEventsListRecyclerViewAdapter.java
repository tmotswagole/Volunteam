package com.BH44IT.volunteam.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Filter;

import com.BH44IT.volunteam.R;
import com.BH44IT.volunteam.activities.VolunteerActivity;
import com.BH44IT.volunteam.databaseAccess.DatabaseAccess;
import com.BH44IT.volunteam.models.Event;
import com.BH44IT.volunteam.models.User;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class MyEventsListRecyclerViewAdapter extends RecyclerView.Adapter<MyEventsListRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "MyEventsListRVAdapter";


    private ArrayList<String> mImages;
    private ArrayList<String> mName;
    private ArrayList<String> mDesc;
    private ArrayList<String> mType;
    private ArrayList<String> avgRating;

    private ArrayList<String> sImages;
    private ArrayList<String> sName;
    private ArrayList<String> sDesc;
    private ArrayList<String> sType;
    private ArrayList<String> sAvgRating;

    private Context context;
    private View view;

    private String eventName;

    public MyEventsListRecyclerViewAdapter(ArrayList<String> mImages, ArrayList<String> mName, ArrayList<String> mDesc, ArrayList<String> mType, ArrayList<String> avgRating, Context context) {
        this.mImages = mImages;
        this.mName = mName;
        this.mDesc = mDesc;
        this.mType = mType;
        this.avgRating = avgRating;
        this.context = context;

        this.sImages = new ArrayList<>();
        this.sName = new ArrayList<>(mName);
        this.sDesc = new ArrayList<>();
        this.sType = new ArrayList<>();
        this.sAvgRating = new ArrayList<>();
    }

    @Override
    public MyEventsListRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_edit_events_item, parent, false);

        return new MyEventsListRecyclerViewAdapter.ViewHolder(view);
    }

    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(sName);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                int i = 0;

                for (String item : sName) {
                    if (item.toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);

                        sImages.add(mImages.get(i));
                        sDesc.add(mDesc.get(i));
                        sType.add(mType.get(i));
                        sAvgRating.add(avgRating.get(i));
                    }

                    i++;

                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            mImages.clear();
            mName.clear();
            mDesc.clear();
            mType.clear();
            avgRating.clear();

            mImages.addAll(sImages);
            mName.addAll((List) results.values);
            mDesc.addAll(sDesc);
            mType.addAll(sType);
            avgRating.addAll(sAvgRating);

            notifyDataSetChanged();
        }
    };

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: called at position: " + i);

        Glide.with(context)
                .asBitmap()
                .load(mImages.get(i))
                .into(viewHolder.cardview_image);

        String avgRate = "Avg Rating: " + avgRating.get(i);

        viewHolder.cardview_event_name.setText(mName.get(i));
        viewHolder.cardview_event_desc.setText(mDesc.get(i));
        viewHolder.cardview_event_type.setText(mType.get(i));
        viewHolder.cardview_avg_rating.setText(avgRate);

        viewHolder.cardview_view_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onBindViewHolder: setOnClickListener: clicked on " + mName.get(i));

                view = v;

                // dialog to be volunteer
                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(mName.get(i));
                builder.setMessage("How would you want to volunteer?");

                // add the buttons
                builder.setPositiveButton("Send SMS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // send sms
                        Toast.makeText(view.getContext(), "Volunteered!", Toast.LENGTH_SHORT).show();
                        eventName = mName.get(i);
                        smsUser(view);
                        VolunteerActivity volunteerActivity = (VolunteerActivity) context;
                        volunteerActivity.volunteer(eventName);
                    }
                });

                builder.setPositiveButton("Send Email", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // send email
                        Toast.makeText(view.getContext(), "Volunteered!", Toast.LENGTH_SHORT).show();
                        eventName = mName.get(i);
                        emailUser(view);
                        VolunteerActivity volunteerActivity = (VolunteerActivity) context;
                        volunteerActivity.volunteer(eventName);
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

    public void emailUser(View view) {

        Event event = null;
        User user = null;

        try {
            event = new DatabaseAccess(view.getContext()).getEvent(eventName);
            user = new DatabaseAccess(view.getContext()).getUser(event.getHostEmail());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, ""); // organizer email
        intent.putExtra(Intent.EXTRA_SUBJECT, "User " + user.getLname() + " Joining As Volunteer"); // subject message
        intent.putExtra(Intent.EXTRA_TEXT, "Volunteer " + user.getFname() + " " + user.getLname() + " would like to volunteer. Number: " + user.getContacts()); // message

        intent.setType("message/rfc822");
        view.getContext().startActivity(Intent.createChooser(intent, "Choose an email client"));

    }

    public void smsUser(View view) {

        Event event = null;
        User user = null;

        try {
            event = new DatabaseAccess(view.getContext()).getEvent(eventName);
            user = new DatabaseAccess(view.getContext()).getUser(event.getHostEmail());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try{
            SmsManager smgr = SmsManager.getDefault();
            smgr.sendTextMessage(String.valueOf(user.getContacts()),null,"Hi hi, I'm your new volunteer!",null,null);
            Toast.makeText(view.getContext(), "SMS Sent Successfully!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(view.getContext(), "SMS Failed to Send, Please try again.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return mName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final CardView cardview_view_layout;
        final ImageView cardview_image;
        final TextView cardview_event_name, cardview_event_desc, cardview_event_type, cardview_avg_rating;

        ViewHolder(View view) {
            super(view);
            mView = view;
            cardview_view_layout = view.findViewById(R.id.card_view_event);
            cardview_image = view.findViewById(R.id.cardview_event_image);
            cardview_event_name = view.findViewById(R.id.cardview_event_name);
            cardview_event_desc = view.findViewById(R.id.cardview_event_desc);
            cardview_event_type = view.findViewById(R.id.cardview_event_type);
            cardview_avg_rating = view.findViewById(R.id.cardview_avg_rating);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + cardview_event_name.getText() + "'";
        }
    }
}

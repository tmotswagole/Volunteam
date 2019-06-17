package com.BH44IT.volunteam.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.BH44IT.volunteam.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewUserFragment extends Fragment {


    public ReviewUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review_user, container, false);
    }

}

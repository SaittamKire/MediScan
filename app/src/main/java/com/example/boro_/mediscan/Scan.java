package com.example.boro_.mediscan;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class Scan extends Fragment {


    View v;

    public Scan() {
        // Required empty public constructor
        // currencies = MainActivity.getCurrencies();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_scan, container, false);


        this.v = v;
        return v;//inflater.inflate(R.layout.fragment_conversion, container, false);
    }

}
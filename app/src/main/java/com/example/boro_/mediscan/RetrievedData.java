package com.example.boro_.mediscan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RetrievedData extends Fragment {


    ExpandableListView eListView;
    ExpandableListAdapter eListAdapter;

    List<String> listDataHeader;
    HashMap<String, List<String>> listHash;


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        listHash = new HashMap<String,List<String>>();
        Bundle b = this.getArguments();
        if(b.getSerializable("hashmap") != null)
        {
            listHash = (HashMap<String,List<String>>)b.getSerializable("hashmap");
            listDataHeader =  getArguments().getStringArrayList("listheader");
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_retrieveddata, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle b = this.getArguments();
        if(b.getSerializable("hashmap") != null)
        {
            listHash = new HashMap<String,List<String>>();
            listHash = (HashMap<String,List<String>>)b.getSerializable("hashmap");
            listDataHeader =  getArguments().getStringArrayList("listheader");
        }


        if (listHash.size() > 0) {
            eListView = (ExpandableListView) view.findViewById(R.id.eLV);
            eListAdapter = new ExpandableListAdapter(this.getActivity(), listDataHeader, listHash);
            eListView.setIndicatorBounds(20, 100);
            eListView.setAdapter(eListAdapter);
        }


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Refresh your fragment here
            update();
        }
    }


    public void update()
    {
        ((MainActivity)getActivity()).updateView(listDataHeader, listHash);
        eListAdapter.notifyDataSetChanged();
    }


}

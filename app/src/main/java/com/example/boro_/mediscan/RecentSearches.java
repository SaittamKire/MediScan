package com.example.boro_.mediscan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecentSearches extends Fragment {

    ListView mListView;
    SearchEntryListAdapter adapter;
    ArrayList<SearchEntry> LastSearches = new ArrayList<>();
    List<String> recentSearchListTitle = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        recentSearchListTitle = getArguments().getStringArrayList("listheaderTitle");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recentsearch, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = view.findViewById(R.id.listView1);


        String text;
        for (int i = 0; i < recentSearchListTitle.size(); i++)
        {
            text = "Saved search :";
            text += i+1;

            SearchEntry entry = new SearchEntry(recentSearchListTitle.get(i), text);
            LastSearches.add(entry);
        }


        adapter = new SearchEntryListAdapter(this.getActivity(), R.layout.searchentry_view_layout, LastSearches);
        mListView.setAdapter(adapter);

    }

}

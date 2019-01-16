package com.example.boro_.mediscan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchEntryListAdapter extends ArrayAdapter<SearchEntry> {

    private static final String TAG = "SearchEntryListAdapter";

    private Context mContext;
    int mResource;

    public SearchEntryListAdapter(Context context, int resource, ArrayList<SearchEntry> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;

    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        String name = getItem(position).getName();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.recentSearch);

        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSearchEntry(position);
            }
        });

        tvName.setText(name);

        return convertView;
    }

        public void updateSearchEntry (int number)
        {
            ((MainActivity)getContext()).switchResultView(number);
        }
}

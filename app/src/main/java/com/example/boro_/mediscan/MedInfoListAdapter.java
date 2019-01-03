package com.example.boro_.mediscan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MedInfoListAdapter extends ArrayAdapter<MedInfo> {

    private static final String TAG = "MedInfoListAdapter";

    private Context mContext;
    int mResource;

    public MedInfoListAdapter(Context context, int resource, ArrayList<MedInfo> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String name = getItem(position).getName();
        String value = getItem(position).getValue();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.textViewMed);
        TextView tvRate = (TextView) convertView.findViewById(R.id.textViewInfo);

        tvName.setText(name);
        tvRate.setText(value);

        return convertView;
    }
}

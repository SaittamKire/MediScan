package com.example.boro_.mediscan;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHashMap;

    public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listHashMap) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
    }


    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return listHashMap.get(listDataHeader.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return listDataHeader.get(i);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String)getGroup(i);
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView)convertView.findViewById(R.id.lbListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String)getChild(groupPosition, childPosition);
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }


        TextView txtListChild = (TextView)convertView.findViewById(R.id.lbListItem);
        TextView txtListChildTitle = (TextView)convertView.findViewById(R.id.lbListItemTitle);
        txtListChild.setText(childText);

        switch (groupPosition) //Nested switch-case to give the correct title to the layout input
        {
            case 0:
                switch (childPosition)
                {
                    case 0:
                        txtListChildTitle.setText(R.string.name);
                        break;
                    case 1:
                        txtListChildTitle.setText(R.string.presc);
                        break;
                    case 2:
                        txtListChildTitle.setText(R.string.prodType);
                        break;
                    case 3:
                        txtListChildTitle.setText(R.string.strength);
                        break;
                    case 4:
                        txtListChildTitle.setText(R.string.medForm);
                        break;
                    case 5:
                        txtListChildTitle.setText(R.string.narc);
                        break;
                    case 6:
                        txtListChildTitle.setText(R.string.saleStopped);
                        break;
                } break;
            case 1:
                txtListChildTitle.setText(R.string.compounds);
                break;
            case 2:
                switch (childPosition) {
                    case 0:
                        txtListChildTitle.setText(R.string.orgName);
                        break;
                    case 1:
                        txtListChildTitle.setText(R.string.orgAdr);
                        break;
                    case 2:
                        txtListChildTitle.setText(R.string.country);
                        break;
                    case 3:
                        txtListChildTitle.setText(R.string.role);
                        break;
                }break;
            case 3:
                switch (childPosition) {
                    case 0:
                        txtListChildTitle.setText(R.string.packageType);
                        break;
                    case 1:
                        txtListChildTitle.setText(R.string.lifespan);
                        break;
                    case 2:
                        txtListChildTitle.setText(R.string.storageInfo);
                        break;
                }break;
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

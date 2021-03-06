package com.example.boro_.mediscan;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.security.AccessController.getContext;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader, orgTitles, packTitles;
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
        Typeface font = lblListHeader.getTypeface();
        lblListHeader.setTypeface(font, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String)getChild(groupPosition, childPosition);
        LayoutInflater inflater;


        TextView txtListChild;
        TextView txtListChildTitle;
        int Pos = 0;
        switch (groupPosition) //Nested switch-case to give the correct title to the layout input
        {
            case 0:
                inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item, null);
                txtListChild = (TextView)convertView.findViewById(R.id.lbListItem);
                txtListChildTitle = (TextView)convertView.findViewById(R.id.lbListItemTitle);
                txtListChild.setText(childText);
                switch (childPosition)
                {
                    case 0:
                        txtListChildTitle.setText(R.string.name);

                        String strength = listHashMap.get(context.getString(R.string.listProduct)).get(3);

                        strength = strength.replace("%", "%25");
                        strength = strength.replace("/", " ");
                        

                        ((MainActivity)context).initializeFassSearch(childText+"%20"+strength);

                        break;
                    case 1:
                        txtListChildTitle.setText(R.string.presc);
                        break;
                    case 2:
                        txtListChildTitle.setText(R.string.medForm);
                        break;
                    case 3:
                        txtListChildTitle.setText(R.string.strength);
                        break;
                    case 4:
                        txtListChildTitle.setText(R.string.narc);
                        break;
                    case 5:
                        txtListChildTitle.setText(R.string.saleStopped);
                        break;
                } break;
            case 1:

                inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_substances, null);
                txtListChild = (TextView)convertView.findViewById(R.id.lbListItemSubstances);
                txtListChild.setText(childText);
                txtListChild.setPaintFlags(txtListChild.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                txtListChild.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Search(childText);
                    }
                });
                break;
            case 2:
                inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item, null);
                txtListChild = (TextView)convertView.findViewById(R.id.lbListItem);
                txtListChildTitle = (TextView)convertView.findViewById(R.id.lbListItemTitle);
                txtListChild.setText(childText);
                Pos = childPosition % 4;
                switch (Pos) {
                    case 0:
                        inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.list_item_multiple_title, null);
                        txtListChildTitle = convertView.findViewById(R.id.lbListItemPackagesTitle);
                        if (txtListChildTitle == null)
                            break;
                        txtListChildTitle.setText(childText);
                        txtListChildTitle.setPaintFlags(txtListChildTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        break;
                    case 1:
                        inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.list_item_multiple_text, null);
                        txtListChildTitle = (TextView)convertView.findViewById(R.id.lbListItemPackagesHeader);
                        txtListChild = (TextView)convertView.findViewById(R.id.lbListItemPackagesItem);
                        if (txtListChildTitle == null || txtListChild == null)
                            break;
                        txtListChildTitle.setText(R.string.orgAdr);
                        txtListChild.setText(childText);
                        break;
                    case 2:
                        inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.list_item_multiple_text, null);
                        txtListChildTitle = (TextView)convertView.findViewById(R.id.lbListItemPackagesHeader);
                        txtListChild = (TextView)convertView.findViewById(R.id.lbListItemPackagesItem);
                        if (txtListChildTitle == null || txtListChild == null)
                            break;
                        txtListChild.setText(childText);
                        txtListChildTitle.setText(R.string.country);
                        break;
                    case 3:
                        inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.list_item_multiple_text, null);
                        txtListChildTitle = (TextView)convertView.findViewById(R.id.lbListItemPackagesHeader);
                        txtListChild = (TextView)convertView.findViewById(R.id.lbListItemPackagesItem);
                        if (txtListChildTitle == null || txtListChild == null)
                            break;
                        txtListChild.setText(childText);
                        txtListChildTitle.setText(R.string.role);
                        break;
                }break;
            case 3:
                Pos = childPosition % 3;
                switch (Pos) {
                    case 0:
                        inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.list_item_multiple_title, null);
                        txtListChildTitle = convertView.findViewById(R.id.lbListItemPackagesTitle);
                        if (txtListChildTitle == null)
                            break;
                        txtListChildTitle.setText(childText);
                        txtListChildTitle.setPaintFlags(txtListChildTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        break;
                    case 1:
                        inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.list_item_multiple_text, null);
                        txtListChildTitle = (TextView)convertView.findViewById(R.id.lbListItemPackagesHeader);
                        txtListChild = (TextView)convertView.findViewById(R.id.lbListItemPackagesItem);
                        if (txtListChildTitle == null || txtListChild == null)
                            break;
                        txtListChildTitle.setText(R.string.lifespan);
                        txtListChild.setText(childText);
                        break;
                    case 2:
                        inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.list_item_multiple_text, null);
                        txtListChildTitle = (TextView)convertView.findViewById(R.id.lbListItemPackagesHeader);
                        txtListChild = (TextView)convertView.findViewById(R.id.lbListItemPackagesItem);
                        if (txtListChildTitle == null || txtListChild == null)
                            break;
                        txtListChildTitle.setText(R.string.storageInfo);
                        txtListChild.setText(childText);
                        int[] padding = new int[4];
                        padding[0] = convertView.getPaddingLeft();
                        padding[1] = convertView.getPaddingTop();
                        padding[2] = convertView.getPaddingRight();
                        padding[3] = convertView.getPaddingBottom() + 65;

                        convertView.setPadding(padding[0], padding[1], padding[2], padding[3]);
                        break;
                }  break;
        }

        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }



    private void Search (String input)
    {
        ((MainActivity)context).SubstanceSearch(input);

    }

    private void fass_Search (String input)
    {
        ((MainActivity)context).FassSearch(input);

    }
}

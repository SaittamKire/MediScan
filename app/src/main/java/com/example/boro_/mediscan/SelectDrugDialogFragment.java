package com.example.boro_.mediscan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static android.text.TextUtils.concat;

public class SelectDrugDialogFragment extends DialogFragment {



    ArrayAdapter arrayAdapter;
    ArrayList InternalID = new ArrayList<String>();
    OnClose close1;

    private String SelectedInternalID;

    public String getInternalID(){
        return SelectedInternalID;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.dialog_Select_Drug))
                .setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                        SelectedInternalID = InternalID.get(which).toString();

                        //här ska vi kolla vilken som klickades samt sedan returna dess internal id
                        Toast.makeText(getActivity(), getResources().getString(R.string.dialog_Drug_Clicked_1) + " " + SelectedInternalID + " " + getResources().getString(R.string.dialog_Drug_Clicked_2),
                                Toast.LENGTH_LONG).show();

                        close1.onClose();



                    }
                });


        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void CreateList(JSONArray array, Context context){

        if (array.isNull(0)){
            return;
        }

        List list = new ArrayList<String>();


        try {

            for(int i = 0; i>-1; i++){
                JSONObject obj = array.getJSONObject(i);

                InternalID.add(obj.optString("InternalID"));


                StringBuilder stringBuilder = new StringBuilder(255);

                stringBuilder.append(obj.optString("Name")+" ");
                stringBuilder.append(obj.optString("Strength")+", ");
                stringBuilder.append(obj.optString("PharmaceuticalForm"));

                String total = (obj.optString("Name"+", ") + obj.optString("Strength"+", ") + obj.optString("PharmaceuticalForm"));
                list.add(stringBuilder.toString());
                //list.add(total);
            }

        }
        catch(Exception ex){

        }

        //list.add("Alvedon");
        //list.add("Ipren");
        //  list.add("treo");

        ArrayAdapter adapter = new ArrayAdapter<String>(
                context,
                R.layout.custom_list_layout_item,
                list
        );


        this.arrayAdapter = adapter;
        return;
    }

    public void addCloseListener(OnClose close){
        close1=close;
    }
    public interface OnClose{

        void onClose();
        void onDismiss();
    }

    @Override
    public void onResume() {
        super.onResume();

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(android.content.DialogInterface dialog,
                                 int keyCode,android.view.KeyEvent event)
            {
                if ((keyCode ==  android.view.KeyEvent.KEYCODE_BACK))
                {
                    // To dismiss the fragment when the back-button is pressed.
                    dismiss();
                    close1.onDismiss();
                    return true;
                }
                // Otherwise, do nothing else
                else return false;
            }
        });
    }
}

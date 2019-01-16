package com.example.boro_.mediscan;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ApiHandler {

    MainActivity mainActivity;

    ApiHandler(MainActivity activity){
        mainActivity = activity;
    }

    public void FirstSearchNoStrength(String name, final String language, final Context context) {


        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://213.66.251.184/Bottles/BottlesService.asmx/FirstSearchNoStrength?name="+name+"&language="+language;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray reader = new JSONArray(response); //OBSERVERA MULTIPLE RESULTS ON SEARCH
                            mainActivity.ShowProductsDialog(reader);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }

    public void FirstSearch(String name, String strength, final String language, final Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://213.66.251.184/Bottles/BottlesService.asmx/FirstSearch?name="+name+"&strength="+strength+"&language="+language;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray reader = new JSONArray(response);
                            JSONObject obj = reader.getJSONObject(0);
                            String InternalID = obj.getString("InternalID");
                            SecondSearch(InternalID,language,context);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }

    public void SecondSearch(String id, String language, final Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://213.66.251.184/Bottles/BottlesService.asmx/SecondSearch?id="+id+"&language_sv_en="+language;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            mainActivity.CreateItem(obj);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }


}

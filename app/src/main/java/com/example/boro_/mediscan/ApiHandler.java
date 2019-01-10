package com.example.boro_.mediscan;

import android.content.Context;
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

    public void FirstSearchNoStrength(String name, String language, final Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://213.66.251.184/Bottles/BottlesService.asmx/FirstSearchNoStrength?name="+name+"&language="+language;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray reader = new JSONArray(response); //TODO Fel vi får tillbaka en array
                            Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                            JSONObject test = reader.getJSONObject(0);

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
    public void FirstSearch(String name,String strength, String language, final Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://213.66.251.184/Bottles/BottlesService.asmx/FirstSearch?name="+name+"&strength="+strength+"&language="+language;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray reader = new JSONArray(response); //TODO Fel vi får tillbaka en array
                            Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                            JSONObject test = reader.getJSONObject(0);

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
                            JSONArray reader = new JSONArray(response); //TODO Fel vi får tillbaka en array
                            Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                            JSONObject test = reader.getJSONObject(0);

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


}

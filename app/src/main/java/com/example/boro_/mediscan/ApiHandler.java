package com.example.boro_.mediscan;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
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
import org.json.JSONTokener;

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

                            Object json = new JSONTokener(response).nextValue();

                            //If only one product is returned it is an JSONObject
                            if (json instanceof JSONObject){

                                String id = ((JSONObject)json).getJSONArray("Products").getJSONObject(0).optString("InternalID");

                                SecondSearch(id,language,context);

                            }
                            //If several products are available then it is an array
                            else{

                                JSONArray reader = new JSONArray(response);
                                if(reader.isNull(0))
                                {
                                    mainActivity.ShowHideProgressBar(false);
                                    Toast.makeText(context, R.string.Not_Found_Error, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                mainActivity.ShowProductsDialog(reader);
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                            mainActivity.ShowHideProgressBar(false);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, R.string.Communication_Error, Toast.LENGTH_SHORT).show();
                mainActivity.ShowHideProgressBar(false);
            }
        });

        queue.add(stringRequest);
    }



    public void SecondSearch(String id, String language, final Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="http://213.66.251.184/Bottles/BottlesService.asmx/SecondSearch?id="+id+"&language_sv_en="+language;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj == null)
                            {
                                mainActivity.ShowHideProgressBar(false);
                                Toast.makeText(context, R.string.Not_Found_Error, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            mainActivity.CreateItem(obj);
                            mainActivity.ShowHideProgressBar(false);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            mainActivity.ShowHideProgressBar(false);
                        }

                        // Display the first 500 characters of the response string.
                        //                       mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO Handle different volley errors
                Toast.makeText(context, R.string.Communication_Error, Toast.LENGTH_SHORT).show();
                mainActivity.ShowHideProgressBar(false);
            }
        });

        queue.add(stringRequest);
    }


}

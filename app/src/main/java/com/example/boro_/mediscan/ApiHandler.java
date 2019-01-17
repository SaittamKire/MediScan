package com.example.boro_.mediscan;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ApiHandler {

    //MainActivity mainActivity;

    /*ApiHandler(MainActivity activity){
        mainActivity = activity;
    }*/
        public ApiHandler(){

        }
/*    public void FirstSearchNoStrength(String name, final String language, final Context context) {


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
                                    return;
                                }
                                mainActivity.ShowProductsDialog(reader);
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, R.string.Communication_Error, Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }*/


    public void FirstSearchNoStrength(String name, final String language, final Context context, final FirstSearchListener firstSearchListener) {


        RequestQueue queue = VolleySingleton.getInstance(context).getRequestQueue();
        String url = "http://213.66.251.184/Bottles/BottlesService.asmx/FirstSearchNoStrength?name="+name+"&language="+language;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            if(response.equals("[]")){
                                firstSearchListener.onEmptyResult();
                                return;
                            }

                            Object json = new JSONTokener(response).nextValue();

                            //If only one product is returned it is an JSONObject
                            if (json instanceof JSONObject){

                                String id = ((JSONObject)json).getJSONArray("Products").getJSONObject(0).optString("InternalID");

                                SecondSearch(id, language, context, new SecondSearchListener() {
                                    @Override
                                    public void onSecondSearchResult(JSONObject product) {

                                        firstSearchListener.onSingleResult(product);
                                    }

                                    @Override
                                    public void onException(JSONException jsonexception) {

                                    }

                                    @Override
                                    public void onVolleyError(VolleyError error) {

                                    }
                                });

                            }
                            //If several products are available then it is an array
                            else{

                                JSONArray reader = new JSONArray(response);
                                if(reader.isNull(0))
                                {
                                    return;
                                }
                                firstSearchListener.onMultipleResults(reader);
                                //mainActivity.ShowProductsDialog(reader);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            firstSearchListener.onException(e);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, R.string.Communication_Error, Toast.LENGTH_SHORT).show();
                firstSearchListener.onVolleyError(error);
            }
        });

        queue.add(stringRequest);
    }

    public void FirstSearch(String name,String dosage, final String language, final Context context, final FirstSearchListener firstSearchListener) {


        RequestQueue queue = VolleySingleton.getInstance(context).getRequestQueue();
        //String url = "http://213.66.251.184/Bottles/BottlesService.asmx/FirstSearch?name="+name+"&language="+language;
        String url="http://213.66.251.184/Bottles/BottlesService.asmx/FirstSearch?name="+name+"&strength="+dosage+"&language="+language+"&fbclid=IwAR00DSzecqYioxMBf3h53q42YNhFrjCbpfjE1BWDGsPg3yZkCqQqg3nxWko";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            if(response.equals("[]")){
                                firstSearchListener.onEmptyResult();
                                return;
                            }

                            Object json = new JSONTokener(response).nextValue();

                            //If only one product is returned it is an JSONObject
                            if (json instanceof JSONObject){

                                String id = ((JSONObject)json).getJSONArray("Products").getJSONObject(0).optString("InternalID");

                                SecondSearch(id, language, context, new SecondSearchListener() {
                                    @Override
                                    public void onSecondSearchResult(JSONObject product) {

                                        firstSearchListener.onSingleResult(product);
                                    }

                                    @Override
                                    public void onException(JSONException jsonexception) {

                                    }

                                    @Override
                                    public void onVolleyError(VolleyError error) {

                                    }
                                });

                            }
                            //If several products are available then it is an array
                            else{

                                JSONArray reader = new JSONArray(response);
                                if(reader.isNull(0))
                                {
                                    return;
                                }
                                firstSearchListener.onMultipleResults(reader);
                                //mainActivity.ShowProductsDialog(reader);
                            }

                        } catch (JSONException e) {
                            firstSearchListener.onException(e);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, R.string.Communication_Error, Toast.LENGTH_SHORT).show();
                firstSearchListener.onVolleyError(error);
            }
        });

        queue.add(stringRequest);
    }

    public void SecondSearch(String id, String language, final Context context, final SecondSearchListener secondSearchListener) {

        RequestQueue queue = VolleySingleton.getInstance(context).getRequestQueue();
        String url ="http://213.66.251.184/Bottles/BottlesService.asmx/SecondSearch?id="+id+"&language_sv_en="+language;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj == null)
                            {
                                return;
                            }

                            secondSearchListener.onSecondSearchResult(obj);
                            //mainActivity.CreateItem(obj);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            secondSearchListener.onException(e);
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        // Display the first 500 characters of the response string.
                        //                       mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO Handle different volley errors
                Toast.makeText(context, R.string.Communication_Error, Toast.LENGTH_SHORT).show();
                secondSearchListener.onVolleyError(error);
            }
        });

        queue.add(stringRequest);
    }

public interface FirstSearchListener{

        void onEmptyResult();
        void onMultipleResults(JSONArray listResult);
        void onSingleResult(JSONObject finalProduct);
        void onException(JSONException jsonexception);
        void onVolleyError(VolleyError volleyError);

}
public interface SecondSearchListener{

    void onSecondSearchResult(JSONObject product);
    void onException(JSONException jsonexception);
    void onVolleyError(VolleyError error);
    }

}

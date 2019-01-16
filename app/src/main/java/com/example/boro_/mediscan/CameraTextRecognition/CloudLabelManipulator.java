package com.example.boro_.mediscan.CameraTextRecognition;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CloudLabelManipulator {
    private FirebaseVisionDocumentText Medication;

    public CloudLabelManipulator(FirebaseVisionDocumentText text) {
        Medication = text;

    }

    public String getFirstStr() { //Todo Buggtesta & Förbättra
        int size = 1; //TODO denna resettar sig hela tiden och jag vet inte varför
        String Apistr = "";
        List<FirebaseVisionDocumentText.Block> blocks = Medication.getBlocks();
        for (FirebaseVisionDocumentText.Block block : blocks) {
            for (FirebaseVisionDocumentText.Paragraph paragraph : block.getParagraphs()) { //TODO See if the size of the paragraph matches size of word, or if we can use paragraph size
                for (FirebaseVisionDocumentText.Word words : paragraph.getWords()) {
//                    if (words.getBoundingBox().height() == size) {
//                        Apistr += "" + words.getText();
//                    }
                    if (words.getBoundingBox().height() > size) {
                        size = words.getBoundingBox().height();
                        Apistr = words.getText();
                    }

                }
            }
        }
        return Apistr;
    }
    public String getDosage() { //Todo Buggtesta
        String Apistr = "";
        List<FirebaseVisionDocumentText.Block> blocks = Medication.getBlocks();
        for (FirebaseVisionDocumentText.Block block : blocks) {
            for (FirebaseVisionDocumentText.Paragraph paragraph : block.getParagraphs()) {
                for (FirebaseVisionDocumentText.Word words : paragraph.getWords()) {
                    if (words.getText().matches(".*\\d+.*")){

                        if(words.getText().matches("mg|g|ml|kg|cl")){
                            Apistr = words.getText(); // TODO Testa denna fungerar som den ska. Behöver mellanslag!!
                            String [] arr = Apistr.split( "(?=\\D)", 2); // Makes an array and keeps the delimiter. The delimiter here is the first non-digit
                            String pt1 = arr[0].trim();
                            String p2 = arr[1].trim(); // TODO might need to be improved
                            Apistr = pt1 +" " +p2; // Formats the string just like the other part of this function. Might
                            return Apistr; //TODO Is this ok ?
                        }
                        else{
                            int pos = paragraph.getWords().indexOf(words);
                            if((pos +1) < paragraph.getWords().size()){
                                FirebaseVisionDocumentText.Word nextword = paragraph.getWords().get(pos + 1);
                                if(nextword != null && nextword.getText().matches("mg|g|ml|kg|cl")){
                                  //  Apistr = words.getText() + "%20" + nextword.getText(); // TODO sätta standard på tillbaka medellande
                                    String Dosage = words.getText(); //TODO Förra kanske var snabbare
                                    String Size = nextword.getText();
                                    Apistr = Dosage +" " + Size;
                                    return Apistr;
                                }

                            }
                        }



                    }

                }
            }
        }
        return Apistr;
    }
    public String getSecondStr(String first){
        String Apistr = "";
        List<FirebaseVisionDocumentText.Block> blocks = Medication.getBlocks();
        for (FirebaseVisionDocumentText.Block block : blocks) {
            for (FirebaseVisionDocumentText.Paragraph paragraph : block.getParagraphs()) {
                for (FirebaseVisionDocumentText.Word words : paragraph.getWords()) {
                    if (words.getText().contains(first)){

                        int pos = paragraph.getWords().indexOf(words);
                        for (int i = 1; (pos+i) < paragraph.getWords().size(); i++){
                            FirebaseVisionDocumentText.Word nextword = paragraph.getWords().get(pos + i);
                            if(nextword != null && nextword.getText().length() > 2){ //TODO förbättra så den inte tar dosage. Exempel är alvedon som inte har en andra str.
                                // TODO lösningen på det kan vara att endast kalla denna om förta str + dosage inte fungerar, då kommer alvedon fungera men inte saker som kräver andra str
                                Apistr = nextword.getText(); // TODO sätta standard på tillbaka medellande
                                return Apistr; // TODO Kanske ska ha return här istället för break
                            }
                        }




                    }

                }
            }
        }
        return Apistr;
    }
    public JSONObject getDrug(JSONArray Drugs){ //Takes in a JSONarray and fetches the one that matches the variation name for the object we have. it there is no variation name EX "Alvedon" then it will return the first object in the list.
        String Match = getFirstStr() + " " + getSecondStr(getFirstStr());
        for (int i=0;i < Drugs.length(); i++)
        {
            try {
                if(Drugs.getJSONObject(i).getString("LegalName").equals(Match))
                {
                    return Drugs.getJSONObject(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            return Drugs.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void SimpleIPrequest(String ProductName, String Dosage, String Variant, Context still){ //TODO i dont think we need to send the strings here if can fetch internal see above
        RequestQueue queue = Volley.newRequestQueue(still); // TODO change everything so it can return something right now it cant return anything that and thats a problem.
 //       String url ="https://api.fda.gov/drug/event.json?search="+parastr+"&limit=1";
//        String url ="http://213.66.251.184/Bottles/BottlesService.asmx/FirstSearchNoStrength?name="+parastr+"&language=en&fbclid=IwAR2sF4ECXAqAh36VKctOkT1iDXMlVPKzxkiTDKRk0v7YMvXM5o-VmPsKlYE";
        String url="http://213.66.251.184/Bottles/BottlesService.asmx/FirstSearch?name="+ProductName+"&strength="+Dosage+"&language=sv&fbclid=IwAR00DSzecqYioxMBf3h53q42YNhFrjCbpfjE1BWDGsPg3yZkCqQqg3nxWko";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray reader = new JSONArray(response); //TODO Fel vi får tillbaka en array
                            getDrug(reader);
                            //TODO Iterera igenom Arrayen och matcha legal name emot Productname och andra strängen. Om inget hittas returnera antingen top result eller alla i en list
                            String stop = "";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  mTextView.setText("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}


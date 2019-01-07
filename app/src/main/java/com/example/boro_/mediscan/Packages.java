package com.example.boro_.mediscan;

import org.json.JSONObject;

public class Packages {

    String Text;
    String Prescription;
    String Safety_Features;
    String ConditionID;
    String PharmaceuticalProductID;
    String ShelflifeValue;
    String UnitName;
    String StorageDescription;
    String PackCondition;
    
    public Packages(JSONObject obj){

        Text = obj.optString("Text");
        Prescription = obj.optString("Prescription");
        Safety_Features = obj.optString("Safety_Features");
        ConditionID = obj.optString("ConditionID");
        PharmaceuticalProductID = obj.optString("PharmaceuticalProductID");
        ShelflifeValue = obj.optString("ShelflifeValue");
        UnitName = obj.optString("UnitName");
        StorageDescription = obj.optString("StorageDescription");
        PackCondition = obj.optString("PackCondition");
    }


}

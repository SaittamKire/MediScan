package com.example.boro_.mediscan;

import org.json.JSONObject;

public class Compositions {

    String Name;
    String Quantity;
    String UnitTermName;
    String UnitAltName;
    String RelatedToID;
    String NarcoticClass;
    String NarcoticClassSpecified;
    String RecommendedNameSV;
    String RecommendedNameEN;
    String Relation;
    String RelationRole;


    public Compositions(JSONObject obj){

        Name = obj.optString("Name");
        Quantity = obj.optString("Quantity");
        UnitTermName = obj.optString("UnitTermName");
        UnitAltName = obj.optString("UnitAltName");
        RelatedToID = obj.optString("RelatedToID");
        NarcoticClass = obj.optString("NarcoticClass");
        NarcoticClassSpecified = obj.optString("NarcoticClassSpecified");
        RecommendedNameSV = obj.optString("RecommendedNameSV");
        RecommendedNameEN = obj.optString("RecommendedNameEN");
        Relation = obj.optString("Relation");
        RelationRole = obj.optString("RelationRole");

    }
}

package com.example.boro_.mediscan;

import org.json.JSONObject;

public class Products {
    String InternalID;
    String Name;
    String LegalName;
    String DateApproved;
    String Prescription;
    String ProductClassification;
    String Strength;
    String StrengthGroupNumber;
    String ApprovalNumber;
    String ClassificationAtcCode;
    String ControledMedicinalProduct;
    String NarcoticClassification;
    String SalesStopped;
    String InterchangeabilityStartdate;
    String PharmaceuticalForm;
    String PharmaceuticalProductID;
    String ComprefName;
    String AuthorizationProcedure;


     public Products(JSONObject obj){

        InternalID = obj.optString("InternalID");
        Name = obj.optString("Name");
        LegalName = obj.optString("LegalName");
        DateApproved = obj.optString("DateApproved");
        Prescription = obj.optString("Prescripton");
        ProductClassification = obj.optString("ProductClassification");
        Strength = obj.optString("Strength");
        StrengthGroupNumber = obj.optString("StrengthGroupName");
        ApprovalNumber = obj.optString("ApprovalNumber");
        ClassificationAtcCode = obj.optString("ClassificationAtcCode");
        ControledMedicinalProduct = obj.optString("ControledMedicinalProduct");
        NarcoticClassification = obj.optString("NarcoticClassification");
        SalesStopped = obj.optString("SalesStopped");
        InterchangeabilityStartdate = obj.optString("InterchangeabilityStartdate");
        PharmaceuticalForm = obj.optString("PharmaceuticalForm");
        PharmaceuticalProductID = obj.optString("PharmaceuticalProductID");
        ComprefName = obj.optString("ComprefName");
        AuthorizationProcedure = obj.optString("AuthorizationProcedure");


    }
}

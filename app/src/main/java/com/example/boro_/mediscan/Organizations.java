package com.example.boro_.mediscan;

import org.json.JSONObject;

public class Organizations {
    String Orgname;
    String OrgaddressAddr1;
    String OrgaddressAddr2;
    String OrgaddressAddr3;
    String OrgaddressAddr4;
    String Country;
    String VatNo;
    String OrgUnitName;
    String OrgRole;


    public Organizations(JSONObject obj){

        Orgname = obj.optString("Orgname");
        OrgaddressAddr1 = obj.optString("OrgaddressAddr1");
        OrgaddressAddr2 = obj.optString("OrgaddressAddr2");
        OrgaddressAddr3 = obj.optString("OrgaddressAddr3");
        OrgaddressAddr4 = obj.optString("OrgaddressAddr4");
        Country = obj.optString("Country");
        VatNo = obj.optString("VatNo");
        OrgUnitName = obj.optString("OrgUnitName");
        OrgRole = obj.optString("OrgRole");




    }
}

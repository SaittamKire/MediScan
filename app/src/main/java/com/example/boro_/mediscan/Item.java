package com.example.boro_.mediscan;

import org.json.JSONObject;

import java.util.ArrayList;

public class Item {


    //Item consits of four subclasses, Products, Compositions, Organizations and Packages.
    //All are saved in list form except for Products since a medicine may contain more than one
    //Package, Organization or Compositions.
    //Each class fills itself when it is given a JSONObject containing only one instance of it self.

    JSONObject JSobj;
    Products Products;
    ArrayList<Compositions> Compositions = new ArrayList<Compositions>();
    ArrayList<Organizations> Organizations = new ArrayList<Organizations>();
    ArrayList<Packages> Packages = new ArrayList<Packages>();


    Item(JSONObject item){
        try{
            try{
                JSobj = item;
                JSobj = item.getJSONArray("Products").getJSONObject(0);
                Products = new Products(JSobj);
            }
            catch (Exception ex){
                //print error
            }

            //infinite counting loop for indexing that breaks when we run out of JSONobjects from the array
            for(int i = 0; i > -1; i++){

                try{
                    JSobj = item;
                    JSobj = item.getJSONArray("Compositions").getJSONObject(i);
                    Compositions temp = new Compositions(JSobj);
                    Compositions.add(temp);

                }
                catch (Exception ex){
                    break;
                }
            }

            for(int i = 0; i > -1; i++){

                try{
                    JSobj = item;
                    JSobj = item.getJSONArray("Organizations").getJSONObject(i);
                    Organizations temp = new Organizations(JSobj);
                    Organizations.add(temp);


                }
                catch (Exception ex){
                    break;
                }

            }

            for(int i = 0; i > -1; i++){
                try{
                    JSobj = item;
                    JSobj = item.getJSONArray("Packages").getJSONObject(i);
                    Packages temp = new Packages(JSobj);
                    Packages.add(temp);
                }
                catch (Exception ex){
                    break;
                }
            }
        }
        catch(Exception ex){

        }
    }
}

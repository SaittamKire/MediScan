package com.example.boro_.mediscan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RetrievedData extends Fragment {

    ListView mListView;
    MedInfoListAdapter adapter;
    ArrayList<MedInfo> medInfoList = new ArrayList<>();

    ExpandableListView eListView;
    ExpandableListAdapter eListAdapter;

    List<String> listDataHeader;
    HashMap<String, List<String>> listHash;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_retrieveddata, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = view.findViewById(R.id.listView);


        eListView = (ExpandableListView) view.findViewById(R.id.eLV);
        initData();
        eListAdapter = new ExpandableListAdapter(this.getActivity(), listDataHeader, listHash);
        eListView.setAdapter(eListAdapter);



        String name = "Title", value = "This is where hopefully a lot of relevant text will appear... Such as dosage, if the medicine requires a perscription to purchase, if it has been classified as a narcotic etc...";

        for (int i = 0; i < 10; i++)
        {
            name = "Title ";
            name += i;
            name += ":";

            MedInfo med = new MedInfo(name , value);
            medInfoList.add(med);
        }

        adapter = new MedInfoListAdapter(this.getActivity(), R.layout.medinfo_view_layout, medInfoList);
        mListView.setAdapter(adapter);



    }

    private void initData() {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();


        listDataHeader.add("Alvedon");
        listDataHeader.add("Alvedon stor");
        listDataHeader.add("Alvedon stolpiller");
        listDataHeader.add("Treo");
        listDataHeader.add("Treo Hallon");
        listDataHeader.add("Prozac");
        listDataHeader.add("Breznak");

        List <String> Alvedon = new ArrayList<>();
        Alvedon.add("125mg");
        Alvedon.add("Barnsize");
        Alvedon.add("Funkar sådär");

        List <String> AlvedonStor = new ArrayList<>();
        AlvedonStor.add("500mg");
        AlvedonStor.add("Vanligt jävla piller");
        AlvedonStor.add("Blandas till fördel med alkohol");

        List <String> AlvedonStolpiller = new ArrayList<>();
        AlvedonStolpiller.add("Massvis med mg");
        AlvedonStolpiller.add("Denna ska upp i rumpan");
        AlvedonStolpiller.add("Inte så roligt längre");

        List <String> Treo = new ArrayList<>();
        Treo.add("500/50mg");
        Treo.add("Brustablett med koffein");
        Treo.add("Botar och piggar upp!");

        List <String> TreoHallon = new ArrayList<>();
        TreoHallon.add("500mg");
        TreoHallon.add("Brustablett utan koffein men smakar bättre");
        TreoHallon.add("Botar och påminner om sommar");

        List <String> Prozac = new ArrayList<>();
        Prozac.add("???mg");
        Prozac.add("Är det någon som vet vad Prozac är?");
        Prozac.add("Never tried this...");

        List <String> Breznak = new ArrayList<>();
        Breznak.add("Dosering? En till!");
        Breznak.add("Öl botar det mesta och lite till.");
        Breznak.add("Om det inte fungerar har du inte tagit tillräckligt hög dos");


        listHash.put(listDataHeader.get(0), Alvedon);
        listHash.put(listDataHeader.get(1), AlvedonStor);
        listHash.put(listDataHeader.get(2), AlvedonStolpiller);
        listHash.put(listDataHeader.get(3), Treo);
        listHash.put(listDataHeader.get(4), TreoHallon);
        listHash.put(listDataHeader.get(5), Prozac);
        listHash.put(listDataHeader.get(6), Breznak);

    }
}

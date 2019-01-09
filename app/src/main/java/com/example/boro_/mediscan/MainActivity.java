package com.example.boro_.mediscan;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ListView mListView;
    public ApiHandler mApiHandler;
    public MyDialogClass cdd;
    public String SearchLanguage;

    Item item;
    ArrayList<String> listDataHeader;
    HashMap<String, List<String>> listHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApiHandler = new ApiHandler();
        cdd = new MyDialogClass(this); //Creates disclaimer-dialog
        SetupLanguage();

        initData();




        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        Bundle bundle = new Bundle();

                        bundle.putSerializable("hashmap", listHash);
                        bundle.putStringArrayList("listheader", listDataHeader);
                        switch (item.getItemId()) {
                            case R.id.recent_search_tab:
                                selectedFragment = new RecentSearches();
                                break;
                            case R.id.scan_tab:
                                selectedFragment = new Scan();
                                break;
                            case R.id.retrived_data_tab:
                                selectedFragment = new RetrievedData();
                                selectedFragment.setArguments(bundle);
                                //((RetrievedData) selectedFragment).update();
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, Scan.newInstance());
        transaction.commit();


        //Dialog logic (NEEDS TO BE UPDATED TO LOOK UP IF THE USER HAS ACCEPTED THIS MESSAGE ALREADY)
        cdd.show(); //Shows it
        cdd.setCanceledOnTouchOutside(false); //Disables cancelations
        cdd.setCancelable(false);
        //End dialog logic

        setupButtons(); //Will setup topbar buttons and searchfield

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */

    public void CreateItem(JSONObject object){

        item = new Item(object);

        initData2(item);

        return;



    }


    private void initData() {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();


        listDataHeader.add("Produkt");
        listDataHeader.add("Aktiva substanser");
        listDataHeader.add("Organisationsinfo");
        listDataHeader.add("Förpackning");

        List<String> Produkt = new ArrayList<>();
        Produkt.add("Treo");
        Produkt.add("Receptfritt");
        Produkt.add("Läkemedel");
        Produkt.add("500mg/50mg");
        Produkt.add("Brustablett");
        Produkt.add("Ej narkotikaklassad");
        Produkt.add("N");

        List<String> Compounds = new ArrayList<>();
        Compounds.add("Acetylsalicylsyra\nKoffein");

        List<String> OrgInfo = new ArrayList<>();
        OrgInfo.add("Treo AB");
        OrgInfo.add("Treogatan 33");
        OrgInfo.add("Treolian Empire");
        OrgInfo.add("Innehavare av godkännande för försäljning");

        List<String> Package = new ArrayList<>();
        Package.add("Rör, 10 brustabletter");
        Package.add("2 år");
        Package.add("Ingen särskild temperaturbegränsning");

        listHash.put(listDataHeader.get(0), Produkt);
        listHash.put(listDataHeader.get(1), Compounds);
        listHash.put(listDataHeader.get(2), OrgInfo);
        listHash.put(listDataHeader.get(3), Package);


    }
    public void initData2(Item item) {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();


        listDataHeader.add("Produkt");
        listDataHeader.add("Aktiva substanser");
        listDataHeader.add("Organisationsinfo");
        listDataHeader.add("Förpackning");

        List <String> Produkt = new ArrayList<>();

        Produkt.add(item.Products.InternalID);
        Produkt.add(item.Products.Name);
        Produkt.add(item.Products.LegalName);
        Produkt.add(item.Products.DateApproved);
        Produkt.add(item.Products.Prescription);
        Produkt.add(item.Products.ProductClassification);
        Produkt.add(item.Products.Strength);
        Produkt.add(item.Products.StrengthGroupNumber);
        Produkt.add(item.Products.ApprovalNumber);
        Produkt.add(item.Products.ClassificationAtcCode);
        Produkt.add(item.Products.ControledMedicinalProduct);
        Produkt.add(item.Products.NarcoticClassification);
        Produkt.add(item.Products.SalesStopped);
        Produkt.add(item.Products.InterchangeabilityStartdate);
        Produkt.add(item.Products.PharmaceuticalForm);
        Produkt.add(item.Products.PharmaceuticalProductID);
        Produkt.add(item.Products.ComprefName);
        Produkt.add(item.Products.AuthorizationProcedure);

        List <String> Compounds = new ArrayList<>();
        Compounds.add(item.Compositions.get(0).Name);
        Compounds.add(item.Compositions.get(0).NarcoticClass);
        Compounds.add(item.Compositions.get(0).NarcoticClassSpecified);
        Compounds.add(item.Compositions.get(0).Quantity);
        Compounds.add(item.Compositions.get(0).RecommendedNameEN);
        Compounds.add(item.Compositions.get(0).RecommendedNameSV);
        Compounds.add(item.Compositions.get(0).RelatedToID);
        Compounds.add(item.Compositions.get(0).Relation);
        Compounds.add(item.Compositions.get(0).RelationRole);
        Compounds.add(item.Compositions.get(0).UnitAltName);
        Compounds.add(item.Compositions.get(0).UnitTermName);
        List <String> OrgInfo = new ArrayList<>();
        OrgInfo.add(item.Organizations.get(0).Orgname);
        OrgInfo.add(item.Organizations.get(0).OrgaddressAddr1);
        OrgInfo.add(item.Organizations.get(0).OrgaddressAddr2);
        OrgInfo.add(item.Organizations.get(0).OrgaddressAddr3);
        OrgInfo.add(item.Organizations.get(0).OrgaddressAddr4);
        OrgInfo.add(item.Organizations.get(0).Country);
        OrgInfo.add(item.Organizations.get(0).VatNo);
        OrgInfo.add(item.Organizations.get(0).OrgUnitName);
        OrgInfo.add(item.Organizations.get(0).OrgRole);

        List <String> Package = new ArrayList<>();
        Package.add(item.Packages.get(0).Text);
        Package.add(item.Packages.get(0).Prescription);
        Package.add(item.Packages.get(0).Safety_Features);
        Package.add(item.Packages.get(0).ConditionID);
        Package.add(item.Packages.get(0).PharmaceuticalProductID);
        Package.add(item.Packages.get(0).ShelflifeValue);
        Package.add(item.Packages.get(0).UnitName);
        Package.add(item.Packages.get(0).StorageDescription);
        Package.add(item.Packages.get(0).PackCondition);

        listHash.put(listDataHeader.get(0), Produkt);
        listHash.put(listDataHeader.get(1), Compounds);
        listHash.put(listDataHeader.get(2), OrgInfo);
        listHash.put(listDataHeader.get(3), Package);


    }

    public void updateView(List<String> lh, HashMap<String, List<String>> lhm) {
        lh = listDataHeader;
        lhm = listHash;
    }

    private void setupButtons() {

        /*Search bar Logic*/
        final EditText SearchBar = (EditText) findViewById(R.id.search_bar);
        SearchBar.setOnEditorActionListener( //Will setup Search bar logic, when user clicks okay a api call will be made!
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        String SearchValue = null;

                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                // the user is done typing.
                                SearchValue = SearchBar.getText().toString();
                                ApiFirstSearchNoStrengthCallback(SearchValue); //Callback to get Context to ApiHandler
                                return true; // consume.
                            }
                        }
                        return false; // pass on to other listeners.
                    }
                }
        );

        final Button InfoButton = (Button) findViewById(R.id.information_button);
        InfoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cdd.show(); //Shows it
                cdd.setCanceledOnTouchOutside(false); //Disables cancelations
                cdd.setCancelable(false);
            }
        });
    }


    public void ApiFirstSearchNoStrengthCallback(String name) { //Need callback to get Context.
        mApiHandler.FirstSearchNoStrength(name, SearchLanguage, this);
    }

    private void SetupLanguage(){

        if (SearchLanguage == null) {
            Locale locale = Resources.getSystem().getConfiguration().locale;
            Locale.setDefault(locale);
            //SaveSharedPreference.setUserLanguage(MainActivity.this, locale.getLanguage());
            Configuration configuration = new Configuration();
            configuration.locale = locale;
            getBaseContext().getResources().updateConfiguration(configuration,
                    getBaseContext().getResources().getDisplayMetrics());

            if (locale.getLanguage() == "sv") {
                SearchLanguage = "sv";
            } else {
                SearchLanguage = "en";
            }
        }
    }

}



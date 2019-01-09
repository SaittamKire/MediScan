package com.example.boro_.mediscan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    ArrayList<String> listDataHeader;
    HashMap<String, List<String>> listHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApiHandler = new ApiHandler();

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
        MyDialogClass cdd = new MyDialogClass(this); //Creates disclaimer-dialog
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

    public void initData2() {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();


        listDataHeader.add("Produkt");
        listDataHeader.add("Aktiva substanser");
        listDataHeader.add("Organisationsinfo");
        listDataHeader.add("Förpackning");

        List<String> Produkt = new ArrayList<>();
        Produkt.add("Alvedon");
        Produkt.add("Receptfritt");
        Produkt.add("Läkemedel");
        Produkt.add("500mg");
        Produkt.add("Filmdragerad tablett");
        Produkt.add("Ej narkotikaklassad");
        Produkt.add("N");

        List<String> Compounds = new ArrayList<>();
        Compounds.add("Paracetamol");

        List<String> OrgInfo = new ArrayList<>();
        OrgInfo.add("Alvedon AB");
        OrgInfo.add("Alvedonvägen 56");
        OrgInfo.add("Sverige");
        OrgInfo.add("Innehavare av godkännande för försäljning");

        List<String> Package = new ArrayList<>();
        Package.add("Burk, 2000 tabletter");
        Package.add("20 år");
        Package.add("Förvaras med fördel i ugn");

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
                                ApiFirstSearchNoStrengthCallback(SearchValue, "sv"); //Callback to get Context to ApiHandler
                                return true; // consume.
                            }
                        }
                        return false; // pass on to other listeners.
                    }
                }
        );

        //ADD MORE BUTTON LOGIC

    }

    public void ApiFirstSearchNoStrengthCallback(String name, String language) { //Need callback to get Context. REMOVE LANGUAGE IN AND MAKE APP-LANGUAGE SPECIFIC INSTEAD!
        mApiHandler.FirstSearchNoStrength(name, language, this);
    }
}



package com.example.boro_.mediscan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
    final Context context = this;
    BottomNavigationView bottomNavigationView;
    Menu menuNav;
    MenuItem tab1;
    MenuItem tab2;
    MenuItem tab3;
    EditText SearchBar;
    boolean tabCreated = false;
    public ApiHandler mApiHandler;
    public MyDialogClass cdd;
    public String SearchLanguage;


    boolean hasScanBeenClicked;
    Item item;
    Fragment ScanFragment;
    ArrayList<Item> RecentSearchesList = new ArrayList<>();
    ArrayList<String> listDataHeader;
    ArrayList<String> recentSearchListTitle = new ArrayList<>();
    HashMap<String, List<String>> listHash;

    List<JSONObject> recentItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApiHandler = new ApiHandler();


        SharedPreferences prefs = getSharedPreferences("disclaimer", MODE_PRIVATE);
        cdd = new MyDialogClass(this); //Creates disclaimer-dialog

        ShowHideProgressBar(false);

        SetupLanguage();





        hasScanBeenClicked = false;

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        menuNav = bottomNavigationView.getMenu();
        tab1 = menuNav.findItem(R.id.recent_search_tab);
        tab2 = menuNav.findItem(R.id.scan_tab);
        tab3 = menuNav.findItem(R.id.retrived_data_tab);
        tab3.setEnabled(tabCreated);
        tab1.setEnabled(tabCreated);

        initData();

        TabListener();


        if (IsSupported())
        {
            TabReselected();
                                //Manually displaying the first fragment - one time only
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, Scan.newInstance());
            transaction.commit();
        }


        if (prefs.getBoolean("bool", true)) {
            //Dialog logic
            cdd.show(); //Shows it
            cdd.setCanceledOnTouchOutside(false); //Disables cancelations
            cdd.setCancelable(false);
            //End dialog logic
        }

        setupButtons(prefs); //Will setup topbar buttons and searchfield
        tab2.setChecked(true);

        final LinearLayout layout = (LinearLayout) findViewById(R.id.top_Bar);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width  = layout.getMeasuredWidth();
                width = (int)(width * 0.8);
                SearchBar.setWidth(width);
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.scan_tab);

    }


    public void TabReselected(){
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {

                Fragment selectedFragment;
                switch(menuItem.getItemId()){
                    case R.id.scan_tab:
                        try{
                            Scan scan = (Scan) getSupportFragmentManager().findFragmentById(R.id.frame_layout);
                            scan.focusLock();
                        }
                        catch(Exception ex){
                            selectedFragment = new Scan();
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frame_layout, selectedFragment, "SCAN");
                            transaction.commit();
                        }
                        return;

                    default: return;
                }


            }
        });
    }

    public void TabListener(){
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
                                bundle.putStringArrayList("listheaderTitle", recentSearchListTitle);
                                selectedFragment.setArguments(bundle);
                                break;
                            case R.id.scan_tab:

                                if (IsSupported())
                                {
                                    try{
                                        Scan scan = (Scan) getSupportFragmentManager().findFragmentById(R.id.frame_layout);
                                        if (scan != null && scan.isVisible()) {
                                            scan.focusLock();
                                        }
                                    }
                                    catch(Exception ex){
                                        selectedFragment = new Scan();
                                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                        transaction.replace(R.id.frame_layout, selectedFragment, "SCAN");
                                        transaction.commit();
                                        ScanFragment = selectedFragment;
                                    }
                                    return true;
                                }
                                else {
                                    selectedFragment = new Scan();
                                    bundle.putBoolean("isCameraSupported", false);
                                    selectedFragment.setArguments(bundle);

                                }
                                break;
                            case R.id.retrived_data_tab:
                                selectedFragment = new RetrievedData();
                                bundle.putSerializable("hashmap", listHash);
                                bundle.putStringArrayList("listheader", listDataHeader);
                                selectedFragment.setArguments(bundle);
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });
    }

    public void ShowHideProgressBar(boolean show){
        if(!show){
            findViewById(R.id.loading_bar).setVisibility(View.GONE);
            findViewById(R.id.information_button).setVisibility(View.VISIBLE);
        }
        else{
            findViewById(R.id.loading_bar).setVisibility(View.VISIBLE);
            findViewById(R.id.information_button).setVisibility(View.GONE);
        }
    }

    public void ShowProductsDialog(JSONArray reader){

        final SelectDrugDialogFragment dialog = new SelectDrugDialogFragment();
        dialog.CreateList(reader, this);
        dialog.show(getSupportFragmentManager(), "SelectDrugs");

        dialog.addCloseListener(new SelectDrugDialogFragment.OnClose() {
            @Override
            public void onClose() {
                //mApiHandler.SecondSearch(dialog.getInternalID(),SearchLanguage,getApplicationContext());

                mApiHandler.SecondSearch(dialog.getInternalID(), SearchLanguage, getApplicationContext(), new ApiHandler.SecondSearchListener() {
                    @Override
                    public void onSecondSearchResult(JSONObject product) {
                        CreateItem(product);
                        ShowHideProgressBar(false);
                    }

                    @Override
                    public void onException(JSONException jsonexception) {
                        ShowHideProgressBar(false);
                    }

                    @Override
                    public void onVolleyError(VolleyError error) {
                        ShowHideProgressBar(false);
                    }
                });
            }

            @Override
            public void onDismiss() {
                ShowHideProgressBar(false);
            }
        });
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

        Item item = new Item(object);
        String saftycheck = object.optString("Products");
        String net = "[]";
        if (saftycheck.equals(net)){
            Toast.makeText(this, R.string.Product_Error, Toast.LENGTH_SHORT).show();
            return;
        }

        item = new Item(object);

        if (object != null)
        {
            String title = item.Products.Name;
            title = title.concat(" ");
            title = title.concat(item.Products.Strength);
            title = title.concat(" ");
            title = title.concat(item.Products.PharmaceuticalForm);


            for (int i = 0; i < RecentSearchesList.size(); i++)
            {
                String stringToBeChecked = item.Products.InternalID;
                if (stringToBeChecked.equals((String)RecentSearchesList.get(i).Products.InternalID))
                {
                    recentItems.remove(i);
                    recentItems.add(0, object);
                    RecentSearchesList.remove(i);
                    RecentSearchesList.add(0, item);
                    recentSearchListTitle.remove(i);
                    recentSearchListTitle.add(0, title);
                    initializeResultView(RecentSearchesList.get(0));
                    return;
                }
            }
            recentItems.add(0, object);
            RecentSearchesList.add(0, item);
            recentSearchListTitle.add(0, title);
            initializeResultView(RecentSearchesList.get(0));
        }

        return;
    }


    private void initData() {

        SharedPreferences preferences = getSharedPreferences("SavedData", Context.MODE_PRIVATE);

        if(Load(preferences))
        {
            tab1.setEnabled(true);
        }
    }

    public void switchResultView(int i)
    {
        if (RecentSearchesList.get(i) != null)
        {
            initializeResultView(RecentSearchesList.get(i));
            Item item = RecentSearchesList.get(i);
            String title = recentSearchListTitle.get(i);
            recentSearchListTitle.remove(i);
            recentSearchListTitle.add(0, title);
            RecentSearchesList.remove(i);
            RecentSearchesList.add(0, item);
        }
    }

    public void initializeResultView(Item item) {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();


        listDataHeader.add(getResources().getString(R.string.listProduct));
        listDataHeader.add(getResources().getString(R.string.listCompositions));
        listDataHeader.add(getResources().getString(R.string.listOrganization));
        listDataHeader.add(getResources().getString(R.string.listPackaging));

        List <String> Product = new ArrayList<>();


        Product.add(item.Products.Name);
        if (item.Products.Prescription.equals(""))
        {
            String input = getResources().getString(R.string.noPrescription);
            Product.add(input);
        }
        else
        {
            Product.add(item.Products.Prescription);
        }

        Product.add(item.Products.PharmaceuticalForm);
        Product.add(item.Products.Strength);
        Product.add(item.Products.NarcoticClassification);
        if (item.Products.SalesStopped.equals("N"))
        {
            String input = getResources().getString(R.string.No);
            Product.add(input);
        }
        else if (item.Products.SalesStopped.equals("Y"))
        {
            String input = getResources().getString(R.string.Yes);
            Product.add(input);
        }
        else
        {
            Product.add(item.Products.SalesStopped);
        }
        //Produkt.add(item.Products.InternalID);
        //Produkt.add(item.Products.LegalName);
        //Produkt.add(item.Products.DateApproved);
        //Produkt.add(item.Products.ProductClassification);
        //Produkt.add(item.Products.StrengthGroupNumber);
        //Produkt.add(item.Products.ApprovalNumber);
        //Produkt.add(item.Products.ClassificationAtcCode);
        //Produkt.add(item.Products.ControledMedicinalProduct);
        //Produkt.add(item.Products.InterchangeabilityStartdate);
        //Produkt.add(item.Products.PharmaceuticalProductID);
        //Produkt.add(item.Products.ComprefName);
        //Produkt.add(item.Products.AuthorizationProcedure);

        List <String> Compositions = new ArrayList<>();

        for (int j = 0; j < item.Compositions.size(); j++)
        {
            if (SearchLanguage == "sv") {
                String compound = item.Compositions.get(j).RecommendedNameSV;
                String compoundUpperCase = compound.substring(0, 1).toUpperCase() + compound.substring(1);
                Compositions.add(compoundUpperCase);
            }
            else if (SearchLanguage == "en") {
                String compound = item.Compositions.get(j).RecommendedNameEN;
                String compoundUpperCase = compound.substring(0, 1).toUpperCase() + compound.substring(1);
                Compositions.add(compoundUpperCase);
            }
            else {
                String compound = item.Compositions.get(j).RecommendedNameEN;
                String compoundUpperCase = compound.substring(0, 1).toUpperCase() + compound.substring(1);
                Compositions.add(compoundUpperCase);
            }
        }


        /*Compounds.add(item.Compositions.get(0).Name);
        Compounds.add(item.Compositions.get(0).NarcoticClass);
        Compounds.add(item.Compositions.get(0).NarcoticClassSpecified);
        Compounds.add(item.Compositions.get(0).Quantity);
        Compounds.add(item.Compositions.get(0).RecommendedNameEN);
        Compounds.add(item.Compositions.get(0).RecommendedNameSV);
        Compounds.add(item.Compositions.get(0).RelatedToID);
        Compounds.add(item.Compositions.get(0).Relation);
        Compounds.add(item.Compositions.get(0).RelationRole);
        Compounds.add(item.Compositions.get(0).UnitAltName);
        Compounds.add(item.Compositions.get(0).UnitTermName);*/
        List <String> OrgInfo = new ArrayList<>();
        String adress;
        for (int i = 0; i < item.Organizations.size(); i++)
        {
            OrgInfo.add(item.Organizations.get(i).Orgname);

            adress = item.Organizations.get(i).OrgaddressAddr1;
            if (!item.Organizations.get(i).OrgaddressAddr2.equals(""))
            {
                adress = adress.concat("\n");
            }

            adress = adress.concat(item.Organizations.get(i).OrgaddressAddr2);
            if (!item.Organizations.get(i).OrgaddressAddr3.equals(""))
            {
            adress = adress.concat("\n");
            }
            adress = adress.concat(item.Organizations.get(i).OrgaddressAddr3);
            if (!item.Organizations.get(i).OrgaddressAddr4.equals(""))
            {
                adress = adress.concat("\n");
            }
            adress = adress.concat(item.Organizations.get(i).OrgaddressAddr4);
            OrgInfo.add(adress);
            OrgInfo.add(item.Organizations.get(i).Country);
            OrgInfo.add(item.Organizations.get(i).OrgRole);
        }

        //OrgInfo.add(item.Organizations.get(0).VatNo);
        //OrgInfo.add(item.Organizations.get(0).OrgUnitName);

        List <String> Package = new ArrayList<>();

        for (int k = 0; k < item.Packages.size(); k++)
        {
            Package.add(item.Packages.get(k).Text);
            String shelflife = item.Packages.get(k).ShelflifeValue;
            shelflife = shelflife.concat(" ");
            shelflife = shelflife.concat(item.Packages.get(k).UnitName);
            Package.add(shelflife);
            Package.add(item.Packages.get(k).StorageDescription);
        }
        //Package.add(item.Packages.get(0).Prescription);
        //Package.add(item.Packages.get(0).Safety_Features);
        //Package.add(item.Packages.get(0).ConditionID);
        //Package.add(item.Packages.get(0).PharmaceuticalProductID);
        //Package.add(item.Packages.get(0).UnitName);
        //Package.add(item.Packages.get(0).PackCondition);

        listHash.put(listDataHeader.get(0), Product);
        listHash.put(listDataHeader.get(1), Compositions);
        listHash.put(listDataHeader.get(2), OrgInfo);
        listHash.put(listDataHeader.get(3), Package);


        tab1.setEnabled(true);
        tab3.setEnabled(true);
        tabCreated = true;
        ManageRecentSearches();
        bottomNavigationView.setSelectedItemId(R.id.retrived_data_tab);
        Save();
    }

    void ManageRecentSearches()
    {
        if (RecentSearchesList.size() > 10)
        {
            RecentSearchesList.remove(10);
            recentSearchListTitle.remove(10);
        }
    }

    private void setupButtons(SharedPreferences prefs) {

        /*Search bar Logic*/
        SearchBar = (EditText) findViewById(R.id.search_bar);

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
                                ShowHideProgressBar(true);
                                SearchValue = SearchBar.getText().toString();
                                ApiFirstSearchNoStrengthCallback(SearchValue); //Callback to get Context to ApiHandler
                                hideSoftKeyboard(SearchBar);
                                SearchBar.setText("");
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

                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.how_to);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                TextView infoClose = dialog.findViewById(R.id.close_help_btn);
                infoClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                final TextView info_text = dialog.findViewById(R.id.info_text);
                final ImageView info_image = dialog.findViewById(R.id.info_image);
                final ImageView info1 = dialog.findViewById(R.id.info_page_1);
                final ImageView info2 = dialog.findViewById(R.id.info_page_2);
                final ImageView info3 = dialog.findViewById(R.id.info_page_3);
                final ImageView info4 = dialog.findViewById(R.id.info_page_4);
                info1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        info_text.setText(getResources().getString(R.string.how_to_scan));
                        info_image.setImageResource(R.drawable.scan);
                        info1.setColorFilter(getResources().getColor(R.color.white));
                        info2.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                        info3.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                        info4.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                    }
                });

                info2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        info_text.setText(getResources().getString(R.string.how_to_manual));
                        info_image.setImageResource(R.drawable.manual);
                        info1.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                        info2.setColorFilter(getResources().getColor(R.color.white));
                        info3.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                        info4.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                    }
                });

                info3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        info_text.setText(getResources().getString(R.string.how_to_links));
                        info_image.setImageResource(R.drawable.links);
                        info1.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                        info2.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                        info3.setColorFilter(getResources().getColor(R.color.white));
                        info4.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                    }
                });

                info4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        info_text.setText(getResources().getString(R.string.how_to_history));
                        info_image.setImageResource(R.drawable.history);
                        info1.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                        info2.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                        info3.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                        info4.setColorFilter(getResources().getColor(R.color.white));
                    }
                });


                //cdd.show(); //Shows it
                //cdd.setCanceledOnTouchOutside(false); //Disables cancelations
                //cdd.setCancelable(false);
            }
        });

        if (prefs.getBoolean("bool", true)) {
            if (!IsSupported()) {
                Toast.makeText(this,"Not Supported",Toast.LENGTH_LONG);
                //TODO Change the UI
            }
            cdd.yes.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    SharedPreferences.Editor editor = getSharedPreferences("disclaimer", MODE_PRIVATE).edit();
                    editor.putBoolean("bool", false);
                    editor.apply();

                    cdd.dismiss();
                }
            });
        }
    }
    public boolean IsSupported (){
        CameraManager cameraManager = (CameraManager) this.getSystemService(CAMERA_SERVICE);
        String cameraID = null;
        try {
            for (String cameranum : cameraManager.getCameraIdList()){
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameranum);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK){
                    if (cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED){
                        return false;
                    }
                    else {
                        return true;
                    }
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    return true;
    }
    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void ApiFirstSearchNoStrengthCallback(final String name) { //Need callback to get Context.
        mApiHandler.FirstSearchNoStrength(name, SearchLanguage, this, new ApiHandler.FirstSearchListener() {
            @Override
            public void onEmptyResult() {
                Toast.makeText(MainActivity.this,getString(R.string.no_product_match) + " " + name,Toast.LENGTH_SHORT).show();
                ShowHideProgressBar(false);
            }

            @Override
            public void onMultipleResults(JSONArray listResult) {
                ShowProductsDialog(listResult);
            }

            @Override
            public void onSingleResult(JSONObject finalProduct) {
                CreateItem(finalProduct);
                ShowHideProgressBar(false);
            }

            @Override
            public void onException(JSONException jsonexception) {
                ShowHideProgressBar(false);
            }

            @Override
            public void onVolleyError(VolleyError volleyError) {
            ShowHideProgressBar(false);
            }
        });
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

    public void SubstanceSearch (String input)
    {
        String search = "https://www.google.com/search?q=";
        search = search.concat(input);
        Intent browsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(search));
        startActivity(browsIntent);
    }

    void Save()
    {
        SharedPreferences.Editor sharedPreferences = this.getSharedPreferences("SavedData", Context.MODE_PRIVATE).edit();


        for (int i = 0; i < listDataHeader.size(); i++)
        {
            Set<String> target = new HashSet<String>();
            List<String> list = new ArrayList<>();
            list = (listHash.get(listDataHeader.get(i)));

            sharedPreferences.putString(listDataHeader.get(i), TextUtils.join(";", list));
            sharedPreferences.apply();

        }

        int jsonAmount = recentItems.size();
        sharedPreferences.putInt("savedSize", jsonAmount);
        for (int k = 0; k < jsonAmount; k++)
        {
            String job = "JOb";
            job += k;
            sharedPreferences.putString(job, recentItems.get(k).toString());
        }
        sharedPreferences.putString("titles", TextUtils.join(";", listDataHeader));
        sharedPreferences.apply();
    }

    private boolean Load(SharedPreferences preferences)
    {

        int amount = preferences.getInt("savedSize", 0);
        if (amount != 0) {
            recentItems = new ArrayList<>();
            recentSearchListTitle.clear();
            RecentSearchesList = new ArrayList<>();

            for (int j = 0; j < amount; j++) {
                String job = "JOb";
                job += j;
                String strJson = preferences.getString(job, null);
                if (strJson != null)
                    try {
                        JSONObject response = new JSONObject(strJson);
                        CreateItem(response);

                    } catch (JSONException e) {

                    }

            }

            String serialized = preferences.getString("titles", null);

            List<String> list = new ArrayList(Arrays.asList(TextUtils.split(serialized, ";")));

            listDataHeader = new ArrayList<String>(list);
            listHash = new HashMap<>();


            for (int i = 0; i < listDataHeader.size(); i++)
            {
                String serializedList = preferences.getString(listDataHeader.get(i), null);
                List<String> productList = Arrays.asList(TextUtils.split(serializedList, ";"));

                listHash.put(listDataHeader.get(i), productList);
            }
            return true;
        }
        return false;

    }

    public void FassSearch (String input)
    {
        String search = "https://www.fass.se/m/sok/";
        search = search.concat(input);
        search = search.concat("/public");
        Intent browsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(search));
        startActivity(browsIntent);
    }

    public void initializeFassSearch (String input)
    {
        final ExpandableListView layout = (ExpandableListView) findViewById(R.id.eLV);
        final String searchString = input;
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Button button = findViewById(R.id.fass_Search);
                if (button!= null) {
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FassSearch(searchString);
                        }
                    });
                }
            }
        });
    }

}



package com.example.android.urbangarden;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;


import com.example.android.urbangarden.controller.GardenAdapter;
import com.example.android.urbangarden.location.GPSTracker;

import com.example.android.urbangarden.Networking.NetworkUtility;
import com.example.android.urbangarden.Networking.RetrofitListener;
import com.example.android.urbangarden.model.Garden;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GardenSearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinner;
    EditText searchEditText;
    String spinnerOption;
    String searchQuery;
    String queryType;

    GPSTracker gps;
    double latitude;
    double longitude;

    private RecyclerView recyclerView;
    List<Garden> gardenList = new ArrayList<>();

    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garden_search);
        setSearchSpinner();

        getLocation();
        recyclerView = findViewById(R.id.search_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));



        searchEditText = (EditText) findViewById(R.id.search_query_edit_text);

    }



    //sets up the spinner
    public void setSearchSpinner() {
        spinner = (Spinner) findViewById(R.id.boro_choices_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.boro_spinner_options, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void onSearchClick(View view){
        queryType = "postcode";
        searchQuery = searchEditText.getText().toString();
        Log.d("search query", searchQuery);

        makeNetworkCall(searchQuery, queryType, new RetrofitListener() {
            @Override
            public void updateUI(Garden[] gardens) {
                Log.d("update UI", String.valueOf(gardens.length));
                //TODO add data to Recycler view
                gardenList.addAll(Arrays.asList(gardens));
                recyclerView.setAdapter(new GardenAdapter(gardenList));
            }

            @Override
            public void onFailureAlert() {

                alertUserAboutError();

            }
        });

    }
    public void makeNetworkCall(String searchQuery, String queryType, RetrofitListener listener){
        NetworkUtility utility = NetworkUtility.getUtility();
        utility.getGardensByQuery(searchQuery, queryType, listener);
    }


    private void alertUserAboutError() {
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        alertDialogFragment.show(getFragmentManager(), "error_dialog");

    }

    //spinner selection on click
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerOption = (String) parent.getItemAtPosition(position);
        if (!spinnerOption.equals("Search by borough")){
            queryType = "boro";
            switch(spinnerOption) {
                case "Brooklyn":
                    searchQuery = "B";
                    break;
                case "Manhattan":
                    searchQuery = "M";
                    break;
                case "Bronx":
                    searchQuery = "X";
                    break;
                case "Queens":
                    searchQuery = "Q";
                    break;
                case "Staten Island":
                    searchQuery = "R";
                    break;
                default:
                    searchQuery = "M";
            }
            makeNetworkCall(searchQuery, queryType, new RetrofitListener() {
                @Override
                public void updateUI(Garden[] gardens) {
                    gardenList.addAll(Arrays.asList(gardens));
                    recyclerView.setAdapter(new GardenAdapter(gardenList));
                }

                @Override
                public void onFailureAlert() {
                    alertUserAboutError();
                }
            });

            }

    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.garden_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fav:
                Intent intent = new Intent(GardenSearchActivity.this, MyGardensActivity.class);
//                intent.putExtra("myGardenList", "");
                startActivity(intent);
                Log.e(TAG, "Fav button was clicked");
                break;
            default:
                Log.e(TAG, "No button was clicked");
        }
        return true;
    }


    private void getLocation() {
        gps = new GPSTracker(GardenSearchActivity.this);

        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Log.d("latitude", String.valueOf(latitude));
            Log.d("longtitude", String.valueOf(longitude));
        } else {
            gps.showSettingsAlert();
        }
    }

}

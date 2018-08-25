package com.example.luist.munidenuncias.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.luist.munidenuncias.R;
import com.example.luist.munidenuncias.Utils.Util;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class GeoPlacesActivity extends AppCompatActivity {

    private static String TAG = GeoPlacesActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_places);

        startPlacePickerActivity();
    }

    private void startPlacePickerActivity()
    {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try
        {
            Intent intent = builder.build(this);
            startActivityForResult(intent, Util.REQUEST_CODE_PLACEPICKER);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Util.REQUEST_CODE_PLACEPICKER && resultCode == RESULT_OK)
        {
            displaySelectedPlaceFromPlacePicker(data);
        }
    }

    private void displaySelectedPlaceFromPlacePicker(Intent data)
    {

        setResult(Util.REQUEST_CODE_PLACEPICKER,data);
        finish();
//
//        Place place = PlacePicker.getPlace(this,data);
//        String name = place.getName().toString();
//        String address = place.getAddress().toString();
//
//        Log.e(TAG, place.getName().toString());
//        Log.e(TAG, place.getAddress().toString());
//        Log.e(TAG, place.getLatLng().toString());


    }
}

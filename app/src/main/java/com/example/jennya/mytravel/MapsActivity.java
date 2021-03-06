/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jennya.mytravel;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This shows how to create a simple activity with a map and a marker on the map.
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener//,
//    ActivityCompat.OnRequestPermissionsResultCallback
{

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;

    private Button mButton, mButtonGoogleMaps;
    private EditText mEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mButton = (Button) findViewById(R.id.estimateDistanceButton);
        mButtonGoogleMaps = (Button) findViewById(R.id.estimateWithGoogleMaps);
        mEdit = (EditText) findViewById(R.id.editText);

        //when "Estimate" button was clicked
        mButton.setOnClickListener(
            new View.OnClickListener()
            {
                public void onClick(View view)
                {
                    String destinationAddress = mEdit.getText().toString();
                    Log.v("EditText", destinationAddress);
//                    Intent searchAddress = new  Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="+mEdit.getText().toString()));
//                    startActivity(searchAddress);

                    Geocoder coder = new Geocoder(MapsActivity.this);
                    double longitude = 0;
                    double latitude = 0;
                    try
                    {
                        ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(destinationAddress, 50);
                        for (Address add : adresses)
                        {
                            if (!destinationAddress.isEmpty())
                            {//Controls to ensure it is right address such as country etc.
                                longitude = add.getLongitude();
                                latitude = add.getLatitude();
                            }
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(destinationAddress));

                }
            });


        // when "Estimate with Google maps" button was clicked
        mButtonGoogleMaps.setOnClickListener(
            new View.OnClickListener()
            {
                public void onClick(View view)
                {
                    String destinationAddress = mEdit.getText().toString();
                    Log.v("EditText", destinationAddress);
                    Intent searchAddress = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + destinationAddress));
                    startActivity(searchAddress);

                }
            });


    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation()
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED)
        {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null)
        {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick()
    {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE)
        {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
            android.Manifest.permission.ACCESS_FINE_LOCATION))
        {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else
        {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments()
    {
        super.onResumeFragments();
        if (mPermissionDenied)
        {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError()
    {
        PermissionUtils.PermissionDeniedDialog
            .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    @Override
    public void onMapReady(GoogleMap map)
    {
        mMap = map;

        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
    }


}

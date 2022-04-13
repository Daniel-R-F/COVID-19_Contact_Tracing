package com.example.contacttracing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/*
    @todo - The service should run all the time. Not just when app is in focus.
    @todo - Get physical address from coordinates using Geocoder.
 */
public class LocationLogging extends Service {
    private static final String TAG = "LocationLogging Service";
    static int DEFAULT_INTERVAL = 1000 * 30; // default interval in ms.
    static int FASTEST_INTERVAL = 1000 * 5; // default interval in ms.

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest; // Config for fusedLocationProviderClient
    private LocationCallback locationCallback;

    // Suppressed b/c permission is granted in LandingActivity.
    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(DEFAULT_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.d(TAG, "onLocationResult: Location Update!");
                updateUI(locationResult.getLastLocation());
            }

            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

 // USED FOR A ONE TIME UPDATE
//        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                updateUI(location);
//            }
//        });
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    static boolean hasPermissions(Context context){
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void updateUI(Location location){
        String coordinates = "Longitude: " + location.getLongitude() + "\nLatitude: " +
                location.getLatitude();
        LandingActivity.test_location.setText(coordinates);
    }
}

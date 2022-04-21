package com.example.contacttracing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.view.ViewDebug;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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
                Location location = locationResult.getLastLocation();
                updateUI(location);
                getFromLocation(location);

            }



//            Geocoder geocoder = new Geocoder(LocationLogging.this) {
//                @Override
//                public List<Address> getFromLocation(@NonNull double latitude, double longitude, int maxResults) throws IOException {
//
//                    try {
//                        List<Address> addresses = getFromLocation(latitude, longitude, maxResults);
//                        Log.d(TAG, Double.toString(latitude) + " " + Double.toString(longitude));
//                        return addresses;
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
////                    return super.getFromLocation(latitude, longitude, maxResults);
////                    return addresses;
//
//            }

private String getFromLocation(Location location) {
    String strAdd = "";
    Geocoder geocoder = new Geocoder(LocationLogging.this, Locale.getDefault());
    try {
        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
        if (addresses != null) {
            Address returnedAddress = addresses.get(0);
            StringBuilder strReturnedAddress = new StringBuilder("");

            for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
            }
            strAdd = strReturnedAddress.toString();
            Log.d("My Current location address", strReturnedAddress.toString());
        } else {
            Log.d("My Current location address", "No Address returned!");
        }
    } catch (Exception e) {
        e.printStackTrace();
        Log.d("My Current location address", "Canont get Address!");
    }
    return strAdd;
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

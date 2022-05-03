package com.example.contacttracing;

import static com.example.contacttracing.App.CHANNEL_ID;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.contacttracing.pojo.Contact;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.FieldValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/*
    @ todo - Contact tracing logic
 */
public class ContactTracing extends Service {

    private static final String TAG = "Tracing Service";
    static HashMap<String, Boolean> connections;
    static HashMap<String, String> recordIds;
    private byte rank;
    private String mAddress;
    private FirebaseAuth fAuth;
    private Message mMessage;
    private MessageListener mMessageListener;

    static boolean hasPermissions(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Map<String, String> value = ServerValue.TIMESTAMP;
        connections = new HashMap<>();
        recordIds = new HashMap<>();
        fAuth = FirebaseAuth.getInstance();

        mMessageListener = new MessageListener() {
            @Override
            public void onFound(@NonNull Message message) {
                super.onFound(message);
                String contactUid = new String(message.getContent());
                // TEST
                connections.put(contactUid, true);
                LandingActivity.msgTV.setText(contactUid);
                // TEST
                initGPS();
                Contact contact = new Contact(contactUid, mAddress);
                Utils.registerContact(fAuth.getUid(), contact);
            }

            @Override
            public void onLost(@NonNull Message message) {
                super.onLost(message);
                String contactUid = new String(message.getContent());
                connections.replace(contactUid, false);

                Utils.endContact(fAuth.getUid(), contactUid);
            }
        };

        mMessage = new Message(fAuth.getUid().getBytes());
        Nearby.getMessagesClient(this).publish(mMessage);
        Nearby.getMessagesClient(this).subscribe(mMessageListener);

//        initGPS();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Contact Tracing")
                .setContentText("Contact Tracing running in background.")
                .setSmallIcon(R.drawable.ic_logo_nearby_48dp)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    // Suppressed b/c permission is granted in LandingActivity.
    @SuppressLint("MissingPermission")
    private void initGPS() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();

                updateUI(location);
                mAddress = getAddress(location);

                Log.d(TAG, "Current Address: " + mAddress);
            }
        };

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private String getAddress(Location location) {
        String address = null;
        Geocoder geocoder = new Geocoder(ContactTracing.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                address = strReturnedAddress.toString();
            } else {
                Log.d(TAG, "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Canont get Address!");
        }
        return address;
    }

    private void updateUI(Location location) {
        String coordinates = "Longitude: " + location.getLongitude() + "\nLatitude: " +
                location.getLatitude();
        LandingActivity.test_location.setText(coordinates);
    }
}

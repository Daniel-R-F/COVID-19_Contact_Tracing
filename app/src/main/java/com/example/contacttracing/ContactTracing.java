package com.example.contacttracing;

import static com.example.contacttracing.App.CHANNEL_ID;
import static com.example.contacttracing.App.EXPOSURE_ID;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.contacttracing.firebase.Contact;
import com.example.contacttracing.firebase.Exposure;
import com.example.contacttracing.firebase.UtilsDB;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/*
    @ todo - Contact tracing logic
 */
public class ContactTracing extends Service {

    public static boolean active;
    private static final String TAG = "Tracing Service";
    public static HashMap<String, String> recordIds;
    private String mAddress;
    private NotificationManagerCompat notificationManager;
    private HashSet<String> connections;
    private FirebaseAuth fAuth;
    private Message mMessage;
    private MessageListener mMessageListener;

    static boolean hasPermissions(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        active = true;

        notificationManager = NotificationManagerCompat.from(ContactTracing.this);
        connections = new HashSet<>();
        recordIds = new HashMap<>();
        fAuth = FirebaseAuth.getInstance();

        initGPS();

        initDbListener();

        Strategy strategy = new Strategy.Builder()
                .setDistanceType(Strategy.DISTANCE_TYPE_EARSHOT)
                .setDiscoveryMode(Strategy.DISCOVERY_MODE_DEFAULT)
                .build();

        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(strategy)
                .build();


        mMessageListener = new MessageListener() {
            @Override
            public void onFound(@NonNull Message message) {
                super.onFound(message);
                String contactUid = new String(message.getContent());
                Log.d(TAG, "onFound: " + contactUid);

                checkContact(contactUid);

                Contact contact = new Contact(mAddress);
                UtilsDB.registerContact(fAuth.getUid(), contactUid, contact);
            }

            @Override
            public void onLost(@NonNull Message message) {
                super.onLost(message);
                String contactUid = new String(message.getContent());
                Log.d(TAG, "onLost: " + contactUid);

                UtilsDB.endContact(fAuth.getUid(), contactUid);
            }

        };

        mMessage = new Message(fAuth.getUid().getBytes());

        Nearby.getMessagesClient(this).subscribe(mMessageListener, options);

        Nearby.getMessagesClient(this).publish(mMessage);


        Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask refresh = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    try {
                        Log.d(TAG, "Refresh! ");
                        initGPS();
                        Nearby.getMessagesClient(ContactTracing.this).unsubscribe(mMessageListener);
                        Nearby.getMessagesClient(ContactTracing.this).unpublish(mMessage);

                        Nearby.getMessagesClient(ContactTracing.this).subscribe(mMessageListener, options);
                        Nearby.getMessagesClient(ContactTracing.this).publish(mMessage);
                    } catch (Exception e) {
                        Log.e(TAG, "refresh run: " + e.getMessage());
                    }
                });

            }
        };

        timer.schedule(refresh, UtilsDB.TEN_MIN / 2, UtilsDB.TEN_MIN / 2);
    }

    private void initDbListener() {
        String path = "Exposures/" + fAuth.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    exposureNotification();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }

    private void checkContact(String contactUid) {
        if (!connections.contains(contactUid)) {
            getStatus(contactUid);
            Log.e(TAG, "checkContact: Notification attempt");
            connections.add(contactUid);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, SettingsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Contact Tracing")
                .setContentText("Scanning for nearby devices.")
                .setSmallIcon(R.drawable.ic_logo_nearby_48dp)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        active = false;
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
            Log.d(TAG, "Cannot get Address!");
        }
        return address;
    }


    private void getStatus(String uid) {
        String dbPath = String.format("Users/%s/infected", uid);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(dbPath);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean infected = Objects.requireNonNull(snapshot.getValue(boolean.class));

                    if (infected)
                        pushExposure(uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });
    }

    private void pushExposure(String uid) {
        String dbPath = String.format("Exposures/%s", fAuth.getUid(), uid);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(dbPath);

        String randKey = ref.push().getKey();
        Date now = new Date();

        ref.child(randKey).setValue(new Exposure(0, now.getTime(), mAddress, randKey));
    }

    private void exposureNotification() {
        Intent notificationIntent = new Intent(this, NotificationsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, EXPOSURE_ID)
                .setContentTitle("Contact Tracing")
                .setContentText("Review possible exposures!")
                .setSmallIcon(R.drawable.ic_ct_icon)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        notificationManager.notify(2, notification);
    }
}

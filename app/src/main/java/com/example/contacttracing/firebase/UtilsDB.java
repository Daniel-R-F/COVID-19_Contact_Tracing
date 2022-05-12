package com.example.contacttracing.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.contacttracing.ContactTracing;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class UtilsDB {
    public static final long TEN_MIN = 600000;
    public static final String TAG = "Firebase";

    public static void registerContact(String uid, String contactUid, Contact contact) {
        String dbPath = String.format("Users/%s/Contacts/%s", uid, contactUid);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(dbPath);

        ref.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Date now = new Date();
                    Long end = null;
                    Long start = null;
                    String contactID = "";
                    long totalDifference = 0;

                    Log.d(TAG, "children in snapshot -> " + snapshot.getChildrenCount());

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        start = ds.child("startTime").getValue(Long.class);
                        end = ds.child("endTime").getValue(Long.class);
                        contactID = ds.getKey();

                        totalDifference = Objects.requireNonNull(ds.child("offTime").getValue(long.class));
                    }


                    Date startTime = new Date(Objects.requireNonNull(start));

                    Log.d(TAG, "start Date: " + startTime.getDay());
                    long differenceMs = Math.abs(now.getTime() - startTime.getTime());
                    long differenceDays = TimeUnit.DAYS.convert(differenceMs, TimeUnit.MILLISECONDS);


                    Log.d(TAG, "\nRecord Created " + differenceMs + "ms ago.");
                    Log.d(TAG, differenceDays + " days");

                    /// Check for contact in the past 24 hours.
                    if (differenceDays < 1) {
                        if (end != null) {
                            Date endTime = new Date(end);

                            differenceMs = Math.abs(now.getTime() - endTime.getTime());
                            totalDifference += differenceMs;

                            // Add offTime (Time devices were disconnected)
                            if (differenceMs > TEN_MIN) {
                                ref.child(Objects.requireNonNull(contactID)).child("offTime").setValue(totalDifference);
                                Log.d(TAG, "offTime added!");
                            } else
                                Log.d(TAG, "No offTime added!");
                        }

                        ref.child(Objects.requireNonNull(contactID)).child("endTime").setValue(ServerValue.TIMESTAMP);

                        return;
                    }

                }
                /// Add new record if no contact in past 24 hours.
                Log.d(TAG, "onDataChange: New contact!");

                String contactId = ref.push().getKey();
                assert contactId != null;

                ref.child(contactId).setValue(contact).addOnCompleteListener(task -> {
                    Log.d(TAG, "add DB: "+ contact.getLocation());
                    if (task.isSuccessful()) {
                        Log.d(TAG, "registerContact: Successful push to DB!");
                        ContactTracing.recordIds.put(contactUid, contactId);
                    } else {
                        String error = Objects.requireNonNull(task.getException()).getMessage();
                        Log.e(TAG, error);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });
    }


    public static void endContact(String uid, String contactUid) {

        String dbPath = String.format("Users/%s/Contacts/%s", uid, contactUid);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(dbPath);

        // Update endTime (Time connection terminated)
        ref.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String contactID = "";

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        contactID = ds.getKey();
                    }

                    ref.child(Objects.requireNonNull(contactID)).child("endTime").setValue(ServerValue.TIMESTAMP);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, error.getMessage());
            }
        });
    }
}

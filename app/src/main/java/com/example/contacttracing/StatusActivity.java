package com.example.contacttracing;

import static com.example.contacttracing.firebase.UtilsDB.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.contacttracing.firebase.Exposure;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class StatusActivity extends AppCompatActivity {

    private static final int TWO_WEEKS = 14;
    private FirebaseAuth fAuth;


    private TextView statusTV;

    private Button positiveBtn;
    private Button negativeBtn;

    private ProgressBar statusPB;

    private boolean infected;

    private String dbPath;

    private DatabaseReference ref;

    /**
     * Factory pattern provided Intent to switch to this activity.
     *
     * @param context current application context.
     * @return activity's intent.
     */
    public static Intent intentFactory(Context context) {
        return new Intent(context, StatusActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        fAuth = FirebaseAuth.getInstance();

        dbPath = String.format("Users/%s/infected", fAuth.getUid());
        ref = FirebaseDatabase.getInstance().getReference(dbPath);


        wireDisplay();
        getStatus();
    }

    private void wireDisplay() {
        statusTV = findViewById(R.id.status_tv);
        positiveBtn = findViewById(R.id.positive_btn);
        negativeBtn = findViewById(R.id.negative_btn);
        statusPB = findViewById(R.id.status_pb);

        positiveBtn.setOnClickListener(view -> {
            toggleDisplay(false);
            changeStatus();
        });
    }

    private void changeStatus() {
        if (!infected) {
            findExposed();
            Toast.makeText(this, "Notifying exposed ...", Toast.LENGTH_SHORT).show();
            return;
        }
        updateDb(false);
    }

    private void updateDb(boolean infected) {
        ref.setValue(infected).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Status changed to " + infected, Toast.LENGTH_LONG).show();
                finish();
                startActivity(LandingActivity.intentFactory(StatusActivity.this));
            } else {
                Toast.makeText(this, "Error, please try again", Toast.LENGTH_LONG).show();
                Log.d(TAG, "changeStatus: " + Objects.requireNonNull(task.getException()).getMessage());
                toggleDisplay(true);
            }
        });
    }

    private void findExposed() {
        // @todo find exposed and add them to exposed table.
        String contactPath = String.format("Users/%s/Contacts/", fAuth.getUid());
        DatabaseReference ContactRef = FirebaseDatabase.getInstance().getReference(contactPath);

        ContactRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot User : snapshot.getChildren()) {
                        Exposure exposure = new Exposure();

                        String contactUid = User.getKey();

                        for (DataSnapshot contact : User.getChildren()) {

                            long start = Objects.requireNonNull(contact.child("startTime").getValue(long.class));

                            if (exposure(start)) {
                                // get date and address of latest contact
                                if (start > (long) exposure.getLatestDate()) {
                                    exposure.setLatestDate(start);
                                    if (contact.child("location").exists()) {
                                        String location = Objects.requireNonNull(contact.child("location").getValue(String.class));
                                        exposure.setLocation(location);
                                    }
                                }

                                long time = getTime(start, contact);
                                exposure.setMinutes(exposure.getMinutes() + time);
                            }

                            String path = String.format("Exposures/%s", contactUid, fAuth.getUid());
                            DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference(path);

                            String randKey = databaseReference.push().getKey();
                            exposure.setKey(randKey);
                            databaseReference.child(randKey).setValue(exposure);
                        }
                    }
                }
                updateDb(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }

    private boolean exposure(long start) {
        Date now = new Date();
        Date startTime = new Date(start);

        long differenceMs = Math.abs(now.getTime() - startTime.getTime());
        long differenceDays = TimeUnit.DAYS.convert(differenceMs, TimeUnit.MILLISECONDS);

        return differenceDays <= TWO_WEEKS;
    }

    private long getTime(long start, DataSnapshot contact) {
        long time = 0;

        if (contact.child("endTime").exists()) {
            long end = Objects.requireNonNull(contact.child("endTime").getValue(long.class));
            long offTime = 0;

            if (contact.child("offTime").exists()) {
                offTime = Objects.requireNonNull(contact.child("offTime").getValue(long.class));
            }

            Date startTime = new Date(start);
            Date endTime = new Date(end);

            long differenceMs = Math.abs(endTime.getTime() - startTime.getTime());
            differenceMs -= offTime;

            time = TimeUnit.MINUTES.convert(differenceMs, TimeUnit.MILLISECONDS);

        }
        return time;
    }

    private void getStatus() {

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    infected = Objects.requireNonNull(snapshot.getValue(boolean.class));
                    if (infected) {
                        statusTV.setText(R.string.positive_label);
                    } else {
                        statusTV.setText(R.string.negative_label);
                    }
                    toggleDisplay(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });
    }

    private void toggleDisplay(boolean on) {
        if (on) {
            statusPB.setVisibility(View.GONE);
            statusTV.setVisibility(View.VISIBLE);
            positiveBtn.setVisibility(View.VISIBLE);
            negativeBtn.setVisibility(View.VISIBLE);
        } else {
            statusPB.setVisibility(View.VISIBLE);
            statusTV.setVisibility(View.GONE);
            positiveBtn.setVisibility(View.GONE);
            negativeBtn.setVisibility(View.GONE);
        }
    }
}
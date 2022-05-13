package com.example.contacttracing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.contacttracing.firebase.Exposure;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Date;
import java.util.Objects;

public class NotificationsActivity extends AppCompatActivity {
    private FirebaseAuth fAuth;
    private DatabaseReference ref;
    private FirebaseListAdapter<Exposure> fAdapter;

    private ListView exposure_lv;

    /**
     * Factory pattern provided Intent to switch to this activity.
     *
     * @param context current application context.
     * @return activity's intent.
     */
    public static Intent intentFactory(Context context) {
        return new Intent(context, NotificationsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        fAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference("Exposures/");

        exposure_lv = findViewById(R.id.notifications_lv);

        initAdapter();
        exposure_lv.setAdapter(fAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fAdapter.stopListening();
    }

    private void initAdapter() {
        Query query = ref.child(Objects.requireNonNull(fAuth.getUid()));
        FirebaseListOptions<Exposure> options = new FirebaseListOptions.Builder<Exposure>()
                .setQuery(query, Exposure.class)
                .setLayout(R.layout.exposure_notification)
                .build();

        fAdapter = new FirebaseListAdapter<Exposure>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Exposure model, int position) {
                TextView dateTV = v.findViewById(R.id.date_tv);
                TextView timeTV = v.findViewById(R.id.time_tv);
                TextView locationTV = v.findViewById(R.id.location_tv);
                TextView dismissTV = v.findViewById(R.id.dismiss_tv);

                String key = model.getKey();
                Long dateLong = model.getLatestDate();
                Date date = new Date(dateLong);

                dateTV.setText(date.toString());

                if (!model.getLocation().isEmpty())
                    locationTV.setText(model.getLocation());
                if (model.getMinutes() != 0)
                    timeTV.setText("Duration: " + model.getMinutes() + "minutes");


                dismissTV.setOnClickListener(view -> {
                    ref.child(fAuth.getUid() + "/" + key).removeValue();
                });
            }
        };
    }
}
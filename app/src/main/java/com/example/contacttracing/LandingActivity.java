package com.example.contacttracing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LandingActivity extends AppCompatActivity {

    private Button mButton_settings;
    private Button notificationsBtn;

    private FirebaseAuth mFirebaseAuth;

    /**
     * Factory pattern provided Intent to switch to this activity.
     *
     * @param context current application context.
     * @return activity's intent.
     */
    public static Intent intentFactory(Context context) {
        return new Intent(context, LandingActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        mFirebaseAuth = FirebaseAuth.getInstance();
        wireDisplay();
    }

    private void wireDisplay() {
        mButton_settings = findViewById(R.id.settings);
        notificationsBtn = findViewById(R.id.notifications_btn);

        mButton_settings.setOnClickListener(view -> {
            startActivity(SettingsActivity.intentFactory(this));
        });

        notificationsBtn.setOnClickListener(view -> {
            startActivity(NotificationsActivity.intentFactory(this));
        });


    }
}
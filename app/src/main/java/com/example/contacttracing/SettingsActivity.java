package com.example.contacttracing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {
    private FirebaseAuth fAuth;
    private TextView mEmailTV;
    private TextView mPasswordTV;
    private TextView mStatusTV;
    private TextView mLogoutTV;

    private SwitchCompat mServiceSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        fAuth = FirebaseAuth.getInstance();

        wireDisplay();
    }

    private void wireDisplay() {
        mEmailTV = findViewById(R.id.email_tv);
        mPasswordTV = findViewById(R.id.password_tv);
        mStatusTV = findViewById(R.id.update_tv);
        mLogoutTV = findViewById(R.id.logout_tv);

        mServiceSwitch = findViewById(R.id.service_switch);
        mServiceSwitch.setChecked(ContactTracing.active);

        mLogoutTV.setOnClickListener( view -> {
            fAuth.signOut();
            startActivity(MainActivity.intentFactory(this));
        });

        mStatusTV.setOnClickListener(view -> {
            startActivity(StatusActivity.intentFactory(this));
        });

        mServiceSwitch.setOnClickListener(view -> {
            if(mServiceSwitch.isChecked())
                startService(new Intent(this, ContactTracing.class));
            else
                stopService(new Intent(this, ContactTracing.class));

        });
    }

    /**
     * Factory pattern provided Intent to switch to this activity.
     *
     * @param context current application context.
     * @return activity's intent.
     */
    public static Intent intentFactory(Context context) {
        return new Intent(context, SettingsActivity.class);
    }
}
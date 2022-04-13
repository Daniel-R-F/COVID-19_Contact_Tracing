package com.example.contacttracing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LandingActivity extends AppCompatActivity {

    private static final int PERMISSION_LOCATION = 15;
    private Button mButton_settings;
    private Button mButton_Account;
    private Button mButton_statusUpdate;
    private Button mButton_logout;
    static TextView test_location; // TO TEST ONLY

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        mButton_settings = findViewById(R.id.settings);
        mButton_Account = findViewById(R.id.account);
        mButton_statusUpdate = findViewById(R.id.status_update);
        mButton_logout = findViewById(R.id.logout);
        test_location = findViewById(R.id.test_location);

        mFirebaseAuth = FirebaseAuth.getInstance();




        //Logout Button
        mButton_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth.signOut();
                startActivity(new Intent(LandingActivity.this, SignInActivity.class));
            }
        });

        // check if location permissions are granted
        if (LocationLogging.hasPermissions(this)){
            startService(new Intent(this, LocationLogging.class));
        }else
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startService(new Intent(this, LocationLogging.class));
                }else{
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                    finish();
                }
        }

    }

    /**
     * Factory pattern provided Intent to switch to this activity.
     *
     * @param context current application context.
     * @return activity's intent.
     */
    public static Intent intentFactory(Context context) {
        return new Intent(context, LandingActivity.class);
    }
}
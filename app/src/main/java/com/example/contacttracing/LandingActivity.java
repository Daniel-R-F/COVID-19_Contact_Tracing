package com.example.contacttracing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class LandingActivity extends AppCompatActivity {

    private Button mButton_settings;
    private Button mButton_Account;
    private Button mButton_statusUpdate;
    private Button mButton_logout;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        mButton_settings = findViewById(R.id.settings);
        mButton_Account = findViewById(R.id.account);
        mButton_statusUpdate = findViewById(R.id.status_update);
        mButton_logout = findViewById(R.id.logout);

        mFirebaseAuth = FirebaseAuth.getInstance();


        //Logout Button
        mButton_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth.signOut();
                startActivity(new Intent(LandingActivity.this, SignInActivity.class));
            }
        });

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
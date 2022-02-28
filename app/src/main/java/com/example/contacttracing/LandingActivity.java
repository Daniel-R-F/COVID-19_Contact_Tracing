package com.example.contacttracing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
    }

    /**
     * Factory pattern provided Intent to switch to this activity.
     *
     * @param context current application context.
     * @return returns activity's intent.
     */
    public static Intent intentFactory(Context context) {
        return new Intent(context, LandingActivity.class);
    }
}
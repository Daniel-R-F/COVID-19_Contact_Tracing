package com.example.contacttracing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Sign in email & password or Google.
 * @author Daniel Rangel Figueroa
 */
public class SignInActivity extends AppCompatActivity {

    private TextView mLinkTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        wireDisplay();
    }

    /**
     * Factory pattern provided Intent to switch to this activity.
     *
     * @param context current application context.
     * @return returns activity's intent.
     */
    public static Intent intentFactory(Context context) {
        return new Intent(context, SignInActivity.class);
    }

    private void wireDisplay(){
        mLinkTV = findViewById(R.id.sign_up_redirect);
        mLinkTV.setOnClickListener(view -> {
            startActivity(SignUpActivity.intentFactory(this));
        });
    }
}
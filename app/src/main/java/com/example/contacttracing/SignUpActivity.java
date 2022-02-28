package com.example.contacttracing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        wireDisplay();
    }



    /**
     * Factory pattern provided Intent to switch to this activity.
     *
     * @param context current application context.
     * @return returns activity's intent.
     */
    public static Intent intentFactory(Context context) {
        return new Intent(context, SignUpActivity.class);
    }


    private void wireDisplay(){
        TextView linkTV = findViewById(R.id.sign_in_redirect);
        linkTV.setOnClickListener(view -> {
            startActivity(SignInActivity.intentFactory(this));
        });
    }
}
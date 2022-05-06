package com.example.contacttracing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.firebase.auth.FirebaseAuth;

public class LandingActivity extends AppCompatActivity {


    static TextView test_location; // TO TEST ONLY
    static TextView msgTV;
    private Button mButton_settings;
    private Button mButton_Account;
    private Button mButton_statusUpdate;
    private Button mButton_logout;
    private Button dataBtn;
    private TextView dataTV;

    private FirebaseAuth mFirebaseAuth;
    private Message mMessage;
    private MessageListener mMessageListener;

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
        mButton_Account = findViewById(R.id.account);
        mButton_statusUpdate = findViewById(R.id.status_update);
        mButton_logout = findViewById(R.id.logout);
        test_location = findViewById(R.id.test_location);

        //Logout Button
        mButton_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth.signOut();
                startActivity(new Intent(LandingActivity.this, SignInActivity.class));
            }
        });

        dataBtn = findViewById(R.id.data_btn);
        msgTV = findViewById(R.id.msg_tv);
        dataTV = findViewById(R.id.data_tv);
        dataBtn.setOnClickListener(view -> {
            String data = "active connections: ";
            int i = 0;
            for (String key : ContactTracing.connections.keySet()) {
                if (ContactTracing.connections.get(key))
                    i++;
            }
            data += i + "\nConnections had: " + ContactTracing.connections.size();
            dataTV.setText(data);
        });


    }
}
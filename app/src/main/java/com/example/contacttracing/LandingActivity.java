package com.example.contacttracing;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LandingActivity extends AppCompatActivity {

    private Button mButton_settings;
    private Button notificationsBtn;

    private ImageView imageExposed;
    private ImageView imageNotExposed;

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

        imageExposed = findViewById(R.id.imageView_exposed);
        imageNotExposed = findViewById(R.id.imageView_notexposed);

        mFirebaseAuth = FirebaseAuth.getInstance();

        // current user id
        String currentUserId = mFirebaseAuth.getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.child("Exposures").child(currentUserId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().getChildrenCount() > 0) {
                notificationsBtn.setVisibility(View.VISIBLE);
                imageExposed.setVisibility(View.VISIBLE);
                imageNotExposed.setVisibility(View.INVISIBLE);
            } else {
                notificationsBtn.setVisibility(View.INVISIBLE);
                imageExposed.setVisibility(View.INVISIBLE);
                imageNotExposed.setVisibility(View.VISIBLE);
            }
        });
        

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
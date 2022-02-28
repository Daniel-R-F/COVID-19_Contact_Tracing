package com.example.contacttracing;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.contacttracing.pojo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

/**
 * Loading screen: redirects user to appropriate activity.
 * @author Daniel Rangel Figueroa
 */
public class MainActivity extends AppCompatActivity {
    public static User USER;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init private variables
        ProgressBar progress = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();


        if(signedIn()){
            progress.setVisibility(View.VISIBLE);
            progress.setProgress(33);
            progress.setProgress(66);

            pullUserData(mAuth.getUid());

            progress.setProgress(100);

            startActivity(LandingActivity.intentFactory(this));
        }else{
            startActivity(SignInActivity.intentFactory(this));
        }

        finish();
    }


    /**
     * Checks to see if user is signed in.
     *
     * @return Returns true if user is signed in.
     */
    private boolean signedIn() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null;
    }

    /**
     * Pulls user from database then goes to Landing page.
     *
     * @param uid User id.
     */
    void pullUserData(final String uid){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").child(uid).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                USER = Objects.requireNonNull(task.getResult()).getValue(User.class);
            } else{
                Log.e("firebase", "error pulling user.");
            }
        });
    }
}
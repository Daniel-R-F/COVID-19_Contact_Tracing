package com.example.contacttracing;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.contacttracing.pojo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

/**
 * Loading screen: redirects user to appropriate activity.
 *
 * @author Daniel Rangel Figueroa
 */
public class MainActivity extends AppCompatActivity {
    public static User USER;

    private FirebaseAuth fAuth;
    private ProgressBar mProgress;

    private String mEmail;


    Toast resent_toast;
    Toast error_toast;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init private variables
        mProgress = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();

        resent_toast = Toast.makeText(this, "Email resent", Toast.LENGTH_LONG);
        error_toast = Toast.makeText(this, "Try again", Toast.LENGTH_LONG);

        if (signedIn()) {
            mEmail = Objects.requireNonNull(fAuth.getCurrentUser()).getEmail();
            initBuilder();
            validateSignIn();
        } else {
            startActivity(SignInActivity.intentFactory(this));
        }

    }


    /**
     * Checks to see if user is signed in.
     *
     * @return Returns true if user is signed in.
     */
    private boolean signedIn() {
        FirebaseUser user = fAuth.getCurrentUser();
        if (user != null) {
            mProgress.setVisibility(View.VISIBLE);
            mProgress.setProgress(33);
            return true;
        }
        return false;
    }

    /**
     * Pulls user from database then goes to Landing page.
     *
     * @param uid User id.
     */
    private void pullUserData(final String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").child(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mProgress.setProgress(100);
                USER = Objects.requireNonNull(task.getResult()).getValue(User.class);
                startActivity(LandingActivity.intentFactory(MainActivity.this));
                finish();
            } else {
                Log.e("firebase", "error pulling user.");
            }
        });
    }

    private void initBuilder() {
        builder = new AlertDialog.Builder(this);
        builder.setMessage("An email was sent to " +
                mEmail + "\n Follow the link " +
                "to verify your account");

        builder.setPositiveButton("Done", (dialogInterface, i) -> {
            dialogInterface.cancel();
            validateSignIn();
        });

        builder.setNegativeButton("Resend", (dialogInterface, i) -> {
            Objects.requireNonNull(fAuth.getCurrentUser()).sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                            resent_toast.show();
                        else
                            error_toast.show();

                        dialogInterface.cancel();
                        validateSignIn();
                    });
        });

        builder.setNeutralButton("Exit", (dialogInterface, i) -> {
            fAuth.signOut();
            startActivity(SignInActivity.intentFactory(MainActivity.this));
            finish();
        });

        builder.create();
    }

    private void validateSignIn() {
        Objects.requireNonNull(fAuth.getCurrentUser()).reload().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (fAuth.getCurrentUser().isEmailVerified()) {
                    mProgress.setProgress(66);
                    pullUserData(fAuth.getUid());
                } else
                    builder.show();
            } else
                validateSignIn();
        });


    }

    /**
     * Factory pattern provided Intent to switch to this activity.
     *
     * @param context current application context.
     * @return activity's intent.
     */
    public static Intent intentFactory(Context context) {
        return new Intent(context, MainActivity.class);
    }
}
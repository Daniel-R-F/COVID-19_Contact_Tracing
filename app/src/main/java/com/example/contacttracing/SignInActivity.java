package com.example.contacttracing;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contacttracing.firebase.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

/**
 * Sign in with email & password or Google.
 *
 * @author Daniel Rangel Figueroa
 */
public class SignInActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 15;
    private EditText mPasswdET;
    private EditText mEmailET;
    private Button mSignInBtn;

    private FirebaseAuth fAuth;
    private GoogleSignInClient mGsiCl;
    private AlertDialog.Builder mBuilder;


    private boolean mValidEmail;
    private boolean mValidPasswd;

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
     * @return activity's intent.
     */
    public static Intent intentFactory(Context context) {
        return new Intent(context, SignInActivity.class);
    }

    /**
     * Initialize variables and event listeners
     */
    private void wireDisplay() {
        fAuth = FirebaseAuth.getInstance();
        mPasswdET = findViewById(R.id.editTextTextPassword);
        mEmailET = findViewById(R.id.editTextTextEmailAddress);

        mSignInBtn = findViewById(R.id.sign_in_btn);
        Button gsiBtn = findViewById(R.id.sign_in_google);

        GoogleSignInOptions mGsiOp = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGsiCl = GoogleSignIn.getClient(this, mGsiOp);

        TextView forgotTV = findViewById(R.id.forgot_password_dialog);
        TextView mLinkTV = findViewById(R.id.sign_up_redirect);

        mLinkTV.setOnClickListener(view -> {
            startActivity(SignUpActivity.intentFactory(this));
            finish();
        });
        forgotTV.setOnClickListener(view -> {
            initBuilder();
            mBuilder.show();
        });


        mEmailET.addTextChangedListener(uiEmailUpdate());

        mPasswdET.addTextChangedListener(uiPasswdUpdate());

        mSignInBtn.setOnClickListener(view -> signIn());


        gsiBtn.setOnClickListener(view -> googleSignIn());

    }

    /**
     * Updates UI based on Email edit text input.
     */
    private TextWatcher uiEmailUpdate() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Function not needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mValidEmail = Utils.validateEmail(mEmailET.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!mValidEmail) {
                    mSignInBtn.setEnabled(false);
                    mEmailET.setError("Invalid Email");
                } else if (mValidPasswd) {
                    mSignInBtn.setEnabled(true);
                }
            }
        };
    }

    /**
     * Updates UI based on password edit text input.signIn
     */
    private TextWatcher uiPasswdUpdate() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Function not needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mValidPasswd = mPasswdET.getText().toString().length() >= 8;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!mValidPasswd) {
                    mSignInBtn.setEnabled(false);
                } else if (mValidEmail) {
                    mSignInBtn.setEnabled(true);
                }
            }
        };
    }

    /**
     * Sign in with Email and password.
     */
    private void signIn() {
        String email = mEmailET.getText().toString().trim();
        String passwd = mPasswdET.getText().toString();

        fAuth.signInWithEmailAndPassword(email, passwd).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                startActivity(MainActivity.intentFactory(this));
                finish();
            } else {
                String error = Objects.requireNonNull(task.getException()).getMessage();
                Log.e("FIREBASE", error);
                Toast.makeText(SignInActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Starts sign in with google intent.
     */
    private void googleSignIn() {
        Intent signInIntent = mGsiCl.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d("Firebase", "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthGoogle(account.getIdToken());
                } catch (ApiException e) {
                    if (!task.isCanceled()) {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    /**
     * Registers users to firebase auth.
     *
     * @param tokenId Google account token id
     */
    private void firebaseAuthGoogle(String tokenId) {
        AuthCredential credential = GoogleAuthProvider.getCredential(tokenId, null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "signInWithCredential:success");
                        FirebaseUser user = fAuth.getCurrentUser();
                        assert user != null;
                        addUserRecord(user.getUid(), user.getEmail());
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Firebase", "signInWithCredential:failure", task.getException());
                    }
                });

    }

    /**
     * Adds User record when google sign in is used.
     *
     * @param uid   User id from firebase auth.
     * @param email Google account email.
     */
    private void addUserRecord(String uid, String email) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("Users").child(uid);
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (!snapshot.exists()) {
                    User user = new User(email);
                    ref.setValue(user).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            startActivity(MainActivity.intentFactory(this));
                            finish();
                        } else {
                            fAuth.signOut();
                            Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    startActivity(MainActivity.intentFactory(this));
                    finish();
                }
            } else {
                fAuth.signOut();
                Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Initializes mBuilder with an EditText.
     * Used as a "reset password" form.
     */
    private void initBuilder() {
        mBuilder = new AlertDialog.Builder(this);
        Toast success_toast = Toast.makeText(this, "Follow the link sent to your Email to reset your password", Toast.LENGTH_LONG);

        final TextView label = new TextView(this);
        label.setText("Email: ");
        label.setTextSize(16);

        final EditText email_ET = new EditText(this);
        email_ET.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        email_ET.requestFocus();

        LinearLayout linearLayout;
        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(75, 75, 5, 5);
        linearLayout.addView(label);
        linearLayout.addView(email_ET);

        mBuilder.setCancelable(false);
        mBuilder.setTitle("Reset password");
        mBuilder.setView(linearLayout);

        mBuilder.setPositiveButton("Send", (dialogInterface, i) -> {
            fAuth.sendPasswordResetEmail(email_ET.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    success_toast.show();
                } else {
                    String error = Objects.requireNonNull(task.getException()).getMessage();
                    Log.e("FIREBASE", error);
                    Toast.makeText(SignInActivity.this, error, Toast.LENGTH_LONG).show();
                }
            });
            dialogInterface.cancel();
        });

        mBuilder.setNeutralButton("Close", (dialogInterface, i) -> {
            dialogInterface.cancel();
        });

        mBuilder.create();
    }
}
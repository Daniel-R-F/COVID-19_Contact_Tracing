package com.example.contacttracing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Sign in email & password or Google.
 *
 * @author Daniel Rangel Figueroa
 */
public class SignInActivity extends AppCompatActivity {
    private EditText mPasswdET;
    private EditText mEmailET;
    private Button mSignInBtn;

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
     * @return returns activity's intent.
     */
    public static Intent intentFactory(Context context) {
        return new Intent(context, SignInActivity.class);
    }

    private void wireDisplay() {
        mPasswdET = findViewById(R.id.editTextTextPassword);
        mEmailET = findViewById(R.id.editTextTextEmailAddress);
        mSignInBtn = findViewById(R.id.sign_in_btn);
        TextView mLinkTV = findViewById(R.id.sign_up_redirect);

        mLinkTV.setOnClickListener(view -> {
            startActivity(SignUpActivity.intentFactory(this));
            finish();
        });


        mEmailET.addTextChangedListener(uiEmailUpdate());

        mPasswdET.addTextChangedListener(uiPasswdUpdate());

        mSignInBtn.setOnClickListener(view -> signIn());


    }


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

    private void signIn() {
        String email = mEmailET.getText().toString().trim();
        String passwd = mPasswdET.getText().toString();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fAuth.signInWithEmailAndPassword(email, passwd).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                startActivity(MainActivity.intentFactory(this));
                finish();
            } else
                Toast.makeText(SignInActivity.this, "Unable to Sign in. Check credentials.", Toast.LENGTH_SHORT).show();
        });
    }
}
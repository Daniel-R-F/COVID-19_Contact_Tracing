package com.example.contacttracing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contacttracing.pojo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private EditText mPasswdET;
    private EditText mEmailET;
    private Button mSignUpBtn;

    private  FirebaseAuth fAuth;

    private boolean mValidEmail;
    private boolean mValidPasswd;

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
        mPasswdET = findViewById(R.id.editTextTextPassword);
        mEmailET = findViewById(R.id.editTextTextEmailAddress);
        mSignUpBtn = findViewById(R.id.sign_up_btn);
        fAuth = FirebaseAuth.getInstance();


        TextView linkTV = findViewById(R.id.sign_in_redirect);
        linkTV.setOnClickListener(view -> startActivity(SignInActivity.intentFactory(this)));

        mEmailET.addTextChangedListener(uiEmailUpdate());

        mPasswdET.addTextChangedListener(uiPasswdUpdate());

        mSignUpBtn.setOnClickListener(view ->registerUser());
    }

    private TextWatcher uiEmailUpdate(){
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
                if(!mValidEmail){
                    mSignUpBtn.setEnabled(false);
                    mEmailET.setError("Invalid Email");
                } else if(mValidPasswd){
                    mSignUpBtn.setEnabled(true);
                }
            }
        };
    }

    private TextWatcher uiPasswdUpdate(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Function not needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mValidPasswd = Utils.validatePassword(mPasswdET.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!mValidPasswd) {
                    mSignUpBtn.setEnabled(false);
                    mPasswdET.setError("Password must contain: upper and lower case letters, at least one number, and at least one special character");
                } else if(mValidEmail){
                    mSignUpBtn.setEnabled(true);
                }
            }
        };
    }

    private void registerUser(){
        String email = mEmailET.getText().toString().trim();
        String passwd = mPasswdET.getText().toString();
        fAuth.createUserWithEmailAndPassword(email,passwd)
                .addOnCompleteListener( task -> {
                    if (task.isSuccessful()){
                        final String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                        User user = new User(uid, email, false);
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(uid)
                                .setValue(user).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()){
                                        startActivity(SignInActivity.intentFactory(this));
                                        fAuth.signOut();
                                        finish();
                                    } else {
                                        Toast.makeText(SignUpActivity.this, "ERROR", Toast.LENGTH_LONG).show();
                                    }
                        });
                    } else {
                        Log.e("FIREBASE", Objects.requireNonNull(task.getResult()).toString());
                        Toast.makeText(SignUpActivity.this,"User already exists." , Toast.LENGTH_LONG).show();
                    }
                });
    }

}
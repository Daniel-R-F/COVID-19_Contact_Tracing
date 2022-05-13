package com.example.contacttracing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfile extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Button saveBtn;
    private Button changePsswrdBtn;
    private Button deleteBtn;
    EditText profileEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String TAG = "TAG";
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account);
        saveBtn = findViewById(R.id.saveProfileInfo);
        deleteBtn = findViewById(R.id.deleteAccount);
        changePsswrdBtn = findViewById(R.id.changePassword);

        String email = user.getEmail();

        if (user != null) {
            profileEmail = findViewById(R.id.profileEmailAddress);
            profileEmail.setText(email);
        } else {
            Log.d("TAG", "failed");
        }

        changePsswrdBtn.setOnClickListener((v) -> {
                    final EditText resetPassword = new EditText(v.getContext());

                    final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                    passwordResetDialog.setTitle("Reset Password?");
                    passwordResetDialog.setMessage("Enter New Password");
                    passwordResetDialog.setView(resetPassword);

                    passwordResetDialog.setPositiveButton("Yes", (dialogInterface, i) -> {
                        String newPassword = resetPassword.getText().toString();
                        user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(EditProfile.this, "Passsword Reset!", Toast.LENGTH_SHORT).show();
                            }
                        });

                    });
                });


        deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//            String email = profileEmail.getText().toString();
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User account deleted.");
                                    }
                                }
                            });
                }
            });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (profileEmail.getText().toString().isEmpty()) {
                    Toast.makeText(EditProfile.this, "Field is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                //            String email = profileEmail.getText().toString();

                user.updateEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User email address updated.");

                                }
                            }
                        });



            }
        });

        }
    }

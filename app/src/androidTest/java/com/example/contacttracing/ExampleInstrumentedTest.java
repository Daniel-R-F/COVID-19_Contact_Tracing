package com.example.contacttracing;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.contacttracing", appContext.getPackageName());
    }

    /**
     * Test Firebase user creation.
     */
    @Test
    public void signUpAssertPass() {
        String email = "testUsr@aol.edu";
        String passwd = "passwd123";
        fAuth.createUserWithEmailAndPassword(email, passwd)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = fAuth.getCurrentUser();
                        assert user != null;
                        user.delete();
                    }
                });
    }
}
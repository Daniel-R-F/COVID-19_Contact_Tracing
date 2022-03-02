package com.example.contacttracing;

import android.util.Patterns;

import java.util.regex.Pattern;

public class Utils {


    public static boolean validateEmail(String Email){
        return Patterns.EMAIL_ADDRESS.matcher(Email).matches();
    }

    public static boolean validatePassword(String passwd){
        Pattern passwd_regex = Pattern. compile(
                "^" + // Beginning anchor
                "(?=.*\\d)"+ // 1 or many numbers
                "(?=.*[a-z])"+ // 1 or many lowercase
                "(?=.*[A-Z])"+ // 1 or many uppercase
                "(?=.*[:punct:])"+ // 1 or many special
                ".{8,20}"+ // min length: 8 | max: 20;
                "$" // End anchor
        );
        return passwd_regex.matcher(passwd).matches();
    }
}

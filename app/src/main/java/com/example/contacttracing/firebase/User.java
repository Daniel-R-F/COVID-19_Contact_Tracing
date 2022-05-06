package com.example.contacttracing.firebase;


public class User {
    public String email;
    public boolean infected;

    public User(String email) {
        this.email = email.toLowerCase();
        this.infected = false;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isInfected() {
        return infected;
    }

    public void setInfected(boolean infected) {
        this.infected = infected;
    }
}

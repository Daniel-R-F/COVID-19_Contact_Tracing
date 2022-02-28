package com.example.contacttracing.pojo;

import java.util.Objects;

public class User {
    public String uid;
    public String email;
    public boolean infected;

    public User() {this.infected = false;}

    public User(String uid, String email, boolean infected) {
        this.uid = uid;
        this.email = email.toLowerCase();
        this.infected = infected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}

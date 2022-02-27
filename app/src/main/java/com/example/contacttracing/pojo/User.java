package com.example.contacttracing.pojo;

import java.util.Objects;

public class User {
    public String uid;
    public String email;
    public boolean infected;

    public User() {this.infected = false;}

    public User(String uid, String email) {
        this.uid = uid;
        this.email = email;
        this.infected = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(uid, user.uid) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, email);
    }
}

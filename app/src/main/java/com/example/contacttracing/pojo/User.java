package com.example.contacttracing.pojo;

import java.util.Objects;

public class User {
    public String email;
    public boolean infected;
    public boolean exposed;

    public User() {
        this.infected = false;
        this.exposed = false;
    }

    public User(String email) {
        this.email = email.toLowerCase();
        this.infected = false;
        this.exposed = false;
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

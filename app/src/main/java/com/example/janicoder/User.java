package com.example.janicoder;

public class User {
    private String mName;
    private String mEmail;

    private static User single_instance = null;
    public static User getSingle_instance() {
        if (single_instance == null) {
            single_instance = new User();
            return single_instance;
        }
        return single_instance;
    }

    private User() {
    }

    public void setName(String name) {
        mName = name;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getmName() {
        return mName;
    }

    public String getmEmail() {
        return mEmail;
    }
}

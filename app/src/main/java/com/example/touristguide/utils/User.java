package com.example.touristguide.utils;

import java.util.HashMap;

public class User {
    /*Used for handling user profile*/

    public String UserId;
    public String UserName;
    public String Email;
    public String Password;
    private HashMap<String, String> dataMap = new HashMap<String, String>();
    public User() {
    }

    public User(String UserId, String UserName, String Email, String Password) {
        this.UserId = UserId;
        this.UserName = UserName;
        this.Email = Email;
        this.Password=Password;
    }

    public HashMap<String, String> firebaseMap(){
        return dataMap;
    }
}

package com.bricefamily.alex.time_tracker;

/**
 * Created by alex on 16.01.2016.
 */
public class User {
    String username,email,
            password;

    public User(String username,String email,String password){
        this.username=username;
        this.email=email;
        this.password=password;
    }
    public User(String email,String password){
        this.email=email;
        this.password=password;
    }
}

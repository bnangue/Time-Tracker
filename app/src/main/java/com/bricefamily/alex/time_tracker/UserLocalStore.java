package com.bricefamily.alex.time_tracker;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by alex on 16.01.2016.
 */
public class UserLocalStore {

    public static final String SP_NAME="userDetails";
    SharedPreferences userLocalDataBase;

    public UserLocalStore(Context context){
        userLocalDataBase=context.getSharedPreferences(SP_NAME,0);
    }
    public void storeUserData(User user){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putString("email",user.email);
        spEditor.putString("username",user.username);
        spEditor.putString("password",user.password);
        spEditor.commit();
    }

    public User getLoggedInUser(){
        String email=userLocalDataBase.getString("email", "");
        String password=userLocalDataBase.getString("password","");

        User storeduser=new User(email,password);
        return storeduser;
    }
    //call with true if logged in
    public void setUserLoggedIn(boolean loggedIn){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putBoolean("loggedIn",loggedIn);
        spEditor.commit();

    }

    public void clearUserData(){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.clear();
        spEditor.commit();
    }

    public boolean getUserLoggedIn(){
        if(userLocalDataBase.getBoolean("loggedIn",false)==true){
            return true;
        }else {
            return false;
        }
    }
}

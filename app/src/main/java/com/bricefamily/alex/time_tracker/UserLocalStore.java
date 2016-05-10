package com.bricefamily.alex.time_tracker;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by alex on 16.01.2016.
 */
public class UserLocalStore {

    private int appVersion;
    private Context context;

    public static final String SP_NAME="userDetails";
    SharedPreferences userLocalDataBase;

    public UserLocalStore(Context context){
        this.context=context;
        userLocalDataBase=context.getSharedPreferences(SP_NAME,0);
    }
    public void storeUserData(User user){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putString("username",user.username);
        spEditor.putString("email",user.email);
        spEditor.putString("password",user.password);
        spEditor.putString("firstname",user.firstname);
        spEditor.putString("lastname",user.lastname);
        spEditor.apply();
    }

    public User getLoggedInUser(){
        String username=userLocalDataBase.getString("username", "");
        String email=userLocalDataBase.getString("email", "");
        String password=userLocalDataBase.getString("password","");
        String firstname=userLocalDataBase.getString("firstname", "");
        String lastname=userLocalDataBase.getString("lastname","");

        return new User(username,email,password,firstname,lastname,1,null,null,null);
    }
    //call with true if logged in
    public void setUserLoggedIn(boolean loggedIn){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.apply();

    }

    public void setUserUserfullname(String userfullname){
        SharedPreferences.Editor editor=userLocalDataBase.edit();
        editor.putString("userfullname", userfullname);

        editor.apply();

    }
    public String getUserfullname() {
        String firstname=userLocalDataBase.getString("firstname", "");
        String lastname=userLocalDataBase.getString("lastname","");

        String userfullname = firstname+" "+lastname;
        if (userfullname.isEmpty()) {
            return "";
        }
        //int registeredVersion = userLocalDataBase.getInt("appVersion", Integer.MIN_VALUE);

        return userfullname;
    }
    public void setUserUserfriendliststring(String friendsname){
        SharedPreferences.Editor editor=userLocalDataBase.edit();
        editor.putString("friendsname", friendsname);

        editor.apply();

    }
    public String getUserfriendliststring() {
        String friendsname = userLocalDataBase.getString("friendsname", "");
        if (friendsname.isEmpty()) {
            return "";
        }
        //int registeredVersion = userLocalDataBase.getInt("appVersion", Integer.MIN_VALUE);

        return friendsname;
    }
    public void setUserGCMregId(String regId,int appversion){
        appVersion=appversion;
        SharedPreferences.Editor editor=userLocalDataBase.edit();
        editor.putString("registration_id", regId);
        editor.putInt("appVersion", appVersion);
        editor.apply();

    }
    public String getUserRegistrationId() {
        String registrationId = userLocalDataBase.getString("registration_id", "");
        if (registrationId.isEmpty()) {
            return "";
        }
        //int registeredVersion = userLocalDataBase.getInt("appVersion", Integer.MIN_VALUE);

        return registrationId;
    }


    public void setUserPicturePath(String path){

        SharedPreferences.Editor editor=userLocalDataBase.edit();
        editor.putString("picturePath", path);
        editor.apply();

    }
    public String getUserPicturePath() {
        String picturePath = userLocalDataBase.getString("picturePath", "");
        if (picturePath.isEmpty()) {
            return "";
        }
        return picturePath;
    }


    public void clearUserData(){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.clear();
        spEditor.apply();
    }

    public boolean getUserLoggedIn(){
        if(userLocalDataBase.getBoolean("loggedIn", false)){
            return true;
        }else {
            return false;
        }
    }

    public Bitmap loadImageFromStorage(String path)
    {
        Bitmap bitmap=null;
        try {
            File f=new File(path, "user.jpg");
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }


    public String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("userProfilePicture", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"user.jpg");

        if(mypath.exists()){
            mypath.delete();
            mypath=new File(directory,"user.jpg");
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

}

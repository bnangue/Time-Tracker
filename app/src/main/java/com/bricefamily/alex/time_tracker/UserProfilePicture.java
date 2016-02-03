package com.bricefamily.alex.time_tracker;

import android.graphics.Bitmap;

/**
 * Created by bricenangue on 03/02/16.
 */
public class UserProfilePicture {
    String username ;
    Bitmap uProfilePicture;
    public UserProfilePicture(String username,Bitmap uProfilePicture){
        this.username=username;
        this.uProfilePicture=uProfilePicture;
    }
}

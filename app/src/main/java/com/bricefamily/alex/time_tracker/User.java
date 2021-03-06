package com.bricefamily.alex.time_tracker;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alex on 16.01.2016.
 */
public class User implements Parcelable {
    String username,email,
            password,firstname,lastname;
    Bitmap picture;
    int status;

    public User(String username,String email,String password){
        this.username=username;
        this.email=email;
        this.password=password;
    }
    public User(String username,String email,String password,int status){
        this.username=username;
        this.email=email;
        this.password=password;
        this.status=status;
    }

    public User(String username,Bitmap picture,int status){
        this.username=username;
        this.picture=picture;
        this.status=status;
    }
    public User(String username, String email, String password, String firstname, String lastname,int status) {
        this.username=username;
        this.email=email;
        this.password=password;
        this.firstname=firstname;
        this.lastname=lastname;
        this.status=status;

    }


    private User(Parcel in){

        username = in.readString();
        email = in.readString();
        password = in.readString();
        firstname=in.readString();
        lastname=in.readString();
        status=in.readInt();


    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {


        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(firstname);
        dest.writeString(lastname);
        dest.writeInt(status);

    }
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}

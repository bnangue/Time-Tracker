package com.bricefamily.alex.time_tracker;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by alex on 16.01.2016.
 */
public class User implements Parcelable {
    String username,email,
            password,firstname,lastname,regId,friendlist;
    Bitmap picture;
    int status;

    public User(String username,String email,String password){
        this.username=username;
        this.email=email;
        this.password=password;
    }
    public User(String username,String email,String password,int status,String regId){
        this.username=username;
        this.email=email;
        this.password=password;
        this.status=status;
        this.regId=regId;
    }

    public User(String username,Bitmap picture,int status){
        this.username=username;
        this.picture=picture;
        this.status=status;
    }
    public User(String regId,String username,Bitmap picture){
        this.username=username;
        this.picture=picture;
        this.regId=regId;
    }
    public User(String regId,String username,String email,String password){
        this.username=username;
        this.regId=regId;
        this.email=email;
        this.password=password;
    }
    public User(String username, String email, String password, String firstname, String lastname,int status,String regId,Bitmap picture,String friendlist) {
        this.username=username;
        this.email=email;
        this.password=password;
        this.firstname=firstname;
        this.lastname=lastname;
        this.status=status;
        this.regId=regId;
        this.picture=picture;
        this.friendlist=friendlist;

    }
    public User(String username,String email,String password,String friendlist,int status){
        this.username=username;
        this.friendlist=friendlist;
        this.email=email;
        this.password=password;
        this.status=status;
    }

    public User() {

    }

    public ArrayList<String> getuserfriendlist(String ufriendlist){
        ArrayList<String> list =new ArrayList<>();
        String[]friends = null;
        if(ufriendlist!=null){
          friends=ufriendlist.split(",");

        }
        assert friends != null;
        for(int i=0;i<friends.length;i++){
            list.add(friends[i]);
        }
        return list;
    }


    private User(Parcel in){

        username = in.readString();
        email = in.readString();
        password = in.readString();
        firstname=in.readString();
        lastname=in.readString();
        status=in.readInt();
        regId=in.readString();
        friendlist=in.readString();
        picture=in.readParcelable(getClass().getClassLoader());


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
        dest.writeString(regId);
        dest.writeString(friendlist);
        dest.writeParcelable(picture,flags);

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

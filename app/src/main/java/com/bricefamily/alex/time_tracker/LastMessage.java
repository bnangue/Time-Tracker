package com.bricefamily.alex.time_tracker;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bricenangue on 22/02/16.
 */
public class LastMessage implements Parcelable {
    String lmessage,lusername;
    String statusRead;//0 if unread 1 if read
    public LastMessage(String lusername,String lmessage,String statusRead){
        this.lmessage=lmessage;
        this.lusername=lusername;
        this.statusRead=statusRead;

    }

    protected LastMessage(Parcel in) {
        lmessage = in.readString();
        lusername = in.readString();
        statusRead = in.readString();
    }

    public static final Creator<LastMessage> CREATOR = new Creator<LastMessage>() {
        @Override
        public LastMessage createFromParcel(Parcel in) {
            return new LastMessage(in);
        }

        @Override
        public LastMessage[] newArray(int size) {
            return new LastMessage[size];
        }
    };

    public LastMessage() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lmessage);
        dest.writeString(lusername);
        dest.writeString(statusRead);
    }
}

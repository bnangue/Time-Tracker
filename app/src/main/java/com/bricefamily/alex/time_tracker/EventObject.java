package com.bricefamily.alex.time_tracker;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by praktikum on 28/01/16.
 */
public class EventObject implements Parcelable {
     String titel,infotext,time,creator;
    public EventObject(String titel,String infotext,String creator,String time){
        this.titel=titel;
        this.infotext=infotext;
        this.creator=creator;
        this.time=time;
    }

    private EventObject(Parcel in){

        creator = in.readString();
        infotext = in.readString();
        titel = in.readString();
        time = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(creator);
        dest.writeString(titel);
        dest.writeString(time);
        dest.writeString(infotext);
    }
    public static final Parcelable.Creator<EventObject> CREATOR = new Parcelable.Creator<EventObject>() {
        public EventObject createFromParcel(Parcel in) {
            return new EventObject(in);
        }

        public EventObject[] newArray(int size) {
            return new EventObject[size];
        }
    };
}

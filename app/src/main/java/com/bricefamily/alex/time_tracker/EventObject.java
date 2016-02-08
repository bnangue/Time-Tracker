package com.bricefamily.alex.time_tracker;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by praktikum on 28/01/16.
 */
public class EventObject implements Parcelable {
     String titel,infotext,creator,creationTime,eDay,eMonth,eYear,eventHash;
    String eventStatus;
    int eventHashcode;
    DateEventObject dateEventObject;
    public EventObject(String titel,String infotext,String creator){
        this.titel=titel;
        this.infotext=infotext;
        this.creator=creator;
    }

    public EventObject(String titel,String infotext,String creator,String creationTime,
                       String eDay,String eMonth,String eYear,String eventStatus){
        this.titel=titel;
        this.infotext=infotext;
        this.creator=creator;
        this.creationTime=creationTime;
        this.eDay=eDay;
        this.eMonth=eMonth;
        this.eYear=eYear;
        this.eventStatus=eventStatus;
    }


    private EventObject(Parcel in){

        titel = in.readString();
        infotext = in.readString();
        creator = in.readString();
        creationTime=in.readString();
        eDay=in.readString();
        eMonth=in.readString();
        eYear=in.readString();
        eventStatus=in.readString();
        eventHash=in.readString();

    }

    public EventObject(String titel, String infotext, String creator, String creationTime,
                       DateEventObject dateEventObject, String eventStatus,String eventHash) {
        this.titel=titel;
        this.infotext=infotext;
        this.creator=creator;
        this.creationTime=creationTime;
        this.dateEventObject=dateEventObject;
        this.eDay=dateEventObject.day;
        this.eMonth=dateEventObject.month;
        this.eYear=dateEventObject.year;
        this.eventStatus=eventStatus;
        this.eventHash= eventHash;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {


        dest.writeString(titel);
        dest.writeString(infotext);
        dest.writeString(creator);
        dest.writeString(creationTime);
        dest.writeString(eDay);
        dest.writeString(eMonth);
        dest.writeString(eYear);
        dest.writeString(eventStatus);
        dest.writeString(eventHash);
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

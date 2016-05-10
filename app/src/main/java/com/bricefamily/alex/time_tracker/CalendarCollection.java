package com.bricefamily.alex.time_tracker;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by bricenangue on 07/03/16.
 */
public class CalendarCollection implements Parcelable {
    String title, description,creator, datetime,startingtime,endingtime,hashid,category,alldayevent,everymonth,creationdatetime;
    int incomingnotifictionid, starthour,startminute,startday,startmonth,startyear,endhour,endminute,endday,endmonth,endyear;

    public String date="";
    public String event_message="";

    public static ArrayList<CalendarCollection> date_collection_arr=new ArrayList<>();
    public static ArrayList<CalendarCollection> date_collection_arr_myevent=new ArrayList<>();
    public static ArrayList<CalendarCollection> date_collection_arr_business=new ArrayList<>();
    public static ArrayList<CalendarCollection> date_collection_arr_birthdays=new ArrayList<>();
    public static ArrayList<CalendarCollection> date_collection_arr_shopping=new ArrayList<>();
    public static ArrayList<CalendarCollection> date_collection_arr_workplan=new ArrayList<>();


    public CalendarCollection(String date, String event_message){

        this.date=date;
        this.event_message=event_message;

    }


    public CalendarCollection(String title, String description, String creator, String datetime, String startingtime, String endingtime, String hashid, String category, String alldayevent, String everymonth, String creationdatetime){
        this.title = title;
        this.description = description;
        this.creator=creator;
        this.datetime = datetime;
        this.startingtime=startingtime;
        this.endingtime=endingtime;
        this.hashid = hashid;
        this.category=category;
        this.alldayevent=alldayevent;
        this.everymonth=everymonth;
        this.creationdatetime=creationdatetime;

    }


    public CalendarCollection(String title, String description, String creator, String category, String alldayevent, String everymonth, int starthour, int startminute, int startday, int startmonth, int startyear,
                              int endhour, int endminute, int endday, int endmonth, int endyear){
        this.title = title;
        this.description = description;
        this.creator=creator;
        this.category=category;
        this.starthour = starthour;
        this.startminute=startminute;
        this.startday=startday;
        this.startmonth = startmonth;
        this.startyear=startyear;
        this.endhour=endhour;
        this.endminute=endminute;
        this.endday = endday;
        this.endmonth=endmonth;
        this.endyear=endyear;
        this.alldayevent=alldayevent;
        this.everymonth=everymonth;

    }

    private CalendarCollection(Parcel in){

        title = in.readString();
        description = in.readString();
        creator = in.readString();
        datetime =in.readString();
        startingtime=in.readString();
        endingtime=in.readString();
       category=in.readString();
        hashid =in.readString();
        alldayevent=in.readString();
        incomingnotifictionid=in.readInt();
        starthour=in.readInt();
        startminute=in.readInt();
        startday=in.readInt();
        startmonth=in.readInt();

        startyear=in.readInt();
        endhour=in.readInt();
        endminute=in.readInt();

        endday=in.readInt();

        endmonth=in.readInt();
        endyear=in.readInt();
        everymonth=in.readString();
        creationdatetime=in.readString();


    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {


        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(creator);
        dest.writeString(datetime);
        dest.writeString(startingtime);
        dest.writeString(endingtime);
        dest.writeString(category);
        dest.writeString(hashid);
        dest.writeString(alldayevent);
        dest.writeInt(incomingnotifictionid);
        dest.writeInt(starthour);
        dest.writeInt(startminute);
        dest.writeInt(startday);
        dest.writeInt(startmonth);
        dest.writeInt(startyear);
        dest.writeInt(endhour);
        dest.writeInt(endminute);
        dest.writeInt(endday);
        dest.writeInt(endmonth);
        dest.writeInt(endyear);
        dest.writeString(everymonth);
        dest.writeString(creationdatetime);
    }
    public static final Creator<CalendarCollection> CREATOR = new Creator<CalendarCollection>() {
        public CalendarCollection createFromParcel(Parcel in) {
            return new CalendarCollection(in);
        }

        public CalendarCollection[] newArray(int size) {
            return new CalendarCollection[size];
        }
    };

}

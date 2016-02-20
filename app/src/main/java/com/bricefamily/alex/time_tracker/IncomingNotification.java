package com.bricefamily.alex.time_tracker;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bricenangue on 19/02/16.
 */
public class IncomingNotification implements Parcelable{
   int id;
    int type,
            readStatus;//1 if read 0 if not
    String body;
    String creationDate;
    public IncomingNotification(){}

    public IncomingNotification(int type, int readStatus,String body,String creationDate){
        super();
        this.type=type;
        this.body=body;
        this.creationDate=creationDate;
        this.readStatus=readStatus;

    }

    protected IncomingNotification(Parcel in) {
        id = in.readInt();
        type = in.readInt();
        readStatus = in.readInt();
        body = in.readString();
        creationDate = in.readString();
    }

    public static final Creator<IncomingNotification> CREATOR = new Creator<IncomingNotification>() {
        @Override
        public IncomingNotification createFromParcel(Parcel in) {
            return new IncomingNotification(in);
        }

        @Override
        public IncomingNotification[] newArray(int size) {
            return new IncomingNotification[size];
        }
    };

    @Override
    public String toString() {
        return "IncomingNotification [id=" + id + ", type=" + type  + ", readStatus=" + readStatus  +", body=" + body + ", creationDate=" + creationDate
                + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(type);
        dest.writeInt(readStatus);
        dest.writeString(body);
        dest.writeString(creationDate);
    }
}

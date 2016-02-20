package com.bricefamily.alex.time_tracker;

/**
 * Created by bricenangue on 19/02/16.
 */
public class IncomingNotification {
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
    @Override
    public String toString() {
        return "IncomingNotification [id=" + id + ", type=" + type  + ", readStatus=" + readStatus  +", body=" + body + ", creationDate=" + creationDate
                + "]";
    }

}

package com.bricefamily.alex.time_tracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bricenangue on 01/02/16.
 */
interface GetEventsCallbacks {
    public abstract void done( ArrayList<EventObject> returnedeventobject);
    public abstract void donec(ArrayList<CalendarCollection> returnedeventobject);
    void updated(String reponse);

}

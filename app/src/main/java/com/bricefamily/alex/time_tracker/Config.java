package com.bricefamily.alex.time_tracker;

/**
 * Created by bricenangue on 06/02/16.
 */
public interface Config {

    public static String PREF_GCM_REG_ID = "PREF_GCM_REG_ID";
    public static String GCM_SENDER_ID = "1007573335631";
    public static String WEB_SERVER_URL = "http://time-tracker.comlu.com/reg.php";
    public static final String SERVER_SUCCESS="server_success";
    public static String API_KEY = "AIzaSyAuskTOPN5tUYP0Z5sWJj_mMsax3oplHrQ";

    static final boolean SECOND_SIMULATOR = false;

    // Server Url absolute url where php files are placed.
    static final String YOUR_SERVER_URL   =  "http://time-tracker.comlu.com/";

    // Google project id
    static final String GOOGLE_SENDER_ID = "1007573335631";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCM Android Example";

    // Broadcast reciever name to show gcm registration messages on screen
    static final String DISPLAY_REGISTRATION_MESSAGE_ACTION =
            "com.bricefamily.alex.time_tracker.DISPLAY_REGISTRATION_MESSAGE";

    // Broadcast reciever name to show user messages on screen
    static final String DISPLAY_MESSAGE_ACTION =
            "com.bricefamily.alex.time_tracker.DISPLAY_MESSAGE";

    // Parse server message with this name
    static final String EXTRA_MESSAGE = "message";

}

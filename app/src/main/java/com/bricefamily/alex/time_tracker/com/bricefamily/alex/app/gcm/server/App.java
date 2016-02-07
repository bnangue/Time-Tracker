package com.bricefamily.alex.time_tracker.com.bricefamily.alex.app.gcm.server;

/**
 * Created by bricenangue on 06/02/16.
 */
public class App {
   static String registerId,username ,email;

    public static void main( String[] args )
    {
        registerId=args[0];
        username=args[1];
        email=args[2];

        System.out.println( "Sending POST to GCM" );

        String apiKey = "AIzaSyAuskTOPN5tUYP0Z5sWJj_mMsax3oplHrQ";
        Content content = createContent();

        new POST2GCM().execute(registerId,username,email);
    }

    public static Content createContent(){

        Content c = new Content();

        c.addRegId(registerId);
        c.createData("Test Title", "Test Message");

        return c;
    }
}

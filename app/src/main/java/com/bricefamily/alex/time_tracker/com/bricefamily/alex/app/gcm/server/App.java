package com.bricefamily.alex.time_tracker.com.bricefamily.alex.app.gcm.server;

/**
 * Created by bricenangue on 06/02/16.
 */
public class App {
   static String registerId,username ,email,titel,message;

    public static void main( String[] args )
    {
        registerId=args[0];
        username=args[1];
        email=args[2];
        titel=args[3];
        message=args[4];

        System.out.println( "Sending POST to GCM" );

        String apiKey = "AIzaSyAuskTOPN5tUYP0Z5sWJj_mMsax3oplHrQ";
        Content content = createContent();

        new POST2GCM.PostGCMBackgroundTasck().execute(apiKey,content);
    }

    public static Content createContent(){

        Content c = new Content();

        c.addRegId(registerId);
        c.createData(titel, message);

        return c;
    }
}

package com.bricefamily.alex.time_tracker;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by bricenangue on 10/02/16.
 */
public class LiveChatBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String action=intent.getAction();

        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                LiveChatIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        if(action.equals("com.google.android.c2dm.intent.RECEIVE")){
            String message=intent.getStringExtra("title");
            if(message!=null && message.contains("New Event")){

            }else{
                startWakefulService(context, (intent.setComponent(comp)));
            }
        }else {

        }


    }

}

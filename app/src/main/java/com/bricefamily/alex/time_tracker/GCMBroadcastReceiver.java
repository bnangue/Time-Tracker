package com.bricefamily.alex.time_tracker;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by bricenangue on 06/02/16.
 */
public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                GCMessageHandler.class.getName());

        String action=intent.getAction();
        if (action.equals("com.google.android.c2dm.intent.REGISTRATION"))
        {


        }

        if(action.equals("com.google.android.c2dm.intent.RECEIVE")){
            String message=intent.getStringExtra("title");
            if(message!=null && message.contains("New Event")){
                startWakefulService(context, (intent.setComponent(comp)));
                setResultCode(Activity.RESULT_OK);
            }
        }

    }
}

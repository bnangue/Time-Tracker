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

        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                LiveChatIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);

        Bundle extras = intent.getExtras();
        Intent i = new Intent("CHAT_MESSAGE_RECEIVED");
        i.putExtra("message", extras.getString("message"));
        i.putExtra("sender", extras.getString("sender"));
        i.putExtra("registration_ids", extras.getString("registrationSenderIDs"));

        context.sendBroadcast(i);

    }
}

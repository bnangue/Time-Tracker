package com.bricefamily.alex.time_tracker;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Random;

/**
 * Created by bricenangue on 06/02/16.
 */
public class GCMessageHandler extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    NotificationManager nmgr;
    Random random = new Random();
    int m = random.nextInt(9999 - 1000) + 1000;
    NotificationCompat.Builder mBuilder;
    String mes,title;
    private Handler handler;
    public GCMessageHandler() {
        super("GcmMessageHandler");
    }
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
        nmgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.colornfo)
                        .setContentTitle(title)
                        .setContentText(mes);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        mBuilder.setAutoCancel(true);

        Intent intent = new Intent(this,LoginActivity.class);
        // The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(LoginActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);


    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        title = extras.getString("title");
        mes=extras.getString("message");

        mBuilder.setContentText(mes);
        mBuilder.setContentTitle(title);
        if(mes.contains("new Event added by")){
            showToast();
            Log.i("GCM", "Received : (" + messageType + ")  " + extras.getString("title"));

            GCMBroadcastReceiver.completeWakefulIntent(intent);

        }

    }

    public void showToast(){
        handler.post(new Runnable() {
            public void run() {
                nmgr.notify(m, mBuilder.build());

            }
        });

    }
}

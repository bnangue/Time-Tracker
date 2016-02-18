package com.bricefamily.alex.time_tracker;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by bricenangue on 10/02/16.
 */
public class LiveChatIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    Notification notification;
    DBOperation dbOperation;
    FriendRequest friendRequest;

    public static final String TAG = "GcmIntentService";
    IBinder mBinder=new Binder() ;

    public LiveChatIntentService() {
        super("GcmIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        dbOperation = new DBOperation(this);
        friendRequest=new FriendRequest(this,null);
        dbOperation.createAndInitializeTables();
         mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.chaticon)
                .setContentTitle("New Message")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(""))
                .setContentText("");
        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        mBuilder.setAutoCancel(true);

    }


    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {
                String s=extras.toString();


                Intent i = new Intent("com.bricefamily.alex.time_tracker.CHAT_MESSAGE_RECEIVED");
                i.putExtra("message", extras.getString("message"));
                i.putExtra("sender", extras.getString("sender"));
                i.putExtra("registrationSenderIDs", extras.getString("registrationSenderIDs"));
                if(extras.getString("message").contains("new Event added by")){

                }else if(extras.getString("message").contains("Do you want to be friend with ")){

                    sendfriendrequestnotification(intent);
                } else if(extras.getString("message").contains(" is now your friend")){

                    sendnewfriendnotification(intent);
                }else if(extras.getString("message").contains(" removed you as friend")){

                    sendremovedfriendnotification(intent);
                }else{
                    sendOrderedBroadcast(i, null);

                    if(LiveChatActivity.messageshowed){
                        sendnotification(intent);
                        LiveChatBroadcastReceiver.completeWakefulIntent(intent);

                    }
                }


                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.


    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendnotification(Intent bintent) {

        Bundle extras=bintent.getExtras();

        //String chattingFrom = extras.getString("chattingFrom");
        String chattingToName = extras.getString("sender");// will be user as receiver name in current Device getting the notifiction
        String chattingToDeviceID = extras.getString("registrationSenderIDs");
        String msg = extras.getString("message");

        String message=chattingToName+": " +msg;
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this,LiveChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //intent.putExtra("chattingFrom", chattingFrom);
        intent.putExtra("recieverName",chattingToName);
        intent.putExtra("recieverregId",chattingToDeviceID);
        intent.putExtra("messagefromgcm", msg);

        ChatPeople cppl=addToChatOnly(chattingToName, msg, "1", chattingToDeviceID);
       addToDBOnly(cppl);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(LiveChatActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT

                );
        //PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        //		new Intent(this, ChatActivity.class), 0);

        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message);
        mBuilder.setContentIntent(resultPendingIntent);
        notification=mBuilder.build();
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void sendfriendrequestnotification(Intent bintent) {

        Bundle extras=bintent.getExtras();

        //String chattingFrom = extras.getString("chattingFrom");
        String chattingToName = extras.getString("sender");// will be user as receiver name in current Device getting the notifiction
        String chattingToDeviceID = extras.getString("registrationSenderIDs");
        String msg = extras.getString("message");
        String receiver = extras.getString("receiver");// will be user as sender name in current Device getting the notifiction

        String message=msg;

        NotificationCompat.Builder Builder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.addusercolor)
                .setContentTitle("New Message")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(""))
                .setContentText("");
        Builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        Builder.setAutoCancel(true);
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this,LoginPanelActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //intent.putExtra("chattingFrom", chattingFrom);
        intent.putExtra("recieverName", chattingToName);
        intent.putExtra("receiver", receiver);
        intent.putExtra("recieverregId", chattingToDeviceID);
        intent.putExtra("messagefromgcm", msg);
        intent.putExtra("request", true);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        Builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message);
        Builder.setContentIntent(resultPendingIntent);
        notification=Builder.build();
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }


    private void sendnewfriendnotification(Intent bintent) {

        Bundle extras=bintent.getExtras();

        //String chattingFrom = extras.getString("chattingFrom");
        String chattingToName = extras.getString("sender");// will be user as receiver name in current Device getting the notifiction
        String chattingToDeviceID = extras.getString("registrationSenderIDs");
        String msg = extras.getString("message");
        String receiver = extras.getString("receiver");// will be user as sender name in current Device getting the notifiction

        String message=msg;

        NotificationCompat.Builder Builder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.addeduserblue)
                .setContentTitle("New Message")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(""))
                .setContentText("");
        Builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        Builder.setAutoCancel(true);
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this,LoginPanelActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //intent.putExtra("chattingFrom", chattingFrom);
        intent.putExtra("recieverName",chattingToName);
        intent.putExtra("receiver", receiver);
        intent.putExtra("recieverregId", chattingToDeviceID);
        intent.putExtra("messagefromgcm", msg);
        intent.putExtra("request", false);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        Builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message);
        Builder.setContentIntent(resultPendingIntent);
        notification=Builder.build();
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void sendremovedfriendnotification(Intent bintent) {

        Bundle extras=bintent.getExtras();

        //String chattingFrom = extras.getString("chattingFrom");
        String chattingToName = extras.getString("sender");// will be user as receiver name in current Device getting the notifiction
        String chattingToDeviceID = extras.getString("registrationSenderIDs");
        String msg = extras.getString("message");
        String receiver = extras.getString("receiver");// will be user as sender name in current Device getting the notifiction

        String message=msg;

        NotificationCompat.Builder Builder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.userremovedloli)
                .setContentTitle("New Message")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(""))
                .setContentText("");
        Builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        Builder.setAutoCancel(true);
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this,RemovedAsFriendActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //intent.putExtra("chattingFrom", chattingFrom);
        intent.putExtra("recieverName",chattingToName);
        intent.putExtra("receiver", receiver);
        intent.putExtra("recieverregId", chattingToDeviceID);
        intent.putExtra("messagefromgcm", msg);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        Builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message);
        Builder.setContentIntent(resultPendingIntent);
        notification=Builder.build();
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }



    private void sendNotification(String msg) {

        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, LiveChatActivity.class), 0);

       mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.chaticon)
                .setContentTitle("GCM Notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        notification=mBuilder.build();
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }
    ChatPeople addToChatOnly(String personName, String chatMessage, String toOrFrom,String receiverregId) {

        ChatPeople curChatObj = new ChatPeople();
        curChatObj.setPERSON_NAME(personName);
        curChatObj.setPERSON_CHAT_MESSAGE(chatMessage);
        curChatObj.setPERSON_CHAT_TO_FROM(toOrFrom);// 1 or 0 convert to boolean in adapter
        curChatObj.setPERSON_DEVICE_ID(receiverregId);
        curChatObj.setPERSON_EMAIL("demo@gmail.com");

        return curChatObj;

    }
    void addToDBOnly(ChatPeople curChatObj) {

        ChatPeople people = new ChatPeople();
        ContentValues values = new ContentValues();
        values.put(people.getPERSON_NAME(), curChatObj.getPERSON_NAME());
        values.put(people.getPERSON_CHAT_MESSAGE(),
                curChatObj.getPERSON_CHAT_MESSAGE());
        values.put(people.getPERSON_DEVICE_ID(),
                curChatObj.getPERSON_DEVICE_ID());
        values.put(people.getPERSON_CHAT_TO_FROM(),
                curChatObj.getPERSON_CHAT_TO_FROM());
        values.put(people.getPERSON_EMAIL(), "demo_email@email.com");
        dbOperation.open();
        long id = dbOperation.insertTableData(people.getTableName(), values);
        dbOperation.close();
        if (id != -1) {

        }

    }


}
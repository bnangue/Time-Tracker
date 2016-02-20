package com.bricefamily.alex.time_tracker;

import android.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RemovedAsFriendActivity extends ActionBarActivity {

    private String senderRegId, receiverername,message,sendername,email,password;
    private MySQLiteHelper mySQLiteHelper;
    private IncomingNotification incomingNotification;
    private  int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_removed_as_friend);
        Bundle extras=getIntent().getExtras();
        mySQLiteHelper=new MySQLiteHelper(this);
        if(extras!=null){
            receiverername =extras.getString("recieverName");
            sendername =extras.getString("receiver");
            senderRegId=extras.getString("recieverregId");
            message=extras.getString("messagefromgcm");
            incomingNotification = extras.getParcelable("notification");
            id = extras.getInt("notificationId");

        }

        if(incomingNotification!=null){
            incomingNotification.id=id;
            if(incomingNotification.readStatus==0){
                incomingNotification.readStatus=1;
                mySQLiteHelper.updateIncomingNotification(incomingNotification);
            }
            mySQLiteHelper.updateIncomingNotification(incomingNotification);

        }
        Button b=(Button)findViewById(R.id.removedasfriendbtn);
        TextView tv=(TextView)findViewById(R.id.removedasfriendtext);
        tv.setText(message);
    }
    public void OnbuttonRemoveAsFriendPressed(View v){
        finish();
    }
}

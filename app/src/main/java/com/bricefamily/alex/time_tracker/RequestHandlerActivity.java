package com.bricefamily.alex.time_tracker;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by bricenangue on 17/02/16.
 */
public class RequestHandlerActivity extends ActionBarActivity implements View.OnClickListener {
    private String senderRegId, receiverername,message,sendername,myemail,mypassword;
    Button btnCancleRequest, btnAcceptRequest;
    TextView tvMessage;
    UserLocalStore userLocalStore;
    User user;
    private boolean request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_handler);
        userLocalStore=new UserLocalStore(this);
        Bundle extras=getIntent().getExtras();
        btnAcceptRequest =(Button)findViewById(R.id.btnacceptRequest);
        btnCancleRequest =(Button)findViewById(R.id.btncancleRequest);
        tvMessage=(TextView)findViewById(R.id.textViewFriendRequestgcmMessage);

        btnAcceptRequest.setOnClickListener(this);
        btnCancleRequest.setOnClickListener(this);


        if(extras!=null){
            receiverername =extras.getString("recieverName");
            sendername =extras.getString("receiver");
            senderRegId=extras.getString("recieverregId");
            message=extras.getString("messagefromgcm");
            myemail=extras.getString("myemail");
            mypassword=extras.getString("mypassword");
            request = extras.getBoolean("request");
        }
        user=new User(sendername,myemail,mypassword,1,senderRegId);
        if(message!=null){
            tvMessage.setText(message);
        }

    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.btnacceptRequest:
                FriendRequest friendRequest=new FriendRequest(getApplicationContext(),user);
                friendRequest.sendFriendresquest(false, senderRegId,sendername);
                friendRequest.adduserinfriendList(receiverername, user.email, user.password,sendername);

                finish();
                break;
            case R.id.btncancleRequest:
                Toast.makeText(getApplicationContext(),"accepted request from "+ receiverername,Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

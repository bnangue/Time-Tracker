package com.bricefamily.alex.time_tracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class LoginPanelActivity extends AppCompatActivity {
    private String senderRegId, receiverername,message,sendername,email,password;

    private EditText edmail,edpass;
    private boolean request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_panel);
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            receiverername =extras.getString("recieverName");
            sendername =extras.getString("receiver");
            senderRegId=extras.getString("recieverregId");
            message=extras.getString("messagefromgcm");
            request=extras.getBoolean("request");
        }
        edmail=(EditText)findViewById(R.id.editTextmail);
        edpass=(EditText)findViewById(R.id.editTextpass);
    }

    public void OnOpenResquestHandler(View view){
        email=edmail.getText().toString();
        password=edpass.getText().toString();
        final User user=new User(sendername,email,password);
        ServerRequestUser serverRequestUser=new ServerRequestUser(this);
        serverRequestUser.fetchUserDataInBackground(user, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {
                if(request){
                    Intent intent =new Intent(LoginPanelActivity.this,RequestHandlerActivity.class);
                    intent.putExtra("user",returneduser);
                    intent.putExtra("recieverName",receiverername);
                    intent.putExtra("receiver",sendername);
                    intent.putExtra("recieverregId",senderRegId);
                    intent.putExtra("messagefromgcm",message);
                    startActivity(intent);
                    finish();
                }else {
                    FriendRequest friendRequest=new FriendRequest(getApplicationContext(),user);
                    friendRequest.adduserinfriendList(user.username,user.email,user.password,user.username);
                    finish();
                }

            }

            @Override
            public void deleted(String reponse) {

            }

            @Override
            public void userlist(ArrayList<User> reponse) {

            }
        });
    }
}

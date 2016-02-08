package com.bricefamily.alex.time_tracker;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class LiveChatActivity extends AppCompatActivity {

    private ChatArrayApadter chatArrayApadter;
    private ListView chatlist;
    private Button sendBtn;
    private EditText chaTtext;
    private boolean side =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat);

        Intent i=getIntent();
        sendBtn=(Button)findViewById(R.id.buttonchatsend);
        chaTtext=(EditText)findViewById(R.id.chatedittext);

        chatlist=(ListView)findViewById(R.id.chatlistivew);
        chatArrayApadter=new ChatArrayApadter(getApplicationContext(),R.layout.chat);
        chaTtext.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(event.getAction()==KeyEvent.ACTION_DOWN&& keyCode==KeyEvent.KEYCODE_ENTER){
                    return sendChatmessage();
                }
                return false;
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendChatmessage();
            }
        });

        chatlist.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatArrayApadter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                chatlist.setSelection(chatArrayApadter.getCount()-1);
            }
        });
        chatlist.setAdapter(chatArrayApadter);
    }
    private boolean sendChatmessage(){

        chatArrayApadter.add(new ChatMessage(side, chaTtext.getText().toString()));
        chaTtext.setText("");
        side=!side;
        chatArrayApadter.notifyDataSetChanged();
        return true;
    }

}

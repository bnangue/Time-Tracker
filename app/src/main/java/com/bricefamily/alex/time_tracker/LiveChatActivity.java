package com.bricefamily.alex.time_tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class LiveChatActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    private ChatArrayApadter chatArrayApadter;
    private ListView chatlist;
    private Button sendBtn;
    private EditText chaTtext;
    private boolean side =false;
    String receiverName;
    String receiverregId;
    UserLocalStore userLocalStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat);

        userLocalStore=new UserLocalStore(this);
        sendBtn=(Button)findViewById(R.id.buttonchatsend);
        chaTtext=(EditText)findViewById(R.id.chatedittext);

        chatlist=(ListView)findViewById(R.id.chatlistivew);

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            receiverName=extras.getString("recieverName");
            receiverregId=extras.getString("recieverregId");

        }
        registerReceiver(broadcastReceiver, new IntentFilter(
                "CHAT_MESSAGE_RECEIVED"));

        chatArrayApadter=new ChatArrayApadter(getApplicationContext(),R.layout.chat);

        chaTtext.setOnEditorActionListener(this);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessagetoServer();
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
        String messagetoSend=chaTtext.getText().toString();
        String sender=userLocalStore.getLoggedInUser().username;
        ChatMessage chatMessage=new ChatMessage(side,messagetoSend,sender);
        populateChatMessages(chatMessage);
       // sendMessagetoServer();

        return true;
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle b = intent.getExtras();

            String message = b.getString("message");

            String sender=b.getString("sender");
            String registration_ids=b.getString("registrationSenderIDs");
            receiverregId=registration_ids;

            receiverName=sender	;

            ChatMessage chatMessage=new ChatMessage(true,message,sender);

            // this demo this is the same device
            //ChatPeople curChatObj = addToChat(sender, message,
             //       "Received");//replace recieve by boolean false
            //addToDB(curChatObj); // adding to db

            populateChatMessages(chatMessage);

        }
    };


    private void populateChatMessages(ChatMessage message) {
        chatArrayApadter.add(message);
        chatArrayApadter.notifyDataSetChanged();
        clearMessageTextBox();
    }

    private static String getData(ArrayList<Pair<String, String>> values) throws UnsupportedEncodingException {
        StringBuilder result=new StringBuilder();
        for(Pair<String,String> pair : values){

            if(result.length()!=0)

                result.append("&");
            result.append(URLEncoder.encode(pair.first, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.second, "UTF-8"));

        }
        return result.toString();
    }

    public void sendMessagetoServer() {

        final String messageToSend = chaTtext.getText().toString().trim();

        if (messageToSend.length() > 0) {


            Thread thread = new Thread() {
                @Override
                public void run() {
                    HttpURLConnection conn=null;
                    try {
                        String regid = userLocalStore.getUserRegistrationId();
                        ArrayList<Pair<String,String>> data=new ArrayList<>();
                        data.add(new Pair<String, String>("message", messageToSend));
                        data.add(new Pair<String, String>("sender", receiverName));
                        data.add(new Pair<String, String>("registrationReceiverIDs", receiverregId));
                        data.add(new Pair<String, String>("registrationSenderIDs", regid));
                        data.add(new Pair<String, String>("apiKey", Config.API_KEY));

                        byte[] bytes = getData(data).getBytes("UTF-8");


                        URL url=new URL(Config.YOUR_SERVER_URL+ "ConnectionGCMServer.php");
                        conn=(HttpURLConnection)url.openConnection();
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setFixedLengthStreamingMode(bytes.length);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type",
                                "application/x-www-form-urlencoded;charset=UTF-8");
                        // post the request
                        OutputStream out = conn.getOutputStream();
                        out.write(bytes);
                        out.close();

                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuffer reponse = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            reponse.append(inputLine);
                        }
                        final String response =reponse.toString();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        if(conn!=null){
                            conn.disconnect();
                        }
                    }
                }
            };

            thread.start();

        }

    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }


    void clearMessageTextBox() {

        chaTtext.clearFocus();
        chaTtext.setText("");

        hideKeyBoard(chaTtext);

    }

    private void hideKeyBoard(EditText edt) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
            sendMessagetoServer();
            sendChatmessage();
        }
        return false;
    }
}

package com.bricefamily.alex.time_tracker;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

public class LiveChatActivity extends ActionBarActivity implements TextView.OnEditorActionListener {

    private ChatArrayApadter chatArrayApadter;
    private ListView chatlist;
    private Button sendBtn;
    private EditText chaTtext;
    private boolean side =false;
    String receiverName,intentrecievemesg;
    String receiverregId;
    UserLocalStore userLocalStore;
    ArrayList<ChatPeople> ChatPeoples;

    DBOperation dbOperation;
    protected ServiceConnection mServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat);


        userLocalStore=new UserLocalStore(this);
        ChatPeoples = new ArrayList<ChatPeople>();
        sendBtn=(Button)findViewById(R.id.buttonchatsend);
        chaTtext=(EditText)findViewById(R.id.chatedittext);

        chatlist=(ListView)findViewById(R.id.chatlistivew);

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            receiverName=extras.getString("recieverName");
            receiverregId=extras.getString("recieverregId");
            intentrecievemesg=extras.getString("messagefromgcm");

        }
        prepareView();
        registerReceiver(broadcastReceiver, new IntentFilter(
                "CHAT_MESSAGE_RECEIVED"));

        dbOperation = new DBOperation(this);
        dbOperation.createAndInitializeTables();
       // adding to db

        if(intentrecievemesg!=null){
            ChatPeople curChatObj = addToChat(receiverName, intentrecievemesg,
                    "1");
            addToDB(curChatObj);
            populateChatMessages();
        }
        populateChatMessages();

        chatlist.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatlist.setStackFromBottom(true);
        chaTtext.setOnEditorActionListener(this);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessagetoServer();
                sendChatmessage();
            }
        });


    }

    public void prepareView() {

        getWindow().getDecorView().setBackgroundColor(Color.WHITE); //Hintergrund der View

        android.support.v7.app.ActionBar ab = getSupportActionBar();

        //Disablen des Zurück Pfeils
        if (findViewById(android.R.id.home) != null) {
            findViewById(android.R.id.home).setVisibility(View.GONE);
        }

        LayoutInflater inflator = (LayoutInflater) getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        View view = inflator.inflate(R.layout.actionbarbackground, null);


        //center des ActionBar Titles
        android.support.v7.app.ActionBar.LayoutParams params = new android.support.v7.app.ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER);

        try {
            ab.setDisplayShowCustomEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
            ab.setCustomView(view, params);
            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.cellSelected)));
            if(receiverName!=null){
                ab.setTitle(receiverName);
            }

        } catch (NullPointerException e) {
            Log.w("ActionBar Error", e.getMessage());
        }
        try {
            //ab Android 5.0
            ab.setElevation(0);
        } catch (NullPointerException e) {
            Log.w("ActionBar Error", e.getMessage());
        }

    }

    private void populateChatMessages() {

        getDataFromDB();
        if (ChatPeoples.size() > 0) {
            chatArrayApadter = new ChatArrayApadter(this, R.layout.chat,ChatPeoples);

            chatArrayApadter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    chatlist.setSelection(chatArrayApadter.getCount() - 1);
                }
            });
            chatlist.setAdapter(chatArrayApadter);

        }

    }

    void getDataFromDB() {

        ChatPeoples.clear();

        Cursor cursor = dbOperation.getDataFromTable(receiverregId);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                // Log.i(TAG,
                // "Name = " + cursor.getString(0) + ", Message = "
                // + cursor.getString(1) + " Device ID = "
                // + cursor.getString(2));

                ChatPeople people = addToChat(cursor.getString(0),
                        cursor.getString(1), cursor.getString(3));
                ChatPeoples.add(people);
            } while (cursor.moveToNext());
        }
        cursor.close();

    }
    ChatPeople addToChat(String personName, String chatMessage, String toOrFrom) {

        ChatPeople curChatObj = new ChatPeople();
        curChatObj.setPERSON_NAME(personName);
        curChatObj.setPERSON_CHAT_MESSAGE(chatMessage);
        curChatObj.setPERSON_CHAT_TO_FROM(toOrFrom);// 1 or 0 convert to boolean in adapter
        curChatObj.setPERSON_DEVICE_ID(receiverregId);
        curChatObj.setPERSON_EMAIL("demo@gmail.com");

        return curChatObj;

    }
    void addToDB(ChatPeople curChatObj) {

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

        populateChatMessages();
    }

    private boolean sendChatmessage(){
        String messagetoSend=chaTtext.getText().toString();
        String sender=userLocalStore.getLoggedInUser().username;

        ChatPeople curChatObj = addToChat(receiverName, messagetoSend, "0");
        addToDB(curChatObj);
        clearMessageTextBox();
       // sendMessagetoServer();

        return true;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle b = intent.getExtras();

        String message = b.getString("message");

        String sender=b.getString("sender");
        String registration_ids=b.getString("registrationSenderIDs");
        receiverregId=registration_ids;

        receiverName=sender	;
        ChatPeople curChatObj = addToChat(sender, message,
                "1");
        addToDB(curChatObj); // adding to db

        populateChatMessages();

        setIntent(intent);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getIntent();
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
            ChatPeople curChatObj = addToChat(sender, message,
                    "1");
            addToDB(curChatObj); // adding to db

            populateChatMessages();

        }
    };


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


    @Override
    protected void onStart() {
        super.onStart();

        PackageManager pm = getPackageManager();
        ComponentName compName =
                new ComponentName(getApplicationContext(),
                        GCMBroadcastReceiver.class);
        pm.setComponentEnabledSetting(
                compName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);


    }

    public void onDestroy() {
        super.onDestroy();


            try {
                if (broadcastReceiver != null) {
                    this.unregisterReceiver(broadcastReceiver);
                }
            } catch (IllegalArgumentException e) {
                broadcastReceiver = null;
            }
        PackageManager pm = getPackageManager();
        ComponentName compName =
                new ComponentName(getApplicationContext(),
                        GCMBroadcastReceiver.class);
        pm.setComponentEnabledSetting(
                compName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


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

package com.bricefamily.alex.time_tracker;

import android.app.ActionBar;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Filter;

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
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    Notification notification;
    public static final int NOTIFICATION_ID = 1;
    Intent intentr;
    private MySQLiteHelper mySQLiteHelper;
    static boolean messageshowed=true;

    Bitmap bitmap;

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
        mySQLiteHelper=new MySQLiteHelper(this);
        Intent i  = getIntent();
        Bundle extras=i.getExtras();
        if(extras!=null){
            receiverName=extras.getString("recieverName");
            receiverregId=extras.getString("recieverregId");
            intentrecievemesg=extras.getString("messagefromgcm");
            bitmap=(Bitmap)extras.getParcelable("friendPicture");

        }

        setContentView(R.layout.activity_live_chat);
        mySQLiteHelper.updateIncomingMessage(1, 3);

        messageshowed=false;

        userLocalStore=new UserLocalStore(this);
        ChatPeoples = new ArrayList<ChatPeople>();
        sendBtn=(Button)findViewById(R.id.buttonchatsend);
        chaTtext=(EditText)findViewById(R.id.chatedittext);

        chatlist=(ListView)findViewById(R.id.chatlistivew);

       intentr=new Intent(this,LiveChatIntentService.class);
        IntentFilter mfilter =new IntentFilter("com.bricefamily.alex.time_tracker.CHAT_MESSAGE_RECEIVED");
        mfilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(broadcastReceiver,mfilter);
        prepareView(receiverName);

        dbOperation = new DBOperation(this);
        dbOperation.createAndInitializeTables();
       // adding to db




        populateChatMessages();


        chaTtext.setOnEditorActionListener(this);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessagetoServer();
                setChatmessage();
            }
        });


    }

    public void prepareView(String name) {

        getWindow().getDecorView().setBackgroundColor(Color.WHITE); //Hintergrund der View

        android.support.v7.app.ActionBar ab = getSupportActionBar();

        //Disablen des Zurück Pfeils
        if (findViewById(android.R.id.home) != null) {
            findViewById(android.R.id.home).setVisibility(View.GONE);
        }

        LayoutInflater inflator = (LayoutInflater) getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        View view = inflator.inflate(R.layout.livechat_actionbar_background, null);
        TextView titelname=(TextView)view.findViewById(R.id.recievername);
        ImageView img =(ImageView)view.findViewById(R.id.avatarfriend);
        if(bitmap!=null){
            img.setImageBitmap(bitmap);
        }
        titelname.setText(name);


        //center des ActionBar Titles
        android.support.v7.app.ActionBar.LayoutParams params = new android.support.v7.app.ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, Gravity.LEFT);

        try {
            ab.setDisplayShowCustomEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
            ab.setCustomView(view, params);
            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.cellSelected)));



        } catch (Exception e) {
           e.printStackTrace();
        }
        try {
            //ab Android 5.0
            ab.setElevation(0);
        } catch (Exception e) {
            e.printStackTrace();
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
            chatlist.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            chatlist.setStackFromBottom(true);
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
            messageshowed=false;
        }

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

    }

    private boolean setChatmessage(){
        String messagetoSend=chaTtext.getText().toString();
        String sender=userLocalStore.getLoggedInUser().username;
        if(!messagetoSend.isEmpty()){
            ChatPeople curChatObj = addToChat(receiverName, messagetoSend, "0");
            addToDB(curChatObj);
            populateChatMessages();
            clearMessageTextBox();


            return true;
        }
       return false;
    }




    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
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
            messageshowed=false;
            abortBroadcast();


        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_live_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                User u=userLocalStore.getLoggedInUser();
                fetchuserlist(u);
                break;
            case R.id.action_delete_chat:
                dbOperation.deleteTableData(new ChatPeople().getTableName(),null);
                break;
        }
        return true;
    }
    private String getData(ArrayList<Pair<String, String>> values) throws UnsupportedEncodingException {
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

        if (!messageToSend.isEmpty()) {


            Thread thread = new Thread() {
                @Override
                public void run() {
                    HttpURLConnection conn=null;
                    try {
                        String regid = userLocalStore.getUserRegistrationId();
                        ArrayList<Pair<String,String>> data=new ArrayList<>();
                        data.add(new Pair<String, String>("message", messageToSend));
                        data.add(new Pair<String, String>("sender", userLocalStore.getLoggedInUser().username));
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

        messageshowed=false;
        PackageManager pm = getPackageManager();
        ComponentName compName =
                new ComponentName(getApplicationContext(),
                        GCMBroadcastReceiver.class);
        pm.setComponentEnabledSetting(
                compName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);


    }

    @Override
    protected void onStop()
    {
        messageshowed=true;
        unregisterReceiver(broadcastReceiver);


        PackageManager pmm = getPackageManager();
        ComponentName compNamme =
                new ComponentName(getApplicationContext(),
                        GCMBroadcastReceiver.class);
        pmm.setComponentEnabledSetting(
                compNamme,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        messageshowed=true;

    }

    @Override
    protected void onResume() {
        super.onResume();
        messageshowed=false;
    }

    void clearMessageTextBox() {

        chaTtext.clearFocus();
        chaTtext.setText("");

        hideKeyBoard(chaTtext);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        messageshowed=true;
    }

    private void hideKeyBoard(EditText edt) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
            sendMessagetoServer();
            setChatmessage();
        }
        return false;
    }

    @Override
    public void onBackPressed()
    {
        User u=userLocalStore.getLoggedInUser();
        fetchuserlist(u);
    }
    private void fetchuserlist(final User user){
        final ServerRequestUser serverRequestUser=new ServerRequestUser(this);
        serverRequestUser.fetchallUserForGcm(user, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void deleted(String reponse) {

            }

            @Override
            public void userlist(ArrayList<User> reponse) {
                if (reponse.size() != 0) {
                    ArrayList<User> users = new ArrayList<User>();
                    users = reponse;
                    final ArrayList<User> finalUsers = users;
                    serverRequestUser.fetchallUsers(user,new GetUserCallbacks() {
                        @Override
                        public void done(User returneduser) {

                        }

                        @Override
                        public void deleted(String reponse) {

                        }

                        @Override
                        public void userlist(ArrayList<User> reponse) {

                            if (reponse.size() != 0) {
                                Intent intent = new Intent(LiveChatActivity.this, NewUserTabsActivity.class);
                                intent.putExtra("userlistforgcm", finalUsers);
                                intent.putExtra("userlist", reponse);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

                }
            }
        });
    }



}

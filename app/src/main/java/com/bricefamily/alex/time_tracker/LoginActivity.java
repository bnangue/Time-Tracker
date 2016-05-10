package com.bricefamily.alex.time_tracker;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import org.json.JSONObject;

public class LoginActivity extends ActionBarActivity implements TextView.OnEditorActionListener,View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private EditText emailed, passworded;
    private String emailstr, passwordstr;
    private PasswordChecker pwchecker;
    private IncomingNotification incomingNotification;
    private MySQLiteHelper mySQLiteHelper;

    private UserLocalStore userLocalStore;
    UserProfilePicture userProfilePicture;
    GoogleCloudMessaging gcm;
    String regid;
     GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private int count=0;


    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if ( "WIFI".equals(ni.getTypeName()))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if ("MOBILE".equals(ni.getTypeName()))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mySQLiteHelper=new MySQLiteHelper(this);
        prepareView();
        userLocalStore = new UserLocalStore(this);

        pwchecker = new PasswordChecker();

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        emailed = (EditText) findViewById(R.id.editTextemail);
        passworded = (EditText) findViewById(R.id.editTextpassword);
        passworded.setOnEditorActionListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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
    @Override
    protected void onStart() {
        super.onStart();
        if (authenticate()) {
            displayUserdetails();

        }

    }

    private void displayUserdetails() {
        User user = userLocalStore.getLoggedInUser();
        emailed.setText(user.email);
       // passworded.setText(user.password);
        userProfilePicture = new UserProfilePicture(user.username, null);
    }

    //true if user logged in
    private boolean authenticate() {
        return userLocalStore.getUserLoggedIn();
    }


    // first check if internet connection
    public void buttonLoginPressed(View view) {
        if(haveNetworkConnection()){
            emailstr = emailed.getText().toString();
            passwordstr = passworded.getText().toString();

            if(userLocalStore.getLoggedInUser().username.isEmpty()){
                User user = new User(null, emailstr, passwordstr,1,userLocalStore.getUserRegistrationId());
                logthisUserin(user);
            }else{
                User user = new User(userLocalStore.getLoggedInUser().username,emailstr, passwordstr,1,userLocalStore.getUserRegistrationId());
                //Toast.makeText(getApplicationContext(),userLocalStore.getUserRegistrationId(),Toast.LENGTH_SHORT).show();
                if(userLocalStore.getUserRegistrationId().isEmpty()|| userLocalStore.getUserRegistrationId()==null){

                }else {

                }
                logthisUserin(user);
            }
        }else {
            Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show();
        }



    }

    void logthisUserin(final User user){
        ServerRequestUser serverRequest = new ServerRequestUser(this);
        String deme=user.email;
        serverRequest.fetchUserDataInBackground(user, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {
                if (returneduser == null) {
                    showdialg();
                } else {
                    String u = returneduser.username;
                    if (returneduser.regId.equals(userLocalStore.getUserRegistrationId())) {
                        userLocalStore.setUserUserfriendliststring(returneduser.friendlist);
                        logUserIn(returneduser);
                    } else {
                        getRegId(returneduser);

                    }

                }
            }

            @Override
            public void deleted(String reponse) {

            }

            @Override
            public void serverReponse(String reponse) {

            }

            @Override
            public void userlist(ArrayList<User> reponse) {

            }
        });
    }



    public void storeregIdsMysql(final User user){
        ServerRequestUser serverRequestUser=new ServerRequestUser(this);
        serverRequestUser.storeUserGcmIds(user, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void deleted(String reponse) {
                if (reponse.contains("Registration id successfully saved")) {
                    logUserIn(user);
                } else {
                    dialg("could not save registration id");
                }

            }

            @Override
            public void serverReponse(String reponse) {

            }

            @Override
            public void userlist(ArrayList<User> reponse) {

            }
        });
    }
    public void getRegId(final User registeredData){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(Config.GCM_SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    Log.i("GCM", msg);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return regid;
            }

            @Override
            protected void onPostExecute(String msg) {

                if(!msg.isEmpty()){

                    User us=new User(msg,registeredData.username,registeredData.email,registeredData.password);

                    userLocalStore.setUserGCMregId(msg,0);
                    storeregIdsMysql(us);
                }
            }
        }.execute(null, null, null);
    }


    private void logUserIn(User returneduser) {

        userLocalStore.storeUserData(returneduser);
        userLocalStore.setUserLoggedIn(true);

        userLocalStore.setUserPicturePath(userLocalStore.saveToInternalStorage(returneduser.picture));

        String friendlist=userLocalStore.getUserfriendliststring();
        if(friendlist==null||friendlist.isEmpty()||friendlist.equals(",")){
            userLocalStore.setUserUserfriendliststring("");
        }


        Intent intent=new Intent(this,HomeScreenActivity.class);
        intent.putExtra("loggedinUser", returneduser);
        startActivity(intent);
        //getEventsFromDatabase(returneduser);

    }


    private void showdialg() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Incorrect user data");
        alert.setPositiveButton("OK", null);
        alert.show();
    }

    private void showdialg2() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Uneable to fetch data form DataBase");
        alert.setPositiveButton("OK", null);
        alert.show();
    }

    private void dialg(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(message);
        alert.setPositiveButton("OK", null);
        alert.show();
    }

    public void buttonCreateUserPressed(View view) {

        Intent intent = new Intent(this, CreateUserActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
            if(haveNetworkConnection()){
                emailstr = emailed.getText().toString();
                passwordstr = passworded.getText().toString();


                if(userLocalStore.getLoggedInUser().username.isEmpty()){
                    User user = new User(null, emailstr, passwordstr,1,userLocalStore.getUserRegistrationId());
                    logthisUserin(user);
                }else{
                    User user = new User(userLocalStore.getLoggedInUser().username,emailstr, passwordstr,1,userLocalStore.getUserRegistrationId());
                    //Toast.makeText(getApplicationContext(),userLocalStore.getUserRegistrationId(),Toast.LENGTH_SHORT).show();
                    if(userLocalStore.getUserRegistrationId().isEmpty()|| userLocalStore.getUserRegistrationId()==null){

                    }else {

                    }
                    logthisUserin(user);
                }
            }else {
                Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show();

            }

        }

        return false;
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.sign_in_button:

                signIn();

                break;

        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        count++;

        if(count==2){
            this.finishAffinity();
            System.exit(0);
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                count = 0;

            }

        }, 10000);
        Toast.makeText(getApplicationContext(),"Click a second time to exit application",Toast.LENGTH_SHORT).show();    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Intent intent= new Intent(LoginActivity.this,HomeScreenActivity.class);
            intent.putExtra("log",acct.getDisplayName());
            Toast.makeText(this,"logged in as "+acct.getDisplayName(),Toast.LENGTH_SHORT).show();

            startActivity(intent);
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

        } else {
            // Signed out, show unauthenticated UI.
            // updateUI(false);
        }
    }



}


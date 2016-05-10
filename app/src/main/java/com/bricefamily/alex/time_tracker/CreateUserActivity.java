package com.bricefamily.alex.time_tracker;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;


public class CreateUserActivity extends ActionBarActivity implements TextView.OnEditorActionListener {
    private EditText emailed,usernameed,passworded,repeatpassworded,firstnameed,lastnameed;
    private String emailstr, passwordstr,repeatpasswordstr,usernamestr,firstnamestr,lastnamestr;

    private PasswordChecker pwChecker;
    private UserLocalStore userLocalStore;
    GoogleCloudMessaging gcm;
    String regid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        prepareView();
        userLocalStore=new UserLocalStore(this);
        pwChecker=new PasswordChecker();
        emailed=(EditText)findViewById(R.id.editTextcreateemail);
        usernameed=(EditText)findViewById(R.id.editTextcreatename);
        passworded=(EditText)findViewById(R.id.editTextcreatepassword);
        firstnameed=(EditText)findViewById(R.id.editTextfirstemail);
        lastnameed=(EditText)findViewById(R.id.editTextlastname);
        repeatpassworded=(EditText)findViewById(R.id.editTextrepeatpassword);
        repeatpassworded.setOnEditorActionListener(this);

    }

    public void storeregIdsMysql(User user){
        ServerRequestUser serverRequestUser=new ServerRequestUser(this);
        serverRequestUser.storeUserGcmIds(user, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void deleted(String reponse) {
                if (reponse.contains("Registration id successfully saved")) {
                    startActivity(new Intent(CreateUserActivity.this, LoginActivity.class));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_user, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void buttonCreateNewAccountPressed(View view){
        emailstr=emailed.getText().toString();
        usernamestr=usernameed.getText().toString();
        passwordstr=passworded.getText().toString();
        repeatpasswordstr=repeatpassworded.getText().toString();
        firstnamestr=firstnameed.getText().toString();
        lastnamestr=lastnameed.getText().toString();
        //do Mysql saving


        if(TextUtils.isEmpty(emailstr)){
            emailed.setError("this field cannot be empty");
        }else if(TextUtils.isEmpty(passwordstr)){
            passworded.setError("this field cannot be empty");

        }else if(TextUtils.isEmpty(repeatpasswordstr)){
            repeatpassworded.setError("this field cannot be empty");
        }else if(!TextUtils.isEmpty(emailstr) && !TextUtils.isEmpty(passwordstr) && !TextUtils.isEmpty(repeatpasswordstr)){
            if(passwordstr.equals(repeatpasswordstr)){
                int password=passwordstr.hashCode();
                User userRegistered=new User(usernamestr,emailstr,String.valueOf(password));
                userRegistered.firstname=firstnamestr;
                userRegistered.lastname=lastnamestr;

                register(userRegistered);
            }else {
                Toast.makeText(getApplicationContext(),"password doesn't match. Please check your entry",Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void register(final User registeredData){
        final ServerRequestUser serverRequest =new ServerRequestUser(this);
        serverRequest.storeUserDataInBackground(registeredData, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {
                getRegId(registeredData);


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
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)){
            emailstr=emailed.getText().toString();
            usernamestr=usernameed.getText().toString();
            passwordstr=passworded.getText().toString();
            repeatpasswordstr=repeatpassworded.getText().toString();
            firstnamestr=firstnameed.getText().toString();
            lastnamestr=lastnameed.getText().toString();
            //do Mysql saving


            if(TextUtils.isEmpty(emailstr)){
                emailed.setError("this field cannot be empty");
            }else if(TextUtils.isEmpty(passwordstr)){
                passworded.setError("this field cannot be empty");

            }else if(TextUtils.isEmpty(repeatpasswordstr)){
                repeatpassworded.setError("this field cannot be empty");
            }else if(!TextUtils.isEmpty(emailstr) && !TextUtils.isEmpty(passwordstr) && !TextUtils.isEmpty(repeatpasswordstr)){
                if(passwordstr.equals(repeatpasswordstr)){
                    int password=passwordstr.hashCode();
                    User userRegistered=new User(usernamestr,emailstr,String.valueOf(password));
                    userRegistered.firstname=firstnamestr;
                    userRegistered.lastname=lastnamestr;

                    register(userRegistered);
                }else {
                    Toast.makeText(getApplicationContext(),"password doesn't match. Please check your entry",Toast.LENGTH_SHORT).show();
                }
            }
        }
            return false;
    }

    public void OnGotoLogin(View view){
        startActivity(new Intent(this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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

                    User user=new User(msg,registeredData.username,registeredData.email,null);

                    userLocalStore.setUserGCMregId(msg,0);
                    storeregIdsMysql(user);
                }

            }
        }.execute(null, null, null);
    }
}

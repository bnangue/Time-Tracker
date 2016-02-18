package com.bricefamily.alex.time_tracker;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class LoginActivity extends ActionBarActivity implements TextView.OnEditorActionListener {
    private EditText emailed, passworded;
    private String emailstr, passwordstr;
    private PasswordChecker pwchecker;

    private UserLocalStore userLocalStore;
    UserProfilePicture userProfilePicture;
    GoogleCloudMessaging gcm;
    String regid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prepareView();
        userLocalStore = new UserLocalStore(this);

        pwchecker = new PasswordChecker();

        emailed = (EditText) findViewById(R.id.editTextemail);
        passworded = (EditText) findViewById(R.id.editTextpassword);
        passworded.setOnEditorActionListener(this);

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
            String path=userLocalStore.getUserPicturePath();
            String uName=userLocalStore.getLoggedInUser().username;
            if(path!=null||  !path.isEmpty()|| !uName.isEmpty()|| uName!=null){
                Bitmap bitmap= loadImageFromStorage(path,uName);
                if(bitmap==null){
                    if(userProfilePicture!=null){
                        getUserPicture(userProfilePicture);
                    }else {
                        Toast.makeText(getApplicationContext(),"Error loading picture",Toast.LENGTH_SHORT).show();
                    }

                }
            }else {
                if(userProfilePicture!=null){
                    getUserPicture(userProfilePicture);
                }else {
                    Toast.makeText(getApplicationContext(),"Error loading picture",Toast.LENGTH_SHORT).show();
                }
            }

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

                    String u=returneduser.username;
                    if(returneduser.regId.equals(userLocalStore.getUserRegistrationId())){
                        userLocalStore.setUserUserfriendliststring(returneduser.friendlist);
                        logUserIn(returneduser);
                    }
                    else {
                        getRegId(returneduser);

                    }

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

    void checkGCMRegistrationIds(final User user){
        //logUserIn(returneduser);
        ServerRequestUser serverRequestUser=new ServerRequestUser(this);
        String deme = user.username;
        String em = user.email;
        String pw = user.password;

        serverRequestUser.fetchUserGcmRegid(user, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {
                if (returneduser != null) {
                    String r = returneduser.regId;
                    if (returneduser.regId.equals(userLocalStore.getUserRegistrationId())) {
                        logUserIn(user);
                    } else {
                        getRegId(user);
                        //    userLocalStore.setUserGCMregId(returneduser.regId,0);
                        //      logUserIn(user);
                    }
                } else {
                    //regid speichern
                    getRegId(user);
                    //showdialg2();
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
    private void authenticateuser(final User user) {
        ServerRequestUser serverRequest = new ServerRequestUser(this);
        String deme=user.email;
        serverRequest.fetchUserDataInBackground(user, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {
                if (returneduser == null) {
                    showdialg();
                } else {

                    checkGCMRegistrationIds(returneduser);

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

    private void updatestatus(final User user){
        ServerRequestUser serverRequestUser=new ServerRequestUser(this);
        serverRequestUser.updtaestatus(user, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void deleted(String reponse) {

                if (reponse.contains("Status successfully updated")) {

                    authenticateuser(user);

                }
            }

            @Override
            public void userlist(ArrayList<User> reponse) {

            }
        });
    }



    private void logUserIn(User returneduser) {

        userLocalStore.storeUserData(returneduser);
        userLocalStore.setUserLoggedIn(true);

        String friendlist=userLocalStore.getUserfriendliststring();
        if(friendlist==null||friendlist.isEmpty()||friendlist.equals(",")){
            userLocalStore.setUserUserfriendliststring("noFrineds");
        }


        Intent intent=new Intent(this,HomeScreenActivity.class);
        intent.putExtra("loggedinUser",returneduser);
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
        }

        return false;
    }

    public Bitmap getThumbnail(String filename) {

        //String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;
        Bitmap thumbnail = null;

// Look for the file on the external storage
        //try {
        //if (tools.isSdReadable() == true) {
        //thumbnail = BitmapFactory.decodeFile(fullPath + "/" + filename);
        // }
        // } catch (Exception e) {
        // Log.e("getThumbnail() on external storage", e.getMessage());
        // }

// If no file on external storage, look in internal storage
        // if (thumbnail == null) {
        try {
            File filePath = getFileStreamPath(filename);
            FileInputStream fi = new FileInputStream(filePath);
            thumbnail = BitmapFactory.decodeStream(fi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return thumbnail;
    }

    void getUserPicture(final UserProfilePicture u){
        if(u.username!=null|| !u.username.isEmpty()){

            ServerRequestUser serverRequest=new ServerRequestUser(this);
            serverRequest.fetchUserPicture(u, new GetImageCallBacks() {
                @Override
                public void done(String reponse) {

                }

                @Override
                public void image(UserProfilePicture reponse) {
                    if (reponse != null) {
                        Bitmap bitmap = reponse.uProfilePicture;
                        String  userName = reponse.username;
                      String picturePath=  saveToInternalStorage(bitmap,userName);
                        userLocalStore.setUserPicturePath(picturePath);
                         //storeimageLocaly(reponse.uProfilePicture);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Picture save for this user", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }

    private String saveToInternalStorage(Bitmap bitmapImage,String username){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("userProfilePicture", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,username+".jpg");

        if(mypath.exists()){
            mypath.delete();
            mypath=new File(directory,username+".jpg");
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }


    private Bitmap loadImageFromStorage(String path,String username)
    {
        Bitmap bitmap=null;
        try {
            File f=new File(path, username+".jpg");
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }

    private boolean  storeimageLocaly(Bitmap picture) {


        FileOutputStream fos=null;
        try {
            fos=openFileOutput("profile.png",Context.MODE_PRIVATE);
            picture.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return true;


        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}


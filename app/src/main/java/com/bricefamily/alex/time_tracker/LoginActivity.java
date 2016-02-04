package com.bricefamily.alex.time_tracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends ActionBarActivity implements TextView.OnEditorActionListener {
    private EditText emailed, passworded;
    private String emailstr, passwordstr;
    private PasswordChecker pwchecker;

    private UserLocalStore userLocalStore;
    UserProfilePicture userProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userLocalStore = new UserLocalStore(this);

        pwchecker = new PasswordChecker();

        emailed = (EditText) findViewById(R.id.editTextemail);
        passworded = (EditText) findViewById(R.id.editTextpassword);
        passworded.setOnEditorActionListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authenticate() == true) {
            displayUserdetails();
            Bitmap bitmap= getThumbnail("profile.png");
            if(bitmap==null){
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
        passworded.setText(user.password);
        userProfilePicture = new UserProfilePicture(user.username, null);
    }

    //true if user logged in
    private boolean authenticate() {
        return userLocalStore.getUserLoggedIn();
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

    public void buttonLoginPressed(View view) {
        emailstr = emailed.getText().toString();
        passwordstr = passworded.getText().toString();

        User user = new User(emailstr, passwordstr);
        authenticateuser(user);

    }

    private void authenticateuser(User user) {
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.fetchUserDataInBackground(user, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {
                if (returneduser == null) {
                    showdialg();
                } else {
                    logUserIn(returneduser);
                }
            }
        });

    }

    private void logUserIn(User returneduser) {

        userLocalStore.storeUserData(returneduser);
        userLocalStore.setUserLoggedIn(true);

        getEventsFromDatabase(returneduser);

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

    void getEventsFromDatabase(final User returneduser) {

        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.fetchAllevents(new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<EventObject> returnedeventobject) {
                if (returnedeventobject != null) {
                    Intent intent = new Intent(LoginActivity.this, CentralPageActivity.class);
                    intent.putExtra("username", returneduser.username);
                    intent.putExtra("eventlist", returnedeventobject);
                    startActivity(intent);
                } else {
                    showdialg2();
                }
            }
        });
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

            User user = new User(emailstr, passwordstr);
            authenticateuser(user);
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

            ServerRequest serverRequest=new ServerRequest(this);
            serverRequest.fetchUserPicture(u, new GetImageCallBacks() {
                @Override
                public void done(String reponse) {

                }

                @Override
                public void image(UserProfilePicture reponse) {
                    if (reponse != null) {
                        Bitmap bitmap = reponse.uProfilePicture;
                        //profilePicture.setImageBitmap(bitmap);
                         storeimageLocaly(reponse.uProfilePicture);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Picture save for this user", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

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


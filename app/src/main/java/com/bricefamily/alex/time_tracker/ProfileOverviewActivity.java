package com.bricefamily.alex.time_tracker;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class ProfileOverviewActivity extends AppCompatActivity {

    private UserLocalStore userLocalStore;

    private User user;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userLocalStore=new UserLocalStore(this);
        user=userLocalStore.getLoggedInUser();

        setContentView(R.layout.activity_profile_overview);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.cellSelected)));

    }

    public void buttonOpencompleteProfilePressed(View view){
        Intent intent=new Intent(ProfileOverviewActivity.this,CompleteProfileActivity.class);
        intent.putExtra("username",user.username);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_delete_account:
                final ServerRequestUser serverRequestUser=new ServerRequestUser(this);
                serverRequestUser.deleteUser(user, new GetUserCallbacks() {
                    @Override
                    public void done(User returneduser) {

                    }

                    @Override
                    public void deleted(String reponse) {
                        if(reponse.contains("User successfully deleted")){
                            serverRequestUser.deleteAlleventfromUser(user, new GetEventsCallbacks() {
                                @Override
                                public void done(ArrayList<EventObject> returnedeventobject) {

                                }

                                @Override
                                public void updated(String reponse) {
                                    if (reponse.contains("All Events from successfully deleted")) {
                                        userLocalStore.clearUserData();
                                        userLocalStore.setUserLoggedIn(false);
                                        //delete user picture and other datei localy and on data base
                                        Intent intent = new Intent(ProfileOverviewActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

                                    }
                                }
                            });

                        }
                    }

                    @Override
                    public void userlist(ArrayList<User> reponse) {
                        
                    }
                });
                break;
            case R.id.action_logout:
                User user=new User(userLocalStore.getLoggedInUser().username,userLocalStore.getLoggedInUser().email,userLocalStore.getLoggedInUser().password,0,userLocalStore.getUserRegistrationId());

                updatestatus(user);
                userLocalStore.clearUserData();
                userLocalStore.setUserLoggedIn(false);

                break;
            case R.id.action_settings:
                break;
            default:
                return true;

        }
        return super.onOptionsItemSelected(item);
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

    private void updatestatus(final User user){
        ServerRequestUser serverRequestUser=new ServerRequestUser(this);
        serverRequestUser.updtaestatus(user, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void deleted(String reponse) {

                if (reponse.contains("Status successfully updated")) {
                    Intent intent = new Intent(ProfileOverviewActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }

            @Override
            public void userlist(ArrayList<User> reponse) {

            }
        });
    }

}

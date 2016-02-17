package com.bricefamily.alex.time_tracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class HomeScreenActivity extends ActionBarActivity implements DialogLogoutFragment.YesNoListenerDeleteAccount{
    User user;
    String username;
    UserLocalStore userLocalStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        userLocalStore=new UserLocalStore(this);
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            user=extras.getParcelable("loggedinUser");
        }


        if(user!=null){
            username=user.username;
        }else {
           username= userLocalStore.getLoggedInUser().username;
        }

    }
    public void homehomePressed(View view){
        if(user!=null){
            getEventsFromDatabase(user);
        }else {
            showdialg2();
        }
    }
    public void homegroupPressed(View view){
        Intent intent = new Intent(HomeScreenActivity.this, NewUserTabsActivity.class);

        startActivity(intent);

    }
    public void homepreferencePressed(View view){
        startActivity(new Intent(this,PreferenceActivity.class));

    }

    void getEventsFromDatabase(final User returneduser) {

        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.fetchAllevents(new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<EventObject> returnedeventobject) {
                if (returnedeventobject != null) {
                    Intent intent = new Intent(HomeScreenActivity.this, CentralPageActivity.class);
                    intent.putExtra("username", returneduser.username);
                    intent.putExtra("eventlist", returnedeventobject);
                    startActivity(intent);
                    finish();
                } else {
                    showdialg2();
                }
            }

            @Override
            public void updated(String reponse) {

            }
        });
    }

    private void fetchuserlist(User user){
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
                    serverRequestUser.fetchallUsers(new GetUserCallbacks() {
                        @Override
                        public void done(User returneduser) {

                        }

                        @Override
                        public void deleted(String reponse) {

                        }

                        @Override
                        public void userlist(ArrayList<User> reponse) {

                            if (reponse.size() != 0) {
                                Intent intent = new Intent(HomeScreenActivity.this, NewUserTabsActivity.class);
                                intent.putExtra("userlistforgcm", finalUsers);
                                intent.putExtra("userlist", reponse);
                                startActivity(intent);
                            }
                        }
                    });

                }
            }
        });
    }


    private void showdialg2() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Uneable to fetch data form DataBase");
        alert.setPositiveButton("OK", null);
        alert.show();
    }

    @Override
    public void onBackPressed() {


        DialogLogoutFragment alertDialogFragmentTwobtn=new DialogLogoutFragment();
        alertDialogFragmentTwobtn.setCancelable(false);
        alertDialogFragmentTwobtn.show(getSupportFragmentManager(), "tag");

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
                    Intent intent = new Intent(HomeScreenActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }

            @Override
            public void userlist(ArrayList<User> reponse) {

            }
        });
    }

    @Override
    public void onYes() {
        String n=userLocalStore.getLoggedInUser().username;
        String e=userLocalStore.getLoggedInUser().email;
        String p=userLocalStore.getLoggedInUser().password;
        User us=new User(n,e,p,0);

        updatestatus(us);
        userLocalStore.clearUserData();
        userLocalStore.setUserLoggedIn(false);
    }

    @Override
    public void onNo() {

    }
}

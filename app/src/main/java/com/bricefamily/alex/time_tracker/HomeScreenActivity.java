package com.bricefamily.alex.time_tracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class HomeScreenActivity extends ActionBarActivity implements DialogLogoutFragment.YesNoListenerDeleteAccount,GoogleApiClient.OnConnectionFailedListener{
    User user;
    String username;
    UserLocalStore userLocalStore;
    boolean save=true;
    private IncomingNotification incomingNotification;
    private MySQLiteHelper mySQLiteHelper;
    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        userLocalStore=new UserLocalStore(this);
        mySQLiteHelper=new MySQLiteHelper(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            user=extras.getParcelable("loggedinUser");
            Toast.makeText(this,"logged in as "+extras.getString("log"),Toast.LENGTH_SHORT).show();
        }

        if(savedInstanceState!=null){
            user=savedInstanceState.getParcelable("user");
        }

        if(user!=null){
            username=user.username;
        }else {
           username= userLocalStore.getLoggedInUser().username;
        }

    }
    public void homehomePressed(View view){
        Intent intent = new Intent(HomeScreenActivity.this, CentralPageActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("loggedinUser",user);
        startActivity(intent);
    }
    public void homegroupPressed(View view){
        Intent intent = new Intent(HomeScreenActivity.this, NewUserTabsActivity.class);

        startActivity(intent);

    }
    public void homepreferencePressed(View view){
        startActivity(new Intent(this,PreferenceAppActivity.class));

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
            public void serverReponse(String reponse) {

            }

            @Override
            public void userlist(ArrayList<User> reponse) {

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(save){
            super.onSaveInstanceState(outState);
            outState.putParcelable("user",user);
        }

    }

    @Override
    public void onYes() {
        String n=userLocalStore.getLoggedInUser().username;
        String e=userLocalStore.getLoggedInUser().email;
        String p=userLocalStore.getLoggedInUser().password;
        String reid=userLocalStore.getUserRegistrationId();
        User us=new User(n,e,p,0,reid);

        updatestatus(us);
        userLocalStore.clearUserData();
        userLocalStore.setUserLoggedIn(false);

    }

    @Override
    public void onNo() {

    }

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

        } else {
            // Signed out, show unauthenticated UI.
            // updateUI(false);
        }
    }


}

package com.bricefamily.alex.time_tracker;

import android.app.AlertDialog;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends ActionBarActivity implements TextView.OnEditorActionListener {
    private EditText emailed,passworded;
    private String emailstr,passwordstr;
    private PasswordChecker pwchecker;

    private  UserLocalStore userLocalStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userLocalStore=new UserLocalStore(this);

        pwchecker=new PasswordChecker();

        emailed =(EditText)findViewById(R.id.editTextemail);
        passworded=(EditText)findViewById(R.id.editTextpassword);
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
        if(authenticate()==true){
            displayUserdetails();
        }

    }

    private void displayUserdetails(){
        User user=userLocalStore.getLoggedInUser();
        emailed.setText(user.email);
        passworded.setText(user.password);
    }
    //true if user logged in
    private boolean authenticate(){
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

    public  void buttonLoginPressed(View view){
        emailstr=emailed.getText().toString();
        passwordstr=passworded.getText().toString();

        User user =new User(emailstr,passwordstr);
        authenticateuser(user);

    }

    private void authenticateuser(User user){
        ServerRequest serverRequest=new ServerRequest(this);
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

    private void logUserIn(User returneduser){

        userLocalStore.storeUserData(returneduser);
        userLocalStore.setUserLoggedIn(true);

        getEventsFromDatabase(returneduser);

    }
    private void showdialg(){
        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setMessage("Incorrect user data");
        alert.setPositiveButton("OK", null);
        alert.show();
    }
    private void showdialg2(){
        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setMessage("Uneable to fetch data form DataBase");
        alert.setPositiveButton("OK",null);
        alert.show();
    }
    void  getEventsFromDatabase(final User returneduser){

        ServerRequest serverRequest=new ServerRequest(this);
        serverRequest.fetchAllevents(new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<EventObject> returnedeventobject) {
                if (returnedeventobject != null) {
                    Intent intent=new Intent(LoginActivity.this,CentralPageActivity.class);
                    intent.putExtra("username",returneduser.username);
                    intent.putExtra("eventlist", returnedeventobject);
                    startActivity(intent);
                }else{
                    showdialg2();
                }
            }
        });
    }

    public  void buttonCreateUserPressed(View view ){

        Intent intent =new Intent(this, CreateUserActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)){
            emailstr=emailed.getText().toString();
            passwordstr=passworded.getText().toString();

            User user =new User(emailstr,passwordstr);
            authenticateuser(user);
        }

            return false;
    }
}

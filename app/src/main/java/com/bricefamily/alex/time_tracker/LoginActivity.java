package com.bricefamily.alex.time_tracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends ActionBarActivity {
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
                if(returneduser==null){
                    showdialg();
                }else{
                    logUserIn(returneduser);
                }
            }
        });

    }

    private void logUserIn(User returneduser){

        userLocalStore.storeUserData(returneduser);
        userLocalStore.setUserLoggedIn(true);

        Intent intent=new Intent(this,CentralPageActivity.class);
        intent.putExtra("username",returneduser.username);
        startActivity(intent);
    }
    private void showdialg(){
        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setMessage("Incorrect user data");
        alert.setPositiveButton("OK",null);
        alert.show();
    }
    public  void buttonCreateUserPressed(View view ){

        Intent intent =new Intent(this, CreateUserActivity.class);
        startActivity(intent);
    }
}

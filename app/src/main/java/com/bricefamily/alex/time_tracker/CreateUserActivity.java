package com.bricefamily.alex.time_tracker;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class CreateUserActivity extends ActionBarActivity {
    private EditText emailed,usernameed,passworded,repeatpassworded;
    private String emailstr, passwordstr,repeatpasswordstr,usernamestr;

    private PasswordChecker pwChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        pwChecker=new PasswordChecker();
        emailed=(EditText)findViewById(R.id.editTextcreateemail);
        usernameed=(EditText)findViewById(R.id.editTextcreatename);
        passworded=(EditText)findViewById(R.id.editTextcreatepassword);
        repeatpassworded=(EditText)findViewById(R.id.editTextrepeatpassword);

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
        //do Mysql saving
        if(pwChecker.checkIfValid(passwordstr,repeatpasswordstr)){

            User registeredData=new User(usernamestr,emailstr,passwordstr);
            register(registeredData);

        }else{
            Toast.makeText(this, "Password doesn't match", Toast.LENGTH_SHORT).show();

        }
    }

    private void register(User registeredData){
        ServerRequest serverRequest =new ServerRequest(this);
        serverRequest.storeUserDataInBackground(registeredData, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

                    startActivity(new Intent(CreateUserActivity.this,LoginActivity.class));
                }
        });
    }
}

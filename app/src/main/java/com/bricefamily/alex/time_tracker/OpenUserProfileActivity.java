package com.bricefamily.alex.time_tracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class OpenUserProfileActivity extends AppCompatActivity implements AlertDailogChangePassword.OnPasswordChanged,AlertDialogChangeEmail.OnEmailChanged
        ,AlertDialogChangeNotSaved.OnChangesCancel,EditFullNameFragment.OnFullNameChanged {

    private int PICK_IMAGE_REQUEST = 1;

    private User currentuser;
    private TextView changepass,fullnametv;

    private EditText emailed;
    private String emailstr,firstnamestr,lastnamestr,passwordhashstr;
    public static boolean arechangesSaved;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private ImageView profilepic;
   private Bitmap bitmap;
    private Bitmap userbitmap=null;

    private Uri filePath;
    private UserLocalStore userLocalStore;
    Menu menu;

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
        arechangesSaved=true;
        setContentView(R.layout.activity_open_user_profile);

        userLocalStore=new UserLocalStore(this);
        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinateLayoutopenprofile);
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            currentuser=extras.getParcelable("loggedinUser");
        }
        profilepic=(ImageView)findViewById(R.id.imageViewUserprofilepicture);
        emailed=(EditText)findViewById(R.id.editTextemailprofileview);
        fullnametv=(TextView)findViewById(R.id.textViewfullname);
        if(currentuser.getfullname().isEmpty()){
            fullnametv.setTextSize(12);
            fullnametv.setText("You have not save your name yet");
        }else {
            fullnametv.setText(currentuser.getfullname());
        }

        emailed.setText(currentuser.email);

        emailstr=currentuser.email;
        passwordhashstr=currentuser.password;
        firstnamestr=" ";
        lastnamestr=" ";
        if(userLocalStore.loadImageFromStorage(userLocalStore.getUserPicturePath())!=null){
            userbitmap=userLocalStore.loadImageFromStorage(userLocalStore.getUserPicturePath());
            if(currentuser.picture!=null){
                profilepic.setImageBitmap(currentuser.picture);
            }else {
                profilepic.setImageBitmap(userbitmap);
            }


        }        Toast.makeText(this,currentuser.email,Toast.LENGTH_SHORT).show();
    }

    public void OnEditProfileClicked(View view){
        arechangesSaved=false;
        showOption(R.id.action_menu_save);
        savefullname();
    }

    private void savefullname() {

        DialogFragment fragment=new EditFullNameFragment();
        fragment.setCancelable(false);
        fragment.show(getSupportFragmentManager(), "CHANGE FULLNAME");
    }

    public void OnProfilePictureClicked(View view){
        arechangesSaved=false;

        showOption(R.id.action_menu_save);
        showFileChooser();
    }
    public void OnEditEmailInProfile(View view){
        editEmail();
    }


    private void editEmail() {

        DialogFragment fragment=new AlertDialogChangeEmail();
        fragment.setCancelable(false);
        fragment.show(getSupportFragmentManager(), "CHANGE EMAIL");
    }

    public void OnChangepassword(View view){
        arechangesSaved=false;

     showOption(R.id.action_menu_save);
        changepasword();
    }

    private void changepasword() {
        DialogFragment fragment=new AlertDailogChangePassword();
        fragment.setCancelable(false);
        fragment.show(getSupportFragmentManager(), "CHANGE PASSWORD");
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_user_profile, menu);
        if(!arechangesSaved){
            showOption(R.id.action_menu_save);
        }else{
            hideOption(R.id.action_menu_save);
        }
        return true;
    }

    private void hideOption(int id){
        MenuItem item=menu.findItem(id);
        item.setVisible(false);
    }
    private void showOption(int id){
        MenuItem item=menu.findItem(id);
        item.setVisible(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_menu_save) {
            saveChangestoServer();
            return true;
        }else if(id == R.id.action_menu_delete_account){
            deleteAccount(userLocalStore.getLoggedInUser());
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAccount(final User user) {
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
                        public void donec(ArrayList<CalendarCollection> returnedeventobject) {

                        }

                        @Override
                        public void updated(String reponse) {
                            if (reponse.contains("All Events from successfully deleted")) {
                                userLocalStore.clearUserData();
                                userLocalStore.setUserLoggedIn(false);
                                //delete user picture and other datei localy and on data base
                                Intent intent = new Intent(OpenUserProfileActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            }
                        }
                    });

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

    void save(final User newu){

        ServerRequests serverRequests=new ServerRequests(this);
        serverRequests.savingEmailAndPassowrdChangedUserinBackground(currentuser, newu, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void deleted(String reponse) {

            }

            @Override
            public void serverReponse(String reponse) {
                if (reponse.contains("User data successfully updated")) {
                    arechangesSaved = true;
                    userLocalStore.storeUserData(newu);
                    userLocalStore.setUserPicturePath(userLocalStore.saveToInternalStorage(newu.picture));
                    String fullname = firstnamestr + " " + lastnamestr;
                    userLocalStore.setUserUserfullname(fullname);

                } else {
                    showSnackBar(" An error occured during registration ");
                }
            }

            @Override
            public void userlist(ArrayList<User> reponse) {

            }
        });
    }
    private void saveChangestoServer() {
        User newuser=new User(emailstr,passwordhashstr);
        if(userbitmap!=null){
            newuser.picture=userbitmap;

        }
        if(!firstnamestr.isEmpty()&& !lastnamestr.isEmpty()){
            newuser.firstname=firstnamestr;
            newuser.lastname=lastnamestr;
        }
        if(haveNetworkConnection()){
            save(newuser);
        }else {
           showSnackBar(" No internet connection detected");
        }
    }


    public void showSnackBar(String message){
        snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveChangestoServer();
                    }
                });
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorSnackbar));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
    @Override
    public void emailchange(String newEmail) {
        if(newEmail!=null){
            arechangesSaved=false;
            showOption(R.id.action_menu_save);
            emailstr=newEmail;
            if(!emailstr.isEmpty()){
                emailed.setText(emailstr);
                emailed.setTextColor(getResources().getColor(R.color.colorAccent));

            }
        }else {
            emailstr=currentuser.password;
        }

    }

    @Override
    public void passwordchange(String newPassword) {
        if(newPassword!=null){
            arechangesSaved=false;
            showOption(R.id.action_menu_save);
            passwordhashstr=newPassword;
        }else {
            passwordhashstr=currentuser.password;
        }

    }

    @Override
    public void onBackPressed() {
        if(arechangesSaved){
             finish();
        }else{
            DialogFragment fragment=new AlertDialogChangeNotSaved();
            fragment.setCancelable(false);
            fragment.show(getSupportFragmentManager(), "CHANGE NOT SAVED");
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                bitmap = decodeBitmap(filePath,getApplicationContext());

                int degree= ImageOrientationUtil.getExifRotation(ImageOrientationUtil.getFromMediaUri(getApplicationContext()
                        ,getContentResolver(),filePath));
                Bitmap  bittmap=rotateImage(bitmap,degree);

                profilepic.setImageBitmap(bittmap);
                userbitmap=bittmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        return rotatedImg;
    }
    public static Bitmap decodeBitmap(Uri selectedImage, Context context)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver()
                .openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 100;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(context.getContentResolver()
                .openInputStream(selectedImage), null, o2);
    }

    @Override
    public void changescanceled(boolean canceled) {
        if(canceled){
            finish();
        }else {
            Toast.makeText(getApplicationContext(),"Make sure you save your changes before exiting",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void fulnamechanged(String firstname, String lastname) {
        firstnamestr=firstname;
        lastnamestr=lastname;

        if(!firstname.isEmpty()){
            fullnametv.setText(firstnamestr+" "+lastnamestr);
            fullnametv.setTextColor(getResources().getColor(R.color.colorAccent));

        }
    }
}

package com.bricefamily.alex.time_tracker;

import android.app.ActionBar;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CompleteProfileActivity extends ActionBarActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    private int PICK_IMAGE_REQUEST = 1;


    private EditText firtsnameed,lastnameed,ageed,postalcodeed,phonenumbed;
    private Button save ,cancel;
    private  String firstname,username,fileUserPicture;
    private ImageView profileopic;
    private UserProfilePicture userPicture;
    private Bitmap bitmap;
    private Uri filePath;
    private UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);
        userLocalStore=new UserLocalStore(this);
        prepareView();
        setViews();

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            username=bundle.getString("username");
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    void setViews(){
        save= (Button)findViewById(R.id.buttonsaveCompleteprofile);
        save= (Button)findViewById(R.id.buttoncancelCompleteprofile);

        profileopic=(ImageView)findViewById(R.id.imageViewprofilepic);
        profileopic.setOnClickListener(this);
        firtsnameed= (EditText)findViewById(R.id.editTextfirstnameCompletprofile);
        lastnameed= (EditText)findViewById(R.id.editTextlastnameCompleteprofile);
        ageed= (EditText)findViewById(R.id.editTextageCompleteprofile);
        postalcodeed= (EditText)findViewById(R.id.editTextAddress);
        phonenumbed= (EditText)findViewById(R.id.editTextphonenumbCompleteprofile);
        phonenumbed.setOnEditorActionListener(this);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            //ab Android 5.0
            ab.setElevation(0);
        } catch (Exception e) {
            e.printStackTrace();
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


    public  void buttonSaveCompleteprofile(View view){

        String path=userLocalStore.getUserPicturePath();
        String uName=userLocalStore.getLoggedInUser().username;

        if(path!=null||!path.isEmpty()){
            Bitmap bitmap = loadImageFromStorage(path, uName);
            if(bitmap!=null){
                if(userPicture!=null){
                    ServerRequestUser serverRequest=new ServerRequestUser(this);
                    serverRequest.updateUserPicture(userPicture, new GetImageCallBacks() {
                        @Override
                        public void done(String reponse) {
                            if (reponse.contains("Profile picture successfully updated")) {
                                userLocalStore.setUserPicturePath(saveToInternalStorage(userPicture.uProfilePicture,username));
                                    Toast.makeText(getApplicationContext(), "Profile picture successfully updated", Toast.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void image(UserProfilePicture reponse) {

                        }
                    });

                }

            }else {
                if(userPicture!=null){
                    ServerRequestUser serverRequest=new ServerRequestUser(this);
                    serverRequest.saveprofilepicture(userPicture, new GetImageCallBacks() {
                        @Override
                        public void done(String reponse) {
                            if(reponse.contains("Image upload successfully")){
                                userLocalStore.setUserPicturePath(saveToInternalStorage(userPicture.uProfilePicture, username));
                                Toast.makeText(getApplicationContext(),"Profile picture successfully added",Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void image(UserProfilePicture reponse) {

                        }
                    });

                }


            }
        }else {
            if(userPicture!=null){
                ServerRequestUser serverRequest=new ServerRequestUser(this);
                serverRequest.saveprofilepicture(userPicture, new GetImageCallBacks() {
                    @Override
                    public void done(String reponse) {
                        if(reponse.contains("Image upload successfully")){
                            userLocalStore.setUserPicturePath(saveToInternalStorage(userPicture.uProfilePicture,username));
                                Toast.makeText(getApplicationContext(),"Profile picture successfully added",Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void image(UserProfilePicture reponse) {

                    }
                });

            }


        }


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

    public  void buttonCancelCompleteprofile(View view){

        finish();
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

                    profileopic.setImageBitmap(bittmap);
                    userPicture=new UserProfilePicture(username,bittmap);
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
    public void onClick(View v) {
        showFileChooser();
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
            //do something to save user changes
        }

            return false;
    }
}

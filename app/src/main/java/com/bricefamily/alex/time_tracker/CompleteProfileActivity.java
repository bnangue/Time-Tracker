package com.bricefamily.alex.time_tracker;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
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

public class CompleteProfileActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    private int PICK_IMAGE_REQUEST = 1;


    private EditText firtsnameed,lastnameed,ageed,postalcodeed,phonenumbed;
    private Button save ,cancel;
    private  String firstname,username,fileUserPicture;
    private ImageView profileopic;
    private UserProfilePicture userPicture;
    private Bitmap bitmap;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
    public  void buttonSaveCompleteprofile(View view){
        Bitmap bitmap=getThumbnail("profile.png");
        if(bitmap==null){
            if(userPicture!=null){
                ServerRequest serverRequest=new ServerRequest(this);
                serverRequest.saveprofilepicture(userPicture, new GetImageCallBacks() {
                    @Override
                    public void done(String reponse) {
                        if(reponse.contains("Image upload successfully")){
                            if(storeimageLocaly(userPicture.uProfilePicture))
                                Toast.makeText(getApplicationContext(),"Profile picture successfully added",Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void image(UserProfilePicture reponse) {

                    }
                });

            }

        }else {
            if(userPicture!=null){
                ServerRequest serverRequest=new ServerRequest(this);
                serverRequest.updateUserPicture(userPicture, new GetImageCallBacks() {
                    @Override
                    public void done(String reponse) {
                        if (reponse.contains("Profile picture successfully updated")) {
                            if(storeimageLocaly(userPicture.uProfilePicture))
                                Toast.makeText(getApplicationContext(),"Profile picture successfully updated",Toast.LENGTH_SHORT).show();

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
        img.recycle();
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

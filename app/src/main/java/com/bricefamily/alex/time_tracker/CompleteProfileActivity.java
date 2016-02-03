package com.bricefamily.alex.time_tracker;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CompleteProfileActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String UPLOAD_KEY = "image";
    public static final String TAG = "MY MESSAGE";

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
    }
    public  void buttonSaveCompleteprofile(View view){
        if(userPicture!=null){
            ServerRequest serverRequest=new ServerRequest(this);
            serverRequest.saveprofilepicture(userPicture, new GetImageCallBacks() {
                @Override
                public void done(String reponse) {
                    if(reponse.contains("Image upload successfully")){
                        storeimageLocaly(userPicture.uProfilePicture);
                    }

                }

                @Override
                public void image(UserProfilePicture reponse) {

                }
            });

        }

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
                    Uri tempUri = getImageUri(getApplicationContext(), bitmap);

                    // CALL THIS METHOD TO GET THE ACTUAL PATH
                    File finalFile = new File(getRealPathFromURI(tempUri));

                    profileopic.setImageBitmap(bitmap);
                    userPicture=new UserProfilePicture(username,bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private String  storeimageLocaly(Bitmap picture) {

        ContextWrapper cw=new ContextWrapper(getApplicationContext());
        File directory=cw.getDir("ProfilePictures",Context.MODE_PRIVATE);
        if(!directory.exists()){
            directory.mkdirs();
        }
        File file=new File(directory,"profile.jpg");

        FileOutputStream fos=null;

        if(file.exists()){
            file.delete();
           file=new File(directory,"profile.jpg");
        }
        try {
                fos=new FileOutputStream(file);
            picture.compress(Bitmap.CompressFormat.JPEG, 100, fos);


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return directory.getAbsolutePath();
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


}

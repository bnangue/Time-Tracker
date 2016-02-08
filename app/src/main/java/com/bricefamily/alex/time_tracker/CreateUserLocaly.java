package com.bricefamily.alex.time_tracker;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by bricenangue on 31.01.16.
 */
public class CreateUserLocaly {

    private Context context;
    FileHelper helper;
    String username;

    public CreateUserLocaly(Context context){
        this.context=context;
    }

    void creatUserFolder(){

        //create Folders if they dont exist
        File accountFolderToCreate = new File(helper.getUserFolder(username));
        if (!accountFolderToCreate.exists())
        {
            accountFolderToCreate.mkdir();
            try{

                File detailsFile = new File(helper.getUserFolder(username) + "/details");
                if(!detailsFile.exists()){
                    detailsFile.createNewFile();

                    FileOutputStream outputStream=new FileOutputStream(detailsFile,true);
                    outputStream.write(String.valueOf("give the data here ").getBytes());
                    outputStream.close();
                }else{
                    FileOutputStream outputStream=new FileOutputStream(detailsFile,true);
                    outputStream.write(String.valueOf(" give the data here").getBytes());
                    outputStream.close();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            try{

                File detailsFile = new File(helper.getUserFolder(username) + "/details");
                if(!detailsFile.exists()){
                    detailsFile.createNewFile();

                    FileOutputStream outputStream=new FileOutputStream(detailsFile,true);
                    outputStream.write(String.valueOf("give the data here ").getBytes());
                    outputStream.close();
                }else{
                    FileOutputStream outputStream=new FileOutputStream(detailsFile,true);
                    outputStream.write(String.valueOf(" give the data here").getBytes());
                    outputStream.close();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}

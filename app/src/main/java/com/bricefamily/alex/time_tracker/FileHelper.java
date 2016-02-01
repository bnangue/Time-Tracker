package com.bricefamily.alex.time_tracker;


import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileHelper {

    //Klasse stellt sätmliche Ordnerpfade bereit


    Context newcontext;

    public FileHelper(Context context)
    {
        newcontext = context;
    }


    //Data/Data/e-Sign/Files
    String getFilesDirectory ()
    {
        return newcontext.getFilesDir().getPath();
    }

    String getFileFromFilesDirectory (String fileName)
    {
        return  getFilesDirectory() + "/" + fileName;
    }

    //Data/Data/e-Sign/Files/Users
    String getUserFolder ()
    {
        return  (getFilesDirectory() + "/user");
    }

    String getPinFile(){

        return (getFilesDirectory() + "/pin");
    }

    //z.B. Data/Data/e-Sign/Files/Users/Max Mustermann
    String getUserFolder (String username)
    {
        return (getUserFolder() + "/" + username);
    }


    //z.B. Data/Data/e-Sign/Files/Users/Max Mustermann/Config.zip
    String getFileFromUserFolder (String username, String filename)
    {
        return (getUserFolder(username) + "/" + filename);
    }

    void deleteRecursive(File fileOrDirectory)
    {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    //helper um die entpackten .config Dateien zu finden
    public String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

//        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
//        }
        return ext;
    }




    public String getLineFromFile(String filename, String stringToLookFor) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            filename = "config.e-Sign.cfg";
            InputStream is = newcontext.openFileInput(filename);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ( (str = in.readLine()) != null ) {


                if (str.contains(stringToLookFor))
                {
                    return str;
                }
            }
            return buf.toString();
        } catch (IOException e) {

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.w("awesome Error", e.getMessage());
                }
            }
        }

        return null;
    }
    public String getreadFile(String filename) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            filename = "config.e-Sign.cfg";
            InputStream is = newcontext.openFileInput(filename);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ( (str = in.readLine()) != null ) {
                buf.append(str).append(" ");
            }
            return buf.toString();
        } catch (IOException e) {

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.w("awesome Error", e.getMessage());
                }
            }
        }

        return null;
    }
}

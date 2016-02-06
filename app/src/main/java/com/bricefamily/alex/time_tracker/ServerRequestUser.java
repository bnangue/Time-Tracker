package com.bricefamily.alex.time_tracker;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
/**
 * Created by bricenangue on 05/02/16.
 */
public class ServerRequestUser {
    ProgressDialog progressDialog;
    private static  int CONNECTION_TIMEOUT=ServerRequest.CONNECTION_TIMEOUT;
     private String SERVER_ADDRESS=ServerRequest.SERVER_ADDRESS;

    private String time;

    public ServerRequestUser(Context context){
        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("please wait.");
    }

    public  void updtaestatus(User user , GetUserCallbacks callbacks){
        progressDialog.setTitle("Logging...");
        progressDialog.show();
        new UpdateUserStatusAsynckTacks(user ,callbacks).execute();
    }
    public void fetchallUsers(GetUserCallbacks callbacks){
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        new FetchAllUsersAsynckTacks(callbacks).execute();
    }
    public void deleteAlleventfromUser(User user , GetEventsCallbacks callbacks){
        progressDialog.setTitle("Deleting all your records...");
        progressDialog.show();
        new DeleteAllEventsOnAccountDeletedAsynckTasks(user,callbacks).execute();
    }
    public void deleteUser(User user,GetUserCallbacks callbacks){
        progressDialog.setTitle("Deleting all your data...");

        progressDialog.show();
        new DeleteUserAsynckTasks(user,callbacks).execute();
    }
    public  void updateUserPicture(UserProfilePicture userProfilePicture,GetImageCallBacks callBacks){
        progressDialog.setTitle("Updating your profile picture...");

        progressDialog.show();
        new UpdateUserPicturesAsynckTacks(userProfilePicture,callBacks).execute();
    }
    public void fetchUserPicture(UserProfilePicture userProfilePicture,GetImageCallBacks callBacks){
        progressDialog.setTitle("Downloading data...");

        progressDialog.show();
        new FetchUserPictureAsynckTacks(userProfilePicture,callBacks).execute();

    }


    public  void saveprofilepicture(UserProfilePicture userProfilePicture,GetImageCallBacks callBacks){
        progressDialog.setTitle("Saving your Profile picture...");

        progressDialog.show();
        new StoreProfilePicturesAsynckTacks(userProfilePicture,callBacks).execute();
    }

    public void storeUserDataInBackground(User user,GetUserCallbacks callbacks){
        progressDialog.setTitle("Creating account...");

        progressDialog.show();
        new StoreUserDataAsynckTacks(user,callbacks).execute();
    }
    public void fetchUserDataInBackground(User user ,GetUserCallbacks callbacks){
        progressDialog.setTitle("Logging user...");

        progressDialog.show();
        new FetchUserDataAsynckTacks(user,callbacks).execute();
    }




    public class StoreUserDataAsynckTacks extends AsyncTask<Void,Void,Void>{

        User user;
        GetUserCallbacks userCallbacks;

        public  StoreUserDataAsynckTacks(User user ,GetUserCallbacks callbacks){
            this.user=user;
            this.userCallbacks=callbacks;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            userCallbacks.done(null);
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<Pair<String,String>> data=new ArrayList<>();
            data.add(new Pair<String, String>("email", user.email));
            data.add(new Pair<String, String>("username", user.username));
            data.add(new Pair<String, String>("password",user.password));

            URL url;
            HttpURLConnection urlConnection=null;
            try {

                byte[] postData= getData(data).getBytes("UTF-8");
                url=new URL(SERVER_ADDRESS + "Register.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postData);

                Reader reader= new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
                StringBuilder bld =new StringBuilder();
                String line=null;
                for(int c=reader.read();c!=-1;c=reader.read()){
                    bld.append(c);
                }

                line=bld.toString();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
    private String getData(ArrayList<Pair<String,String>> values) throws UnsupportedEncodingException{
        StringBuilder result=new StringBuilder();
        for(Pair<String,String> pair : values){

            if(result.length()!=0)

                result.append("&");
            result.append(URLEncoder.encode(pair.first, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.second, "UTF-8"));

        }
        return result.toString();
    }

    public class FetchUserDataAsynckTacks extends AsyncTask<Void,Void,User> {
        User user;
        GetUserCallbacks userCallbacks;

        public FetchUserDataAsynckTacks(User user, GetUserCallbacks callbacks) {
            this.user = user;
            this.userCallbacks = callbacks;
        }

        @Override
        protected void onPostExecute(User returneduser) {
            progressDialog.dismiss();
            userCallbacks.done(returneduser);
            super.onPostExecute(returneduser);
        }

        @Override
        protected User doInBackground(Void... params) {

            User returneduser=null;
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(SERVER_ADDRESS + "FetchUserData.php");
                urlConnection=(HttpURLConnection)url.openConnection();
//                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
//                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                OutputStream out=urlConnection.getOutputStream();
                BufferedWriter buff=new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
                String data =URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(user.email,"UTF-8")+"&"+
                        URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(String.valueOf(user.password.hashCode()),"UTF-8");
                buff.write(data);
                buff.flush();
                buff.close();
                out.close();

                InputStream in =urlConnection.getInputStream();
                String respons="";
                StringBuilder bi=new StringBuilder();
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                String line;
                while((line=reader.readLine())!=null){
                    bi.append(line).append("\n");
                }
                reader.close();
                in.close();

                respons =bi.toString();
                JSONObject jsonObject= new JSONObject(respons);
                if(jsonObject.length()==0){
                    returneduser=null;
                }else {
                    String username=null;
                    if(jsonObject.has("username")){
                        username=jsonObject.getString("username");
                        returneduser=new User(username,user.email,user.password);
                    }

                }

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return returneduser;
        }


    }


    //Picture

    public class StoreProfilePicturesAsynckTacks extends AsyncTask<Void,Void,String>{

        UserProfilePicture profilePicture;
        GetImageCallBacks imageCallBacks;

        public StoreProfilePicturesAsynckTacks(UserProfilePicture profilePicture, GetImageCallBacks callbacks){
            this.imageCallBacks=callbacks;
            this.profilePicture=profilePicture;
        }
        @Override
        protected void onPostExecute(String reponse) {
            progressDialog.dismiss();
            imageCallBacks.done(reponse);
            super.onPostExecute(reponse);
        }

        @Override
        protected String doInBackground(Void... params) {

            Bitmap bitmap=profilePicture.uProfilePicture;

            String uploadimage=getStringImage(bitmap);
            String uname=profilePicture.username;

            ArrayList<Pair<String,String>> data=new ArrayList<>();
            data.add(new Pair<String, String>("username", uname));
            data.add(new Pair<String, String>("Image", uploadimage));

            // String data = "{ image_data: \"" + uploadimage.toString() + "\", uploadedBy: \"1\" }";
            String line="";
            URL url;
            HttpURLConnection urlConnection;
            try {

                //String dataPosted=getData(data);
                byte[] postData= getData(data).getBytes("UTF-8");
                url=new URL(SERVER_ADDRESS + "Upload.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postData);
                //  BufferedWriter buff=new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
                //String data =URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(profilePicture.username,"UTF-8")+"&"+
                //        URLEncoder.encode("Image","UTF-8")+"="+URLEncoder.encode(String.valueOf(uploadimage),"UTF-8");
                //// buff.flush();
                // buff.close();
                urlConnection.getOutputStream().close();

                int responsecode=urlConnection.getResponseCode();
                if(responsecode==HttpURLConnection.HTTP_OK){
                    InputStream in =urlConnection.getInputStream();

                    BufferedReader reader= new BufferedReader(new InputStreamReader(in));
                    StringBuilder bld =new StringBuilder();
                    String il;
                    while((il=reader.readLine())!=null){
                        bld.append(il);
                    }
                    line=bld.toString();
                }else{
                    line="Error";
                }



            }catch (Exception e){
                e.printStackTrace();
            }
            return line;
        }
    }


    public class FetchUserPictureAsynckTacks extends AsyncTask<Void,Void,UserProfilePicture> {

        UserProfilePicture userProfilePicture;
        GetImageCallBacks imageCallBacks;


        public FetchUserPictureAsynckTacks(UserProfilePicture userProfilePicture , GetImageCallBacks callbacks) {
            this.imageCallBacks = callbacks;
            this.userProfilePicture=userProfilePicture;
        }

        @Override
        protected void onPostExecute(UserProfilePicture returned) {
            progressDialog.dismiss();
            imageCallBacks.image(returned);
            super.onPostExecute(returned);
        }

        @Override
        protected UserProfilePicture doInBackground(Void... params) {

            UserProfilePicture returned = null;
            URL url;
            HttpURLConnection urlConnection=null;
            try {

                url=new URL(SERVER_ADDRESS+ "FetchProfilePictures.php");
                urlConnection=(HttpURLConnection)url.openConnection();
//                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
//                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                OutputStream out=urlConnection.getOutputStream();
                BufferedWriter buff=new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
                String data =URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(userProfilePicture.username,"UTF-8");
                buff.write(data);
                buff.flush();
                buff.close();
                out.close();


                InputStream in =urlConnection.getInputStream();
                String respons="";
                StringBuilder bi=new StringBuilder();
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                String line;
                while((line=reader.readLine())!=null){
                    bi.append(line).append("\n");
                }
                reader.close();
                in.close();

                respons =bi.toString();
                JSONObject jsonObject= new JSONObject(respons);

                if(jsonObject.length()==0){
                    returned=null;
                }else {
                    String username=null;
                    Bitmap bitmap=null;
                    if(jsonObject.has("image")){
                        String imgString=jsonObject.getString("image");
                        bitmap=decodeBase64(imgString);
                    }

                    if(jsonObject.has("username")){
                        username=jsonObject.getString("username");
                    }
                    returned=new UserProfilePicture(username,bitmap);
                }

                // fetch data to a jason object
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }

            return returned;
        }


    }



    public class UpdateUserPicturesAsynckTacks extends AsyncTask<Void,Void,String>{

        UserProfilePicture profilePicture;
        GetImageCallBacks imageCallBacks;

        public UpdateUserPicturesAsynckTacks(UserProfilePicture profilePicture, GetImageCallBacks callbacks){
            this.imageCallBacks=callbacks;
            this.profilePicture=profilePicture;
        }
        @Override
        protected void onPostExecute(String reponse) {
            progressDialog.dismiss();
            imageCallBacks.done(reponse);
            super.onPostExecute(reponse);
        }

        @Override
        protected String doInBackground(Void... params) {

            Bitmap bitmap=profilePicture.uProfilePicture;

            String uploadimage=getStringImage(bitmap);
            String uname=profilePicture.username;

            ArrayList<Pair<String,String>> data=new ArrayList<>();
            data.add(new Pair<String, String>("username", uname));
            data.add(new Pair<String, String>("Image", uploadimage));

            // String data = "{ image_data: \"" + uploadimage.toString() + "\", uploadedBy: \"1\" }";
            String line="";
            URL url;
            HttpURLConnection urlConnection;
            try {

                //String dataPosted=getData(data);
                byte[] postData= getData(data).getBytes("UTF-8");
                url=new URL(SERVER_ADDRESS + "UpdatePictures.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postData);
                //  BufferedWriter buff=new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
                //String data =URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(profilePicture.username,"UTF-8")+"&"+
                //        URLEncoder.encode("Image","UTF-8")+"="+URLEncoder.encode(String.valueOf(uploadimage),"UTF-8");
                //// buff.flush();
                // buff.close();
                urlConnection.getOutputStream().close();

                int responsecode=urlConnection.getResponseCode();
                if(responsecode==HttpURLConnection.HTTP_OK){
                    InputStream in =urlConnection.getInputStream();

                    BufferedReader reader= new BufferedReader(new InputStreamReader(in));
                    StringBuilder bld =new StringBuilder();
                    String il;
                    while((il=reader.readLine())!=null){
                        bld.append(il);
                    }
                    line=bld.toString();
                }else{
                    line="Error";
                }



            }catch (Exception e){
                e.printStackTrace();
            }
            return line;
        }
    }


    public class DeleteUserAsynckTasks extends AsyncTask<Void,Void,String>{

        User user;
        GetUserCallbacks userCallbacks;

        public DeleteUserAsynckTasks(User user, GetUserCallbacks callbacks){
            this.user=user;
            this.userCallbacks=callbacks;
        }
        @Override
        protected void onPostExecute(String aVoid) {
            progressDialog.dismiss();
            userCallbacks.deleted(aVoid);
            super.onPostExecute(aVoid);
        }

        @Override
        protected String doInBackground(Void... params) {


            URL url;
            String line=null;
            HttpURLConnection urlConnection=null;
            try {

                url=new URL(SERVER_ADDRESS + "DeleteUser.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                OutputStream out=urlConnection.getOutputStream();
                BufferedWriter buff=new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
                String data =URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(user.username,"UTF-8");
                buff.write(data);
                buff.flush();
                buff.close();
                out.close();
                urlConnection.getOutputStream().close();
                int responsecode=urlConnection.getResponseCode();
                if(responsecode==HttpURLConnection.HTTP_OK){
                    InputStream in =urlConnection.getInputStream();

                    BufferedReader reader= new BufferedReader(new InputStreamReader(in));
                    StringBuilder bld =new StringBuilder();
                    String il;
                    while((il=reader.readLine())!=null){
                        bld.append(il);
                    }
                    line=bld.toString();
                }else{
                    line="Error";
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return line;
        }
    }


    public class UpdateUserStatusAsynckTacks extends AsyncTask<Void,Void,String>
    {

        User user;
        GetUserCallbacks getUserCallbacks;

        public UpdateUserStatusAsynckTacks(User user, GetUserCallbacks callbacks){
            this.getUserCallbacks=callbacks;
            this.user=user;
        }
        @Override
        protected void onPostExecute(String reponse) {
            progressDialog.dismiss();
            getUserCallbacks.deleted(reponse);
            super.onPostExecute(reponse);
        }

        @Override
        protected String doInBackground(Void... params) {

            String line="";
            URL url;
            HttpURLConnection urlConnection;
            try {
                url=new URL(SERVER_ADDRESS + "UpdateOnlineStatus.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                OutputStream out=urlConnection.getOutputStream();
                BufferedWriter buff=new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
                String data =URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(user.email,"UTF-8")+"&"+
                        URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(String.valueOf(user.password.hashCode()),"UTF-8")
                        +"&"+
                        URLEncoder.encode("onlineStatus","UTF-8")+"="+URLEncoder.encode(String.valueOf(user.status),"UTF-8");
                buff.write(data);
                buff.flush();
                buff.close();
                out.close();

                int responsecode=urlConnection.getResponseCode();
                if(responsecode==HttpURLConnection.HTTP_OK){
                    InputStream in =urlConnection.getInputStream();

                    BufferedReader reader= new BufferedReader(new InputStreamReader(in));
                    StringBuilder bld =new StringBuilder();
                    String il;
                    while((il=reader.readLine())!=null){
                        bld.append(il);
                    }
                    line=bld.toString();
                }else{
                    line="Error";
                }



            }catch (Exception e){
                e.printStackTrace();
            }
            return line;
        }
    }

    public String getStringImage(Bitmap bmp){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String temp=Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return temp;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
    public static String encodeTobase64(Bitmap image)
    {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);

        Log.e("LOOK", imageEncoded);
        return imageEncoded;
    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public class DeleteAllEventsOnAccountDeletedAsynckTasks extends AsyncTask<Void,Void,String>{

        User user;
        GetEventsCallbacks eventsCallbacks;

        public DeleteAllEventsOnAccountDeletedAsynckTasks(User user, GetEventsCallbacks callbacks){
            this.eventsCallbacks=callbacks;
            this.user=user;
        }
        @Override
        protected void onPostExecute(String aVoid) {
            progressDialog.dismiss();
            eventsCallbacks.updated(aVoid);
            super.onPostExecute(aVoid);
        }

        @Override
        protected String doInBackground(Void... params) {

            ArrayList<Pair<String,String>> data=new ArrayList<>();

            data.add(new Pair<String, String>("eventCreator", user.username));



            URL url;
            String line=null;
            HttpURLConnection urlConnection=null;
            try {

                byte[] postData= getData(data).getBytes("UTF-8");
                url=new URL(SERVER_ADDRESS + "DeleteEventsOnAccountDeleted.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postData);

                urlConnection.getOutputStream().close();
                int responsecode=urlConnection.getResponseCode();
                if(responsecode==HttpURLConnection.HTTP_OK){
                    InputStream in =urlConnection.getInputStream();

                    BufferedReader reader= new BufferedReader(new InputStreamReader(in));
                    StringBuilder bld =new StringBuilder();
                    String il;
                    while((il=reader.readLine())!=null){
                        bld.append(il);
                    }
                    line=bld.toString();
                }else{
                    line="Error";
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return line;
        }
    }


    public class FetchAllUsersAsynckTacks extends AsyncTask<Void,Void,ArrayList<User>> {

        GetUserCallbacks userCallbacks;


        public FetchAllUsersAsynckTacks(GetUserCallbacks callbacks) {
            this.userCallbacks = callbacks;
        }

        @Override
        protected void onPostExecute(ArrayList<User> returnedevents) {
            progressDialog.dismiss();
            userCallbacks.userlist(returnedevents);
            super.onPostExecute(returnedevents);
        }

        @Override
        protected ArrayList<User> doInBackground(Void... params) {

            ArrayList<User> returnedEvents=new ArrayList<>();
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(SERVER_ADDRESS + "FetchAllUserAndPictures.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);


                InputStream in =urlConnection.getInputStream();
                String respons="";
                StringBuilder bi=new StringBuilder();
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                String line;
                while((line=reader.readLine())!=null){
                    bi.append(line).append("\n");
                }
                reader.close();
                in.close();

                respons =bi.toString();
                JSONArray jsonArray= new JSONArray(respons);
                returnedEvents= getDetails(jsonArray);


                // fetch data to a jason object
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }

            return returnedEvents;
        }


    }

    public ArrayList<User> getDetails(JSONArray jsonArray){
        ArrayList<User> events=new ArrayList<>();

        try {


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo_inside = jsonArray.getJSONObject(i);

                String username = jo_inside.getString("username");
                String email = jo_inside.getString("email");
                String password = jo_inside.getString("password");
                String firstname = jo_inside.getString("firstname");
                String lastname = jo_inside.getString("lastname");
                int  status = jo_inside.getInt("onlineStatus");


                User  object =new User(username, email, password, firstname,
                        lastname,status);

                events.add(object);


            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;

    }

    private int getstatusInteger(boolean status){
        if(status){
            return 1;

        }else {
            return 0;
        }
    }
}

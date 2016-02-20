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


    public void updatefriendFriendList(User user, GetUserCallbacks callbacks){
        progressDialog.setTitle("removing "+user.username+" your friend list...");
        progressDialog.show();
        new UpdatefriendFriendListAsynckTacks(user,callbacks).execute();
    }
    public void updateFriendList(User user, GetUserCallbacks callbacks){
        new UpdateUserFriendListAsynckTacks(user,callbacks).execute();
    }
    public void fetchallUserForGcm(User user, GetUserCallbacks callbacks){
        progressDialog.setTitle("Loading your friend list...");
        progressDialog.show();
        new FetchAllUserForGcmAsynckTacks(user,callbacks).execute();
    }
    public void storeUserGcmIds(User user, GetUserCallbacks callbacks){

        new StoreUserGCMIdsAsynckTacks(user,callbacks).execute();
    }
    public void fetchUserGcmRegid(User user, GetUserCallbacks callbacks){
        new FetchUserGcmRegIdAsynckTacks(user, callbacks).execute();
    }
    public  void updtaestatus(User user , GetUserCallbacks callbacks){
        progressDialog.setTitle("Logging...");
        progressDialog.show();
        new UpdateUserStatusAsynckTacks(user ,callbacks).execute();
    }
    public void fetchallUsers(User user,GetUserCallbacks callbacks){
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        new FetchAllUsersAsynckTacks(user,callbacks).execute();
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
            }finally {
                assert urlConnection != null;
                urlConnection.disconnect();
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
                url=new URL(SERVER_ADDRESS + "LoggingUserIn.php");
                urlConnection=(HttpURLConnection)url.openConnection();
//                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
//                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
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

                JSONObject jsonObject= jsonArray.getJSONObject(0);
                if(jsonObject.length()==0){
                    returneduser=null;
                }else {
                    String username=null;
                    if(jsonObject.has("username")){
                        String regId=jsonObject.getString("gcm_regid");
                        username=jsonObject.getString("username");
                       String friendlist=jsonObject.getString("friendList");
                        int stat=jsonObject.getInt("onlineStatus");
                        returneduser=new User(username,user.email,user.password,null,null,stat,regId,null,friendlist);
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
            HttpURLConnection urlConnection = null;
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
            }finally {
                assert urlConnection != null;
                urlConnection.disconnect();
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
                JSONArray jsonArray=new JSONArray(respons);
                JSONObject jsonObject= jsonArray.getJSONObject(0);

                if(jsonObject.length()==0){
                    returned=null;
                }else {
                    String username=null;
                    Bitmap bitmap=null;
                    if(jsonObject.has("Image")){
                        String imgString=jsonObject.getString("Image");
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
            HttpURLConnection urlConnection = null;
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
            }finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }
            return line;
        }
    }


    public class UpdateUserFriendListAsynckTacks extends AsyncTask<Void,Void,String>
    {

        User user;
        GetUserCallbacks getUserCallbacks;

        public UpdateUserFriendListAsynckTacks(User user, GetUserCallbacks callbacks){
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
            HttpURLConnection urlConnection = null;
            try {
                url=new URL(SERVER_ADDRESS + "UpdateUserFriendsList.php");
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
                        URLEncoder.encode("friendList","UTF-8")+"="+URLEncoder.encode(user.friendlist,"UTF-8");
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
            }finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }
            return line;
        }
    }
    public class FetchUserGcmRegIdAsynckTacks extends AsyncTask<Void,Void,User> {
        User user;
        GetUserCallbacks userCallbacks;

        public FetchUserGcmRegIdAsynckTacks(User user, GetUserCallbacks callbacks) {
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
                url=new URL(SERVER_ADDRESS + "FetchUserRegID.php");
                urlConnection=(HttpURLConnection)url.openConnection();
//                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
//                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                OutputStream out=urlConnection.getOutputStream();
                BufferedWriter buff=new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
                String data =URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(user.username,"UTF-8")+"&"+
                        URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(user.email,"UTF-8");
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
                        String regid=jsonObject.getString("gcm_regid");
                        returneduser=new User(regid,username,user.email,null);
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
            }finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }
            return line;
        }
    }


    public class FetchAllUsersAsynckTacks extends AsyncTask<Void,Void,ArrayList<User>> {

        GetUserCallbacks userCallbacks;
        User user;


        public FetchAllUsersAsynckTacks(User user,GetUserCallbacks callbacks) {
            this.userCallbacks = callbacks;
            this.user=user;
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
                String regId = jo_inside.getString("gcm_regid");
                String imgString=jo_inside.getString("Image");
                Bitmap bitmap=decodeBase64(imgString);
                String friendlist=jo_inside.getString("friendList");


                User  object =new User(username, email, password, firstname,
                        lastname,status,regId,bitmap,friendlist);

                events.add(object);


            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;

    }

    public Bitmap decodeToBitmap(byte[] decodeByte){
        return BitmapFactory.decodeByteArray(decodeByte, 0, decodeByte.length);
    }
    public class StoreUserGCMIdsAsynckTacks extends AsyncTask<Void,Void,String>{

        User user;
        GetUserCallbacks userCallbacks;

        public StoreUserGCMIdsAsynckTacks(User user, GetUserCallbacks callbacks){
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

            ArrayList<Pair<String,String>> data=new ArrayList<>();
            data.add(new Pair<String, String>("gcm_regid", user.regId));
            data.add(new Pair<String, String>("username", user.username));
            data.add(new Pair<String, String>("email",user.email));

            String repomse=null;
            URL url;
            HttpURLConnection urlConnection=null;
            try {

                byte[] postData= getDataIds(data).getBytes("UTF-8");
                url=new URL(SERVER_ADDRESS + "RegisterUserForGCM.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postData);

                InputStream in =urlConnection.getInputStream();
                BufferedReader reader= new BufferedReader(new InputStreamReader(in));
                StringBuilder bld =new StringBuilder();
                String il;
                while((il=reader.readLine())!=null){
                    bld.append(il);
                }
                repomse=bld.toString();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(urlConnection!=null){
                    urlConnection.disconnect();
                }
            }
            return repomse;
        }
    }
    private String getDataIds(ArrayList<Pair<String,String>> values) throws UnsupportedEncodingException{
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


    public class FetchAllUserForGcmAsynckTacks extends AsyncTask<Void,Void,ArrayList<User>> {
        User user;
        GetUserCallbacks userCallbacks;


        public FetchAllUserForGcmAsynckTacks(User user,GetUserCallbacks callbacks) {
            this.userCallbacks = callbacks;
            this.user=user;
        }

        @Override
        protected void onPostExecute(ArrayList<User> returnedusres) {
            progressDialog.dismiss();
            userCallbacks.userlist(returnedusres);
            super.onPostExecute(returnedusres);
        }

        @Override
        protected ArrayList<User> doInBackground(Void... params) {

            ArrayList<User> returnedusres=new ArrayList<>();
            URL url;
            String username= user.username;
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(SERVER_ADDRESS + "SelectUserForGCM.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                OutputStream out=urlConnection.getOutputStream();
                BufferedWriter buff=new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
                String data =URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(username,"UTF-8");
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
                JSONArray jsonArray= new JSONArray(respons);
                returnedusres= getAllIds(jsonArray);


                // fetch data to a jason object
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }

            return returnedusres;
        }


    }

    public ArrayList<User> getAllIds(JSONArray jsonArray){
        ArrayList<User> usersids=new ArrayList<>();

        try {


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo_inside = jsonArray.getJSONObject(i);

                String regid = jo_inside.getString("gcm_regid");
                String username = jo_inside.getString("username");
                String email = jo_inside.getString("email");

                User  object =new User(regid, username, email,null);

                usersids.add(object);


            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return usersids;

    }



    public class UpdatefriendFriendListAsynckTacks extends AsyncTask<Void,Void,String>
    {

        User user;
        GetUserCallbacks getUserCallbacks;

        public UpdatefriendFriendListAsynckTacks(User user, GetUserCallbacks callbacks){
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
            HttpURLConnection urlConnection = null;
            try {
                url=new URL(SERVER_ADDRESS + "UpdateUserFriendsList.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                OutputStream out=urlConnection.getOutputStream();
                BufferedWriter buff=new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
                String data =URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(user.email,"UTF-8")+"&"+
                        URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(user.password,"UTF-8")
                        +"&"+
                        URLEncoder.encode("friendList","UTF-8")+"="+URLEncoder.encode(user.friendlist,"UTF-8");
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
            }finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }
            return line;
        }
    }

    public void fetchUsertoUpdateFriend(String friendusername,String myusername, GetUserCallbacks callbacks){
        new FetchUsertoUpdateFriendListAsynckTacks(friendusername,myusername,callbacks).execute();
    }
    public class FetchUsertoUpdateFriendListAsynckTacks extends AsyncTask<Void,Void,User> {
        String username;
        String myusername;
        GetUserCallbacks userCallbacks;


        public FetchUsertoUpdateFriendListAsynckTacks(String friendusername,String myusername, GetUserCallbacks callbacks) {
            this.userCallbacks = callbacks;
            this.username=friendusername;
            this.myusername=myusername;
        }

        @Override
        protected void onPostExecute(User returnedusres) {
            progressDialog.dismiss();
            userCallbacks.done(returnedusres);
            if(returnedusres.friendlist.isEmpty()||returnedusres.friendlist==null){
                User user=new User(returnedusres.username,returnedusres.email,returnedusres.password,null,null,returnedusres.status,returnedusres.regId,returnedusres.picture,myusername);
                new UpdatefriendFriendListAsynckTacks(user,userCallbacks).execute();
            }else {
                StringBuilder b= new StringBuilder(returnedusres.friendlist).append(",").append(myusername);
                User user=new User(returnedusres.username,returnedusres.email,returnedusres.password,null,null,returnedusres.status,returnedusres.regId,returnedusres.picture,b.toString());
                new UpdatefriendFriendListAsynckTacks(user,userCallbacks).execute();
            }

            super.onPostExecute(returnedusres);
        }

        @Override
        protected User doInBackground(Void... params) {

            User returnedusres=null;
            URL url;

            HttpURLConnection urlConnection=null;
            try {
                url=new URL(SERVER_ADDRESS + "FetchUserToUpdateFriendList.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                OutputStream out=urlConnection.getOutputStream();
                BufferedWriter buff=new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
                String data =URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(username,"UTF-8");
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
                JSONArray jsonArray= new JSONArray(respons);
                JSONObject jsonObject=jsonArray.getJSONObject(0);
                if(jsonObject.length()==0){
                    returnedusres=null;
                }else {
                    String username=null;
                    if(jsonObject.has("username")){
                        String regId=jsonObject.getString("gcm_regid");
                        username=jsonObject.getString("username");
                        String friendlist=jsonObject.getString("friendList");
                        String email=jsonObject.getString("email");
                        String hashpassword=jsonObject.getString("password");
                        int stat=jsonObject.getInt("onlineStatus");
                        returnedusres=new User(username,email,hashpassword,null,null,stat,regId,null,friendlist);
                    }

                }


                // fetch data to a jason object
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }

            return returnedusres;
        }


    }

}

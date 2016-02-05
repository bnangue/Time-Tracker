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
 * Created by alex on 17.01.2016.
 */
public class ServerRequest {
    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT=1000*15;
    public static final String SERVER_ADDRESS="http://time-tracker.comlu.com/";

    private String time;

    public ServerRequest(Context context){
        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("please wait.");
    }

    public void fetchcurrentevent(EventObject eventObject,GetEventsCallbacks callbacks){
        progressDialog.show();
        new FetchCurrentEventAsynckTacks(eventObject,callbacks);

    }
    public void updateEvents(EventObject eventObject,GetEventsCallbacks callBacks,String hash){
        progressDialog.show();
        new UpdateEventsAsynckTacks(eventObject,callBacks,hash).execute();
    }

    public  void updateUserPicture(UserProfilePicture userProfilePicture,GetImageCallBacks callBacks){
        progressDialog.show();
        new UpdateUserPicturesAsynckTacks(userProfilePicture,callBacks).execute();
    }
    public void fetchUserPicture(UserProfilePicture userProfilePicture,GetImageCallBacks callBacks){
        progressDialog.show();
        new FetchUserPictureAsynckTacks(userProfilePicture,callBacks).execute();

    }
    public  void saveprofilepicture(UserProfilePicture userProfilePicture,GetImageCallBacks callBacks){
        progressDialog.show();
        new StoreProfilePicturesAsynckTacks(userProfilePicture,callBacks).execute();
    }
    public void fetchAllevents(GetEventsCallbacks callbacks){
        progressDialog.show();
        new FetchAllEventsAsynckTacks(callbacks).execute();
    }
    public void createEventinBackground(EventObject eventObject, GetEventsCallbacks callbacks){
        progressDialog.show();
        new StoreEventsAsynckTacks(eventObject,callbacks).execute();

    }
    public void storeUserDataInBackground(User user,GetUserCallbacks callbacks){
        progressDialog.show();
        new StoreUserDataAsynckTacks(user,callbacks).execute();
    }
    public void fetchUserDataInBackground(User user ,GetUserCallbacks callbacks){
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


    public class CreateTableForUserAsynckTask extends AsyncTask<Void,Void,String>{

        User user;

        public CreateTableForUserAsynckTask(User user){
            this.user=user;
        }
        @Override
        protected void onPostExecute(String aVoid) {
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }

        @Override
        protected String doInBackground(Void... params) {
            URL url;
            HttpURLConnection urlConnection=null;
            String reponse=null;

            try {

                url=new URL(SERVER_ADDRESS + "CreateTableTest.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                String data =URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(user.username,"UTF-8");
                OutputStream out=urlConnection.getOutputStream();
                BufferedWriter buff=new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
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
                    bi.append(line + "\n");
                }
                reader.close();
                in.close();

                respons =bi.toString();

                reponse=respons;
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                urlConnection.disconnect();
            }
            return reponse;
        }
    }


    public class StoreEventsAsynckTacks extends AsyncTask<Void,Void,Void>{

        EventObject eventObject;
        GetEventsCallbacks eventsCallbacks;

        public StoreEventsAsynckTacks( EventObject eventObject,GetEventsCallbacks callbacks){
            this.eventsCallbacks=callbacks;
            this.eventObject=eventObject;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            eventsCallbacks.done(null);
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<Pair<String,String>> data=new ArrayList<>();
            data.add(new Pair<String, String>("eventTitel", eventObject.titel));
            data.add(new Pair<String, String>("eventDetails",eventObject.infotext));
            data.add(new Pair<String, String>("eventCreator", eventObject.creator));
            data.add(new Pair<String, String>("eventDay", eventObject.eDay));
            data.add(new Pair<String, String>("eventMonth", eventObject.eMonth));
            data.add(new Pair<String, String>("eventYear",eventObject.eYear));
            data.add(new Pair<String, String>("eventStatus",eventObject.eventStatus));
            data.add(new Pair<String, String>("eventHash",eventObject.eventHash));


            URL url;
            HttpURLConnection urlConnection=null;
            try {

                byte[] postData= getData(data).getBytes("UTF-8");
                url=new URL(SERVER_ADDRESS + "CreateEvent.php");
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


    public class FetchAllEventsAsynckTacks extends AsyncTask<Void,Void,ArrayList<EventObject>> {

        GetEventsCallbacks eventsCallbacks;


        public FetchAllEventsAsynckTacks( GetEventsCallbacks callbacks) {
            this.eventsCallbacks = callbacks;
        }

        @Override
        protected void onPostExecute(ArrayList<EventObject> returnedevents) {
            progressDialog.dismiss();
            eventsCallbacks.done(returnedevents);
            super.onPostExecute(returnedevents);
        }

        @Override
        protected ArrayList<EventObject> doInBackground(Void... params) {

            ArrayList<EventObject> returnedEvents=new ArrayList<>();
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(SERVER_ADDRESS + "FetchAllEvents.php");
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

    public ArrayList<EventObject> getDetails(JSONArray jsonArray){
        ArrayList<EventObject> events=new ArrayList<>();

        try {


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo_inside = jsonArray.getJSONObject(i);

                String titel = jo_inside.getString("eventTitel");
                String infotext = jo_inside.getString("eventDetails");
                String creator = jo_inside.getString("eventCreator");
                String creationTime = jo_inside.getString("eventCreationtime");
                String eDay = jo_inside.getString("eventDay");
                String eMonth = jo_inside.getString("eventMonth");
                String eYear = jo_inside.getString("eventYear");
                String eventStatus = jo_inside.getString("eventStatus");
                String eventHash = jo_inside.getString("eventHash");


                String[] creationtime=creationTime.split(" ");
                DateEventObject dateEventObject=new DateEventObject(eDay,eMonth,eYear);

                EventObject  object =new EventObject(titel, infotext, creator, creationtime[0],
                        dateEventObject, eventStatus,eventHash);
                String t=object.titel;
                String ifo=object.infotext;
                if(t.equals(ifo)){

                }
                events.add(object);


            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;

    }

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

    public class UpdateEventsAsynckTacks extends AsyncTask<Void,Void,String>{

        EventObject eventObject;
        GetEventsCallbacks eventsCallbacks;
        String hash;

        public UpdateEventsAsynckTacks(EventObject eventObject, GetEventsCallbacks callbacks,String hash){
            this.eventsCallbacks=callbacks;
            this.eventObject=eventObject;
            this.hash=hash;
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
            data.add(new Pair<String, String>("eventTitel", eventObject.titel));
            data.add(new Pair<String, String>("eventDetails",eventObject.infotext));
            data.add(new Pair<String, String>("eventCreator", eventObject.creator));
            data.add(new Pair<String, String>("eventDay", eventObject.eDay));
            data.add(new Pair<String, String>("eventMonth", eventObject.eMonth));
            data.add(new Pair<String, String>("eventYear",eventObject.eYear));
            data.add(new Pair<String, String>("eventStatus",eventObject.eventStatus));
            data.add(new Pair<String, String>("eventHash",eventObject.eventHash));
            data.add(new Pair<String, String>("currentHash",hash));



            URL url;
            String line=null;
            HttpURLConnection urlConnection=null;
            try {

                byte[] postData= getData(data).getBytes("UTF-8");
                url=new URL(SERVER_ADDRESS + "UpdateEvents.php");
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

    public class FetchCurrentEventAsynckTacks extends AsyncTask<Void,Void,ArrayList<EventObject>> {

        GetEventsCallbacks eventsCallbacks;

        EventObject eventObject;

        public FetchCurrentEventAsynckTacks(EventObject eventObject, GetEventsCallbacks callbacks) {
            this.eventsCallbacks = callbacks;
            this.eventObject=eventObject;
        }

        @Override
        protected void onPostExecute(ArrayList<EventObject> returnedevents) {
            progressDialog.dismiss();
            eventsCallbacks.done(returnedevents);
            super.onPostExecute(returnedevents);
        }

        @Override
        protected ArrayList<EventObject> doInBackground(Void... params) {

            ArrayList<EventObject> returnedEvents=new ArrayList<>();
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(SERVER_ADDRESS + "FetchEventsDetails.php");
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


}

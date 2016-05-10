package com.bricefamily.alex.time_tracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Base64;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by bricenangue on 03/03/16.
 */
public class ServerRequests {

    private ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT=1000*15;
    public static final String SERVER_ADDRESS="http://timeme.comlu.com/";
    private Context context;
    private MySQLiteHelper mySQLiteHelper;

    public ServerRequests(Context context) {
        this.context=context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        mySQLiteHelper=new MySQLiteHelper(context);
    }

    public void registerUserinBackground(User user, GetUserCallbacks callbacks){
        progressDialog.setTitle("Registring...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new RegisterUserAsyncTask(user,callbacks).execute();
    }

    public void fetchallUserstoSQL(GetUserCallbacks callbacks){
        new FetchAllUserstoSQLAsynckTacks(callbacks).execute();
    }
    public void loggingUserinBackground(User user, GetUserCallbacks callbacks){
        progressDialog.setTitle("Logging...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new LoggingUserAsynckTacks(user,callbacks).execute();
    }

    public void savingEmailAndPassowrdChangedUserinBackground(User currentuser, User newuser,GetUserCallbacks callbacks){
        progressDialog.setTitle("Saving...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new UpdateUserPasswordAndEmailListAsynckTacks(currentuser,newuser,callbacks).execute();
    }

    public void saveCalenderEventInBackgroung(CalendarCollection calendarCollection,GetEventsCallbacks callbacks){
        progressDialog.setTitle("Storing event...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new StoreCalenderEventsAsynckTacks(calendarCollection,callbacks).execute();
    }

    public void getCalenderEventInBackgroung(GetEventsCallbacks callbacks){

        new FetchAllEventsAsynckTacks(callbacks).execute();
    }

    public void getCalenderEventAndUserInBackgroung(GetEventsCallbacks callbacks){
        progressDialog.setTitle("Logging...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new FetchAllEventsAsynckTacks(callbacks).execute();
    }
    public void deleteCalenderEventInBackgroung(CalendarCollection calendarCollection,GetEventsCallbacks callbacks){
        progressDialog.setTitle("Deleting event...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new DeleteEventsAsynckTasks(calendarCollection,callbacks).execute();
    }

    public void logginguserOutInBackgroung(User user,GetUserCallbacks callbacks){
        progressDialog.setTitle("Logging out...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new UpdateUserStatusAsynckTacks(user,callbacks).execute();
    }








    public class RegisterUserAsyncTask extends AsyncTask<Void,Void,String> {

        User user;
        GetUserCallbacks userCallbacks;

        public RegisterUserAsyncTask(User user, GetUserCallbacks callbacks){
            this.user=user;
            this.userCallbacks=callbacks;
        }
        @Override
        protected void onPostExecute(String reponse) {
            progressDialog.dismiss();
            userCallbacks.serverReponse(reponse);
            super.onPostExecute(reponse);
        }

        @Override
        protected String doInBackground(Void... params) {
            String response="";
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(SERVER_ADDRESS + "RegisterUser.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
               urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                OutputStream out=urlConnection.getOutputStream();
                BufferedWriter buff=new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
                String data =URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(user.email,"UTF-8")+"&"+
                        URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(String.valueOf(user.password),"UTF-8")
                        +"&"+
                        URLEncoder.encode("firstname","UTF-8")+"="+URLEncoder.encode(String.valueOf(user.firstname),"UTF-8")
                        +"&"+
                        URLEncoder.encode("lastname","UTF-8")+"="+URLEncoder.encode(String.valueOf(user.lastname),"UTF-8");
                buff.write(data);
                buff.flush();
                buff.close();
                out.close();

                InputStream in =urlConnection.getInputStream();

                StringBuilder bi=new StringBuilder();
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                String line;
                while((line=reader.readLine())!=null){
                    bi.append(line);
                }
                reader.close();
                in.close();

                response =bi.toString();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }
            return response;
        }
    }


    public class LoggingUserAsynckTacks extends AsyncTask<Void,Void,User> {
        User user;
        GetUserCallbacks userCallbacks;

        public LoggingUserAsynckTacks(User user, GetUserCallbacks callbacks) {
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
                url=new URL(SERVER_ADDRESS + "LogUserIn.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                OutputStream out=urlConnection.getOutputStream();
                BufferedWriter buff=new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
                String data =URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(user.email,"UTF-8")+"&"+
                        URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(String.valueOf(user.password),"UTF-8")
                        +"&"+
                        URLEncoder.encode("onlinestatus","UTF-8")+"="+URLEncoder.encode(String.valueOf(user.status),"UTF-8");
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
                    String email=null;
                    if(jsonObject.has("email")){
                        String regId=jsonObject.getString("gcmregid");
                        String firstname=jsonObject.getString("firstname");
                        String lastname=jsonObject.getString("lastname");
                        email=jsonObject.getString("email");
                        String imgString=jsonObject.getString("picture");
                        Bitmap bitmap=decodeBase64(imgString);
                        String friendlist=jsonObject.getString("friendlist");
                        int stat=jsonObject.getInt("onlinestatus");

                        returneduser=new User(email,user.password,firstname,lastname,stat,regId,bitmap,friendlist);
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


    public class FetchAllUserstoSQLAsynckTacks extends AsyncTask<Void,Void,ArrayList<User>> {

        GetUserCallbacks userCallbacks;


        public FetchAllUserstoSQLAsynckTacks(GetUserCallbacks callbacks) {
            this.userCallbacks = callbacks;

        }

        @Override
        protected void onPostExecute(ArrayList<User> returnedevents) {
            userCallbacks.userlist(returnedevents);
            super.onPostExecute(returnedevents);
        }

        @Override
        protected ArrayList<User> doInBackground(Void... params) {

            ArrayList<User> returnedEvents=new ArrayList<>();
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(ServerRequest.SERVER_ADDRESS + "FetchAllUserAndPictures.php");
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
                returnedEvents= getDetailstosql(jsonArray);


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


    public ArrayList<User> getDetailstosql(JSONArray jsonArray){
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


    public ArrayList<User> getDetails(JSONArray jsonArray){
        ArrayList<User> events=new ArrayList<>();

        try {


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo_inside = jsonArray.getJSONObject(i);

                String email = jo_inside.getString("email");
                String password = jo_inside.getString("password");
                String firstname = jo_inside.getString("firstname");
                String lastname = jo_inside.getString("lastname");
                int  status = jo_inside.getInt("onlinestatus");
                String regId = jo_inside.getString("gcmregid");
                String imgString=jo_inside.getString("picture");
                Bitmap bitmap=decodeBase64(imgString);
                String friendlist=jo_inside.getString("friendlist");


                User  object =new User(email, password, firstname,
                        lastname,status,regId,bitmap,friendlist);

                events.add(object);


            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;

    }



    public class UpdateUserPasswordAndEmailListAsynckTacks extends AsyncTask<Void,Void,String>
    {

        User newuser;
        User currentuser;
        GetUserCallbacks getUserCallbacks;

        public UpdateUserPasswordAndEmailListAsynckTacks(User currentuser,User newuser, GetUserCallbacks callbacks){
            this.getUserCallbacks=callbacks;
            this.newuser=newuser;
            this.currentuser=currentuser;
        }
        @Override
        protected void onPostExecute(String reponse) {
            progressDialog.dismiss();
            getUserCallbacks.serverReponse(reponse);
            super.onPostExecute(reponse);
        }

        @Override
        protected String doInBackground(Void... params) {

            String line="";
            String uploadimage;
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                if(newuser.picture!=null){
                    uploadimage=getStringImage(newuser.picture);
                }else {
                    uploadimage="";
                }


                url=new URL(SERVER_ADDRESS + "UpdateEmailAndPass.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                OutputStream out=urlConnection.getOutputStream();
                BufferedWriter buff=new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
                String data =URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(newuser.email,"UTF-8")+"&"+
                        URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(newuser.password,"UTF-8")
                        +"&"+
                        URLEncoder.encode("currentEmail","UTF-8")+"="+URLEncoder.encode(currentuser.email,"UTF-8")
                        +"&"+
                        URLEncoder.encode("currentPassword","UTF-8")+"="+URLEncoder.encode(currentuser.password,"UTF-8")
                        +"&"+
                        URLEncoder.encode("picture","UTF-8")+"="+URLEncoder.encode(uploadimage,"UTF-8")
                        +"&"+
                        URLEncoder.encode("firstname","UTF-8")+"="+URLEncoder.encode(newuser.firstname,"UTF-8")
                        +"&"+
                        URLEncoder.encode("lastname","UTF-8")+"="+URLEncoder.encode(newuser.lastname,"UTF-8");
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



    public class StoreCalenderEventsAsynckTacks extends AsyncTask<Void,Void,String>{

        CalendarCollection eventObject;
        GetEventsCallbacks eventsCallbacks;

        public StoreCalenderEventsAsynckTacks(CalendarCollection eventObject, GetEventsCallbacks callbacks){
            this.eventsCallbacks=callbacks;
            this.eventObject=eventObject;
        }
        @Override
        protected void onPostExecute(String aVoid) {
            progressDialog.dismiss();
            eventsCallbacks.updated(aVoid);
            super.onPostExecute(aVoid);
        }

        @Override
        protected String doInBackground(Void... params) {

            String reponse=null;
            ArrayList<Pair<String,String>> data=new ArrayList<>();
            String d=eventObject.title;
            data.add(new Pair<String, String>("title", d));
            data.add(new Pair<String, String>("description",eventObject.description));
            data.add(new Pair<String, String>("datetime", eventObject.datetime));
            data.add(new Pair<String, String>("creator", eventObject.creator));
            data.add(new Pair<String, String>("category", eventObject.category));
            data.add(new Pair<String, String>("startingtime",eventObject.startingtime));
            data.add(new Pair<String, String>("endingtime",eventObject.endingtime));
            data.add(new Pair<String, String>("hashid",eventObject.hashid));
            data.add(new Pair<String, String>("alldayevent",eventObject.alldayevent));
            data.add(new Pair<String, String>("everymonth",eventObject.everymonth));
            data.add(new Pair<String, String>("defaulttime",eventObject.creationdatetime));


            URL url;
            HttpURLConnection urlConnection=null;
            try {

                byte[] postData= getData(data).getBytes("UTF-8");
                url=new URL(ServerRequest.SERVER_ADDRESS + "CreateEventCalendar.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postData);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                reponse=response.toString();

                in.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            return reponse;
        }
    }




    public class FetchAllEventsAsynckTacks extends AsyncTask<Void,Void,ArrayList<CalendarCollection>> {

        GetEventsCallbacks eventsCallbacks;


        public FetchAllEventsAsynckTacks( GetEventsCallbacks callbacks) {
            this.eventsCallbacks = callbacks;
        }

        @Override
        protected void onPostExecute(ArrayList<CalendarCollection> returnedevents) {
            progressDialog.dismiss();
            eventsCallbacks.donec(returnedevents);
            super.onPostExecute(returnedevents);
        }

        @Override
        protected ArrayList<CalendarCollection> doInBackground(Void... params) {

            ArrayList<CalendarCollection> returnedEvents=new ArrayList<>();
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(ServerRequest.SERVER_ADDRESS + "FetchEventCalendar.php");
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
                returnedEvents= getDetailsEvents(jsonArray);


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

    public ArrayList<CalendarCollection> getDetailsEvents(JSONArray jsonArray){
        ArrayList<CalendarCollection> events=new ArrayList<>();

        try {


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo_inside = jsonArray.getJSONObject(i);

                String titel = jo_inside.getString("title");
                String infotext = jo_inside.getString("description");
                String creator = jo_inside.getString("creator");
                String creationTime = jo_inside.getString("datetime");
                String category = jo_inside.getString("category");
                String startingtime = jo_inside.getString("startingtime");
                String endingtime = jo_inside.getString("endingtime");
                String alldayevent = jo_inside.getString("alldayevent");
                String eventHash = jo_inside.getString("hashid");
                String everymonth = jo_inside.getString("everymonth");
                String creationdatetime = jo_inside.getString("defaulttime");


                String[] creationtime=creationTime.split(" ");

                CalendarCollection  object =new CalendarCollection(titel,infotext,creator,creationTime,startingtime,endingtime,eventHash,category,alldayevent,everymonth,creationdatetime);

                events.add(object);


            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;

    }



    public class DeleteEventsAsynckTasks extends AsyncTask<Void,Void,String>{

        CalendarCollection eventObject;
        GetEventsCallbacks eventsCallbacks;

        public DeleteEventsAsynckTasks(CalendarCollection eventObject, GetEventsCallbacks callbacks){
            this.eventsCallbacks=callbacks;
            this.eventObject=eventObject;

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

            data.add(new Pair<String, String>("creator", eventObject.creator));
            data.add(new Pair<String, String>("hashid",eventObject.hashid));



            URL url;
            String line=null;
            HttpURLConnection urlConnection=null;
            try {

                byte[] postData= getData(data).getBytes("UTF-8");
                url=new URL(ServerRequest.SERVER_ADDRESS + "DeleteEventCalendar.php");
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
            getUserCallbacks.serverReponse(reponse);
            super.onPostExecute(reponse);
        }

        @Override
        protected String doInBackground(Void... params) {

            String line="";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url=new URL(SERVER_ADDRESS + "UpdateUserOnlineStatus.php");
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
                        URLEncoder.encode("onlinestatus","UTF-8")+"="+URLEncoder.encode(String.valueOf(user.status),"UTF-8");
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
                    if(line.contains("Status successfully updated")){
                       // mySQLiteHelper.reInitializeSqliteTable();
                    }

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

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
    private String getData(ArrayList<Pair<String,String>> values) throws UnsupportedEncodingException {
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

    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
}

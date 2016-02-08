package com.bricefamily.alex.time_tracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

    public void fetchallgcmregistrationIds(GetUserCallbacks callbacks){
        new FetchAllGCMIDSAsynckTacks(callbacks).execute();

    }


    public void deleteEvents(EventObject eventObject,GetEventsCallbacks callbacks,String hash){
        progressDialog.setTitle("Deleting record...");

        progressDialog.show();
        new DeleteEventsAsynckTasks(eventObject,callbacks,hash).execute();
    }
    public void updateEvents(EventObject eventObject,GetEventsCallbacks callBacks,String hash){
        progressDialog.setTitle("Updating record...");

        progressDialog.show();
        new UpdateEventsAsynckTacks(eventObject,callBacks,hash).execute();
    }


    public void fetchAllevents(GetEventsCallbacks callbacks){
        progressDialog.setTitle("Downloading data...");

        progressDialog.show();
        new FetchAllEventsAsynckTacks(callbacks).execute();
    }
    public void createEventinBackground(EventObject eventObject, GetEventsCallbacks callbacks){
        progressDialog.setTitle("Record in creation...");

        progressDialog.show();
        new StoreEventsAsynckTacks(eventObject,callbacks).execute();

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


    public class StoreEventsAsynckTacks extends AsyncTask<Void,Void,String>{

        EventObject eventObject;
        GetEventsCallbacks eventsCallbacks;

        public StoreEventsAsynckTacks( EventObject eventObject,GetEventsCallbacks callbacks){
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


    public class DeleteEventsAsynckTasks extends AsyncTask<Void,Void,String>{

        EventObject eventObject;
        GetEventsCallbacks eventsCallbacks;
        String hash;

        public DeleteEventsAsynckTasks(EventObject eventObject, GetEventsCallbacks callbacks, String hash){
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

            data.add(new Pair<String, String>("eventCreator", eventObject.creator));
            data.add(new Pair<String, String>("eventHash",hash));



            URL url;
            String line=null;
            HttpURLConnection urlConnection=null;
            try {

                byte[] postData= getData(data).getBytes("UTF-8");
                url=new URL(SERVER_ADDRESS + "DeleteUserEvents.php");
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



    public class FetchAllGCMIDSAsynckTacks extends AsyncTask<Void,Void,ArrayList<User>> {

        GetUserCallbacks userCallbacks;


        public FetchAllGCMIDSAsynckTacks( GetUserCallbacks callbacks) {
            this.userCallbacks = callbacks;
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
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(SERVER_ADDRESS + "FetchAllUserGCMIds.php");
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



}

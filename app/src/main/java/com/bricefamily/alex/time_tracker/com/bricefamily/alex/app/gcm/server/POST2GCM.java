package com.bricefamily.alex.time_tracker.com.bricefamily.alex.app.gcm.server;

import android.content.Context;
import android.os.AsyncTask;

import com.bricefamily.alex.time_tracker.Config;
import com.bricefamily.alex.time_tracker.User;

import org.codehaus.jackson.map.ObjectMapper;
import com.bricefamily.alex.time_tracker.GetUserCallbacks;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by bricenangue on 06/02/16.
 */
public class POST2GCM{




    public static String post(String apiKey, Content content){

        String reponse=null;
        try{

            // 1. URL
            URL url = new URL("https://android.googleapis.com/gcm/send");

            // 2. Open connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 3. Specify POST method
            conn.setRequestMethod("POST");

            // 4. Set the headers
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + apiKey);

            conn.setDoOutput(true);

            // 5. Add JSON data into POST request body

            //`5.1 Use Jackson object mapper to convert Contnet object into JSON
            ObjectMapper mapper = new ObjectMapper();

            // 5.2 Get connection output stream
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

            // 5.3 Copy Content "JSON" into
            mapper.writeValue(wr, content);

            // 5.4 Send the request
            wr.flush();

            // 5.5 close
            wr.close();

            // 6. Get the response
            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            reponse=response.toString();
            in.close();

            // 7. Print result
            System.out.println(response.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reponse;
    }


    //post to google cloud messaging
    public static class PostGCMBackgroundTasck  extends AsyncTask<Object,Void,String>{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }

        @Override
        protected String doInBackground(Object... params) {
            String apiKey=(String)params[0];
           Content content=(Content)params[1];

            String s= postTask(apiKey, content);
            return s;
        }
    }
    public static String postTask(String apiKey, Content content){

        String reponse=null;
        try{

            // 1. URL
            URL url = new URL("https://android.googleapis.com/gcm/send");

            // 2. Open connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 3. Specify POST method
            conn.setRequestMethod("POST");

            // 4. Set the headers
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + apiKey);

            conn.setDoOutput(true);
            OutputStream out =conn.getOutputStream();

            // 5. Add JSON data into POST request body

            //`5.1 Use Jackson object mapper to convert Contnet object into JSON
            ObjectMapper mapper = new ObjectMapper();

            // 5.2 Get connection output stream
            DataOutputStream wr = new DataOutputStream(out);


            // 5.3 Copy Content "JSON" into
            mapper.writeValue(wr, content);

            // 5.4 Send the request
            wr.flush();

            // 5.5 close
            wr.close();

            // 6. Get the response
            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            reponse=response.toString();
            in.close();

            // 7. Print result
            System.out.println(response.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reponse;
    }




}

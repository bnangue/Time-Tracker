package com.bricefamily.alex.time_tracker;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by bricenangue on 17/02/16.
 */
public class FriendRequest {

    private Context context;
    private User user;
     UserLocalStore userLocalStore;
     String sendertname;
     String requestquestion="Do you want to be friend with ";
    String requestanswer=" is now your friend";
    String removeinfo=" removed you as friend";
    private ArrayList<User> userArrayList;
    private int[] status;
    AllUserTabAdapter allUserTabAdapter;

    public FriendRequest(Context context, User user,ArrayList<User> list,int[] status,Fragment fragment){
        this.context=context;
        this.user=user;
        userLocalStore=new UserLocalStore(context);
        this.userArrayList=list;
        this.status=status;
        allUserTabAdapter=new AllUserTabAdapter(context,list,fragment);
    }

    public FriendRequest(Context context, User user){
        this.context=context;
        this.user=user;
        userLocalStore=new UserLocalStore(context);

    }

    static boolean resquestAccected;
    static boolean isResquesSent;
    private static String getData(ArrayList<Pair<String, String>> values) throws UnsupportedEncodingException {
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

    public  void sendFriendresquest(final boolean requestype,final String receiverregId,final String sendername) {// true if request false if answer to request

        if (!user.regId.isEmpty()) {


            Thread thread = new Thread() {
                @Override
                public void run() {
                    HttpURLConnection conn=null;
                    try {

                        String regid = userLocalStore.getUserRegistrationId();
                        sendertname=userLocalStore.getLoggedInUser().username;
                        ArrayList<Pair<String,String>> data=new ArrayList<>();
                        if(requestype){
                            data.add(new Pair<String, String>("message", requestquestion +sendertname));
                            data.add(new Pair<String, String>("registrationReceiverIDs", user.regId));
                            data.add(new Pair<String, String>("receiver", user.username));
                            data.add(new Pair<String, String>("sender", sendertname));
                            data.add(new Pair<String, String>("receiveremail", user.email));
                            data.add(new Pair<String, String>("receiverhashpass", user.password));
                        }else{
                            data.add(new Pair<String, String>("message",sendername+ requestanswer));
                            data.add(new Pair<String, String>("registrationReceiverIDs", receiverregId));
                            data.add(new Pair<String, String>("receiver", sendername));
                            data.add(new Pair<String, String>("sender", sendername));
                        }

                        data.add(new Pair<String, String>("registrationSenderIDs", regid));
                        data.add(new Pair<String, String>("apiKey", Config.API_KEY));

                        byte[] bytes = getData(data).getBytes("UTF-8");


                        URL url=new URL(Config.YOUR_SERVER_URL+ "FriendRequestGCMconnection.php");
                        conn=(HttpURLConnection)url.openConnection();
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setFixedLengthStreamingMode(bytes.length);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type",
                                "application/x-www-form-urlencoded;charset=UTF-8");
                        // post the request
                        OutputStream out = conn.getOutputStream();
                        out.write(bytes);
                        out.close();

                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuffer reponse = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            reponse.append(inputLine);
                        }
                        final String response =reponse.toString();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        if(conn!=null){
                            conn.disconnect();
                        }
                    }
                }
            };

            thread.start();

        }

    }

    private void removeuserinfriendlist(final int position) {

        ServerRequestUser serverRequestUser=new ServerRequestUser(context);
        String f=userLocalStore.getUserfriendliststring();
        StringBuilder fadd=new StringBuilder();
        String finalfriendlist=null;
        if(f.equals("noFrineds")){
            Toast.makeText(context, "You currently have no friends", Toast.LENGTH_SHORT).show();
        }else{
            String[] fls=f.split(",");
            for (int i =0;i<fls.length;i++){
                if(!userArrayList.get(position).username.equals(fls[i])){
                    if(i==fls.length-1){
                        fadd.append(fls[i]);
                    }else {
                        fadd.append(fls[i]).append(",");
                    }

                }
            }

            userLocalStore.setUserUserfriendliststring(fadd.toString());
            finalfriendlist=fadd.toString();
        }


        String password=userLocalStore.getLoggedInUser().password;
        String email=userLocalStore.getLoggedInUser().email;
        String uname=userLocalStore.getLoggedInUser().username;
        User user=new User(uname,email,password,finalfriendlist,1);
        serverRequestUser.updateFriendList(user, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void deleted(String reponse) {
                if (reponse.contains("Friendlist successfully updated")) {
                    Toast.makeText(context, userArrayList.get(position).username + " removed from your friend list", Toast.LENGTH_SHORT).show();
                    status[position]=0;
                    allUserTabAdapter.setRequeststatus(status);
                }

            }

            @Override
            public void userlist(ArrayList<User> reponse) {

            }
        });
    }


     public void adduserinfriendList(final String username,final String email,final String password,final String myusername){
       final ServerRequestUser serverRequestUser=new ServerRequestUser(context);
        String f=userLocalStore.getUserfriendliststring();
        StringBuilder fadd=null;
        String finalfriendlist=null;
        if(f.equals("noFrineds")||f==null||f.isEmpty()||f.equals(",")){
            userLocalStore.setUserUserfriendliststring(username);
            finalfriendlist=username;
        }else{
            fadd=new StringBuilder(f).append(",").append(username);
            userLocalStore.setUserUserfriendliststring(fadd.toString());
            finalfriendlist=fadd.toString();
        }


        String pword=password;
        String mail=email;
        String uname=username;
        User user=new User(uname,mail,pword,finalfriendlist,1);
        serverRequestUser.fetchUsertoUpdateFriend(myusername,username, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

                if (returneduser != null) {
                    serverRequestUser.fetchUsertoUpdateFriend(username, myusername, new GetUserCallbacks() {
                        @Override
                        public void done(User returneduser) {
                            if (returneduser != null) {
                                Toast.makeText(context, username + " added to your friend list", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void deleted(String reponse) {

                        }

                        @Override
                        public void userlist(ArrayList<User> reponse) {

                        }
                    });
                }
            }

            @Override
            public void deleted(String reponse) {
                if (reponse.contains("Friendlist successfully updated")) {


                }

            }

            @Override
            public void userlist(ArrayList<User> reponse) {

            }
        });
    }


    public void uddateuserinfriendList(final User us){
        ServerRequestUser serverRequestUser=new ServerRequestUser(context);


        serverRequestUser.updateFriendList(us, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {

            }

            @Override
            public void deleted(String reponse) {
                if (reponse.contains("Friendlist successfully updated")) {

                }

            }

            @Override
            public void userlist(ArrayList<User> reponse) {

            }
        });
    }


    public  void sendFriendremove() {

        if (!user.regId.isEmpty()) {


            Thread thread = new Thread() {
                @Override
                public void run() {
                    HttpURLConnection conn=null;
                    try {

                        String regid = userLocalStore.getUserRegistrationId();
                        sendertname=userLocalStore.getLoggedInUser().username;
                        ArrayList<Pair<String,String>> data=new ArrayList<>();

                            data.add(new Pair<String, String>("message", sendertname+removeinfo ));
                            data.add(new Pair<String, String>("registrationReceiverIDs", user.regId));
                            data.add(new Pair<String, String>("receiver", user.username));
                            data.add(new Pair<String, String>("sender", sendertname));

                        data.add(new Pair<String, String>("registrationSenderIDs", regid));
                        data.add(new Pair<String, String>("apiKey", Config.API_KEY));

                        byte[] bytes = getData(data).getBytes("UTF-8");


                        URL url=new URL(Config.YOUR_SERVER_URL+ "ConnectionGCMServer.php");
                        conn=(HttpURLConnection)url.openConnection();
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setFixedLengthStreamingMode(bytes.length);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type",
                                "application/x-www-form-urlencoded;charset=UTF-8");
                        // post the request
                        OutputStream out = conn.getOutputStream();
                        out.write(bytes);
                        out.close();

                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuffer reponse = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            reponse.append(inputLine);
                        }
                        final String response =reponse.toString();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        if(conn!=null){
                            conn.disconnect();
                        }
                    }
                }
            };

            thread.start();

        }

    }
}

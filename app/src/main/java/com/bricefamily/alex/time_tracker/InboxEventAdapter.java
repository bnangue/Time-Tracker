package com.bricefamily.alex.time_tracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by bricenangue on 16/02/16.
 */
public class InboxEventAdapter extends BaseAdapter
{
    private ArrayList<IncomingNotification> incominglist;
    private Context context;
    private int[] readstatus;

    public InboxEventAdapter(Context context, ArrayList<IncomingNotification> incominglist){
        this.context=context;
        this.incominglist=incominglist;
        readstatus =new int[incominglist.size()];

    }

    public void setUserStatus(int[] status){

        this.readstatus =status;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return incominglist.size();
    }

    @Override
    public Object getItem(int position) {
        return incominglist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.new_inbox_tab_item,null);
            holder=new Holder();

            holder.inboxtitle=(TextView)convertView.findViewById(R.id.inboxeventTitle);
            holder.picture=(ImageView)convertView.findViewById(R.id.avatarfriend);
            holder.dtime=(TextView)convertView.findViewById(R.id.timeindicator);



            convertView.setTag(holder);
        }else{

            holder=(Holder)convertView.getTag();
        }


        String creationtime=incominglist.get(position).creationDate;
        int readstatus=incominglist.get(position).readStatus;
        int type=incominglist.get(position).type;
       // holder.datetime.setText(creationtime);

        if(readstatus==1){

            convertView.setBackgroundColor(context.getResources().getColor(R.color.white));

        }else if(readstatus==0){
            convertView.setBackgroundColor(context.getResources().getColor(R.color.inboxunread));
        }
        switch (type){
            case 1:
                holder.inboxtitle.setText("New friend request");
                holder.picture.setImageResource(R.drawable.useraddedblue);
                break;
            case 2:
                holder.inboxtitle.setText("New event posted");
                holder.picture.setImageResource(R.drawable.calendar);
                break;
            case 3:
                holder.inboxtitle.setText("New message");
                holder.picture.setImageResource(R.drawable.chaticon);
                break;
            case 4:
                holder.inboxtitle.setText("You have one new friend");
                holder.picture.setImageResource(R.drawable.addeduserblue);
                break;
            case 5:
                holder.inboxtitle.setText("Your friend request has been rejected");
                holder.picture.setImageResource(R.drawable.userremovedloli);
                break;
        }

        String dtimes=incominglist.get(position).creationDate;
        String[] ti=dtimes.split("-");
        String tiday=ti[2];
        Calendar c=new GregorianCalendar();
        Date dat=c.getTime();
        //String day= String.valueOf(dat.getDay());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String day = (String) android.text.format.DateFormat.format("dd", dat); //20

        int t=Integer.parseInt(day);
        int y=Integer.parseInt(tiday);
        if(tiday.equals(day)){
            holder.dtime.setText("today");
        }else if(t-1==y){
            holder.dtime.setText("yesterday");
        }else if(t-2==y){
            holder.dtime.setText("2 days ago");
        }else if(t-3==y){
            holder.dtime.setText("3 days ago");
        }else if(t-4==y){
            holder.dtime.setText("4 days ago");
        }else if(t-5==y){
            holder.dtime.setText("5 days ago");
        }else if(t-6==y){
            holder.dtime.setText("6 days ago");
        }else if(t-7==y){
            holder.dtime.setText("a week ago");
        }else {
            holder.dtime.setText(dtimes);
        }



        return convertView;
    }


    static class Holder {
        public TextView inboxtitle;
        public ImageView picture;
        public TextView dtime;

    }
}

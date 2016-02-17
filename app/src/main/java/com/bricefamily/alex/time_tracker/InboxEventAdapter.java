package com.bricefamily.alex.time_tracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bricenangue on 16/02/16.
 */
public class InboxEventAdapter extends BaseAdapter
{
    private ArrayList<User> incominglist;
    private Context context;
    private int[] readstatus;

    public InboxEventAdapter(Context context, ArrayList<User> incominglist){
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
            holder.datetime=(TextView)convertView.findViewById(R.id.timeindicator);



            convertView.setTag(holder);
        }else{

            holder=(Holder)convertView.getTag();
        }

        String usernam=incominglist.get(position).username;
        holder.datetime.setText("");
        holder.inboxtitle.setText(usernam);



        if(readstatus[position]==1){

            convertView.setBackgroundColor(context.getResources().getColor(R.color.white));

        }else{
            convertView.setBackgroundColor(context.getResources().getColor(R.color.inboxunread));
        }
        return convertView;
    }


    static class Holder {
        public TextView inboxtitle;
        public ImageView picture;
        public TextView datetime;

    }
}

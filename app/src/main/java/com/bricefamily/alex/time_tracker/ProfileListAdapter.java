package com.bricefamily.alex.time_tracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bricenangue on 05/02/16.
 */
public class ProfileListAdapter extends BaseAdapter
{
    private ArrayList<User> userlist;
    private Context context;
    private int[] onlinestatus;

    public ProfileListAdapter(Context context,ArrayList<User> userlist){
        this.context=context;
        this.userlist=userlist;
        onlinestatus=new int[userlist.size()];

    }

    public void setUserStatus(int[] status){

            this.onlinestatus=status;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return userlist.size();
    }

    @Override
    public Object getItem(int position) {
        return userlist.get(position);
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
            convertView=inflater.inflate(R.layout.new_my_friends_tab_item,null);
            holder=new Holder();

            holder.username=(TextView)convertView.findViewById(R.id.usernamefriend);
            holder.userPicture=(ImageView)convertView.findViewById(R.id.avatarfriend);
            holder.checker=(RadioButton)convertView.findViewById(R.id.radioButtonstatus);
            holder.friendindicator=(TextView)convertView.findViewById(R.id.friendindicator);


            convertView.setTag(holder);
        }else{

            holder=(Holder)convertView.getTag();
        }

        String usernam=userlist.get(position).username;
        Bitmap picture=userlist.get(position).picture;

        if(picture!=null){
            holder.userPicture.setImageBitmap(picture);
        }

        holder.username.setText(usernam);
        holder.friendindicator.setText("friend");

        holder.checker.setChecked(setstatus(userlist.get(position).status));
        holder.checker.setClickable(false);


        if(onlinestatus[position]==1){
            holder.checker.setChecked(true);
            convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
        }else{
            holder.checker.setChecked(false);
            convertView.setBackgroundColor(context.getResources().getColor(R.color.useroffline));
        }


        return convertView;
    }

    private boolean setstatus(int i){
        if(i==0){
            return false;
        }else {
            return true;
        }
    }
    static class Holder {
        public TextView friendindicator;
        public TextView username;
        public ImageView userPicture;
        public RadioButton checker;

    }
}

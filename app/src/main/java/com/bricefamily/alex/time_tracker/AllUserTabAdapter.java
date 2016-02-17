package com.bricefamily.alex.time_tracker;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by bricenangue on 16/02/16.
 */
public class AllUserTabAdapter extends BaseAdapter implements DialogRequestAddFriendFragment.OnRequestconfirm
{


    @Override
    public void onAdd(int position) {

    }

    @Override
    public void onRemove(int position) {

    }

    public interface OnbuttonAddFriendPressed {
        void onbuttonAddPressed(int position);

    }
    OnbuttonAddFriendPressed onbuttonAddFriendPressed;

    private ArrayList<User> userlist;
    private Context context;
    private int[] requeststatus;

    public AllUserTabAdapter(Context context, ArrayList<User> userlist,Fragment fragment){
        this.context=context;
        this.userlist=userlist;
        requeststatus =new int[userlist.size()];
        onbuttonAddFriendPressed=(OnbuttonAddFriendPressed)fragment;

    }

    public void setRequeststatus(int[] status){

        this.requeststatus =status;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.new_all_users_tab_item,null);
            holder=new Holder();

            holder.username=(TextView)convertView.findViewById(R.id.usernamefriend);
            holder.userPicture=(ImageView)convertView.findViewById(R.id.avatarfriend);
            holder.addfriend=(Button)convertView.findViewById(R.id.btnaddfriend);
            holder.friendindicator=(TextView)convertView.findViewById(R.id.friendindicator);



            convertView.setTag(holder);
        }else{

            holder=(Holder)convertView.getTag();
        }

        String usernam=userlist.get(position).username;
        holder.friendindicator.setText(" ");
        holder.username.setText(usernam);
        holder.addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requeststatus[position]==1) {
                    onbuttonAddFriendPressed.onbuttonAddPressed(position);
                }else {
                    onbuttonAddFriendPressed.onbuttonAddPressed(position);
                }

            }
        });

        if(requeststatus[position]==1){

            holder.addfriend.setText("REMOVE");
            convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.friendindicator.setText("request sent");

        }else{
            convertView.setBackgroundColor(context.getResources().getColor(R.color.cellselect));
            holder.addfriend.setText("ADD FRIEND");
        }

        return convertView;
    }


    static class Holder {
        public TextView username;
        public ImageView userPicture;
        public Button addfriend;
        public TextView friendindicator;

    }
}

package com.bricefamily.alex.time_tracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

/**
 * Created by praktikum on 28/01/16.
 */
public class ShareWithFriendAdapter extends BaseAdapter{
    private Context context ;
    private ArrayList<User> list=new ArrayList<>();
    private int count=0;
    boolean[] selectionEvent;


    public interface OnEventSelected{
        void selected(int count, boolean[] events, int position);
    }

    OnEventSelected onEventSelected;

    public ShareWithFriendAdapter(Context context, ArrayList<User> list, OnEventSelected onEventSelected){
        this.list=list;
        selectionEvent=new boolean[list.size()];
        this.context=context;
        this.onEventSelected=onEventSelected;

    }

    void setEventSelection(boolean[] events,int countE){
        selectionEvent=events;
        count=countE;
        notifyDataSetChanged();

    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Holder holder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             convertView=inflater.inflate(R.layout.share_with_friend_item,null);
            holder=new Holder();

            holder.userpic=(CircularImageView)convertView.findViewById(R.id.imageViewfriendpic);
            holder.username=(TextView)convertView.findViewById(R.id.textViewname);
            holder.checker=(CheckBox)convertView.findViewById(R.id.checkboxsharewithfrienditem);

            convertView.setTag(holder);
        }else{

            holder=(Holder)convertView.getTag();
        }

        holder.userpic.setImageBitmap(list.get(position).picture);
        String name=list.get(position).getfullname();
        holder.username.setText(name);
       // holder.dtime.setText(dtimes);
        holder.checker.setChecked(false);


        if(selectionEvent[position]){
            holder.checker.setChecked(true);
            convertView.setBackgroundColor(context.getResources().getColor(R.color.calender));
        }else{
            holder.checker.setChecked(false);
            convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        final View finalConvertView = convertView;
        holder.checker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.checker.isChecked()){
                    selectionEvent[position]=true;
                    finalConvertView.setBackgroundColor(context.getResources().getColor(R.color.calender));
                    count++;
                    if(onEventSelected!=null){
                        onEventSelected.selected(count,selectionEvent,position);

                    }
                }else {
                    selectionEvent[position]=false;
                    finalConvertView.setBackgroundColor(context.getResources().getColor(R.color.white));
                    if(count!=0){
                        count--;
                    }
                    if(onEventSelected!=null){
                        onEventSelected.selected(count,selectionEvent,position);

                    }


                }
            }
        });

        return convertView;
    }

    static class Holder {
        public TextView username;
        public CircularImageView userpic;
        public CheckBox checker;

    }
}

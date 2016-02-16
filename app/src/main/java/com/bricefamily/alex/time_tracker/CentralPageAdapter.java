package com.bricefamily.alex.time_tracker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by praktikum on 28/01/16.
 */
public class CentralPageAdapter extends BaseAdapter{
    private Context context ;
    private ArrayList<EventObject> list=new ArrayList<>();
    private int count=0;
    boolean[] selectionEvent;


    public interface OnEventSelected{
        void selected(int count,boolean[] events);
    }

    OnEventSelected onEventSelected;

    public CentralPageAdapter(Context context ,ArrayList<EventObject> list,OnEventSelected onEventSelected){
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
             convertView=inflater.inflate(R.layout.central_page_item,null);
            holder=new Holder();

            holder.creator=(TextView)convertView.findViewById(R.id.itemcreator);
            holder.titel=(TextView)convertView.findViewById(R.id.itemTitel);
            holder.dtime=(TextView)convertView.findViewById(R.id.itemtime);
            holder.checker=(CheckBox)convertView.findViewById(R.id.checkBoxcentralpage);

            convertView.setTag(holder);
        }else{

            holder=(Holder)convertView.getTag();
        }

        String titeeel=list.get(position).titel;
        String creato=list.get(position).creator;
        String dtimes=list.get(position).creationTime;

        holder.creator.setText(creato);
        holder.titel.setText(titeeel);
        holder.dtime.setText(dtimes);
        holder.checker.setChecked(false);


        if(selectionEvent[position]){
            holder.checker.setChecked(true);
            convertView.setBackgroundColor(context.getResources().getColor(R.color.cellselect));
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
                    finalConvertView.setBackgroundColor(context.getResources().getColor(R.color.cellselect));
                    count++;
                    if(onEventSelected!=null){
                        onEventSelected.selected(count,selectionEvent);

                    }
                }else {
                    selectionEvent[position]=false;
                    finalConvertView.setBackgroundColor(context.getResources().getColor(R.color.white));
                    if(count!=0){
                        count--;
                    }
                    if(onEventSelected!=null){
                        onEventSelected.selected(count,selectionEvent);

                    }


                }
            }
        });

        return convertView;
    }

    static class Holder {
        public TextView titel;
        public TextView creator;
        public TextView dtime;
        public CheckBox checker;

    }
}

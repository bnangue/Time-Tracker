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
public class CentralPageAdapter extends ArrayAdapter<EventObject> {
    private Context context ;
    private List<EventObject> list=new ArrayList<>();
    private int count=0;
    SparseBooleanArray mSelectedItemsIds;
    private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();


    public interface OndeleteFromList{
        void delete(int position);
    }

    OndeleteFromList ondeleteFromList;

    public CentralPageAdapter(Context context ,int resId,List<EventObject> list){
        super(context,resId,list);
        this.list=list;
        mSelectedItemsIds=new SparseBooleanArray();
        this.context=context;

    }

    public void toggleSelection(int position)
    {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value)
    {
        if(value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();// mSelectedCount;
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }


    public void setNewSelection(int position, boolean value) {
        mSelection.put(position, value);
        notifyDataSetChanged();
    }

    public boolean isPositionChecked(int position) {
        Boolean result = mSelection.get(position);
        return result == null ? false : result;
    }

    public Set<Integer> getCurrentCheckedPosition() {
        return mSelection.keySet();
    }


    public void removeSelection(int position) {
        mSelection.remove(position);
        notifyDataSetChanged();
    }

    public void clearSelection() {
        mSelection = new HashMap<Integer, Boolean>();
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Holder holder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             convertView=inflater.inflate(R.layout.central_page_item,null);
            holder=new Holder();

            holder.creator=(TextView)convertView.findViewById(R.id.itemcreator);
            holder.titel=(TextView)convertView.findViewById(R.id.itemTitel);
            holder.infotext=(TextView)convertView.findViewById(R.id.itmeinfotext);
            holder.dtime=(TextView)convertView.findViewById(R.id.itemtime);

            convertView.setTag(holder);
        }else{

            holder=(Holder)convertView.getTag();
        }


        holder.creator.setText(list.get(position).creator);
        holder.titel.setText(list.get(position).titel);
        holder.infotext.setText(list.get(position).infotext);
        holder.dtime.setText(list.get(position).time);

        if(mSelection.get(position)!=null){
            convertView.setBackgroundColor(context.getResources().getColor(R.color.phoneCreator));
        }else{
            convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        return convertView;
    }

    public void addEvent(EventObject event) {
        list.add(event);
        notifyDataSetChanged();
        Toast.makeText(context, "New event added!" , Toast.LENGTH_LONG).show();
    }

    public void removeEvent(int position) {
        // super.remove(object);
        list.remove(position);
        removeSelection(position);

    }

    public List<EventObject> getLaptops() {
        return list;
    }


    static class Holder {
        public TextView titel;
        public TextView infotext;
        public TextView creator;
        public TextView dtime;

    }
}

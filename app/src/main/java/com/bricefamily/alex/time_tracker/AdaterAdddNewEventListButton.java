package com.bricefamily.alex.time_tracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by bricenangue on 09/03/16.
 */
public class AdaterAdddNewEventListButton extends BaseAdapter {
    Context context;
    ArrayList<String> categoryList;

    public interface OnButtonString{
        void buttonString(int position);

    }

    OnButtonString onButtonString;
    public AdaterAdddNewEventListButton(Context context, ArrayList<String> categoryList, OnButtonString onButtonString){
        this.context=context;
        this.categoryList=categoryList;
        this.onButtonString=onButtonString;
    }
    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final Holder holder;

        if(convertView==null){
            holder=new Holder();
            convertView = inflater.inflate(R.layout.layout_listview_button_item,null);
            holder.categoryButton=(Button)convertView.findViewById(R.id.button);

            convertView.setTag(holder);

        }else {
            holder=(Holder)convertView.getTag();

        }

        holder.categoryButton.setText(categoryList.get(position));
        holder.categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonString.buttonString(position);
            }
        });
        return convertView;
    }

    static class Holder {
        public Button categoryButton;

    }
}

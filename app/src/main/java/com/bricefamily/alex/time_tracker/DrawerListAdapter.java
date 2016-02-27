package com.bricefamily.alex.time_tracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by alex on 18.01.2016.
 */
public class DrawerListAdapter extends BaseAdapter{
    Context mcontext;
    ArrayList<NavItem> mNavItems;

    public DrawerListAdapter(Context context, ArrayList<NavItem> navItems){
        this.mcontext=context;
        this.mNavItems=navItems;
    }
    @Override
    public int getCount() {
        return mNavItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNavItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if(convertView==null){

            LayoutInflater inflater=(LayoutInflater)mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.drawer_item,null);
            holder=new ViewHolder();
            holder.titelview=(TextView)convertView.findViewById(R.id.titel);
            holder.subtitelview=(TextView)convertView.findViewById(R.id.subtitel);
            holder.iconView=(ImageView)convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }


        holder.titelview.setText(mNavItems.get(position).mTitel);
        holder.subtitelview.setText(mNavItems.get(position).mSubTitel);
        holder.iconView.setImageResource(mNavItems.get(position).micon);

        return convertView;
    }

    public class ViewHolder{
        TextView titelview;
        TextView subtitelview;
        ImageView iconView;

    }
}

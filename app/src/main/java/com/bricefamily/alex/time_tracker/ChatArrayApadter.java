package com.bricefamily.alex.time_tracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bricenangue on 08/02/16.
 */
public class ChatArrayApadter extends ArrayAdapter<ChatMessage>{
    private TextView chattext;
    private List<ChatMessage> messageList=new ArrayList<ChatMessage>();

    public ChatArrayApadter(Context context,int textRessourceId){
        super(context, textRessourceId);

    }

    public void add(ChatMessage chatMessage){
        messageList.add(chatMessage);
        super.add(chatMessage);
    }

    public int getCount(){
        return this.messageList.size();
    }

    public ChatMessage getItem(int index){
        return this.messageList.get(index);
    }

    public View getView(int positon,View convertView, ViewGroup parent){
        View v=convertView;

        if(v==null){
            LayoutInflater inflater=(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=inflater.inflate(R.layout.chat,parent,false);

        }
        LinearLayout layout=(LinearLayout)v.findViewById(R.id.message1);
        ChatMessage obj=getItem(positon);
        chattext=(TextView)v.findViewById(R.id.singlemessage);
        chattext.setText(obj.message);
        chattext.setBackgroundResource(obj.left ? R.drawable.out_message_bg : R.drawable.in_message_bg);
        layout.setGravity(obj.left? Gravity.LEFT : Gravity.RIGHT);

        return v;

    }
    public Bitmap decodeToBitmap(byte[] decodeByte){
        return BitmapFactory.decodeByteArray(decodeByte,0,decodeByte.length);
    }
}

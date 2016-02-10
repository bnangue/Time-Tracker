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
public class ChatArrayApadter extends ArrayAdapter<ChatPeople>{
    private TextView chattext;
    private List<ChatPeople> messageList=new ArrayList<ChatPeople>();
    boolean left=false;

    public ChatArrayApadter(Context context,int textRessourceId,ArrayList<ChatPeople> list){
        super(context, textRessourceId,list);
        this.messageList=list;

    }

    public void add(ChatPeople chatMessage){
        messageList.add(chatMessage);
        super.add(chatMessage);
    }

    public int getCount(){
        return this.messageList.size();
    }

    public ChatPeople getItem(int index){
        return this.messageList.get(index);
    }

    public View getView(int positon,View convertView, ViewGroup parent){
        View v=convertView;

        if(v==null){
            LayoutInflater inflater=(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=inflater.inflate(R.layout.chat,parent,false);

        }
        LinearLayout layout=(LinearLayout)v.findViewById(R.id.message1);
        ChatPeople obj=getItem(positon);
        if(obj.getPERSON_CHAT_TO_FROM().equals("1")){
            left=true;
        }else{
            left=false;
        }
        chattext=(TextView)v.findViewById(R.id.singlemessage);
        chattext.setText(obj.getPERSON_CHAT_MESSAGE());
        chattext.setBackgroundResource(left ? R.drawable.out_message_bg : R.drawable.in_message_bg);
        layout.setGravity(left? Gravity.LEFT : Gravity.RIGHT);

        return v;

    }
    public Bitmap decodeToBitmap(byte[] decodeByte){
        return BitmapFactory.decodeByteArray(decodeByte,0,decodeByte.length);
    }
}

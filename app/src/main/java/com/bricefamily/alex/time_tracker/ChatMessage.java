package com.bricefamily.alex.time_tracker;

/**
 * Created by bricenangue on 08/02/16.
 */
public class ChatMessage {
    String message,sender;
    boolean left;
    public ChatMessage(boolean left, String message,String sender) {
        super();
        this.message=message;
        this.left=left;
        this.sender=sender;
    }
}

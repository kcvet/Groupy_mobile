package com.tpo.groupy;

public class BaseMessage {
    String msg;
    int message_type;

    public BaseMessage(String msg, int message_type) {
        this.msg = msg;
        this.message_type = message_type;

    }

    public String getMsg(){ return this.msg; }

    public int getMsgtype(){ return this.message_type; }

}

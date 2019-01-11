package com.tpo.groupy;

public class BaseMessage {
    String msg, date, name;
    int message_type; // če je msg type 1 pomeni, da je uporabnik poslal sporocilo(modre barve), drugače pa ga je poslal nekdo drug(oranžne barve)

    public BaseMessage(String msg, String date, String name, int message_type) {
        this.msg = msg;
        this.message_type = message_type;
        this.date = date;
        this.name = name;

    }

    public String getMsg(){ return this.msg; }

    public String getName(){ return this.name; }

    public String getDate(){ return this.date; }

    public int getMessagetype(){ return this.message_type; }

}

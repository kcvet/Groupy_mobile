package com.tpo.groupy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;

public class MessageListActivity extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private ArrayList<BaseMessage> messageList = new ArrayList<>();
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        // fill arraylist with random data
        messageList.add(new BaseMessage("Zdravo", "11.1.2018", "Peter", 2));
        messageList.add(new BaseMessage("Zdravo tudi tebi", "12.1.2018", "Kevin", 1));
        messageList.add(new BaseMessage("Ali ti je kaj slabo od TPO-ja", "12.1.2018", "Peter", 2));
        messageList.add(new BaseMessage("Kje pa jaz ljubim TPO", "13.1.2018", "Kevin", 1));
        messageList.add(new BaseMessage("OHH!, Da te nism slišov", "13.1.2018", "Peter", 2));


        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, messageList);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);
    }
}
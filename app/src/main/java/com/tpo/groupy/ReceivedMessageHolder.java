package com.tpo.groupy;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
    TextView messageText, timeText, nameText;

    ReceivedMessageHolder(View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.text_message_body);
        timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        nameText = (TextView) itemView.findViewById(R.id.text_message_name);
    }

    void bind(String message, String date, String name) {
        messageText.setText(message);

        // Format the stored timestamp into a readable String using method.
        timeText.setText(date);
        nameText.setText(name);

    }
}

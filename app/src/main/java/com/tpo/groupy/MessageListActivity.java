package com.tpo.groupy;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageListActivity extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private ArrayList<BaseMessage> messageList = new ArrayList<>();
    Button button_chatbox_send;
    EditText edittext_chatbox;
    private RequestQueue requestQueue;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    int delay=100;
    Handler handler;
    boolean stop = true;
    @Override



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_message_list);
        // fill arraylist with random data
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        prefs = getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        editor = prefs.edit();

        button_chatbox_send =(Button)findViewById(R.id.button_chatbox_send);
        edittext_chatbox =(EditText) findViewById(R.id.edittext_chatbox);
        getMSG();
        button_chatbox_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMSG();
            }
        });
        edittext_chatbox.setText("");
        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));

        handler = new Handler();


        handler.postDelayed(new Runnable() {
            public void run() {
                //do something

                    getMSG();
                    handler.postDelayed(this, delay);

            }
        }, delay);
    }

    public void sendMSG(){
        String msg = edittext_chatbox.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH);
        String formattedDate = sdf.format(new Date());
        messageList.add(new BaseMessage(msg, formattedDate, "Kevin", 1));
        postmsg();
        edittext_chatbox.setText("");

    }

    private void postmsg(){
            JSONObject parameters = new JSONObject();
            int user_id = prefs.getInt("ID_USER",0);
            Bundle extras = getIntent().getExtras();
            try {
                parameters.put("send_by_id_user", user_id);
                parameters.put("text", edittext_chatbox.getText().toString().trim());
                parameters.put("project", 0);
                parameters.put("contained_in_id_chat", extras.getInt("id_chat", 0));

                Toast.makeText(getApplicationContext(), parameters.toString(), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {


            }
            //final String mRequestBody = jsonBody.toString();
            //status.setText(mRequestBody);

            String URL = "http://grupyservice.azurewebsites.net/MessageService.svc/";
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, URL, parameters, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                        // For each repo, add a new line to our repo list.
                        getMSG();


                    Log.i("LOG_VOLLEY", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    Log.e("LOG_VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
                /*@Override
                protected Map<String, String> getParams() {
                    Map<String, String> paramsValue = new HashMap<String, String>();
                    // paramsValue.put("abc@abc.com", new String("geslo123"));
                    //paramsValue.put("email", username);
                    //paramsValue.put("password", password);
                    return paramsValue;
                }*/

            };
            //Toast.makeText(getApplicationContext(),stringRequest.toString(), Toast.LENGTH_SHORT).show();
            requestQueue.add(stringRequest);

    }
    public void getMSG(){
        messageList = new ArrayList<>();
        final int user_id = prefs.getInt("ID_USER",0);
        final String em = prefs.getString("user", "none");
        Bundle extras = getIntent().getExtras();
        int id_group = extras.getInt("id_chat", 0);
        String url = "http://grupyservice.azurewebsites.net/MessageService.svc/ID_CHAT/"+id_group;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Do something with response
                        //mTextView.setText(response.toString());

                        // Process the JSON
                        try{
                            // Loop through the array elements
                            Display display = getWindowManager().getDefaultDisplay();
                            Point size = new Point();
                            display.getSize(size);
                            int width = size.x;
                            int height = size.y;
                            float density = getApplicationContext().getResources().getDisplayMetrics().density;
                            float px = 50 * density;
                            int px_ = Math.round(px);
                            height = height - height/5-height/10;
                            int n_messages = height/px_;
                            int a = 0;
                            if(response.length() > n_messages) a = response.length() -(n_messages-1);
                            Toast.makeText(getApplicationContext(), "height: "+ height+ " density"+px_+ " start: "+a+" n_messages: "+n_messages, Toast.LENGTH_LONG).show();

                            for(int i=a;i<response.length();i++){
                                // Get current json object
                                JSONObject msg = response.getJSONObject(i);

                                // Get the current student (json object) data
                                if(msg.getInt("send_by_id_user")== user_id) messageList.add(new BaseMessage(msg.getString("text"),  msg.getString("datetime"), msg.getString("msgFrom"), 1));
                                else messageList.add(new BaseMessage(msg.getString("text"),  msg.getString("datetime"), msg.getString("msgFrom"), 2));

                            }
                                update();

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred

                    }
                }
        );

        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);

    }
    public void update() {
        mMessageAdapter = new MessageListAdapter(this, messageList);
        mMessageRecycler.setAdapter(mMessageAdapter);

    }


}
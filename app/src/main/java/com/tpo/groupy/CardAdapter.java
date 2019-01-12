package com.tpo.groupy;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {

    private Context mContext;
    private List<Card> albumList;
    LinearLayout layout;
    private View subItem;
    private RequestQueue requestQueue;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, count, descript;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            requestQueue = Volley.newRequestQueue(view.getContext());
            title = (TextView) view.findViewById(R.id.title);
            descript = (TextView) view.findViewById(R.id.sub_item_description);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            subItem = itemView.findViewById(R.id.sub_item);
            layout = view.findViewById(R.id.card_layout);
            //Collapse card in the beginning
            ViewGroup.LayoutParams params = layout.getLayoutParams();
            params.height = 770;
            layout.setLayoutParams(params);
            //
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, "YOUR: "+this.getLayoutPosition(), Toast.LENGTH_SHORT).show();
        }

        private void bind(Card album) {
            boolean expanded = album.isExpanded();
            subItem.setVisibility(expanded ? View.VISIBLE : View.GONE);
            descript.setText("Description: "+album.getDescription()+"\n"+
                    "Place to stay: "+album.getPlace_to_stay()+"\n"+"Place to visit: "+album.getPlace_to_visit());
            title.setText(album.getName());
            count.setText(album.getNumOfSongs() + " people");

        }
    }


    public CardAdapter(Context mContext, List<Card> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.carddesign, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Card album = albumList.get(position);
        final int position1 = position;
        holder.bind(album);
        /*
        holder.title.setText(album.getName());
        holder.count.setText(album.getNumOfSongs() + " people");
            */
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expand();
                boolean expanded = album.isExpanded();
                album.setExpanded(!expanded);
                notifyItemChanged(position1);
            }
        });
        // loading album cover using Glide library
        Glide.with(mContext).load(album.getPhoto()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, album.getID());
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, int id) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.card_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(id));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        int id;

        public MyMenuItemClickListener(int id) {

            this.id = id;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    joinGroup(id);
                    return true;
                case R.id.action_play_next:
                    getChatId(String.valueOf(id));
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }



    public void expand(){
// Gets the layout params that will allow you to resize the layout
        ViewGroup.LayoutParams params = layout.getLayoutParams();
// Changes the height and width to the specified *pixels*
        if(params.height == 770){
            params.height = 1270;
            ValueAnimator animator = ValueAnimator.ofInt(770, 1270);
            animator.setDuration(1000);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    int val = (Integer) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
                    layoutParams.height = val;
                    layout.setLayoutParams(layoutParams);

                }
            });
            animator.start();
        }
        else{
            params.height = 770;
            ValueAnimator animator = ValueAnimator.ofInt(1270, 770);
            animator.setDuration(1000);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    int val = (Integer) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
                    layoutParams.height = val;
                    layout.setLayoutParams(layoutParams);

                }
            });
            animator.start();
        }

    }

    private void joinGroup(int id){
        JSONObject parameters = new JSONObject();
        prefs = mContext.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        editor = prefs.edit();
        int user_id = prefs.getInt("ID_USER",0);
        int group_id = id;




        try {

            parameters.put("ID_GROUP", group_id);
            parameters.put("ID_USER", user_id);



            Toast.makeText(mContext, parameters.toString(), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {


        }
        //final String mRequestBody = jsonBody.toString();
        //status.setText(mRequestBody);

        String URL = "http://grupyservice.azurewebsites.net/AMember.svc/";
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, URL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Toast.makeText(mContext, response.toString(), Toast.LENGTH_SHORT).show();

                try {

                    Toast.makeText(mContext, "You successfully joined the group!", Toast.LENGTH_SHORT).show();
                    String status = response.get("status").toString();
                    //user_login();

                    //JSONObject jsonObj1=jsonObj.getJSONObject(0);
                    //JSONObject jsonObj = response.getJSONObject(0);
                    Toast.makeText(mContext, status, Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    //Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_SHORT).show();
                    // If there is an error then output this to the logs.

                    Log.e("Volley", "Invalid JSON Object.");

                }

                Log.i("LOG_VOLLEY", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(mContext, "Attempt to join group failed, you may already be a member of this group.", Toast.LENGTH_SHORT).show();
                Log.e("LOG_VOLLEY", error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }


        };
        requestQueue.add(stringRequest);



    }
    public void getChatId(String id){
        final String _id = id;
        String url = "http://grupyservice.azurewebsites.net/ChatService.svc/ID_GROUP/"+id;

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

                                // Get current json object

                                JSONObject msg = response.getJSONObject(0);
                                int a = msg.getInt("ID_CHAT");

                                isUserInGroup(_id, a);

                            // Get the current student (json object) data

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

    public void isUserInGroup(final String group_id, int chat_id){
        final int id = chat_id;
        prefs = mContext.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        editor = prefs.edit();
        int user_id = prefs.getInt("ID_USER",0);
        String url = "http://grupyservice.azurewebsites.net/GroupService.svc/ID_USER/"+user_id;
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

                            // Get current json object
                            int m = 0;
                            for(int i=0;i<response.length();i++){
                                // Get current json object
                                JSONObject msg = response.getJSONObject(i);
                                if (msg.getInt("ID_GROUP") == Integer.parseInt(group_id)) {
                                    m = 1;
                                    Intent myIntent = new Intent(mContext, MessageListActivity.class);
                                    myIntent.putExtra("id_chat", id);
                                    mContext.startActivity(myIntent);
                                }
                            }
                        if(m == 0) {
                                Toast.makeText(mContext, "You are not a member of this group", Toast.LENGTH_LONG).show();
                        }

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
}

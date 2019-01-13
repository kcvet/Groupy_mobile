package com.tpo.groupy;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;


public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {

    private Context mContext;
    private List<Card> albumList;
    RelativeLayout layout;
    LinearLayout layout1;
    private View subItem1, subItem;
    private RequestQueue requestQueue;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            requestQueue = Volley.newRequestQueue(view.getContext());
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            subItem = view.findViewById(R.id.sub_item);
            layout = view.findViewById(R.id.rel_layout);
            //Collapse card in the beginning
            ViewGroup.LayoutParams params = layout.getLayoutParams();
            Log.e(TAG, Integer.toString(params.height));
            params.height = 500;
            layout.setLayoutParams(params);
            //
        }

        private void bind(Card album) {
            subItem.setVisibility(View.GONE);
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Card album = albumList.get(position);
        holder.bind(album);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1 = v.findViewById(R.id.card_layout);
                subItem1 = v.findViewById(R.id.sub_item);
                ViewGroup.LayoutParams params = layout1.getLayoutParams();
                Log.e(TAG, album.getName());
                if(!(album.isExpanded())){
                    Log.e(TAG, "Visible");
                    subItem1.setVisibility(View.VISIBLE);
                    // Changes the height and width to the specified *pixels*
                    if(params.height == 559){
                        params.height = 700;
                        ValueAnimator animator = ValueAnimator.ofInt(559, 700);
                        animator.setDuration(1000);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int val = (Integer) animation.getAnimatedValue();
                                ViewGroup.LayoutParams layoutParams = layout1.getLayoutParams();
                                layoutParams.height = val;
                                layout1.setLayoutParams(layoutParams);

                            }
                        });
                        animator.start();
                    }
                    album.setExpanded(true);
                }
                else{
                    params.height = 559;
                    ValueAnimator animator = ValueAnimator.ofInt(700, 559);
                    animator.setDuration(1000);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int val = (Integer) animation.getAnimatedValue();
                            ViewGroup.LayoutParams layoutParams = layout1.getLayoutParams();
                            layoutParams.height = val;
                            layout1.setLayoutParams(layoutParams);

                        }
                    });
                    animator.start();
                    Log.e(TAG, "Gone");
                    subItem1.setVisibility(View.GONE);
                    album.setExpanded(false);
                }

            }
        });
        // loading album cover using Glide library
        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.card_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "You successfully joined the group!", Toast.LENGTH_SHORT).show();
                    joinGroup();
                    return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();

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


    private void joinGroup(){

        String url = "http://grupyservice.azurewebsites.net/GroupService.svc/";


        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Check the length of our response (to see if the user has any repos)
                        // The user does have repos, so let's loop through them all
                        //JSONObject obj = response;
                        try{
                            JSONObject jsonobject = response.getJSONObject(0);
                            Toast.makeText(mContext, jsonobject.toString(), Toast.LENGTH_SHORT).show();

                        }catch (Exception e){
                            e.printStackTrace();
                        }



                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there a HTTP error then add a note to our repo list.
                        Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();

                        //setRepoListText(String.valueOf(a));
                        Log.e("Volley", error.toString());
                    }


                }

        ){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> paramsValue = new HashMap<String, String>();
                // paramsValue.put("abc@abc.com", new String("geslo123"));
                return paramsValue;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                return headers;
            }
        };
        // Add the request we just defined to our request queue.
        // The request queue will automatically handle the request as soon as it can.

        requestQueue.add(arrReq);


    }

}

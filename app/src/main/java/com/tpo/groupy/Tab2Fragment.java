package com.tpo.groupy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Tab2Fragment extends Fragment {

    public TextView name, phone, email, sex;
    private RequestQueue requestQueue;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, container, false);


        name = (TextView)view.findViewById(R.id.user_name);
        phone = (TextView)view.findViewById(R.id.phone);
        email = (TextView)view.findViewById(R.id.email) ;
        sex = (TextView)view.findViewById(R.id.sex);
        requestQueue = Volley.newRequestQueue(getActivity());

        prefs = this.getActivity().getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        editor = prefs.edit();

        getUserInfo();

        return view;

    }

    private void getUserInfo() {


        int user_id = Tab2Fragment.getInt
        String url = "http://grupyservice.azurewebsites.net/UserService.svc/41";


        JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Check the length of our response (to see if the user has any repos)
                        // The user does have repos, so let's loop through them all
                        //JSONObject obj = response;
                        try{
                            String email_r= response.getString("email");
                            String name_r = response.getString("name");
                            String surname_r = response.getString("surname");
                            String sex_r = response.getString("sex");

                            sex.setText(sex_r);
                            email.setText(email_r);
                            name.setText(name_r+ " "+surname_r);

                        }catch (JSONException e){
                            e.printStackTrace();
                        }



                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there a HTTP error then add a note to our repo list.
                        int a = error.networkResponse.statusCode;
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();

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
        Toast.makeText(getActivity(), "Authenticating", Toast.LENGTH_SHORT).show();

        requestQueue.add(arrReq);



    }
}

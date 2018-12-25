package com.tpo.groupy;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class Tab2Fragment extends Fragment {

    public TextView name;
    public int editable = 0; //to check if we make data editaable, or send it to update data
    public Button user_settings;
    EditText email, phone, introduction;
    private RequestQueue requestQueue;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    ImageView profile_pic;
    Spinner sex;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, container, false);



        name = (TextView)view.findViewById(R.id.user_name);
        phone = (EditText) view.findViewById(R.id.phone);
        email = (EditText) view.findViewById(R.id.email) ;
        sex = (Spinner) view.findViewById(R.id.sex);
        introduction= (EditText) view.findViewById(R.id.introduction);
        profile_pic = (ImageView)view.findViewById(R.id.profile_pic);
        user_settings = (Button)view.findViewById(R.id.user_settings);
        requestQueue = Volley.newRequestQueue(getActivity());
        String[] arraySpinner = new String[] {
                "Male", "Female", "Non-binary"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sex.setAdapter(adapter);

        prefs = this.getActivity().getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        editor = prefs.edit();

        getUserInfo();

        user_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editable == 0) setEditable();
                else updateUserInfo(v);
            }
        });

        return view;



    }

    private void updateUserInfo(View v) {
        introduction.setFocusable(false);
        introduction.setClickable(false);
        phone.setFocusable(false);
        phone.setClickable(false);
        email.setClickable(false);
        email.setFocusable(false);
        sex.setClickable(false);
        sex.setFocusable(false);
        user_settings.setText("Edit info");

        updateUser(v);
        editable = 0;
    }
    private void setEditable(){
        introduction.setFocusableInTouchMode(true);
        introduction.setClickable(true);
        phone.setFocusableInTouchMode(true);
        phone.setClickable(true);
        email.setClickable(true);
        email.setFocusableInTouchMode(true);
        sex.setClickable(true);
        sex.setFocusableInTouchMode(true);
        user_settings.setText("Update");
        Toast.makeText(getActivity(), "this is some straight A bullshit", Toast.LENGTH_LONG);
        editable = 1;
    }

    private void getUserInfo() {


        int user_id = prefs.getInt("ID_USER",0);
        final String em = prefs.getString("user", "none");
        String url = "http://grupyservice.azurewebsites.net/UserService.svc/"+user_id;


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
                            String introduction_r = response.getString("introduction");
                            String sex_r = response.getString("sex");
                            String phone_r = response.getString("phone");
                            String profile_pic_r = response.getString("profile_pic");

                            editor.putString("name", name_r);
                            editor.putString("surname", surname_r);
                            editor.putString("introduction", introduction_r);
                            editor.putString("sex", sex_r);
                            editor.putString("phone", phone_r);
                            editor.putString("profile_pic", profile_pic_r);

                            editor.commit();



                            new DownloadImageTask(profile_pic)
                                    .execute(profile_pic_r);
                            /*
                            profile_pic.setImageBitmap(bmp);

*/
                            introduction.setText(introduction_r);
                            //sex.setText(sex_r);
                            email.setText(email_r);
                            name.setText(name_r+ " "+surname_r);
                            phone.setText(phone_r);

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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    //TODO save every user info, and send every thing, or it gets deleted
    private void updateUser(View view){

            JSONObject parameters = new JSONObject();
            final String em = prefs.getString("user", "none");
            final String pass = prefs.getString("userpwd", "none");
            final int id = prefs.getInt("ID_USER", 0);
            final String profile_pic = prefs.getString("profile_pic", "https://www.chaarat.com/wp-content/uploads/2017/08/placeholder-user.png");
            final String name = prefs.getString("name", "");
            final String surname = prefs.getString("surname", "");
            final String sex = prefs.getString("sex", "non-binary");

            try {
                parameters.put("ID_USER", id);
                parameters.put("email", this.email.getText().toString().trim());
                parameters.put("introduction", this.introduction.getText().toString().trim());
                parameters.put("phone", this.phone.getText().toString().trim());
                parameters.put("profile_pic", profile_pic);
                parameters.put("name", name);
                parameters.put("surname", surname);
                parameters.put("sex", sex);
                parameters.put("password", pass);
                parameters.put("phone_verified", 0);
                parameters.put("email_verified", 0);

            } catch (JSONException e) {


            }
            //final String mRequestBody = jsonBody.toString();
            //status.setText(mRequestBody);

            String URL = "http://grupyservice.azurewebsites.net/UserService.svc/";
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.PUT, URL, parameters, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();

                    try {

                        String status = response.get("status").toString();

                        Toast.makeText(getActivity(), "You have succesfully updated your user profile", Toast.LENGTH_LONG).show();


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

                    Toast.makeText(getActivity(), "There was an error while updating your data", Toast.LENGTH_SHORT).show();
                    Log.e("LOG_VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization",em+":"+pass);
                    return headers;
                }
            };

            //Toast.makeText(getApplicationContext(),stringRequest.toString(), Toast.LENGTH_SHORT).show();
            requestQueue.add(stringRequest);


        }
}


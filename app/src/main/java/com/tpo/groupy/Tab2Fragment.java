package com.tpo.groupy;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
                else updateUserInfo();
            }
        });

        return view;



    }

    private void updateUserInfo() {
        introduction.setFocusable(false);
        introduction.setClickable(false);
        phone.setFocusable(false);
        phone.setClickable(false);
        email.setClickable(false);
        email.setFocusable(false);
        sex.setClickable(false);
        sex.setFocusable(false);
        user_settings.setText("Edit info");

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
}


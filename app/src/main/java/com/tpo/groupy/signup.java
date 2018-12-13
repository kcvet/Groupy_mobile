package com.tpo.groupy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


public class signup extends AppCompatActivity implements View.OnClickListener {

    EditText email;
    EditText password;
    Button register,ret_login;
    private RequestQueue requestQueue;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String status1;
    Boolean succesful;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        editor = prefs.edit();
        register = (Button) findViewById(R.id.register);
        ret_login = (Button) findViewById(R.id.ret_login);
        email = (EditText) findViewById(R.id.email_register);
        password = (EditText) findViewById(R.id.password_register);
        status1="0";
        register.setOnClickListener(this);
        ret_login.setOnClickListener(this);

    }

    private void registerUser(View view){




        if(!(false)) {
            final String username = this.email.getText().toString().trim();
            final String password = MD5(this.password.getText().toString().trim());

            if (username.isEmpty()) {
                this.email.setError("Username is required");
                this.email.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                this.email.setError("Enter a valid username(email)");
                this.email.requestFocus();
                return;

            }

            if (password.isEmpty()) {
                this.password.setError("Password is required");
                this.password.requestFocus();
                return;
            }

            if (password.length() < 6) {
                this.password.setError("Minimum length of password is 6");
                this.password.requestFocus();
                return;
            }


            //this.status.setText("Dodajam v " + URL);
            //JSONObject jsonBody = new JSONObject();
            //Map<String, String> params = new HashMap();
            //params.put("email", username);
            //params.put("password", password);

            JSONObject parameters = new JSONObject();
            //Toast.makeText(getApplicationContext(),parameters.toString(), Toast.LENGTH_SHORT).show();

            try {
                parameters.put("email", username);
                parameters.put("password", password);
                parameters.put("project", 1);
                Toast.makeText(getApplicationContext(), parameters.toString(), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {


            }
            //final String mRequestBody = jsonBody.toString();
            //status.setText(mRequestBody);

            String URL = "http://grupyservice.azurewebsites.net/UserService.svc/";
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, URL, parameters, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();

                    try {

                        // For each repo, add a new line to our repo list.
                        String status = response.get("status").toString();
                        //user_login();

                        //JSONObject jsonObj1=jsonObj.getJSONObject(0);
                        //JSONObject jsonObj = response.getJSONObject(0);
                        Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();


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


            // UNCOMENT THIS WHEN READY
            //startActivity(new Intent(getApplication(), OnRegister.class));
        }
        else{
            Toast.makeText(getApplicationContext(),"User with this email already exists",Toast.LENGTH_LONG).show();
        }
    }

    private void checkUser(View view) {
        final View view1=view;

        final String username = this.email.getText().toString().trim();
        final String password= MD5(this.password.getText().toString().trim());
        Toast.makeText(getApplicationContext(), password, Toast.LENGTH_SHORT).show();
        editor.putString("useremail", username);
        editor.putString("userpwd", password);

        editor.commit();

        JSONObject parameters = new JSONObject();
        //Toast.makeText(getApplicationContext(),parameters.toString(), Toast.LENGTH_SHORT).show();

        try {
            parameters.put("email", username);
            parameters.put("password", password);
            Toast.makeText(getApplicationContext(), parameters.toString(), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {


        }
        //final String mRequestBody = jsonBody.toString();
        //status.setText(mRequestBody);

        String URL = "http://grupyservice.azurewebsites.net/UserService.svc/";
        URL=URL+username;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, URL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();

                try {

                    // For each repo, add a new line to our repo list.
                    String status = response.get("status").toString();
                    status1=status;

                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
                    if(Integer.parseInt(status1)>0) Toast.makeText(getApplicationContext(),"User with this email already exists",Toast.LENGTH_LONG).show();
                    else registerUser(view1);

                    //JSONObject jsonObj1=jsonObj.getJSONObject(0);
                    //JSONObject jsonObj = response.getJSONObject(0);
                    // Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();


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



    public static String MD5(String s) {
        MessageDigest m = null;

        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        m.update(s.getBytes(),0,s.length());
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;
    }





    private void user_login(){
        final String username = this.email.getText().toString().trim();
        final String password = MD5(this.password.getText().toString().trim());
        Toast.makeText(getApplicationContext(), password, Toast.LENGTH_SHORT).show();
        editor.putString("useremail", username);
        editor.putString("userpwd", password);

        editor.commit();





        succesful=false;

        if(password.isEmpty()){
            this.password.setError("Password is required");
            this.password.requestFocus();
            return;
        }

        if(username.isEmpty()){
            this.email.setError("Username is required");
            this.email.requestFocus();
            return;
        }

        String url = "http://grupyservice.azurewebsites.net/UserService.svc";

        Toast.makeText(getApplicationContext(), "Authenticating", Toast.LENGTH_SHORT).show();
        JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Check the length of our response (to see if the user has any repos)
                        // The user does have repos, so let's loop through them all
                        //JSONObject obj = response;
                        try{
                            Integer status=response.getInt("status");
                            if(status>0) succesful=true;
                            //Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();
                            editor.putInt("userid", status);

                            editor.commit();
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
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

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
                headers.put("Authorization",username+":"+password);
                return headers;
            }
        };
        // Add the request we just defined to our request queue.
        // The request queue will automatically handle the request as soon as it can.
        requestQueue.add(arrReq);




    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.register:
                checkUser(view);
                //finish();
                //finish();
                break;
            case R.id.ret_login:
                startActivity(new Intent(getApplication(),MainActivity.class));
                finish();
                break;


        }
    }
}
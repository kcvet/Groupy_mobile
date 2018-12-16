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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;



public class MainActivity extends AppCompatActivity {

    EditText password, email;
    Button login, register;
    private RequestQueue requestQueue;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String status1;
    Boolean succesful;
    CheckBox check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //naredim brez bara na androidu
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        editor = prefs.edit();
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // določimo mu kateri komponenti pripadajo
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);
        check = (CheckBox)findViewById(R.id.checkBox);

        if(prefs.contains("user")){
            check.setChecked(true);
            String user = prefs.getString("user","");
            String pass = prefs.getString("pass", "");
            email.setText(user);
            password.setText(pass);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(check.isChecked()){
                    String user = email.getText().toString().trim();
                    String pass = password.getText().toString().trim();
                    editor.putString("user", user);
                    editor.putString("pass", pass);
                    editor.commit();
                }else{
                    editor.remove("user");
                    editor.remove("pass");
                    editor.commit();
                }
                user_login();
                //finish()
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_signup_activity();
            }
        });

    }



    private void start_signup_activity(){
        Intent intent = new Intent(getApplicationContext(), signup.class);
        startActivity(intent);
    }
    private void user_login(){
        final String username = this.email.getText().toString().trim();
        final String password = MD5(this.password.getText().toString());

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

        String url = "http://grupyservice.azurewebsites.net/UserService.svc/login/";


        JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Check the length of our response (to see if the user has any repos)
                        // The user does have repos, so let's loop through them all
                        //JSONObject obj = response;
                        try{
                            Integer status=response.getInt("ID_USER");
                            if(status>0) succesful=true;
                            Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();
                            editor.putInt("ID_USER", status);
                            editor.commit();
                            startAct(succesful);
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
        Toast.makeText(getApplicationContext(), "Authenticating", Toast.LENGTH_SHORT).show();

        requestQueue.add(arrReq);


    }

    private void startAct(Boolean succesful){
        if(succesful==true){  Toast.makeText(getApplicationContext(), "succesful", Toast.LENGTH_SHORT).show(); }
        //else Toast.makeText(getApplicationContext(), "unsuccesful", Toast.LENGTH_SHORT).show();
    }

    // zahashiramo password
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
}

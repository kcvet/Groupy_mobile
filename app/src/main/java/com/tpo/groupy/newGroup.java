package com.tpo.groupy;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class newGroup extends AppCompatActivity{

    EditText description, group_name, group_photo, place_to_stay, place_to_visit, number_of_people;
    Button add;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        description = (EditText) findViewById(R.id.description_group);
        group_name = (EditText) findViewById(R.id.name);
        group_photo = (EditText) findViewById(R.id.group_photo);
        place_to_stay = (EditText) findViewById(R.id.place_to_stay);
        place_to_visit = (EditText) findViewById(R.id.place_to_visit);
        number_of_people = (EditText) findViewById(R.id.number_of_people);
        add = (Button) findViewById(R.id.button_add);
        requestQueue = Volley.newRequestQueue(getApplicationContext());


        prefs = getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        editor = prefs.edit();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewGroup();
            }
        });

    }

    public void addNewGroup() {
        JSONObject parameters = new JSONObject();
        int user_id = prefs.getInt("ID_USER",0);

        try {
            parameters.put("place_to_stay", place_to_stay.getText().toString());
            parameters.put("place_to_visit", place_to_visit.getText().toString());
            parameters.put("name", group_name.getText().toString());
            parameters.put("group_photo", group_photo.getText().toString());
            parameters.put("description", description.getText().toString());
            parameters.put("number_of_people", number_of_people.getText().toString());
            parameters.put("hosted_by_user_id", user_id);
            parameters.put("created_by_user_id", user_id);
            parameters.put("project", 1); //change here for piknik project to 0



            Toast.makeText(getApplicationContext(), parameters.toString(), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {


        }
        //final String mRequestBody = jsonBody.toString();
        //status.setText(mRequestBody);

        String URL = "http://grupyservice.azurewebsites.net/GroupService.svc/";
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

                    finish();
                } catch (JSONException e) {
                    //Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_SHORT).show();
                    // If there is an error then output this to the logs.

                    Log.e("Volley", "Invalid JSON Object.");
                    finish();
                }

                Log.i("LOG_VOLLEY", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("LOG_VOLLEY", error.toString());
                finish();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }


        };
        requestQueue.add(stringRequest);


    }

}

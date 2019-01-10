package com.tpo.groupy;

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

import org.json.JSONException;
import org.json.JSONObject;

public class newGroup extends AppCompatActivity implements View.OnClickListener{

    EditText descript, group_name, group_photo, place_to_stay, place_to_visit, number_of_people;
    Button add;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        descript = (EditText) findViewById(R.id.description_group);
        group_name = (EditText) findViewById(R.id.name);
        group_photo = (EditText) findViewById(R.id.group_photo);
        place_to_stay = (EditText) findViewById(R.id.place_to_stay);
        place_to_visit = (EditText) findViewById(R.id.place_to_visit);
        number_of_people = (EditText) findViewById(R.id.number_of_people);
        add = (Button) findViewById(R.id.button_add);
        add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        JSONObject parameters = new JSONObject();

        try {
            parameters.put("place_to_stay", place_to_stay.getText().toString());
            parameters.put("place_to_visit", place_to_visit.getText().toString());
            parameters.put("name", group_name.getText().toString());
            parameters.put("group_photo", group_photo.getText().toString());
            parameters.put("description", descript.getText().toString());
            parameters.put("number_of_people", number_of_people.getText().toString());

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

}

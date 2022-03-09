package com.oet.quiz.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.hsalf.smileyrating.SmileyRating;
import com.oet.quiz.Database.DbHelper;
import com.oet.quiz.R;
import com.oet.quiz.WebServices.Constant;
import com.oet.quiz.WebServices.WebServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityFeedback extends AppCompatActivity {

    private final Constant mConstant = new Constant();
    private EditText Messsage;
    private SmileyRating smile_rating;
    private String UID;
    private Context mContext;
    private int rating;
    private ArrayList<String> Data = new ArrayList<>();
    private MaterialButton BtnFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        mContext = ActivityFeedback.this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ActivityDrawer.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });

        DbHelper dbHelper = new DbHelper(mContext);
        Data = dbHelper.GetAllUserData();
        UID = Data.get(0);
        FindViewByID();
        Body();
    }

    private void Body() {
        BtnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConstant.isNetworkAvailable(mContext)) {
                    if (rating < 0) {
                        Toast.makeText(mContext, "Select Smile", Toast.LENGTH_SHORT).show();
                    } else if (mConstant.isEmpty(Messsage.getText().toString().trim())) {
                        mConstant.vibrate(mContext);
                        Toast.makeText(mContext, "Write Message!", Toast.LENGTH_SHORT).show();
                    } else {
                        new FeedBack().execute();
                    }
                } else {
                    Toast.makeText(mContext, "No Internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        smile_rating.setSmileySelectedListener(new SmileyRating.OnSmileySelectedListener() {
            @Override
            public void onSmileySelected(SmileyRating.Type type) {
                rating = type.getRating();
            }
        });
    }

    public void FindViewByID() {
        smile_rating = findViewById(R.id.smile_rating);
        BtnFeedback = findViewById(R.id.BtnFeedback);
        Messsage = findViewById(R.id.UserMessage);
    }

    public class FeedBack extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            mConstant.AppColorShowProgressBar(mContext);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);
            StringRequest mStringRequest = new StringRequest(Request.Method.POST, WebServices.GetUserFeedback, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject mObject = new JSONObject(response);
                        String Message = mObject.getString("Message");
                        int Status = mObject.getInt("Status");
                        if (Status == 1) {
                            Toast.makeText(mContext, Message, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(mContext, ActivityDrawer.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error: ", "" + error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> param = new HashMap<>();
                    param.put("UID", UID);
                    param.put("Message", Messsage.getText().toString().trim());
                    param.put("Rating", "" + rating);
                    return param;
                }
            };
            mRequestQueue.add(mStringRequest);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mConstant.CloseProgressBar();
            super.onPostExecute(aVoid);
        }
    }
}
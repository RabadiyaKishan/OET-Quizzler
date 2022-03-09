package com.oet.quiz.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.oet.quiz.Database.DbHelper;
import com.oet.quiz.R;
import com.oet.quiz.WebServices.Constant;
import com.oet.quiz.WebServices.WebServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivityPasswordSettingUpFirstTime extends AppCompatActivity {
    private final Constant mConstant = new Constant();
    private Context mContext;
    private Intent mIntent;
    private EditText EtNewPassword, EtConfirmPassword;
    private TextView TextNewPasswordUpdate;
    private SharedPreferences mSharedPreferences;
    private String ID, FirstName, LastName, MobileNumber, DateTime, BoardID, StreamID, StateID, SchoolID, CityID, Email, Activity, FirebaseID, DeviceID;
    private DbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_setting_up);
        mContext = ActivityPasswordSettingUpFirstTime.this;
        mIntent = getIntent();
        Activity = mIntent.getStringExtra("Activity"); //Come From ActivityOTPVerify
        mDbHelper = new DbHelper(mContext);
        mDbHelper.EmptyData();
        FindViewByID();
        Body();
    }

    private void Body() {
        TextNewPasswordUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConstant.isNetworkAvailable(mContext)) {
                    if (mConstant.isEmpty(EtNewPassword.getText().toString().trim())) {
                        mConstant.vibrate(mContext);
                        EtNewPassword.requestFocus();
                        Toast.makeText(mContext, "Enter Password", Toast.LENGTH_SHORT).show();
                    } else if (mConstant.isEmpty(EtNewPassword.getText().toString().trim())) {
                        mConstant.vibrate(mContext);
                        EtConfirmPassword.requestFocus();
                        Toast.makeText(mContext, "Enter Confirm Password", Toast.LENGTH_SHORT).show();
                    } else if (EtNewPassword.getText().toString().trim().equals(EtConfirmPassword.getText().toString().trim())) {
                        if (Activity.equals("ActivitySelectStateCitySchool")) {
                            mSharedPreferences = getSharedPreferences(WebServices.RegisterProcess, Context.MODE_PRIVATE);

                            ID = mSharedPreferences.getString("ID", "");
                            FirstName = mSharedPreferences.getString("FirstName", "");
                            LastName = mSharedPreferences.getString("LastName", "");
                            MobileNumber = mSharedPreferences.getString("MobileNumber", "");
                            BoardID = mSharedPreferences.getString("BoardID", "");
                            StreamID = mSharedPreferences.getString("StreamID", "");
                            StateID = mSharedPreferences.getString("StateID", "");
                            SchoolID = mSharedPreferences.getString("SchoolID", "");
                            CityID = mSharedPreferences.getString("CityID", "");
                            Email = mSharedPreferences.getString("Email", "");

                            FirebaseID = "fnwagaoigernearnbuioaernboiueabniedbn";
                            DeviceID = "ndsgnoirseronoernoieranboearinb";

                            new RegisterUsers().execute();
                        } else if (Activity.equals("ActivityForgotPassword")) {
                            MobileNumber = mIntent.getStringExtra("MobileNumber"); //Come From ActivityOTPVerify
                            new PasswordChange().execute();
                        }
                    } else {
                        Toast.makeText(mContext, "Password Not Match!", Toast.LENGTH_SHORT).show();
                        mConstant.vibrate(mContext);
                        EtConfirmPassword.requestFocus();
                    }
                } else {
                    Toast.makeText(mContext, "No Internet!", Toast.LENGTH_SHORT).show();
                    mConstant.vibrate(mContext);
                }
            }
        });
    }

    private void FindViewByID() {
        EtNewPassword = findViewById(R.id.EtNewPassword);
        EtConfirmPassword = findViewById(R.id.EtConfirmPassword);
        TextNewPasswordUpdate = findViewById(R.id.TextNewPasswordUpdate);
    }

    public class RegisterUsers extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            mConstant.AppColorShowProgressBar(mContext);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);
            StringRequest mStringRequest = new StringRequest(Request.Method.POST, WebServices.UserRegister,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response:", response);
                            try {
                                JSONObject mJsonObject = new JSONObject(response);
                                int status = mJsonObject.getInt("Status");
                                String Message = mJsonObject.getString("Message");
                                if (status == 1) {

                                    SharedPreferences preferences = getSharedPreferences(WebServices.MYPREF, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("Email", Email);
                                    editor.apply();

                                    Toast.makeText(mContext, Message, Toast.LENGTH_SHORT).show();

                                    Intent mIntent = new Intent(mContext, ActivityDrawer.class);
                                    startActivity(mIntent);
                                    finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error:", "" + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> param = new HashMap<>();
                    param.put("Email", Email);
                    param.put("Password", EtNewPassword.getText().toString().trim());
                    param.put("FirstName", FirstName);
                    param.put("LastName", LastName);
                    param.put("MobileNumber", MobileNumber);
                    param.put("BoardID", BoardID);
                    param.put("StreamID", StreamID);
                    param.put("StateID", StateID);
                    param.put("SchoolID", SchoolID);
                    param.put("CityID", CityID);
                    param.put("FirebaseID", FirebaseID);
                    param.put("DeviceID", DeviceID);
                    return param;
                }
            };
            mRequestQueue.add(mStringRequest);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mConstant.CloseProgressBar();
        }
    }

    public class PasswordChange extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            mConstant.AppColorShowProgressBar(mContext);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);
            StringRequest mStringRequest = new StringRequest(Request.Method.POST, WebServices.UsersPasswordChange, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("response", "" + response);
                    try {
                        JSONObject mObject = new JSONObject();
                        String status = mObject.getString("Status");
                        String Message = mObject.getString("Message");
                        if (status.equals("1")) {
                            Toast.makeText(mContext, Message, Toast.LENGTH_SHORT).show();
                            Intent mIntent = new Intent(mContext, ActivityLogin.class);
                            startActivity(mIntent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error:", "" + error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> param = new HashMap<>();
                    param.put("MobileNumber", MobileNumber);
                    param.put("NewPassword", EtNewPassword.getText().toString());
                    return param;
                }
            };
            mRequestQueue.add(mStringRequest);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mConstant.CloseProgressBar();
        }
    }
}
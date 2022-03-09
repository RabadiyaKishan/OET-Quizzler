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
import com.oet.quiz.R;
import com.oet.quiz.WebServices.Constant;
import com.oet.quiz.WebServices.WebServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivityRegister extends AppCompatActivity {

    private final Constant mConstant = new Constant();
    private Context mContext;
    private EditText EtFirstName, EtLastName, EtEmail, EtMobileNumber;
    private TextView TextRegister, TextBackToLogin;
    private Intent mIntent;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = ActivityRegister.this;
        FindViewByID();
        Body();
    }

    private void FindViewByID() {
        EtFirstName = findViewById(R.id.EtFirstName);
        EtLastName = findViewById(R.id.EtLastName);
        EtEmail = findViewById(R.id.EtEmail);
        EtMobileNumber = findViewById(R.id.EtMobileNumber);
        TextRegister = findViewById(R.id.TextRegister);
        TextBackToLogin = findViewById(R.id.TextBackToLogin);
    }

    private void Body() {
        TextBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(mContext, ActivityLogin.class);
                startActivity(mIntent);
                finish();
            }
        });
        TextRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConstant.isNetworkAvailable(mContext)) {
                    if (mConstant.isEmpty(EtFirstName.getText().toString().trim())) {
                        mConstant.vibrate(mContext);
                        EtFirstName.requestFocus();
                        Toast.makeText(mContext, "Enter First Name", Toast.LENGTH_SHORT).show();
                    } else if (mConstant.isEmpty(EtLastName.getText().toString().trim())) {
                        mConstant.vibrate(mContext);
                        EtFirstName.requestFocus();
                        Toast.makeText(mContext, "Enter Last Name", Toast.LENGTH_SHORT).show();
                    } else if (mConstant.isEmpty(EtEmail.getText().toString().trim())) {
                        mConstant.vibrate(mContext);
                        EtFirstName.requestFocus();
                        Toast.makeText(mContext, "Enter Email", Toast.LENGTH_SHORT).show();
                    } else if (mConstant.Email_validation(EtEmail.getText().toString().trim())) {
                        mConstant.vibrate(mContext);
                        EtFirstName.requestFocus();
                        Toast.makeText(mContext, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                    } else if (mConstant.isEmpty(EtMobileNumber.getText().toString().trim())) {
                        mConstant.vibrate(mContext);
                        EtFirstName.requestFocus();
                        Toast.makeText(mContext, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                    } else {
                        new CheckMobileNumber().execute();
                    }
                } else {
                    Toast.makeText(mContext, "No Internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class CheckMobileNumber extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            mConstant.AppColorShowProgressBar(mContext);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            StringRequest sr = new StringRequest(Request.Method.POST, WebServices.CheckMobileNumberRegistered,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.d("response : ", response);
                                JSONObject mJsonObject = new JSONObject(response);
                                String Message = mJsonObject.getString("Message");
                                int status = mJsonObject.getInt("Status");
                                if (status == 1) {
                                    Toast.makeText(mContext, Message, Toast.LENGTH_SHORT).show();
                                } else if (status == 2) {
                                    //Saving Temporary Data In Shared Preferences
                                    mSharedPreferences = getSharedPreferences(WebServices.RegisterProcess, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                                    editor.putString("FirstName", EtFirstName.getText().toString().trim());
                                    editor.putString("LastName", EtLastName.getText().toString().trim());
                                    editor.putString("Email", EtEmail.getText().toString().trim());
                                    editor.putString("MobileNumber", EtMobileNumber.getText().toString().trim());
                                    editor.apply();

                                    String MobileNumber = mSharedPreferences.getString("MobileNumber", "");
                                    mIntent = new Intent(mContext, ActivityOTPVerify.class);
                                    mIntent.putExtra("Activity", "ActivityRegister");
                                    mIntent.putExtra("MobileNumber", MobileNumber);
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
                    Log.d("Error: ", "" + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> param = new HashMap<String, String>();
                    param.put("MobileNumber", EtMobileNumber.getText().toString().trim());
                    return param;
                }
            };
            requestQueue.add(sr);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mConstant.CloseProgressBar();
        }
    }
}
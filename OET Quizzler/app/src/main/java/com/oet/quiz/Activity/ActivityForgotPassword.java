package com.oet.quiz.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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

public class ActivityForgotPassword extends AppCompatActivity {
    private static final String TAG = "ActivityForgotPassword";
    private final Constant mConstant = new Constant();
    Context mContext;
    EditText EtMobileNumber;
    TextView BtnForgotPassword;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mContext = ActivityForgotPassword.this;
        FindViewByID();
        Body();
    }

    private void FindViewByID() {
        EtMobileNumber = findViewById(R.id.EtMobileNumber);
        BtnForgotPassword = findViewById(R.id.BtnForgotPassword);
    }

    private void Body() {
        BtnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(EtMobileNumber.getText().toString())) {
                    Toast.makeText(mContext, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
                } else {
                    new CheckMobileNumber().execute();
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
                            Log.d("response", "" + response);
                            try {
                                JSONObject mJsonObject = new JSONObject(response);
                                String Message = mJsonObject.getString("Message");
                                int status = mJsonObject.getInt("Status");
                                if (status == 1) {
                                    //Toast.makeText(mContext, Message, Toast.LENGTH_SHORT).show();
                                    String phone = EtMobileNumber.getText().toString();
                                    Intent intent = new Intent(mContext, ActivityOTPVerify.class);
                                    intent.putExtra("Activity", TAG);
                                    intent.putExtra("MobileNumber", phone);
                                    startActivity(intent);
                                    finish();
                                } else if (status == 2) {
                                    Toast.makeText(mContext, Message, Toast.LENGTH_SHORT).show();
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
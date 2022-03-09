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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.oet.quiz.R;
import com.oet.quiz.WebServices.Constant;
import com.oet.quiz.WebServices.WebServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivityLogin extends AppCompatActivity {

    private final Constant mConstant = new Constant();
    private Context mContext;
    private EditText EtLoginEmail, EtLoginPassword;
    private TextView TextSignUpLink, TextForgotPasswordLink;
    private MaterialButton BtnLoginButton;
    private Intent mIntent;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = ActivityLogin.this;
        mSharedPreferences = getSharedPreferences(WebServices.MYPREF, Context.MODE_PRIVATE);
        FindViewByID();
        Body();
    }

    public void FindViewByID() {
        EtLoginEmail = findViewById(R.id.EtLoginEmail);
        EtLoginPassword = findViewById(R.id.EtLoginPassword);
        BtnLoginButton = findViewById(R.id.BtnLoginButton);
        TextSignUpLink = findViewById(R.id.TextSignUpLink);
        TextForgotPasswordLink = findViewById(R.id.TextForgotPasswordLink);
    }

    public void Body() {
        BtnLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConstant.isNetworkAvailable(mContext)) {
                    if (mConstant.isEmpty(EtLoginEmail.getText().toString().trim())) {
                        mConstant.vibrate(mContext);
                        EtLoginEmail.requestFocus();
                        Toast.makeText(mContext, "Enter Email", Toast.LENGTH_SHORT).show();
                    } else if (mConstant.Email_validation(EtLoginEmail.getText().toString().trim())) {
                        mConstant.vibrate(mContext);
                        EtLoginEmail.requestFocus();
                        Toast.makeText(mContext, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                    } else if (mConstant.isEmpty(EtLoginPassword.getText().toString().trim())) {
                        Toast.makeText(mContext, "Enter Password", Toast.LENGTH_SHORT).show();
                    } else {
                        new UserLogin().execute();
                    }
                } else {
                    Toast.makeText(mContext, "No Internet !", Toast.LENGTH_SHORT).show();
                }
            }
        });
        TextForgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(mContext, ActivityForgotPassword.class);
                startActivity(mIntent);
            }
        });
        TextSignUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(mContext, ActivityRegister.class);
                startActivity(mIntent);
            }
        });
    }

    public class UserLogin extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            mConstant.AppColorShowProgressBar(mContext);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue queue = Volley.newRequestQueue(mContext);
            StringRequest sr = new StringRequest(Request.Method.POST, WebServices.Login,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mConstant.CloseProgressBar();
                            try {
                                Log.d("Response",""+response);
                                JSONObject mJsonObject = new JSONObject(response);
                                String Message = mJsonObject.getString("Message");
                                int status = mJsonObject.getInt("Status");
                                if (status == 2) { // Login Success
                                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                                    editor.putString("Email", EtLoginEmail.getText().toString().trim());
                                    editor.commit();
                                    Toast.makeText(mContext, Message, Toast.LENGTH_SHORT).show();
                                    mIntent = new Intent(mContext, ActivityDrawer.class);
                                    startActivity(mIntent);
                                    finish();
                                } else if (status == 2) { // Already Login
                                    Toast.makeText(mContext, Message, Toast.LENGTH_SHORT).show();
                                }
                                else if (status == 3) { // Login Fail
                                    Toast.makeText(mContext, Message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error", "" + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("u", EtLoginEmail.getText().toString().trim()); // passing enrollment number as username
                    params.put("p", EtLoginPassword.getText().toString().trim()); // passing enrollment number as password
                    return params;
                }
            };
            queue.add(sr);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mConstant.CloseProgressBar();
        }
    }
}
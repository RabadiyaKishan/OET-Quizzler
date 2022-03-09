package com.oet.quiz.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.oet.quiz.Adapter.CustomAdListener;
import com.oet.quiz.Database.DbHelper;
import com.oet.quiz.R;
import com.oet.quiz.WebServices.Constant;
import com.oet.quiz.WebServices.WebServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityPasswordChangeAfterLogin extends AppCompatActivity {

    private final Constant mConstant = new Constant();
    private Context mContext;
    private Intent mIntent;
    private TextInputEditText NewPassword, NewConfirmPassword;
    private MaterialButton BtnPasswordChange;
    private String MobileNumber;
    private ArrayList<String> Data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        mContext = ActivityPasswordChangeAfterLogin.this;
        DbHelper dbHelper = new DbHelper(mContext);
        Data = dbHelper.GetAllUserData();
        MobileNumber = Data.get(3);
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
        FindViewByID();
        Body();
    }

    private void FindViewByID() {
        NewPassword = findViewById(R.id.NewPassword);
        NewConfirmPassword = findViewById(R.id.NewConfirmPassword);
        BtnPasswordChange = findViewById(R.id.BtnPasswordChange);
    }

    private void Body() {
        MobileAds.initialize(mContext, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                AdView mAdViewFinal = findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdViewFinal.loadAd(adRequest);
                mAdViewFinal.setAdListener(new CustomAdListener());
            }
        });
        BtnPasswordChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConstant.isNetworkAvailable(mContext)) {
                    if (mConstant.isEmpty(NewPassword.getText().toString().trim())) {
                        mConstant.vibrate(mContext);
                        Toast.makeText(mContext, "Enter New Password!", Toast.LENGTH_SHORT).show();
                    } else if (mConstant.isEmpty(NewConfirmPassword.getText().toString().trim())) {
                        mConstant.vibrate(mContext);
                        Toast.makeText(mContext, "Enter Confirm Password!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (NewPassword.getText().toString().trim().equals(NewConfirmPassword.getText().toString().trim())) {
                            new PasswordChange().execute();
                        } else {
                            Toast.makeText(mContext, "Password Not Match!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(mContext, "No Internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                    try {
                        JSONObject mObject = new JSONObject(response);
                        int Status = mObject.getInt("Status");
                        String Message = mObject.getString("Message");
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

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> param = new HashMap<>();
                    param.put("MobileNumber", MobileNumber);
                    param.put("NewPassword", NewPassword.getText().toString().trim());
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
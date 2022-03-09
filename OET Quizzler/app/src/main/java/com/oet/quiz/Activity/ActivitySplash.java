package com.oet.quiz.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.oet.quiz.Database.DbHelper;
import com.oet.quiz.R;
import com.oet.quiz.WebServices.Constant;
import com.oet.quiz.WebServices.WebServices;

public class ActivitySplash extends AppCompatActivity {

    private final Constant mConstant = new Constant();
    private Context mContext;
    private Intent mIntent;
    private ProgressBar progressBar;
    private SharedPreferences mSharedPreferences;
    private String Email;
    private DbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = ActivitySplash.this;
        mDbHelper = new DbHelper(mContext);
        mDbHelper.EmptyData();
        progressBar = findViewById(R.id.ProgressBar);
        mSharedPreferences = getSharedPreferences(WebServices.MYPREF, Context.MODE_PRIVATE);
        Email = mSharedPreferences.getString("Email", null);
        if (Email != null) {
            new CountDownTimer(3000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish() {
                    mIntent = new Intent(mContext, ActivityDrawer.class);
                    startActivity(mIntent);
                    finish();
                }
            }.start();
        } else {
            new CountDownTimer(3000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish() {
                    mIntent = new Intent(mContext, ActivityLogin.class);
                    startActivity(mIntent);
                    finish();
                }
            }.start();
        }
    }


}
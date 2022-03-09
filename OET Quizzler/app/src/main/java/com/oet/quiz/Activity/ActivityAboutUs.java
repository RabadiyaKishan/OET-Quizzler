package com.oet.quiz.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.oet.quiz.R;

public class ActivityAboutUs extends AppCompatActivity {

    private Context mContext;
    private Intent mIntent;
    private TextView txtDiscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        mContext = ActivityAboutUs.this;
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

    @Override
    public void onBackPressed() {
        mIntent = new Intent(mContext, ActivityDrawer.class);
        startActivity(mIntent);
        finish();
    }

    private void FindViewByID() {
        txtDiscription = findViewById(R.id.txtDiscription);
    }

    private void Body() {
        txtDiscription.setText(Html.fromHtml("<b>About the Trust</b><br><br>" +
                "The Oshwal Education Trust, a charitable Trust, ventured into the field of education on 14th March 1986. Today, the trust has fourteen Educational institutions and a Nature cure and Yoga Research Centre." +
                "<br><br>" +
                "<b>About the Institution</b><br><br>" +
                "The Oshwal Education Trust managed Colleges sits on a ten-acre land in a pristine environment. It houses Eight Colleges and a study Centre for ICSI, with a total of nearly 1800 students." +
                "<br><br>" +
                "<b>Oshwal Education Trust</b><br><br>" +
                "The OET managed Colleges have indigenously prepared an App to successfully carry on the academic activities such as:" +
                "<br><br><b>&nbsp;&nbsp;MCQ test</b><br>" +
                "<br>Moreover, this App will prove helpful to the students as it is:" +
                "<br><br><b>&nbsp;&nbsp;User friendly</b>" +
                "<br><b>&nbsp;&nbsp;Easily accessible</b>" +
                "<br><b>&nbsp;&nbsp;Offers Flexible working time</b>" +
                "<br><br>This App is purely for academic purpose of schools students."));
    }
}
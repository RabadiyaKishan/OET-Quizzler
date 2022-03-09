package com.oet.quiz.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.oet.quiz.Database.DbHelper;
import com.oet.quiz.R;

import java.util.ArrayList;

public class ActivityProfile extends AppCompatActivity {

    private Context mContext;
    private ArrayList<String> Data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mContext = ActivityProfile.this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ActivityDrawer.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)));

        DbHelper dbHelper = new DbHelper(mContext);
        Data = dbHelper.GetAllUserData();

        FindViewByID();
    }

    private void FindViewByID() {
        TextView UserName = findViewById(R.id.UserName);
        TextView MobileNumber = findViewById(R.id.MobileNumber);
        TextView SchoolName = findViewById(R.id.SchoolName);
        TextView Stream = findViewById(R.id.Stream);
        TextView Board = findViewById(R.id.Board);
        TextView Email = findViewById(R.id.Email);

        UserName.setText(Data.get(1) + " " + Data.get(2));
        MobileNumber.setText(Data.get(3));
        SchoolName.setText(Data.get(16));
        Email.setText(Data.get(13));
        Stream.setText(Data.get(12));
        Board.setText(Data.get(11));
    }
}
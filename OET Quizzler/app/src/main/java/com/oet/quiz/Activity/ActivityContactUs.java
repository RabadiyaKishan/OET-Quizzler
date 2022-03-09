package com.oet.quiz.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.oet.quiz.R;
import static android.Manifest.permission.CALL_PHONE;

public class ActivityContactUs extends AppCompatActivity {

    private Context mContext;
    private Intent mIntent;
    private WebView myWebView;
    private TextView Location, CollegeContactNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        mContext = ActivityContactUs.this;
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
        myWebView = findViewById(R.id.mapview);
        Location = findViewById(R.id.Location);
        WebSettings webViewSettings = myWebView.getSettings();
        webViewSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webViewSettings.setJavaScriptEnabled(true);
        webViewSettings.setBuiltInZoomControls(true);
        webViewSettings.setPluginState(WebSettings.PluginState.ON);
        myWebView.loadData("<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3687.5156490758604!2d70.03714631495735!3d22.44724898524743!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3957152fe244ea63%3A0xc7550e6b9f20b9ee!2sOshwal%20Education%20Trust!5e0!3m2!1sen!2sin!4v1603695618661!5m2!1sen!2sin\" width=\"600\" height=\"450\" frameborder=\"0\" style=\"border:0;\" allowfullscreen=\"\" aria-hidden=\"false\" tabindex=\"0\"></iframe>", "text/html",
                "utf-8");
        Location.setText("Shri B.K.Shah Education Complex,\n" +
                "Ahead Octroi Post Indira Gandhi Marg,\n" +
                "Near Gokul Nagar,B/H Kailash Nagar,\n" +
                "Jamnagar, Gujarat 361004");
        Body();
    }

    @Override
    public void onBackPressed() {
        mIntent = new Intent(mContext, ActivityDrawer.class);
        startActivity(mIntent);
        finish();
    }

    private void FindViewByID() {
        CollegeContactNumber = findViewById(R.id.CollegeContactNumber);
    }

    private void Body() {
        CollegeContactNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:+ 91 0288 2573475"));

                if (ContextCompat.checkSelfPermission(mContext, CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{CALL_PHONE}, 1);
                    }
                }
            }
        });
    }
}
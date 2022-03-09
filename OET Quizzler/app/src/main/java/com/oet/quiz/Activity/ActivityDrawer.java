package com.oet.quiz.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.google.android.material.navigation.NavigationView;
import com.oet.quiz.Adapter.CustomAdListener;
import com.oet.quiz.Database.DbHelper;
import com.oet.quiz.Fragment.HomeFragment;
import com.oet.quiz.R;
import com.oet.quiz.WebServices.Constant;
import com.oet.quiz.WebServices.WebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivityDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final Constant mConstant = new Constant();
    DbHelper mDbHelper;
    private DrawerLayout mDrawerLayout;
    private Context mContext;
    private Activity mActivity;
    private String Email;
    private SharedPreferences mSharedPreferences;
    private TextView UserName, UserEmail;

    private static void shareApp(Context context) {
        final String appPackageName = "com.oet.quiz";
        final String appName = context.getString(R.string.app_name);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareBodyText = "https://play.google.com/store/apps/details?id=" + appPackageName;
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, appName);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
        context.startActivity(Intent.createChooser(shareIntent, "Share This Application : "));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        mContext = ActivityDrawer.this;
        mActivity = ActivityDrawer.this;

        mDbHelper = new DbHelper(mContext);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(mActivity, mDrawerLayout, toolbar, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) mContext);

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);

        HomeFragment mFragmentHome = new HomeFragment();
        setFragment(mFragmentHome);

        UserName = (TextView) headerLayout.findViewById(R.id.UserName);
        UserEmail = (TextView) headerLayout.findViewById(R.id.UserEmail);
        mSharedPreferences = getSharedPreferences(WebServices.MYPREF, Context.MODE_PRIVATE);
        Email = mSharedPreferences.getString("Email", null);
        if (Email != null) {
            new GetUserData().execute();
        }

        MobileAds.initialize(mContext, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                AdView mAdViewFinal = findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdViewFinal.loadAd(adRequest);
                mAdViewFinal.setAdListener(new CustomAdListener());
            }
        });
    }

    protected void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.FragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_exam:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(mContext, ActivityExamScheduleList.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;
            case R.id.nav_result:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(mContext, ActivityResultList.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;
            case R.id.nav_profile:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(mContext, ActivityProfile.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;
            case R.id.nav_password:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(mContext, ActivityPasswordChangeAfterLogin.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;
            case R.id.nav_feedback:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(mContext, ActivityFeedback.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;
            case R.id.nav_about:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(mContext, ActivityAboutUs.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;
            case R.id.nav_contact:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(mContext, ActivityContactUs.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;
            case R.id.nav_logout:
                mDrawerLayout.closeDrawers();

                SharedPreferences pref = getSharedPreferences(WebServices.MYPREF, MODE_PRIVATE);
                SharedPreferences.Editor Ed = pref.edit();
                Ed.clear();
                Ed.commit();

                SharedPreferences prefs = getSharedPreferences(WebServices.mypreference, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.commit();

                startActivity(new Intent(mContext, ActivityLogin.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;
            case R.id.nav_share:
                mDrawerLayout.closeDrawers();
                shareApp(mContext);
                break;
            case R.id.nav_rating:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.czmg.itgenius")));
                break;
        }
        return true;

    }

    public class GetUserData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue queue = Volley.newRequestQueue(mContext);
            StringRequest sr = new StringRequest(Request.Method.POST, WebServices.GetUserData,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response", response);
                            try {
                                JSONObject mJsonObject = new JSONObject(response);
                                JSONArray mJArray = mJsonObject.getJSONArray("Table");
                                for (int i = 0; i < mJArray.length(); i++) {
                                    JSONObject JSONData = mJArray.getJSONObject(i);
                                    String ID = JSONData.getString("ID");
                                    String FirstName = JSONData.getString("FirstName");
                                    String LastName = JSONData.getString("LastName");
                                    String MobileNumber = JSONData.getString("MobileNumber");
                                    String DateTime = JSONData.getString("DateTime");
                                    String BoardID = JSONData.getString("BoardID");
                                    String StreamID = JSONData.getString("StreamID");
                                    String AuthID = JSONData.getString("AuthID");
                                    String StateID = JSONData.getString("StateID");
                                    String SchoolID = JSONData.getString("SchoolID");
                                    String CityID = JSONData.getString("CityID");
                                    String BoardName = JSONData.getString("BoardName");
                                    String StreamName = JSONData.getString("StreamName");
                                    String Email = JSONData.getString("Email");
                                    String PasswordHash = JSONData.getString("PasswordHash");
                                    String State = JSONData.getString("State");
                                    String SchoolName = JSONData.getString("SchoolName");
                                    String City = JSONData.getString("City");

                                    mDbHelper.insertContact(ID, FirstName, LastName, MobileNumber, DateTime, BoardID, StreamID, AuthID, StateID, SchoolID, CityID, BoardName, StreamName, Email, PasswordHash, State, SchoolName, City);

                                    UserName.setText("" + FirstName + " " + LastName);
                                    UserEmail.setText("" + Email);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error : ", "" + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Email", Email);
                    return params;
                }
            };
            queue.add(sr);
            return null;
        }
    }
}

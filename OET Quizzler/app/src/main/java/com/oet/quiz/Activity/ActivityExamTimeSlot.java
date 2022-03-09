package com.oet.quiz.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.oet.quiz.Database.DbHelper;
import com.oet.quiz.Model.ModelTimeSlot;
import com.oet.quiz.R;
import com.oet.quiz.WebServices.Constant;
import com.oet.quiz.WebServices.WebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityExamTimeSlot extends AppCompatActivity {

    private final Constant mConstant = new Constant();
    ArrayList<ModelTimeSlot> data = new ArrayList<>();
    Context mContext;
    Intent mIntent;
    String ExamName, ExamImageURL, Duration, TimeTableID, UserID, TimeSlotID, SelectedDate;
    int i = 0;
    ImageView ExamImage;
    TextView Exam_Name, ExamDuration;
    LinearLayout ExamTimeSlotList;
    private ArrayList<String> Data = new ArrayList<>();
    private SwipeRefreshLayout pullToRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_time_slot);
        mContext = ActivityExamTimeSlot.this;
        mIntent = getIntent();
        TimeTableID = mIntent.getStringExtra("TimeTableID");
        ExamName = mIntent.getStringExtra("ExamName");
        ExamImageURL = mIntent.getStringExtra("ExamImageURL");
        Duration = mIntent.getStringExtra("Duration");
        DbHelper dbHelper = new DbHelper(mContext);
        Data = dbHelper.GetAllUserData();
        UserID = Data.get(0);
        Toolbar toolbar = findViewById(R.id.toolbar);
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
        if (mConstant.isNetworkAvailable(mContext)) {
            new GetTimeSlotList().execute();
        } else {
            Toast.makeText(mContext, "No Internet!", Toast.LENGTH_SHORT).show();
        }
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mConstant.isNetworkAvailable(mContext)) {
                    data.clear();
                    new GetTimeSlotList().execute();
                    pullToRefresh.setRefreshing(false);
                } else {
                    Toast.makeText(mContext, "No Internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void FindViewByID() {
        pullToRefresh = findViewById(R.id.pullToRefresh);
        ExamTimeSlotList = findViewById(R.id.ExamTimeSlotList);
        Exam_Name = findViewById(R.id.ExamName);
        ExamImage = findViewById(R.id.ExamImage);
        ExamDuration = findViewById(R.id.ExamDuration);

        Exam_Name.setText(ExamName);
        ExamDuration.setText("" + Duration);
        Glide.with(mContext).load(ExamImageURL).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(ExamImage);
    }


    private class GetTimeSlotList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);
            StringRequest mStringRequest = new StringRequest(Request.Method.POST, WebServices.GetTimeSlotList, response -> {
                Log.d("response", "" + response);
                try {
                    JSONObject mJsonObject = new JSONObject(response);
                    JSONArray mJsonArray = mJsonObject.getJSONArray("Table");
                    for (int i = 0; i < mJsonArray.length(); i++) {
                        JSONObject object = mJsonArray.getJSONObject(i);

                        ModelTimeSlot mModelTimeSlot = new ModelTimeSlot();

                        mModelTimeSlot.setID(object.getString("ID"));
                        mModelTimeSlot.setSelectedDates(object.getString("SelectedDates"));
                        mModelTimeSlot.setTimeSlotID(object.getString("TimeSlotID"));
                        mModelTimeSlot.setStartTime(object.getString("StartTime"));
                        mModelTimeSlot.setEndTime(object.getString("EndTime"));

                        data.add(mModelTimeSlot);
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter<String>(mContext, R.layout.time_slot, data.size());
                    for (int k = i; k < data.size(); k++) {

                        LayoutInflater inflater = LayoutInflater.from(mContext);
                        View v = inflater.inflate(R.layout.time_slot, ExamTimeSlotList, false);

                        TextView ExamDates = v.findViewById(R.id.ExamDates);
                        TextView ExamTimeSlot = v.findViewById(R.id.ExamTimeSlot);
                        Button BtnScheduleExam = v.findViewById(R.id.BtnScheduleExam);

                        ExamDates.setText("Date : " + data.get(k).getSelectedDates());
                        ExamTimeSlot.setText(data.get(k).getStartTime() + "  To  " + data.get(k).getEndTime());
                        int finalK = k;
                        BtnScheduleExam.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SelectedDate = data.get(finalK).getSelectedDates();
                                TimeSlotID = data.get(finalK).getTimeSlotID();
                                new ScheduleExamByUser().execute();
                            }
                        });
                        ExamTimeSlotList.addView(v);
                    }
                    i = data.size();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> Log.d("Error", "" + error)) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> param = new HashMap<>();
                    param.put("TimeTableID", "" + TimeTableID);
                    return param;
                }
            };
            mStringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            mRequestQueue.add(mStringRequest);
            return null;
        }
    }

    public class ScheduleExamByUser extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            mConstant.AppColorShowProgressBar(mContext);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue queue = Volley.newRequestQueue(mContext);
            StringRequest sr = new StringRequest(Request.Method.POST, WebServices.ScheduleExamByUser,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mConstant.CloseProgressBar();
                            try {
                                Log.d("Response", "" + response);
                                JSONObject mJsonObject = new JSONObject(response);
                                String Message = mJsonObject.getString("Message");
                                int status = mJsonObject.getInt("Status");
                                if (status == 1) { // ScheduleExamByUser success
                                    Toast.makeText(mContext, "" + Message, Toast.LENGTH_SHORT).show();
                                    mIntent = new Intent(mContext, ActivityDrawer.class);
                                    startActivity(mIntent);
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
                    Log.d("Error", "" + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("UserID", "" + UserID);
                    params.put("TimeSlotID", "" + TimeSlotID);
                    params.put("TimeTableID", "" + TimeTableID);
                    params.put("SelectedDate", "" + SelectedDate);
                    return params;
                }
            };
            queue.add(sr);

            return null;
        }

    }
}
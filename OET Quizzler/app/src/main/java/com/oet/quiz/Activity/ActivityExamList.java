package com.oet.quiz.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.oet.quiz.Database.DbHelper;
import com.oet.quiz.Model.ModelExamList;
import com.oet.quiz.R;
import com.oet.quiz.Receiver.AlertReceiver;
import com.oet.quiz.WebServices.Constant;
import com.oet.quiz.WebServices.WebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ActivityExamList extends AppCompatActivity {


    private final Constant mConstant = new Constant();
    private final ArrayList<ModelExamList> data = new ArrayList<>();
    private int i = 0;
    private Context mContext;
    private LinearLayout ExamList;
    private ArrayAdapter arrayAdapter;
    private SwipeRefreshLayout pullToRefresh;
    private String UserID, TID, Time, Limit;
    private Intent mIntent;
    private ArrayList<String> Data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_list);
        mContext = ActivityExamList.this;

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
            new GetExamList().execute();
        } else {
            Toast.makeText(mContext, "No Internet!", Toast.LENGTH_SHORT).show();
        }
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mConstant.isNetworkAvailable(mContext)) {
                    data.clear();
                    new GetExamList().execute();
                    pullToRefresh.setRefreshing(false);
                } else {
                    Toast.makeText(mContext, "No Internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void FindViewByID() {
        pullToRefresh = findViewById(R.id.pullToRefresh);
        ExamList = findViewById(R.id.ExamList);
    }

    class GetExamList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mConstant.AppColorShowProgressBar(mContext);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);
            StringRequest mStringRequest = new StringRequest(Request.Method.POST, WebServices.GetExamList, response -> {
                Log.d("response", "" + response);
                try {
                    JSONArray mJsonArray = new JSONArray(response);
                    for (int i = 0; i < mJsonArray.length(); i++) {
                        JSONObject object = mJsonArray.getJSONObject(i);

                        ModelExamList mModelExam = new ModelExamList();

                        mModelExam.setID(object.getString("ID"));
                        mModelExam.setName(object.getString("Name"));
                        mModelExam.setImages(object.getString("Images"));
                        mModelExam.setDuration(object.getString("Duration"));
                        mModelExam.setIsActive(object.getString("IsActive"));
                        mModelExam.setQuestionLimit(object.getString("QuestionLimit"));
                        mModelExam.setMarkingSystem(object.getString("MarkingSystem"));
                        mModelExam.setStartTime(object.getString("StartTime"));
                        mModelExam.setEndTime(object.getString("EndTime"));
                        mModelExam.setSelectedDate(object.getString("SelectedDate"));
                        mModelExam.setIsAttemptable(object.getBoolean("IsAttemptable"));
                        mModelExam.setTimeDiff(object.getString("TimeDiff"));

                        data.add(mModelExam);
                    }

                    arrayAdapter = new ArrayAdapter<String>(mContext, R.layout.exam_list, data.size());
                    for (int k = i; k < data.size(); k++) {

                        LayoutInflater inflater = LayoutInflater.from(mContext);
                        View v = inflater.inflate(R.layout.exam_list, ExamList, false);

                        TextView ExamName = v.findViewById(R.id.ExamName);
                        TextView ExamDate = v.findViewById(R.id.ExamDate);
                        TextView ExamStatus = v.findViewById(R.id.ExamStatus);
                        TextView ExamDurationTime = v.findViewById(R.id.ExamDurationTime);
                        TextView TotalQuestion = v.findViewById(R.id.TotalQuestion);
                        TextView MarkingSystem = v.findViewById(R.id.MarkingSystem);
                        ImageView ExamImage = v.findViewById(R.id.ExamImage);
                        Button BtnStartExam = v.findViewById(R.id.BtnStartExam);
                        TextView ExamTimeLeft = v.findViewById(R.id.ExamTimeLeft);
                        TextView ExamStartTime = v.findViewById(R.id.ExamStartTime);
                        TextView ExamEndTime = v.findViewById(R.id.ExamEndTime);

                        Glide.with(mContext).load(WebServices.ImageURL + data.get(k).getImages()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(ExamImage);
                        ExamName.setText(Html.fromHtml(data.get(k).getName()));
                        ExamDate.setText("Date : " + data.get(k).getSelectedDate());
                        TotalQuestion.setText("Total Questions : " + data.get(k).getQuestionLimit());
                        if (data.get(k).getMarkingSystem().equals("1")) {
                            String text = "<font color=#000000>Marking System : </font> <font color=#FF0000>Negative</font>";
                            MarkingSystem.setText(Html.fromHtml(text));
                        } else if (data.get(k).getMarkingSystem().equals("0")) {
                            String text = "<font color=#000000>Marking System : </font> <font color=#008000>Simple</font>";
                            MarkingSystem.setText(Html.fromHtml(text));
                        }
                        ExamDurationTime.setText("Duration : " + data.get(k).getDuration());

                        String TimeDiff = data.get(k).getTimeDiff();
                        if (!TimeDiff.equals("00:00:00")) {
                            String Time = TimeDiff.substring(0, 8);
                            String[] separated = Time.split(":");

                            int hour = Integer.parseInt(separated[0]);
                            int minutes = Integer.parseInt(separated[1]);
                            int second = Integer.parseInt(separated[2]);

                            long mills = (hour * 3600 * 1000) + (minutes * 60 * 1000) + (second * 1000);

                            int two_minutes_reminder = minutes - 2;
                            Calendar notification_time = Calendar.getInstance();
                            notification_time.set(Calendar.HOUR_OF_DAY, hour);
                            notification_time.set(Calendar.MINUTE, two_minutes_reminder);
                            notification_time.set(Calendar.SECOND, second);

                            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            Intent intent = new Intent(mContext, AlertReceiver.class);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, Math.abs(notification_time.getTimeInMillis()), PendingIntent.getBroadcast(mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT));

                            new CountDownTimer(mills, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    long millis = millisUntilFinished;
                                    String END_TIME = String.format("%02d:%02d:%02d",
                                            (TimeUnit.MILLISECONDS.toHours(millis) -
                                                    TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis))),
                                            (TimeUnit.MILLISECONDS.toMinutes(millis) -
                                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))),
                                            (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
                                    ExamTimeLeft.setText("Time Left : " + END_TIME);
                                }

                                public void onFinish() {
                                    ExamTimeLeft.setVisibility(View.GONE);
                                    BtnStartExam.setVisibility(View.GONE);
                                }
                            }.start();
                        }

                        if (data.get(k).getIsActive().equals("0")) {
                            String text = "<font color=#000000>Status : </font> <font color=#FF0000>Not Active</font>";
                            ExamStatus.setText(Html.fromHtml(text));
                        } else if (data.get(k).getIsActive().equals("1")) {
                            String text = "<font color=#000000>Status : </font> <font color=#008000>Active</font>";
                            ExamStatus.setText(Html.fromHtml(text));
                        }
                        ExamStartTime.setText("Start Time : " + data.get(k).getStartTime());
                        ExamEndTime.setText("End Time : " + data.get(k).getEndTime());

                        if (!data.get(k).getIsAttemptable()) {
                            ExamStartTime.setVisibility(View.GONE);
                            ExamEndTime.setVisibility(View.GONE);
                            ExamTimeLeft.setVisibility(View.GONE);
                            BtnStartExam.setVisibility(View.GONE);
                        }

                        int position = k;
                        BtnStartExam.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String Message = "<b>Are you sure you want to start exam now!.</b>" +
                                        "<br><br>" +
                                        "once you start the exam you need no complete test!";
                                new MaterialAlertDialogBuilder(mContext, R.style.AlertDialogTheme)
                                        .setTitle("Warning !")
                                        .setIcon(R.drawable.ic_error)
                                        .setMessage(Html.fromHtml(Message))
                                        .setPositiveButton("START", (dialogInterface, i) -> {
                                            TID = data.get(position).getID();
                                            Limit = data.get(position).getQuestionLimit();
                                            Time = data.get(position).getDuration();
                                            new CheckExamIsAttemplted().execute();
                                        })
                                        .setNeutralButton("LATER", (dialogInterface, i) -> dialogInterface.dismiss())
                                        .show();
                            }
                        });
                        ExamList.addView(v);
                    }
                    i = data.size();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> Log.d("Error", "" + error)) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> param = new HashMap<>();
                    param.put("UserID", "" + UserID);
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mConstant.CloseProgressBar();
        }
    }

    private class CheckExamIsAttemplted extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);
            StringRequest mStringRequest = new StringRequest(Request.Method.POST, WebServices.CheckExamIsAttemplted, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("response",""+response);
                        JSONObject mObject = new JSONObject(response);
                        String Message = mObject.getString("Message");
                        int status = mObject.getInt("Status");
                        if (status == 1) { // Exam Start
                            mIntent = new Intent(mContext, ActivityLoadQuestionList.class);
                            mIntent.putExtra("TID", TID);
                            mIntent.putExtra("Limit", Limit);
                            mIntent.putExtra("Time", Time);
                            startActivity(mIntent);
                            finish();
                        } else if (status == 2) {//Exam already Given
                            Toast.makeText(mContext, Message, Toast.LENGTH_SHORT).show();
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
                    param.put("TID", "" + TID);
                    param.put("UID", "" + UserID);
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
}
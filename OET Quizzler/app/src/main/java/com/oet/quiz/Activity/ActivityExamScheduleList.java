package com.oet.quiz.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.oet.quiz.Database.DbHelper;
import com.oet.quiz.Model.ModelExamSchedule;
import com.oet.quiz.R;
import com.oet.quiz.WebServices.Constant;
import com.oet.quiz.WebServices.WebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityExamScheduleList extends AppCompatActivity {

    private final Constant mConstant = new Constant();
    private final ArrayList<ModelExamSchedule> data = new ArrayList<>();
    private Context mContext;
    private LinearLayout ExamList;
    private ArrayAdapter arrayAdapter;
    private SwipeRefreshLayout pullToRefresh;
    private int i = 0;
    private Intent mIntent;
    private ArrayList<String> Data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_schedule_list);
        mContext = ActivityExamScheduleList.this;

        DbHelper dbHelper = new DbHelper(mContext);
        Data = dbHelper.GetAllUserData();

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
            StringRequest mStringRequest = new StringRequest(Request.Method.POST, WebServices.GetTimeTableList, response -> {
                Log.d("response", "" + response);
                try {
                    JSONObject mJsonObject = new JSONObject(response);
                    JSONArray mJsonArray = mJsonObject.getJSONArray("Table");
                    for (int i = 0; i < mJsonArray.length(); i++) {
                        JSONObject object = mJsonArray.getJSONObject(i);

                        ModelExamSchedule mModelExam = new ModelExamSchedule();

                        mModelExam.setID(object.getString("ID"));
                        mModelExam.setName(object.getString("Name"));
                        mModelExam.setImages(object.getString("Images"));
                        mModelExam.setDuration(object.getString("Duration"));
                        mModelExam.setIsActive(object.getString("IsActive"));
                        mModelExam.setQuestionLimit(object.getString("QuestionLimit"));
                        mModelExam.setDateTime(object.getString("DateTime"));
                        mModelExam.setMarkingSystem(object.getString("MarkingSystem"));

                        data.add(mModelExam);
                    }

                    arrayAdapter = new ArrayAdapter<String>(mContext, R.layout.exam_schedule, data.size());
                    for (int k = i; k < data.size(); k++) {

                        LayoutInflater inflater = LayoutInflater.from(mContext);
                        View v = inflater.inflate(R.layout.exam_schedule, ExamList, false);

                        TextView ExamName = v.findViewById(R.id.ExamName);
                        TextView ExamDate = v.findViewById(R.id.ExamDate);
                        TextView ExamStatus = v.findViewById(R.id.ExamStatus);
                        TextView ExamDurationTime = v.findViewById(R.id.ExamDurationTime);
                        TextView TotalQuestion = v.findViewById(R.id.TotalQuestion);
                        TextView MarkingSystem = v.findViewById(R.id.MarkingSystem);
                        ImageView ExamImage = v.findViewById(R.id.ExamImage);
                        Button BtnScheduleExam = v.findViewById(R.id.BtnScheduleExam);

                        Glide.with(mContext).load(WebServices.ImageURL + data.get(k).getImages()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(ExamImage);
                        ExamName.setText(Html.fromHtml(data.get(k).getName()));
                        ExamDate.setText("Date : " + data.get(k).getDateTime());
                        TotalQuestion.setText("Total Questions : " + data.get(k).getQuestionLimit());
                        if (data.get(k).getMarkingSystem().equals("1")) {
                            String text = "<font color=#000000>Marking System : </font> <font color=#FF0000>Negative</font>";
                            MarkingSystem.setText(Html.fromHtml(text));
                        } else if (data.get(k).getMarkingSystem().equals("0")) {
                            String text = "<font color=#000000>Marking System : </font> <font color=#008000>Simple</font>";
                            MarkingSystem.setText(Html.fromHtml(text));
                        }
                        ExamDurationTime.setText("Duration : " + data.get(k).getDuration());
                        if (data.get(k).getIsActive().equals("0")) {
                            String text = "<font color=#000000>Status : </font> <font color=#FF0000>Not Active</font>";
                            ExamStatus.setText(Html.fromHtml(text));
                        } else if (data.get(k).getIsActive().equals("1")) {
                            String text = "<font color=#000000>Status : </font> <font color=#008000>Active</font>";
                            ExamStatus.setText(Html.fromHtml(text));
                        }
                        int finalK = k;
                        BtnScheduleExam.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent mIntent = new Intent(mContext, ActivityExamTimeSlot.class);
                                mIntent.putExtra("TimeTableID", "" + data.get(finalK).getID());
                                mIntent.putExtra("ExamName", "" + data.get(finalK).getName());
                                mIntent.putExtra("ExamImageURL", "" + WebServices.ImageURL + data.get(finalK).getImages());
                                mIntent.putExtra("Duration", "" + data.get(finalK).getDuration());
                                startActivity(mIntent);
                                finish();
                            }
                        });
                        /*int position = k;
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String Message = "<b>Are you sure you want to start exam now!.</b>" +
                                        "<br><br>" +
                                        "Once You Start The Exam You Need To Complete Test!";
                                new MaterialAlertDialogBuilder(mContext, R.style.AlertDialogTheme)
                                        .setTitle("Alert!")
                                        .setIcon(R.drawable.ic_error)
                                        .setMessage(Html.fromHtml(Message))
                                        .setPositiveButton("START", (dialogInterface, i) -> {
                                            mIntent = new Intent(mContext, ActivityLoadQuestionList.class);
                                            mIntent.putExtra("TID", data.get(position).getID());
                                            mIntent.putExtra("Time", data.get(position).getDuration());
                                            startActivity(mIntent);
                                        })
                                        .setNeutralButton("LATER", (dialogInterface, i) -> dialogInterface.dismiss())
                                        .show();
                            }
                        });*/
                        ExamList.addView(v);
                    }
                    i = data.size();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> Log.d("Error", "" + error));
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
}
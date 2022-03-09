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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.oet.quiz.Database.DbHelper;
import com.oet.quiz.Model.ModelResult;
import com.oet.quiz.R;
import com.oet.quiz.WebServices.Constant;
import com.oet.quiz.WebServices.WebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ActivityResultList extends AppCompatActivity {

    private final Constant mConstant = new Constant();
    private final ArrayList<ModelResult> data = new ArrayList<>();
    private Context mContext;
    private LinearLayout ResultList;
    private SwipeRefreshLayout pullToRefresh;
    private String UID;
    private int i = 0;
    private ArrayList<String> Userdata = new ArrayList<>();
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);
        mContext = ActivityResultList.this;
        DbHelper dbHelper = new DbHelper(mContext);
        Userdata = dbHelper.GetAllUserData();
        UID = Userdata.get(0);
        Log.d("UID", UID);
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

    public void FindViewByID() {
        pullToRefresh = findViewById(R.id.pullToRefresh);
        ResultList = findViewById(R.id.ResultList);
    }

    public void Body() {
        if (mConstant.isNetworkAvailable(mContext)) {
            new GetResultList().execute();
        } else {
            Toast.makeText(mContext, "No Internet!", Toast.LENGTH_SHORT).show();
        }
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mConstant.isNetworkAvailable(mContext)) {
                    data.clear();
                    new GetResultList().execute();
                    pullToRefresh.setRefreshing(false);
                } else {
                    Toast.makeText(mContext, "No Internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class GetResultList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            mConstant.AppColorShowProgressBar(mContext);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);
            StringRequest mStringRequest = new StringRequest(Request.Method.POST, WebServices.GetUserResult, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("response",""+response);
                        JSONObject mJsonObject = new JSONObject(response);
                        JSONArray mJsonArray = mJsonObject.getJSONArray("Table");
                        for (int i = 0; i < mJsonArray.length(); i++) {

                            JSONObject mObject = mJsonArray.getJSONObject(i);

                            ModelResult mModelResult = new ModelResult();

                            mModelResult.setID(mObject.getString("ID"));
                            mModelResult.setWrongAnswer(mObject.getString("WrongAnswer"));
                            mModelResult.setRightAnswer(mObject.getString("RightAnswer"));
                            mModelResult.setTotalQuestion(mObject.getString("TotalQuestion"));
                            mModelResult.setAttemptTime(mObject.getString("AttemptTime"));
                            mModelResult.setDuration(mObject.getString("Duration"));
                            mModelResult.setExamName(mObject.getString("ExamName"));
                            mModelResult.setImageURL(mObject.getString("Images"));

                            data.add(mModelResult);
                        }

                        arrayAdapter = new ArrayAdapter<String>(mContext, R.layout.result_list, data.size());
                        for (int k = i; k < data.size(); k++) {
                            LayoutInflater inflater = LayoutInflater.from(mContext);
                            View v = inflater.inflate(R.layout.result_list, ResultList, false);

                            TextView txt_quiz_name = v.findViewById(R.id.txt_quiz_name);
                            TextView txt_date_time = v.findViewById(R.id.txt_date_time);
                            TextView txt_total_question_attempted = v.findViewById(R.id.txt_total);
                            TextView txt_wrong_answer = v.findViewById(R.id.txt_wrong_answer);
                            TextView txt_right_answer = v.findViewById(R.id.txt_right_answer);
                            TextView txt_duration = v.findViewById(R.id.txt_duration);
                            ImageView ExamImage = v.findViewById(R.id.ExamImage);


                            String ExamName = "<b>" + data.get(k).getExamName() + "</b>";
                            txt_quiz_name.setText(Html.fromHtml(ExamName));
                            txt_date_time.setText("Date : " + data.get(k).getAttemptTime());
                            txt_total_question_attempted.setText("Total Questions : " + "" + data.get(k).getTotalQuestion());
                            txt_wrong_answer.setText("Wrong : " + "" + data.get(k).getWrongAnswer());
                            txt_right_answer.setText("Right : " + "" + data.get(k).getRightAnswer());
                            txt_duration.setText("Consume Time : " + "" + data.get(k).getDuration());

                            Glide.with(mContext).load(WebServices.ImageURL + data.get(k).getImageURL()).into(ExamImage);

                            ResultList.addView(v);

                            int position = k;
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent mIntent = new Intent(mContext,ActivityViewAttemptedQuestionList.class);
                                    mIntent.putExtra("ResultID",""+data.get(position).getID());
                                    startActivity(mIntent);
                                }
                            });
                        }
                        i = data.size();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error: ", "" + error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> param = new HashMap<>();
                    param.put("UID", UID);
                    return param;
                }
            };
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
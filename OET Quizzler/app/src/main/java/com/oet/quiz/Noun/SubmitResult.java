package com.oet.quiz.Noun;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.oet.quiz.Activity.ActivityResult;
import com.oet.quiz.Services.TimerService;
import com.oet.quiz.WebServices.Constant;
import com.oet.quiz.WebServices.WebServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SubmitResult extends AsyncTask<Void, Void, String> {

    private final int WrongAnswer;
    private final int RightAnswer;
    private final int TotalAttemptedQuestion;
    private final String UserID;
    private final String TimeTableID;
    private final String ConsumedTime;
    String QuestionList;
    String  AnswerList;
    private final Context mContext;
    public Constant mC = new Constant();
    private int second;
    private Intent mIntent;

    public SubmitResult(Context mContext) {

        this.mContext = mContext;

        mContext.stopService(new Intent(mContext, TimerService.class));

        SharedPreferences prefs = mContext.getSharedPreferences(WebServices.mypreference, Context.MODE_PRIVATE);

        UserID = prefs.getString("UserID", "");
        TimeTableID = prefs.getString("TimeTableID", "");
        WrongAnswer = prefs.getInt("WrongAnswer", 0);
        RightAnswer = prefs.getInt("RightAnswer", 0);
        TotalAttemptedQuestion = prefs.getInt("TotalAttemptedQuestion", 0);
        ConsumedTime = prefs.getString("ConsumedTime", "");
        QuestionList = prefs.getString("QuestionList", "");
        AnswerList = prefs.getString("AnswerList", "");

        Log.d("Result:", UserID + " : " + TimeTableID + " : " + WrongAnswer + " : " + RightAnswer + " : " + TotalAttemptedQuestion + " : " + ConsumedTime + " : " + QuestionList + " : " + AnswerList);
    }

    @Override
    protected String doInBackground(Void... voids) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);
        StringRequest mStringRequest = new StringRequest(Request.Method.POST, WebServices.SubmitResult, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    mContext.stopService(new Intent(mContext, TimerService.class));
                    Log.d("response",""+response);
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("Status");
                    String message = jsonObject.getString("Message");
                    if (status == 1) {
                        mIntent = new Intent(mContext, ActivityResult.class);
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mIntent.putExtra("WrongAnswer", "" + WrongAnswer);
                        mIntent.putExtra("RightAnswer", "" + RightAnswer);
                        mIntent.putExtra("TotalAttemptedQuestion", "" + TotalAttemptedQuestion);
                        mContext.startActivity(mIntent);
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error:", "" + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                param.put("TID", "" + TimeTableID);
                param.put("UID", "" + UserID);
                param.put("Wrong", "" + WrongAnswer);
                param.put("Right", "" + RightAnswer);
                param.put("Total", "" + TotalAttemptedQuestion);
                param.put("Duration", "" + ConsumedTime);
                param.put("QuestionList", "" + QuestionList);
                param.put("AnswerList", "" + AnswerList);
                return param;
            }
        };
        mRequestQueue.add(mStringRequest);
        return null;
    }
}
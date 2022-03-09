package com.oet.quiz.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.oet.quiz.Model.ModelQuestion;
import com.oet.quiz.R;
import com.oet.quiz.WebServices.Constant;
import com.oet.quiz.WebServices.WebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityViewAttemptedQuestionList extends AppCompatActivity {

    Constant mConstant = new Constant();
    ArrayList<ModelQuestion> Data = new ArrayList<>();
    Context mContext;
    String ResultID;
    Intent mIntent;
    private int i = 0;
    private LinearLayout QuestionList;
    private ArrayAdapter arrayAdapter;
    private SwipeRefreshLayout pullToRefresh;
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attempted_question_list);
        mContext = ActivityViewAttemptedQuestionList.this;
        mIntent = getIntent();
        ResultID = mIntent.getStringExtra("ResultID");

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
            new getAllQuestion().execute();
        } else {
            Toast.makeText(mContext, "No Internet!", Toast.LENGTH_SHORT).show();
        }
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mConstant.isNetworkAvailable(mContext)) {
                    Data.clear();
                    new getAllQuestion().execute();
                    pullToRefresh.setRefreshing(false);
                } else {
                    Toast.makeText(mContext, "No Internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void FindViewByID() {
        pullToRefresh = findViewById(R.id.pullToRefresh);
        QuestionList = findViewById(R.id.QuestionList);
    }

    public class getAllQuestion extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            mConstant.AppColorShowProgressBar(mContext);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);
            StringRequest mStringRequest = new StringRequest(Request.Method.POST, WebServices.GetUserExamResult, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("Response", "" + response);
                        JSONArray mJsonArray = new JSONArray(response);
                        for (int i = 0; i < mJsonArray.length(); i++) {
                            JSONObject mObject = mJsonArray.getJSONObject(i);
                            ModelQuestion modelQuestion = new ModelQuestion();

                            modelQuestion.setID(mObject.getString("ID"));
                            modelQuestion.setQuestion(mObject.getString("Question"));
                            modelQuestion.setOptionOne(mObject.getString("OptionOne"));
                            modelQuestion.setOptionTwo(mObject.getString("OptionTwo"));
                            modelQuestion.setOptionThree(mObject.getString("OptionThree"));
                            modelQuestion.setOptionFour(mObject.getString("OptionFour"));
                            modelQuestion.setAnswerNumber(mObject.getString("AnswerNumber"));
                            modelQuestion.setSelectedAnswer(mObject.getString("SelectedAnswer"));
                            modelQuestion.setTID(mObject.getString("TID"));

                            Data.add(modelQuestion);
                        }

                        arrayAdapter = new ArrayAdapter<String>(mContext, R.layout.question_list, Data.size());
                        for (int k = i; k < Data.size(); k++) {

                            LayoutInflater inflater = LayoutInflater.from(mContext);
                            View v = inflater.inflate(R.layout.question_list, QuestionList, false);

                            TextView txtQuestion = v.findViewById(R.id.txtQuestion);
                            TextView optionOne = v.findViewById(R.id.optionOne);
                            TextView optionTwo = v.findViewById(R.id.OptionTwo);
                            TextView OptionThree = v.findViewById(R.id.OptionThree);
                            TextView optionFour = v.findViewById(R.id.optionFour);
                            TextView RightAnswer = v.findViewById(R.id.RightAnswer);
                            TextView SelectedAnswer = v.findViewById(R.id.SelectedAnswer);
                            TextView norms = v.findViewById(R.id.norms);

                            txtQuestion.setText(Data.get(k).getQuestion());
                            optionOne.setText("1). " + Data.get(k).getOptionOne());
                            optionTwo.setText("2). " + Data.get(k).getOptionTwo());
                            OptionThree.setText("3). " + Data.get(k).getOptionThree());
                            optionFour.setText("4). " + Data.get(k).getOptionFour());

                            int RightAnswerPosition = Integer.parseInt(Data.get(k).getAnswerNumber());
                            int SelectedAnswerPosition = Integer.parseInt(Data.get(k).getSelectedAnswer());

                            if (RightAnswerPosition == 1) {
                                RightAnswer.setText(Data.get(k).getOptionOne());
                            } else if (RightAnswerPosition == 2) {
                                RightAnswer.setText(Data.get(k).getOptionTwo());
                            } else if (RightAnswerPosition == 3) {
                                RightAnswer.setText(Data.get(k).getOptionThree());
                            } else if (RightAnswerPosition == 4) {
                                RightAnswer.setText(Data.get(k).getOptionFour());
                            }

                            if (SelectedAnswerPosition == 1) {
                                SelectedAnswer.setText(Data.get(k).getOptionOne());
                            } else if (SelectedAnswerPosition == 2) {
                                SelectedAnswer.setText(Data.get(k).getOptionTwo());
                            } else if (SelectedAnswerPosition == 3) {
                                SelectedAnswer.setText(Data.get(k).getOptionThree());
                            } else if (SelectedAnswerPosition == 4) {
                                SelectedAnswer.setText(Data.get(k).getOptionFour());
                            }


                            if (Data.get(k).getAnswerNumber().equals(Data.get(k).getSelectedAnswer())) {
                                SelectedAnswer.setVisibility(View.GONE);
                                norms.setVisibility(View.GONE);
                            }

                            QuestionList.addView(v);
                        }
                        i = Data.size();
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
                    param.put("ResultID", "" + ResultID);
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
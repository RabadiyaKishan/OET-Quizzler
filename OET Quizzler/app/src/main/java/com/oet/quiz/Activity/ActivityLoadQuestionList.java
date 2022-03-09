package com.oet.quiz.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.oet.quiz.Database.DbHelper;
import com.oet.quiz.Model.ModelQuestion;
import com.oet.quiz.Noun.SubmitResult;
import com.oet.quiz.R;
import com.oet.quiz.Services.TimerService;
import com.oet.quiz.WebServices.Constant;
import com.oet.quiz.WebServices.WebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ActivityLoadQuestionList extends AppCompatActivity {

    private final Constant mConstant = new Constant();
    private final ArrayList<ModelQuestion> questionModels = new ArrayList<>();
    private Boolean isTimeServiceStarted = true;
    private ArrayList<String> List = new ArrayList<String>();
    private Context mContext;
    private Intent mIntent;
    private TextView txtQuestion, txttime, QuestionNumber;
    private RadioButton optionOne, optionTwo, optionThree, optionFour;
    private Button btnPrevious, btnNext, btnResult;
    private RadioGroup radioGroup;
    private String QuestionLimit, MarkingSystem, Marks, END_TIME, ConsumedTime, TID, Time, UserID;
    private int counter = 0, LAST_QID, second, right = 0, wrong = 0, total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_question_list);
        mContext = ActivityLoadQuestionList.this;
        DbHelper dbHelper = new DbHelper(mContext);
        List = dbHelper.GetAllUserData();
        UserID = List.get(0);
        mIntent = getIntent();
        TID = mIntent.getStringExtra("TID");
        Time = mIntent.getStringExtra("Time");
        QuestionLimit = mIntent.getStringExtra("Limit");
        String[] split = Time.split(":");
        int hour = Integer.parseInt(split[0]);
        int min = Integer.parseInt(split[1]);
        int sec = Integer.parseInt(split[2]);
        second = (hour) * 60 * 60 + (min) * 60 + (sec);
        if (mConstant.isNetworkAvailable(mContext)) {
            new getAllQuestion().execute();
        } else {
            Toast.makeText(mContext, "No Internet!", Toast.LENGTH_SHORT).show();
        }
        FindViewByID();
        Body();
    }

    public void FindViewByID() {
        txtQuestion = findViewById(R.id.txtQuestion);
        QuestionNumber = findViewById(R.id.QuestionNumber);
        optionOne = findViewById(R.id.optionOne);
        optionTwo = findViewById(R.id.optionTwo);
        optionThree = findViewById(R.id.optionThree);
        optionFour = findViewById(R.id.optionFour);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnResult = findViewById(R.id.btn_result);
        radioGroup = findViewById(R.id.rg_option);
        txttime = findViewById(R.id.txtTime);
    }

    public void Body() {
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter == 0) {
                    Toast.makeText(mContext, "Pressed Next Button", Toast.LENGTH_SHORT).show();
                } else {
                    counter--;
                    loadQuestion(counter);
                    btnResult.setVisibility(View.GONE);
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    if (selectedId == -1) {
                        Toast.makeText(mContext, "Select Option !", Toast.LENGTH_SHORT).show();
                    } else {
                        RadioButton Radio_Button = findViewById(selectedId);
                        int SelectedIndex = radioGroup.indexOfChild(Radio_Button);
                        int Index = SelectedIndex + 1;
                        questionModels.get(counter).setSelectedAnswer(String.valueOf(Index));
                        int QID = Integer.parseInt(questionModels.get(counter).getID());
                        Log.d("Question Id :::: ", "" + QID);
                        if (LAST_QID == QID) {
                            btnResult.setVisibility(View.VISIBLE);
                        } else {
                            counter++;
                            loadQuestion(counter);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mConstant.isNetworkAvailable(mContext)) {
                    GenerateResult();
                    isTimeServiceStarted = false;
                    new SubmitResult(mContext).execute();
                } else {
                    Toast.makeText(mContext, "No Internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void GenerateResult() {
        StringBuilder QuestionList = new StringBuilder();
        StringBuilder AnswerList = new StringBuilder();
        if (mConstant.isNetworkAvailable(mContext)) {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            try {
                Date date2 = format.parse(Time);
                Date date1 = format.parse(END_TIME);
                long millis = date2.getTime() - date1.getTime();
                ConsumedTime = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(millis) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                Log.d("Consumed Time : ", "" + ConsumedTime);
                right = 0;
                wrong = 0;
                total = 0;
                for (int k = 0; k < questionModels.size(); k++) {
                    String Two = questionModels.get(k).getAnswerNumber();
                    String One = questionModels.get(k).getSelectedAnswer();

                    QuestionList.append(questionModels.get(k).getID());
                    QuestionList.append(",");

                    AnswerList.append(questionModels.get(k).getSelectedAnswer());
                    AnswerList.append(",");

                    int SelectedAnswer = Integer.parseInt(questionModels.get(k).getSelectedAnswer());
                    if (SelectedAnswer != -1) {
                        total++;
                        if (Two.equals(One)) {
                            right++;
                        } else {
                            wrong++;
                        }
                    }
                }

                Log.d("AnswerList", "" + AnswerList);
                Log.d("QuestionList", "" + QuestionList);

                SharedPreferences prefs = getSharedPreferences(WebServices.mypreference, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("UserID", UserID);
                editor.putString("TimeTableID", TID);
                editor.putString("Time", Time);
                editor.putInt("WrongAnswer", wrong);
                editor.putInt("RightAnswer", right);
                editor.putInt("TotalAttemptedQuestion", total);
                editor.putString("ConsumedTime", ConsumedTime);
                editor.putString("AnswerList", "" + AnswerList);
                editor.putString("QuestionList", "" + QuestionList);
                editor.apply();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(mContext, "No Internet !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        GenerateResult();
        if (isTimeServiceStarted) {
            startService(new Intent(mContext, TimerService.class));
            Log.i("Service:", "Started service");
            Log.d("Pause:", "OnPause");
        }
    }

    @Override
    protected void onResume() {
        Log.d("Resume:", "OnResume");
        super.onResume();
        stopService(new Intent(mContext, TimerService.class));
        SharedPreferences prefs = getSharedPreferences(WebServices.mypreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(mContext, TimerService.class));
        Log.i("Destroy:", "Stopped service");
    }

    public void startTimer(int _sec) {

        new CountDownTimer(_sec * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                END_TIME = String.format("%02d:%02d:%02d",
                        (TimeUnit.MILLISECONDS.toHours(millis) -
                                TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis))),
                        (TimeUnit.MILLISECONDS.toMinutes(millis) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))),
                        (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
                txttime.setText("Time Left : " + END_TIME);
            }

            public void onFinish() {
                Toast.makeText(mContext, "Time Finished !", Toast.LENGTH_SHORT).show();
                new SubmitResult(mContext).execute();
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, "complete exam !", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void loadQuestion(int counter) {
        int QuestionNumber_ = counter;
        int number = QuestionNumber_ + 1;
        QuestionNumber.setText("Question No : " + number);
        txtQuestion.setText(questionModels.get(counter).getQuestion());
        optionOne.setText(questionModels.get(counter).getOptionOne());
        optionTwo.setText(questionModels.get(counter).getOptionTwo());
        optionThree.setText(questionModels.get(counter).getOptionThree());
        optionFour.setText(questionModels.get(counter).getOptionFour());
        String USelectedIndex = questionModels.get(counter).getSelectedAnswer();
        switch (USelectedIndex) {
            case "-1":
                radioGroup.clearCheck();
                break;
            case "1":
                optionOne.setChecked(true);
                break;
            case "2":
                optionTwo.setChecked(true);
                break;
            case "3":
                optionThree.setChecked(true);
                break;
            case "4":
                optionFour.setChecked(true);
                break;
        }
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
            StringRequest mStringRequest = new StringRequest(Request.Method.POST, WebServices.GetQuestionList, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("response", "" + response);
                        JSONObject mJsonObject = new JSONObject(response);
                        JSONArray mJsonArray = mJsonObject.getJSONArray("Table");
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
                            modelQuestion.setTID(mObject.getString("TID"));
                            modelQuestion.setSelectedAnswer("-1");

                            questionModels.add(modelQuestion);
                            LAST_QID = Integer.parseInt(questionModels.get(questionModels.size() - 1).getID());
                            loadQuestion(counter);
                        }
                        startTimer(second);
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
                    param.put("TimeTableID", "" + TID);
                    param.put("Limit", "" + QuestionLimit);
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
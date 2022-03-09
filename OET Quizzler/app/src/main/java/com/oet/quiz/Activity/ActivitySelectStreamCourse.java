package com.oet.quiz.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.oet.quiz.Model.ModelBoard;
import com.oet.quiz.Model.ModelStream;
import com.oet.quiz.R;
import com.oet.quiz.WebServices.Constant;
import com.oet.quiz.WebServices.WebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivitySelectStreamCourse extends AppCompatActivity {

    private static final String TAG = "ActivitySelectStreamCourse";
    private final Constant mConstant = new Constant();
    private final ArrayList<ModelStream> mModelStreams = new ArrayList<ModelStream>();
    private final ArrayList<ModelBoard> mModelBoards = new ArrayList<ModelBoard>();
    private Context mContext;
    private Spinner Stream, Course;
    private TextView TextSelectBaordAndStream;
    private Intent mIntent;
    private String StreamID, BoardID;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_stream_course);
        mContext = ActivitySelectStreamCourse.this;
        Stream = findViewById(R.id.Stream);
        Course = findViewById(R.id.Course);
        TextSelectBaordAndStream = findViewById(R.id.TextSelectBaordAndStream);
        mSharedPreferences = getSharedPreferences(WebServices.RegisterProcess, Context.MODE_PRIVATE);
        new GetStreamList().execute();
        new GetBoardList().execute();
        Body();
    }

    private void Body() {
        TextSelectBaordAndStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConstant.isNetworkAvailable(mContext)) {
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString("StreamID", StreamID);
                    editor.putString("BoardID", BoardID);
                    editor.apply();

                    startActivity(new Intent(mContext, ActivitySelectStateCitySchool.class));
                    finish();
                } else {
                    Toast.makeText(mContext, "No Internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class GetStreamList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            mConstant.AppColorShowProgressBar(mContext);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue queue = Volley.newRequestQueue(mContext);
            StringRequest sr = new StringRequest(Request.Method.POST, WebServices.GetStreamList,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.d("response:", response);
                                JSONObject mJsonObject = new JSONObject(response);
                                JSONArray mJsonArray = mJsonObject.getJSONArray("Table");
                                for (int i = 0; i < mJsonArray.length(); i++) {
                                    JSONObject data = mJsonArray.getJSONObject(i);

                                    ModelStream modelStream = new ModelStream();

                                    modelStream.setID(data.getString("ID"));
                                    modelStream.setStreamName(data.getString("StreamName"));

                                    mModelStreams.add(modelStream);
                                }
                                ArrayList<String> arrayList = new ArrayList<>();
                                for (int i = 0; i < mModelStreams.size(); i++) {
                                    arrayList.add(mModelStreams.get(i).getStreamName());
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, R.layout.spinner_row, R.id.spinner_row, arrayList);
                                Stream.setAdapter(adapter);

                                Stream.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        StreamID = mModelStreams.get(position).getID();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error", "" + error.getMessage());
                }
            });
            queue.add(sr);
            return null;
        }
    }

    public class GetBoardList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue queue = Volley.newRequestQueue(mContext);
            StringRequest sr = new StringRequest(Request.Method.POST, WebServices.GetBoardList,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.d("response:", response);
                                JSONObject mJsonObject = new JSONObject(response);
                                JSONArray mJsonArray = mJsonObject.getJSONArray("Table");
                                for (int i = 0; i < mJsonArray.length(); i++) {
                                    JSONObject data = mJsonArray.getJSONObject(i);

                                    ModelBoard modelBoard = new ModelBoard();

                                    modelBoard.setID(data.getString("ID"));
                                    modelBoard.setBoardName(data.getString("BoardName"));

                                    mModelBoards.add(modelBoard);
                                }
                                ArrayList<String> arrayList = new ArrayList<>();
                                for (int i = 0; i < mModelBoards.size(); i++) {
                                    arrayList.add(mModelBoards.get(i).getBoardName());
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, R.layout.spinner_row, R.id.spinner_row, arrayList);
                                Course.setAdapter(adapter);

                                Course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        BoardID = mModelBoards.get(position).getID();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error", "" + error.getMessage());
                }
            });
            queue.add(sr);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mConstant.CloseProgressBar();
        }
    }
}

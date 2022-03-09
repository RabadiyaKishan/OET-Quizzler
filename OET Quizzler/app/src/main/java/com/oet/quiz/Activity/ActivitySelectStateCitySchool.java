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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.oet.quiz.Model.ModelCity;
import com.oet.quiz.Model.ModelSchool;
import com.oet.quiz.Model.ModelState;
import com.oet.quiz.R;
import com.oet.quiz.WebServices.Constant;
import com.oet.quiz.WebServices.WebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivitySelectStateCitySchool extends AppCompatActivity {

    Context mContext;
    Spinner State, City, School;
    TextView btnSubmit;
    Constant mConstant = new Constant();
    SharedPreferences mSharedPreferences;
    private String StateID, CityID, SchoolID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_state_city_chool);
        mContext = ActivitySelectStateCitySchool.this;
        mSharedPreferences = getSharedPreferences(WebServices.RegisterProcess, Context.MODE_PRIVATE);
        FindViewByID();
        Body();
    }

    public void FindViewByID() {
        City = findViewById(R.id.City);
        State = findViewById(R.id.State);
        School = findViewById(R.id.School);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    public void Body() {
        new GetStateList().execute();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConstant.isNetworkAvailable(mContext)) {
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString("StateID", StateID);
                    editor.putString("CityID", CityID);
                    editor.putString("SchoolID", SchoolID);
                    editor.apply();

                    Intent mIntent = new Intent(mContext, ActivityPasswordSettingUpFirstTime.class);
                    mIntent.putExtra("Activity", "ActivitySelectStateCitySchool");
                    startActivity(mIntent);
                    finish();
                } else {
                    Toast.makeText(mContext, "No Internet !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class GetStateList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            mConstant.AppColorShowProgressBar(mContext);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue queue = Volley.newRequestQueue(mContext);
            StringRequest sr = new StringRequest(Request.Method.POST, WebServices.GetStateList,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.d("response:", response);
                                ArrayList<ModelState> StateList = new ArrayList<>();
                                JSONObject mJsonObject = new JSONObject(response);
                                JSONArray mJsonArray = mJsonObject.getJSONArray("Table");
                                for (int i = 0; i < mJsonArray.length(); i++) {
                                    JSONObject data = mJsonArray.getJSONObject(i);

                                    ModelState mModelState = new ModelState();

                                    mModelState.setID(data.getString("ID"));
                                    mModelState.setStateName(data.getString("State"));

                                    StateList.add(mModelState);
                                }

                                ArrayList<String> arrayList = new ArrayList<>();
                                for (int i = 0; i < StateList.size(); i++) {
                                    arrayList.add(StateList.get(i).getStateName());
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, R.layout.spinner_row, R.id.spinner_row, arrayList);
                                State.setAdapter(adapter);

                                State.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        StateID = StateList.get(position).getID();
                                        new GetCityList().execute();
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

    public class GetCityList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue queue = Volley.newRequestQueue(mContext);
            StringRequest sr = new StringRequest(Request.Method.POST, WebServices.GetCityList,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.d("response:", response);
                                ArrayList<ModelCity> CityList = new ArrayList<>();
                                JSONObject mJsonObject = new JSONObject(response);
                                JSONArray mJsonArray = mJsonObject.getJSONArray("Table");
                                for (int i = 0; i < mJsonArray.length(); i++) {
                                    JSONObject data = mJsonArray.getJSONObject(i);

                                    ModelCity mModelState = new ModelCity();
                                    mModelState.setID(data.getString("ID"));
                                    mModelState.setCityName(data.getString("City"));

                                    CityList.add(mModelState);
                                }

                                ArrayList<String> arrayList = new ArrayList<>();
                                for (int i = 0; i < CityList.size(); i++) {
                                    arrayList.add(CityList.get(i).getCityName());
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, R.layout.spinner_row, R.id.spinner_row, arrayList);
                                City.setAdapter(adapter);

                                City.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        CityID = CityList.get(position).getID();
                                        new GetSchoolList().execute();
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
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> param = new HashMap<>();
                    param.put("StateID", "" + StateID);
                    return param;
                }
            };
            queue.add(sr);
            return null;
        }
    }

    public class GetSchoolList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue queue = Volley.newRequestQueue(mContext);
            StringRequest sr = new StringRequest(Request.Method.POST, WebServices.GetSchoolList,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.d("response:", response);
                                ArrayList<ModelSchool> SchoolList = new ArrayList<>();
                                JSONObject mJsonObject = new JSONObject(response);
                                JSONArray mJsonArray = mJsonObject.getJSONArray("Table");
                                for (int i = 0; i < mJsonArray.length(); i++) {
                                    JSONObject data = mJsonArray.getJSONObject(i);

                                    ModelSchool mModelState = new ModelSchool();
                                    mModelState.setID(data.getString("ID"));
                                    mModelState.setSchoolName(data.getString("SchoolName"));

                                    SchoolList.add(mModelState);
                                }

                                ArrayList<String> arrayList = new ArrayList<>();
                                for (int i = 0; i < SchoolList.size(); i++) {
                                    arrayList.add(SchoolList.get(i).getSchoolName());
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, R.layout.spinner_row, R.id.spinner_row, arrayList);
                                School.setAdapter(adapter);

                                School.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        SchoolID = SchoolList.get(position).getID();
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
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> param = new HashMap<>();
                    param.put("CityID", "" + CityID);
                    return param;
                }
            };
            queue.add(sr);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mConstant.CloseProgressBar();
        }
    }
}
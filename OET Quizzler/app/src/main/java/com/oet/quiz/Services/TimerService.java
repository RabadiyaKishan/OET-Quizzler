package com.oet.quiz.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.oet.quiz.Noun.SubmitResult;
import com.oet.quiz.R;
import com.oet.quiz.WebServices.WebServices;

import java.util.concurrent.TimeUnit;

public class TimerService extends Service {

    private final static String TAG = "TimerService";
    private final String Time = "00:00:30";
    private CountDownTimer cdt = null;
    private NotificationManager notificationManager;
    private int second, WrongAnswer, RightAnswer, TotalAttemptedQuestion;
    private String UserID, TimeTableID, ConsumedTime, NewTime, _Time, QuestionList, AnswerList;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = getSharedPreferences(WebServices.mypreference, MODE_PRIVATE);

        UserID = prefs.getString("UserID", "");
        TimeTableID = prefs.getString("TimeTableID", "");
        WrongAnswer = prefs.getInt("WrongAnswer", 0);
        RightAnswer = prefs.getInt("RightAnswer", 0);
        TotalAttemptedQuestion = prefs.getInt("TotalAttemptedQuestion", 0);
        ConsumedTime = prefs.getString("ConsumedTime", "");
        NewTime = prefs.getString("Time", "");
        AnswerList = prefs.getString("AnswerList", "");
        QuestionList = prefs.getString("QuestionList", "");

        Log.d("Result:", UserID + " : " + TimeTableID + " : " + WrongAnswer + " : " + RightAnswer + " : " + TotalAttemptedQuestion + " : " + ConsumedTime + " : " + QuestionList + " : " + AnswerList);

        String[] split = Time.split(":");
        int hour = Integer.parseInt(split[0]);
        int min = Integer.parseInt(split[1]);
        int sec = Integer.parseInt(split[2]);
        second = (hour) * 60 * 60 + (min) * 60 + (sec);

        Log.i(TAG, "Starting timer...");

        cdt = new CountDownTimer(second * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                _Time = String.format("%02d:%02d:%02d",
                        (TimeUnit.MILLISECONDS.toHours(millis) -
                                TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis))),
                        (TimeUnit.MILLISECONDS.toMinutes(millis) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))),
                        (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
                Log.d(TAG, "Countdown seconds remaining: " + _Time);
                if (_Time.equals("00:00:00")) {
                    Log.d("Time Count : ", "Time Over");
                    cdt.cancel();
                    new SubmitResult(getApplicationContext()).execute();
                } else {
                    showSmallNotification(R.drawable.logo, "Alert!", "Please get back to the exam otherwise your exam will get over by ");
                }
            }

            @Override
            public void onFinish() {
                cdt.cancel();
                Log.i(TAG, "Timer finished");
            }
        };
        cdt.start();
    }

    @Override
    public void onDestroy() {
        cdt.cancel();
        Log.i(TAG, "Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private void showSmallNotification(int icon, String title, String message) {
        String CHANNEL_ID = "001";
        String CHANNEL_NAME = "Notification";

        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(message);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.setLightColor(Color.BLUE);
            channel.enableLights(true);
            channel.setSound(null, null);
            new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setVibrate(new long[]{0, 100})
                .setPriority(Notification.PRIORITY_HIGH)
                .setLights(Color.BLUE, 3000, 3000)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setSound(null)
                .setSmallIcon(R.drawable.logo)
                .setStyle(inboxStyle)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), icon))
                .setContentText(message);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(CHANNEL_ID);
        }
        notificationManager.notify(CHANNEL_ID, 1, notificationBuilder.build());
    }
}
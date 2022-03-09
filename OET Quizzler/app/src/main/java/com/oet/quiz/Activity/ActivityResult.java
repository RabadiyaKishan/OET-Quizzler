package com.oet.quiz.Activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import com.oet.quiz.R;
import com.oet.quiz.Services.TimerService;

public class ActivityResult extends AppCompatActivity {

    private Intent mIntent;
    private NotificationManager notificationManager;
    private String Right, Wrong, Total;
    private Context mContext;
    private TextView TotalQuestionAttempted, RightAnswer, WrongAnswer, Obtain_Marks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mContext = ActivityResult.this;
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
        showSmallNotification(R.drawable.logo, "Message!", "Your exam has been submitted successfully!");
        stopService(new Intent(mContext, TimerService.class));
        mIntent = getIntent();
        Wrong = mIntent.getStringExtra("WrongAnswer");
        Right = mIntent.getStringExtra("RightAnswer");
        Total = mIntent.getStringExtra("TotalAttemptedQuestion");
        FindViewByID();
        Body();
    }

    private void Body() {
        RightAnswer.setText("Right Answers : " + Right);
        WrongAnswer.setText("Wrong Answers : " + Wrong);
        TotalQuestionAttempted.setText("Total Questions Attempted : " + Total);
        Obtain_Marks.setText("Obtained Marks : " + Total);
    }

    private void FindViewByID() {
        WrongAnswer = findViewById(R.id.WrongAnswer);
        TotalQuestionAttempted = findViewById(R.id.TotalQuestionAttempted);
        RightAnswer = findViewById(R.id.RightAnswer);
        Obtain_Marks = findViewById(R.id.ObtainMarks);
    }

    @Override
    public void onBackPressed() {
        mIntent = new Intent(mContext, ActivityDrawer.class);
        startActivity(mIntent);
        finish();
    }

    private void showSmallNotification(int icon, String title, String message) {
        String CHANNEL_ID = "0001";
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
        notificationManager.notify(CHANNEL_ID, 2, notificationBuilder.build());
    }

}
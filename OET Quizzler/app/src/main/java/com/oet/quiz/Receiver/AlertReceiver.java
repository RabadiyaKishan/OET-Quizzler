package com.oet.quiz.Receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.oet.quiz.R;
import com.oet.quiz.WebServices.Constant;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Receive:","onReceive");
        Constant.showSmallNotification(context, R.drawable.logo,"OET Quizzical","Your exam about to end !");
    }
}
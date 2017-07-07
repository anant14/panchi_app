package com.halfdotfull.panchi_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.halfdotfull.panchi_app.Activities.FakeRinging;

/**
 * Created by anant bansal on 6/4/2017.
 */

public class FakeCallReceiver extends BroadcastReceiver {

    public void onReceive(final Context context, Intent intent) {
        String getFakeName = intent.getStringExtra("FAKENAME");
        String getFakePhoneNumber = intent.getStringExtra("FAKENUMBER");
        AlarmManager manager= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intentObject = new Intent(context.getApplicationContext(),FakeRinging.class);
        intentObject.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentObject.putExtra("myfakename", getFakeName);
        intentObject.putExtra("myfakenumber", getFakePhoneNumber);
        java.util.Calendar calender= java.util.Calendar.getInstance();
        calender.setTimeInMillis(System.currentTimeMillis());
        calender.add(java.util.Calendar.SECOND, 10);
        long currentFakeTime = calender.getTimeInMillis();
        manager.set(AlarmManager.RTC_WAKEUP,currentFakeTime, PendingIntent.getActivities(context,0, new Intent[]{intentObject},PendingIntent.FLAG_CANCEL_CURRENT));
    }
}

package com.halfdotfull.panchi_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.halfdotfull.panchi_app.Activities.FakeRinging;

/**
 * Created by anant bansal on 6/4/2017.
 */

public class FakeCallReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        String getFakeName = intent.getStringExtra("FAKENAME");
        String getFakePhoneNumber = intent.getStringExtra("FAKENUMBER");

        Intent intentObject = new Intent(context.getApplicationContext(),FakeRinging.class);
        intentObject.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentObject.putExtra("myfakename", getFakeName);
        intentObject.putExtra("myfakenumber", getFakePhoneNumber);
        context.startActivity(intentObject);
    }
}

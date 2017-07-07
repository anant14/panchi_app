package com.halfdotfull.panchi_app.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

/**
 * Created by anant bansal on 6/4/2017.
 */

public class ScreenReceiver extends BroadcastReceiver {

    public boolean isScreenOn() {
        return screenOn;
    }

    public boolean screenOn=true;
    IntentFilter filter=new IntentFilter(Intent.ACTION_SCREEN_ON);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOn = false;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOn = true;
            Toast.makeText(context, "Screen On", Toast.LENGTH_SHORT).show();
        }
    }


}

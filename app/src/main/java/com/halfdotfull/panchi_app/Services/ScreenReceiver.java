package com.halfdotfull.panchi_app.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by anant bansal on 6/4/2017.
 */

public class ScreenReceiver extends BroadcastReceiver {

    public static boolean screenOn;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOn = false;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOn = true;
        }
        /*Intent i = new Intent(context, MessageService.class);
        i.putExtra("screen_state", screenOn);
        context.startService(i);*/
    }
}

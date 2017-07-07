package com.halfdotfull.panchi_app.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.halfdotfull.panchi_app.R;

public class FakeRinging extends AppCompatActivity{
    private String networkCarrier;
    private MediaPlayer mp;
    private Ringtone ringtone;
    BroadcastReceiver mBroadcastReceiver;
    boolean screenOn=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setContentView(R.layout.activity_fake_ringing);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView fakeName = (TextView)findViewById(R.id.chosenfakename);
        TextView fakeNumber = (TextView)findViewById(R.id.chosenfakenumber);
        mBroadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                    screenOn=false;
                    try {
                        ringtone.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                    screenOn=true;
                    ringtone.play();
                }
            }
        };
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mBroadcastReceiver,filter);

        final TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        networkCarrier = tm.getNetworkOperatorName();

        /*TextView titleBar = (TextView)findViewById(R.id.textView1);
        if(networkCarrier != null){
            titleBar.setText("Incoming call - " + networkCarrier);
        }else{
            titleBar.setText("Incoming call");
        }*/

        String callNumber = getContactNumber();
        String callName = getContactName();

        fakeName.setText(callName);
        fakeNumber.setText(callNumber);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone=RingtoneManager.getRingtone(getApplicationContext(),notification);
        if(screenOn==true)
            ringtone.play();

        ImageView answerCall = (ImageView) findViewById(R.id.answercall);
        ImageView rejectCall = (ImageView) findViewById(R.id.rejectcall);

        answerCall.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ringtone.stop();
                unregisterReceiver(mBroadcastReceiver);
                Intent intent =new Intent(FakeRinging.this,FakeReceiving.class);
                startActivity(intent);
                finish();
            }
        });
        rejectCall.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                unregisterReceiver(mBroadcastReceiver);
                ringtone.stop();
                finish();
            }
        });
    }
    private String getContactNumber(){
        String contact = null;
        Intent myIntent = getIntent();
        Bundle mIntent = myIntent.getExtras();
        if(mIntent != null){
            contact  = mIntent.getString("myfakenumber");
        }
        return contact;
    }

    private String getContactName(){
        String contactName = null;
        Intent myIntent = getIntent();
        Bundle mIntent = myIntent.getExtras();
        if(mIntent != null){
            contactName  = mIntent.getString("myfakename");
        }
        return contactName;
    }
}

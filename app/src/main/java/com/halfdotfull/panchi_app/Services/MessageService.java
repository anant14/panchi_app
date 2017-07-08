package com.halfdotfull.panchi_app.Services;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.halfdotfull.panchi_app.Database.ContactDataBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by anant bansal on 6/4/2017.
 */

public class MessageService extends Service implements SensorEventListener {

    private final String DEBUG_TAG = "[GPS Ping]";
    private boolean xmlSuccessful = false;
    private boolean locationTimeExpired = false;
    private LocationManager lm;
    public static String latitude=null,longitude=null;
    public static double accuracy;
    String address, city, state;
    Boolean wasShaken = false;
    boolean screenOn=true;
    SharedPreferences sharedpreferences;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravit
    ContactDataBase db;
    BroadcastReceiver mReceiver;

    public MessageService() {
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // REGISTER RECEIVER THAT HANDLES SCREEN ON AND SCREEN OFF LOGIC
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        mReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                    screenOn=true;
                }
                if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                    screenOn=false;
                }
            }
        };
        wasShaken = false;
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        MyLocationListner listner = new MyLocationListner();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {

        }
        else {
            lm.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000,
                    100,
                    listner
            );
            lm.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    10000,
                    100,
                    listner
            );
        }

        sharedpreferences = getSharedPreferences("panchi", Context.MODE_PRIVATE);//To display MESSAGE
        Log.d("Registered", "onstart service");
        SensorManager sManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL); // or other delay
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent se) {
        try {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            db = new ContactDataBase(MessageService.this);
            if (mAccel > 50 && wasShaken == false && screenOn) {
                String message=createMessage();
                SmsManager smsmanager = SmsManager.getDefault();
                Cursor res = db.getAllData();
                Log.d("TAGGER", "onSensorChanged: "+String.valueOf(res.getCount()));
                if (res.getCount() == 0)
                    Toast.makeText(this, "No contacts given", Toast.LENGTH_SHORT).show();
                else {
                    while (res.moveToNext()) {
                        smsmanager.sendTextMessage(res.getString(0),null,message,null,null);
                            Toast.makeText(this, address, Toast.LENGTH_SHORT).show();
                            wasShaken = true;
                        }
                        Log.d("service", message);
                        Toast.makeText(this, "Emergency Message sent to " + res.getString(1), Toast.LENGTH_LONG).show();
                    }
                Toast.makeText(this, sharedpreferences.getString("Message", ""), Toast.LENGTH_SHORT).show();
                }

            }
             catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String createMessage() {
        String message="";
        Geocoder geocoder=new Geocoder(MessageService.this,Locale.getDefault());
        List<Address> addressList=new ArrayList<>();
        if(latitude!=null&&longitude!=null){
            try {
                addressList=geocoder.getFromLocation(Double.valueOf(latitude),Double.valueOf(longitude),1);
                address=addressList.get(0).getAddressLine(0);
                city=addressList.get(0).getLocality();
                state=addressList.get(0).getAdminArea();
                StringBuffer smsAddressLink = new StringBuffer();
                smsAddressLink.append("http://maps.google.com/?q=");
                smsAddressLink.append(latitude);
                smsAddressLink.append(",");
                smsAddressLink.append(longitude);
                String messageByUser=sharedpreferences.getString("Message", "");
                if (messageByUser.equals("")) {
                    messageByUser = " Please help me ";
                }
                message=messageByUser + System.getProperty("line.separator")+
                        " I am at " + address +
                        " " + city + " " +
                        state +" "+ System.getProperty("line.separator")+
                        smsAddressLink.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            message="Please help me";
        }
        return message;
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public class MyLocationListner implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
            location.getProvider();
           /* Toast.makeText(MessageService.this, "Latitude "+ latitude, Toast.LENGTH_SHORT).show();
            Toast.makeText(MessageService.this, "longitude"+ longitude, Toast.LENGTH_SHORT).show();
          */  accuracy = location.getAccuracy();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged: "+provider+" "+String.valueOf(status));
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled: "+provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled: "+provider);
        }
    }
}

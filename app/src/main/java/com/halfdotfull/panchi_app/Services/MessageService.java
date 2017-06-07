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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

/**
 * Created by anant bansal on 6/4/2017.
 */

public class MessageService extends Service implements SensorEventListener {

    private final String DEBUG_TAG = "[GPS Ping]";
    private boolean xmlSuccessful = false;
    private boolean locationTimeExpired = false;
    private LocationManager lm;
    public static String latitude;
    public static String longitude;
    public static double accuracy;
    String add, cit, stat;
    Boolean wasShaken = false;
    boolean screenOn;
    SharedPreferences sharedpreferences;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravit
    ContactDataBase db;

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
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        screenOn = intent.getBooleanExtra("screen_state", false);
        wasShaken = false;
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        MyLocationListner listner = new MyLocationListner();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        lm.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                600000,
                100,
                listner
        );
        lm.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                600000,
                100,
                listner
        );

        sharedpreferences = getSharedPreferences("panchi", Context.MODE_PRIVATE);//To display MESSAGE
        Log.d("service", "onstart service");
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

            if (mAccel > 50 && wasShaken == false && !screenOn) {
                String msg = null;
                Geocoder geocoder;
                geocoder = new Geocoder(MessageService.this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    if (latitude != null && longitude != null) {
                        addresses = geocoder.getFromLocation(Double.valueOf(latitude), Double.valueOf(longitude), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    } else {
                        Toast.makeText(this, "Value of latitude and logitude are NULL", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                add = addresses.get(0).getAddressLine(0); // If any additional address line present than only,
                                                            // check with max available address lines by getMaxAddressLineIndex()
                cit = addresses.get(0).getLocality();
                stat = addresses.get(0).getAdminArea();
                StringBuffer smsBody = new StringBuffer();
                smsBody.append("http://maps.google.com/?q=");
                smsBody.append(latitude);
                smsBody.append(",");
                smsBody.append(longitude);
                SmsManager smsmanager = SmsManager.getDefault();
                Cursor res = db.getAllData();
                if (res.getCount() == 0)
                    Toast.makeText(this, "No contacts given", Toast.LENGTH_SHORT).show();
                else {
                    while (res.moveToNext()) {
                        msg = sharedpreferences.getString("Message", "");
                        if (msg == "") {
                            msg = " Please help me ";
                        }
                        if (latitude == null && longitude == null) {
                            smsmanager.sendTextMessage(res.getString(0), null, msg, null, null);
                        } else {
                            smsmanager.sendTextMessage(res.getString(0), null, msg + System.getProperty("line.separator")+
                                    " I am at " + add + " " + cit + " " + stat+" "+ System.getProperty("line.separator")+
                                    smsBody.toString(), null, null);
                            Toast.makeText(this, add, Toast.LENGTH_SHORT).show();
                            wasShaken = true;
                        }
                        Log.d("service", msg);
                        Toast.makeText(getApplicationContext(), "Emergency Message sent to " + res.getString(1), Toast.LENGTH_LONG).show();
                    }
                }
                File file = getFilesDir();
                File in = new File(file, "ContactsList");
                FileInputStream fin = null;
                fin = new FileInputStream(in);
                InputStreamReader isr = new InputStreamReader(fin);
                BufferedReader bufRdr = new BufferedReader(isr);
                String str = "";
                Toast.makeText(this, sharedpreferences.getString("Message", ""), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
         /*   Toast.makeText(MessageService.this, "Latitude "+ latitude, Toast.LENGTH_SHORT).show();
            Toast.makeText(MessageService.this, "longitude"+ longitude, Toast.LENGTH_SHORT).show();*/
            accuracy = location.getAccuracy();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}

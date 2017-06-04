package com.halfdotfull.panchi_app.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.halfdotfull.panchi_app.R;
import com.halfdotfull.panchi_app.Services.MessageService;

import java.util.ArrayList;
import java.util.HashMap;

public class isSafe extends AppCompatActivity {

    ProgressDialog progress=null;
    TextView score,title,safe;
    int open=1;
    ArrayList<String> a =new ArrayList<>();
    HashMap<String ,Integer> places=new HashMap<String , Integer>();
    double sum=0;
    int count_open=0,count_close=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_is_safe);

        Toast.makeText(this, "latitude "+ MessageService.latitude+" "+"longitude "+MessageService.longitude, Toast.LENGTH_SHORT).show();
        score= (TextView) findViewById(R.id.score);
        title= (TextView) findViewById(R.id.title);
        safe= (TextView) findViewById(R.id.safe);
        places.put("hospital",8);
        places.put("airport",9);
        places.put("mosque",8);
        places.put("bus_station",7);
        places.put("night_club",6);
        places.put("convenience_store",6);
        places.put("fire_station",7);
        a.add("hospital");
        a.add("hospital");
        a.add("hospital");a.add("hospital");
        a.add("fire_station");
        a.add("bus_station");
        a.add("fire_station");a.add("bus_station");
        a.add("airport");a.add("airport");a.add("airport");
        a.add("bus_station");a.add("night_club");a.add("convenience_store");
        for(int i=0;i<a.size();i++)
        {
            if(open!=0)
            {
                count_open++;
            }
            else
                count_close++;

            sum=sum+places.get(a.get(i));
        }
        sum=sum/a.size();
    }

    @Override
    protected void onStart() {
        super.onStart();
        progress = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progress.setMessage("Loading Score");
        progress.setCancelable(false);
        progress.show();
        long time = System.currentTimeMillis();
        new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                progress.dismiss();
                Toast.makeText(isSafe.this, String.valueOf(sum), Toast.LENGTH_LONG).show();
                title.setText("THE SCORE IS");
                score.setText(String.valueOf(sum));
                Toast.makeText(isSafe.this, String.valueOf(count_open) + "OPEN", Toast.LENGTH_LONG).show();

                if(sum>7)
                {
                    safe.setText("Yor are Pretty Safe With Score Greater Than 7");
                }
                else if(sum<7 && sum >6)
                {
                    safe.setText("You are in range of 6-7, Keep your phone handy");
                }
                else
                {
                    safe.setText("Below average , Be safe and Be Atentive");
                }
            }

        }.start();

    }
}

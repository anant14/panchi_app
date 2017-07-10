package com.halfdotfull.panchi_app.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.halfdotfull.panchi_app.Model.PlaceDetail;
import com.halfdotfull.panchi_app.Model.Request;
import com.halfdotfull.panchi_app.Model.PoliceDetail;
import com.halfdotfull.panchi_app.R;
import com.halfdotfull.panchi_app.Services.MessageService;

import org.json.JSONObject;

import java.util.ArrayList;

public class Police extends AppCompatActivity {
    RecyclerView mRecyclerView;
    ArrayList<String> police;
    ArrayList<String> policeName;
    ArrayList<PoliceDetail> policeDetails;
    Adapter adapter;
    Gson mGson;
    RequestQueue mRequestQueue;
    ProgressDialog progressdialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police);

        mRecyclerView = (RecyclerView) findViewById(R.id.policeRecycle);
        police = new ArrayList<>();
        policeName = new ArrayList<>();
        policeDetails = new ArrayList<>();
        adapter = new Adapter();
        mGson = new Gson();
        mRequestQueue = Volley.newRequestQueue(this);
        populateArray();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        progressdialog = new ProgressDialog(Police.this);
        progressdialog.setMessage("Loading police stations");
        progressdialog.show();
        progressdialog.setCancelable(false);
        populateArray();
    }

    private void populateArray() {
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ MessageService.latitude+","+MessageService.longitude+"&radius=2000&type=police&key=AIzaSyBgEqbEuZ8LJdG7BmDXn3frx89AK1IVd0c",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Request s = mGson.fromJson(response.toString(), Request.class);
                        for (int i = 0; i < s.getResults().size(); i++) {
                            police.add(s.getResults().get(i).getPlace_id());
                            policeName.add(s.getResults().get(i).getName());
                            Log.d("POLICEID", "onResponse: " + s.getResults().get(i).getPlace_id());
                            //Toast.makeText(MainActivity.this, s.getResults().get(i).getPlace_id(), Toast.LENGTH_SHORT).show();
                        }
                        getPhoneNumber(policeName);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", "onErrorResponse: " + error.getMessage());
                    }
                });
        mRequestQueue.add(jsonRequest);
    }

    private void getPhoneNumber(final ArrayList<String> policeNames) {
        Log.d("numPolice", "getPhoneNumber: " + police.size());
        for (int i = 0; i < police.size(); i++) {
            String id = police.get(i);
            Log.d("numPolice", "getPhoneNumber: " + id);
            String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + id + "&key=AIzaSyBgEqbEuZ8LJdG7BmDXn3frx89AK1IVd0c";
            Log.d("URL", "getPhoneNumber: " + url);
            final int finalI = i;
            JsonObjectRequest number = new JsonObjectRequest(url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("jsonRequest", "onResponse: " + response.toString());
                            PlaceDetail details = mGson.fromJson(response.toString(), PlaceDetail.class);
                            if (details.getResult().getFormatted_phone_number() == null) {
                                policeDetails.add(new PoliceDetail(policeNames.get(finalI), "100"));
                                adapter.notifyDataSetChanged();
                            } else {
                                policeDetails.add(new PoliceDetail(policeNames.get(finalI), details.getResult().getFormatted_phone_number()));
                                adapter.notifyItemInserted(finalI);
                                Log.d("Number", "onResponse: " + details.getResult().getFormatted_phone_number());
                            }
                            Log.d("Address", "onResponse: " + details.getResult().getFormatted_address());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
            mRequestQueue.add(number);
        }
    }

    public class ViewH extends RecyclerView.ViewHolder {
        TextView number, policeName;

        public ViewH(View itemView) {
            super(itemView);
            number = (TextView) itemView.findViewById(R.id.policeNumber);
            policeName = (TextView) itemView.findViewById(R.id.policeStation);

        }
    }

    public class Adapter extends RecyclerView.Adapter<ViewH> {

        @Override
        public ViewH onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflate = getLayoutInflater();
            View v = inflate.inflate(R.layout.police_layout, parent, false);

            return new ViewH(v);
        }

        @Override
        public void onBindViewHolder(final ViewH holder, int position) {
            PoliceDetail detail = policeDetails.get(position);
            progressdialog.dismiss();
            holder.policeName.setText(detail.getPoliceStation());
            holder.number.setText(detail.getNumber());
            holder.number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + holder.number.getText()));
                    if (ActivityCompat.checkSelfPermission(Police.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return policeDetails.size();
        }
    }
}

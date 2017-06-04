package com.halfdotfull.panchi_app;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by anant bansal on 6/4/2017.
 */

public class Permissions {
    private static int i;

    public interface OnpermissionResultListner
    {
        void OnGranted(String fperman);
        void OnDenied(String fperman);
    }
    private static OnpermissionResultListner sopr1;

    public static void askforPermission(Activity act, String[] perm, OnpermissionResultListner opr1)
    {
        sopr1=opr1;
        ActivityCompat.requestPermissions(act,perm ,114);
    }
    public static void OnPermResult(int requestcode,String[] perm,int[] rescodes)
    {
        if(requestcode==114) {
            for(i=0;i<perm.length;i++) {
                if(rescodes[i]== PERMISSION_GRANTED && perm[i]== Manifest.permission.SEND_SMS) {
                    sopr1.OnGranted("permission for Message given");
                }
                else {sopr1.OnDenied("permission for Message not given");
                }
                if(rescodes[i]== PERMISSION_GRANTED && perm[i]== Manifest.permission.READ_EXTERNAL_STORAGE) {
                    sopr1.OnGranted("permission for external storage given");
                }
                else {sopr1.OnDenied("permission for external storage not given");
                }
                if(rescodes[i]== PERMISSION_GRANTED && perm[i]== Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    sopr1.OnGranted("permission for external storage given");
                }
                else {sopr1.OnDenied("permission for external storage not given");
                }
                if(rescodes[i]== PERMISSION_GRANTED && perm[i]== Manifest.permission.READ_CONTACTS) {
                    sopr1.OnGranted("Permisssion for contacts given");
                }
                else {
                    sopr1.OnDenied("Permisssion for contacts not given");
                }

                if(rescodes[i]== PERMISSION_GRANTED && perm[i]== Manifest.permission.WRITE_CONTACTS) {
                    sopr1.OnGranted("Permisssion for contacts given");
                }
                else {
                    sopr1.OnDenied("Permisssion for contacts not given");
                }
                if(rescodes[i]== PERMISSION_GRANTED && perm[i]== Manifest.permission.ACCESS_FINE_LOCATION) {
                    sopr1.OnGranted("Permisssion for Location given");
                }
                else {
                    sopr1.OnDenied("Permisssion for Location not given");
                }
                if(rescodes[i]== PERMISSION_GRANTED && perm[i]== Manifest.permission.ACCESS_COARSE_LOCATION) {
                    sopr1.OnGranted("Permisssion for Location given");
                }
                else {
                    sopr1.OnDenied("Permisssion for Location not given");
                }
                if(rescodes[i]== PERMISSION_GRANTED && perm[i]== Manifest.permission.CALL_PHONE) {
                    sopr1.OnGranted("Permisssion for Calling given");
                }
                else {
                    sopr1.OnDenied("Permisssion for Calling not given");
                }
            }
        }
    }

}

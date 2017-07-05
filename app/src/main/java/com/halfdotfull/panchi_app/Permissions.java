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
                if(perm[i].equals(Manifest.permission.SEND_SMS)) {
                    if(rescodes[i]== PERMISSION_GRANTED )
                    sopr1.OnGranted("permission for Message given");
                else sopr1.OnDenied("permission for Message not given");
                }

                else
                if(perm[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if(rescodes[i]== PERMISSION_GRANTED )
                    sopr1.OnGranted("permission for external storage given");
                else sopr1.OnDenied("permission for external storage not given");
                }

                else
                if(perm[i].equals(Manifest.permission.READ_CONTACTS)) {
                    if(rescodes[i]== PERMISSION_GRANTED)
                    sopr1.OnGranted("Permisssion for contacts given");
                else sopr1.OnDenied("Permisssion for contacts not given");
                }

                else
                if( perm[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if(rescodes[i]== PERMISSION_GRANTED)
                    sopr1.OnGranted("Permisssion for Location given");
                else sopr1.OnDenied("Permisssion for Location not given");
                }

                else
                if(perm[i]== Manifest.permission.CALL_PHONE) {
                    if(rescodes[i]== PERMISSION_GRANTED)
                    sopr1.OnGranted("Permisssion for Calling given");
                else sopr1.OnDenied("Permisssion for Calling not given");
                }
            }
        }
    }

}

// Created by plusminus on 00:23:14 - 03.10.2008
package org.osmdroid;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

/**
 * Default map view activity.
 *
 * @author Manuel Stahl
 *
 */
public class MapActivity extends FragmentActivity
{

    private static final int DIALOG_ABOUT_ID = 1;
	private static final String MAP_FRAGMENT_TAG = "org.osmdroid.MAP_FRAGMENT_TAG";

    // ===========================================================
    // Constructors
    // ===========================================================
    /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.setContentView(org.osmdroid.R.layout.main);

        FragmentManager fm = this.getSupportFragmentManager();

		if (fm.findFragmentByTag(MAP_FRAGMENT_TAG) == null) {
			MapFragment mapFragment = MapFragment.newInstance();
			fm.beginTransaction().add(org.osmdroid.R.id.map_container, mapFragment, MAP_FRAGMENT_TAG).commit();
		}
        locationPermissionRequest();
    }

    @Override
    protected Dialog onCreateDialog(final int id)
    {
        Dialog dialog;

        switch (id) {
            case DIALOG_ABOUT_ID:
                return new AlertDialog.Builder(MapActivity.this).setIcon(org.osmdroid.R.drawable.icon)
                        .setTitle(org.osmdroid.R.string.app_name).setMessage(org.osmdroid.R.string.about_message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int whichButton)
                            {
                                //
                            }
                        }).create();

            default:
                dialog = null;
                break;
        }
        return dialog;
    }

    private void locationPermissionRequest() {

        if (Build.VERSION.SDK_INT>=23){
            int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
//TODO
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showMessageOKCancel("You need to allow access to Contacts",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT>=23){
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                122);
                                        Log.e("request","value1");
                                    }
                                }
                            });
                    return;
                }
                Log.e("request","value2");
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        122);
                return;
//TODO
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}

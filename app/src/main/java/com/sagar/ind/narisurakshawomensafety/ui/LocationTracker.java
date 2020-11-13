package com.sagar.ind.narisurakshawomensafety.ui;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.tasks.OnSuccessListener;
import java.util.concurrent.Executor;

public class LocationTracker extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{


    private final Context mainContext;
    public boolean is_GPS_Enabled = false;
    public boolean is_Network_Enabled = false;

    public double myLatitude;
    public double myLongitude;

    //google new starts
    private FusedLocationProviderClient fusedLPC;
    public GoogleApiClient myGoogleApiC;
    private Location myLoc;
    public LocationRequest locationRequest;
    //google new ends

    //The min distance to change updates in meters
    private static final long MinDistChangeForUpdate = 10; //10 meters

    //The minimum time between updates in milliseconds
    private static final long MinTimeBwUpdate = 1000*60*1; // 1 minute

    protected LocationManager locManager;

    //Constructor for getting the Context of MainActivity
    public LocationTracker(Context context)
    {
        this.mainContext = context;
        checkMyConnection();
    }

    public void checkMyConnection(){
        //google api for getting location service
        myGoogleApiC = new GoogleApiClient.Builder(mainContext)
                .addConnectionCallbacks( this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locManager = (LocationManager) mainContext.getSystemService(Context.LOCATION_SERVICE);
        fusedLPC = LocationServices.getFusedLocationProviderClient(mainContext);

        //Getting GPS status
        is_GPS_Enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        //Getting network status
        is_Network_Enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public Location get_Location() {

        try {

            if (!is_GPS_Enabled && !is_Network_Enabled)
            {
                // no network provider is enabled
            } else {
                //First get location from Network Provider

                if (is_Network_Enabled)
                {
                    if (ActivityCompat.checkSelfPermission(mainContext, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(mainContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                            int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return null;
                    }
                    locManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MinTimeBwUpdate,
                            MinDistChangeForUpdate, this);


                    if (locManager != null)
                    {
                        myLoc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        updateGPSCoordinates();

                    }
                }

                //if GPS Enabled get lat/long using GPS Services
                if (is_GPS_Enabled)
                {
                    if (ActivityCompat.checkSelfPermission(mainContext, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(mainContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return null;
                    }

                    fusedLPC.getLastLocation()
                            .addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location)
                                {
                                    myLoc=location;
                                    updateGPSCoordinates();
                                    
                                }
                            });
                }
            }
        }
        catch (Exception e)
        {
         e.printStackTrace();
        }
        return myLoc;
    }

    public void updateGPSCoordinates()
    {       if(myLoc!=null) {
        myLatitude = myLoc.getLatitude();
        myLongitude = myLoc.getLongitude();
    }
    }

    //google method
    @Override
    public void onLocationChanged(Location location) {

    }
    //google method ends

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//google implements starts
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        myGoogleApiC.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
